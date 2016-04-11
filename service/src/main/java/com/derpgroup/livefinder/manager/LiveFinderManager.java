package com.derpgroup.livefinder.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.derpgroup.derpwizard.voice.model.ServiceOutput;
import com.derpgroup.derpwizard.voice.model.SsmlDocumentBuilder;
import com.derpgroup.derpwizard.voice.model.ServiceInput;
import com.derpgroup.derpwizard.voice.util.ConversationHistoryUtils;
import com.derpgroup.livefinder.LiveFinderMetadata;
import com.derpgroup.livefinder.MixInModule;
import com.derpgroup.livefinder.dao.AccountLinkingDAO;
import com.derpgroup.livefinder.model.SteamClientWrapper;
import com.derpgroup.livefinder.model.TwitchClientWrapper;
import com.derpgroup.livefinder.model.UserData;
import com.derpgroup.livefinder.model.accountlinking.AccountLinkingNotLinkedException;
import com.derpgroup.livefinder.model.accountlinking.ExternalAccountLink;
import com.derpgroup.livefinder.model.accountlinking.UserAccount;
import com.derpgroup.livefinder.model.accountlinking.AuthenticationException;
import com.derpgroup.livefinder.model.accountlinking.InterfaceName;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.friendslist.Friend;
import com.lukaspradel.steamapi.data.json.friendslist.GetFriendList;
import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.data.json.playersummaries.Player;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetFriendListRequest;
import com.lukaspradel.steamapi.webapi.request.GetPlayerSummariesRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;

public class LiveFinderManager{
  private final Logger LOG = LoggerFactory.getLogger(LiveFinderManager.class);
  
  private static final String SERVICE_SLOT_NAME = "service";

  private SteamWebApiClient steamClient;
  private static SteamClientWrapper steamClientWrapper;
  private static TwitchClient twitchClient;
  private Map<Integer, String> steamStateValues;
  
  private AccountLinkingDAO accountLinkingDAO;

  static {
    steamClientWrapper = SteamClientWrapper.getInstance();
    twitchClient = TwitchClientWrapper.getInstance().getClient();
  }

  static {
    ConversationHistoryUtils.getMapper().registerModule(new MixInModule());
  }

  public LiveFinderManager(AccountLinkingDAO accountLinkingDAO) {
    super();

    this.accountLinkingDAO = accountLinkingDAO;
    steamStateValues = new HashMap<Integer, String>();
    steamStateValues.put(1, "online.");
    steamStateValues.put(2, "online, but busy.");
    steamStateValues.put(3, "online, but away.");
    steamStateValues.put(4, "online, but snoozing.");
    steamStateValues.put(5, "online.");
    steamStateValues.put(6, "online and looking to play!");
  }

  public void handleRequest(ServiceInput serviceInput,
      ServiceOutput serviceOutput) throws DerpwizardException {
    String messageSubject = serviceInput.getSubject();
    
    switch(messageSubject){
    case "START_OF_CONVERSATION":
      doHelloRequest(serviceInput, serviceOutput);
      break;
    case "FIND_BY_SERVICE":
      findByService(serviceInput, serviceOutput);
      break;
      default:
        String message = "Unknown request type '" + messageSubject + "'.";
        LOG.warn(message);
        throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, "Unknown request.");
    }
  }

  protected void doHelpRequest(ServiceInput serviceInput,
      ServiceOutput serviceOutput) throws DerpwizardException {
    serviceOutput.setConversationEnded(false);

    StringBuilder sb = new StringBuilder();
    sb.append("Example requests:");
    sb.append("\n\n");
    sb.append("\"Do I have any friends on Steam?\"");
    sb.append("\"What services do I use?\"");
    sb.append("\"Are any of my Twitch streams live?\"");
    String cardMessage = sb.toString();
    serviceOutput.getVisualOutput().setTitle("How it works:");
    serviceOutput.getVisualOutput().setText(cardMessage);

    String audioMessage = "You can ask questions such as <break />Do I have any friends on Steam right now?<break /> or <break />Are any of my favorite Twitch streams live at the moment?";
    serviceOutput.getVoiceOutput().setSsmltext(audioMessage);
  }

  protected void doHelloRequest(ServiceInput serviceInput,
      ServiceOutput serviceOutput) throws DerpwizardException {
    serviceOutput.setConversationEnded(false);

    UserAccount user = getUser(serviceInput,null);
    StringBuilder outputMessageBuilder = new StringBuilder();
    if(!StringUtils.isEmpty(user.getFirstName())){
      outputMessageBuilder.append("Hi " + user.getFirstName() + "! ");
    }
    outputMessageBuilder.append("Who or what would you like me to find?");
    String outputMessage = outputMessageBuilder.toString();
    serviceOutput.getVisualOutput().setTitle("Hello...");
    serviceOutput.getVisualOutput().setText(outputMessage);
    serviceOutput.getVoiceOutput().setSsmltext(outputMessage);
  }

  protected void doGoodbyeRequest(ServiceInput serviceInput,
      ServiceOutput serviceOutput) throws DerpwizardException {
  }

  protected void doCancelRequest(ServiceInput serviceInput,
      ServiceOutput serviceOutput) throws DerpwizardException {
  }

  protected void doStopRequest(ServiceInput serviceInput,
      ServiceOutput serviceOutput) throws DerpwizardException {
  }
  
  private void findByService(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException{
    Map<String,String> messageMap = serviceInput.getMessageAsMap();
    if(messageMap == null || StringUtils.isEmpty(messageMap.get(SERVICE_SLOT_NAME))){
      String errorMessage = "Could not find by service, because service name was not provided.";
      LOG.warn(errorMessage);
      throw new DerpwizardException(new SsmlDocumentBuilder().text(errorMessage).build().getSsml(), errorMessage, errorMessage);
    }
    
    String service = messageMap.get("service").toLowerCase();
    switch(service){
    case "steam":
      findSteamFriends(serviceInput, serviceOutput);
      break;
    case "twitch":
      findTwitchStreams(serviceInput,serviceOutput);
      break;
      default:
        String message = "Unknown service '" + service + "'.";
        LOG.warn(message);
        throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, message);
    }
  }

  private void findSteamFriends(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException{
    steamClient = steamClientWrapper.getClient();
    
    UserAccount user = getUser(serviceInput, InterfaceName.STEAM);
    ExternalAccountLink accountLink = getExternalAccountLink(user.getUserId(), InterfaceName.STEAM);
    
    List<String> friends = getListOfFriendIdsByUserId(accountLink.getExternalUserId());
    
    GetPlayerSummaries playerSummaries;
    try {
      GetPlayerSummariesRequest playerSummariesRequest = SteamWebApiRequestFactory.createGetPlayerSummariesRequest(friends);
      playerSummaries = steamClient.<GetPlayerSummaries> processRequest(playerSummariesRequest);
    } catch (SteamApiException e) {
      String message = "Unknown Steam exception '" + e.getMessage() + "'.";
      LOG.warn(message);
      throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, "Unknown Steam exception.");
    }

    StringBuilder visualOutputTextBuilder = new StringBuilder();
    visualOutputTextBuilder.append("Friends online:");
    visualOutputTextBuilder.append("\n");
    
    SsmlDocumentBuilder voiceOutputSsmlBuilder = new SsmlDocumentBuilder(Arrays.asList("speak"));
    boolean hasFriendsOnline = false;
    for (Player player : playerSummaries.getResponse().getPlayers()) {
      Integer state = player.getPersonastate();
      if (state == null || state <= 0 || state >= 7) {
        continue;
      }
      String username = player.getPersonaname();
      visualOutputTextBuilder.append("\n" + username + " is " + steamStateValues.get(state));
      voiceOutputSsmlBuilder.text(username + " is " + steamStateValues.get(state)).endSentence();
      
      hasFriendsOnline = true;
    }
    if(!hasFriendsOnline){
      String message = "You don't have any friends logged in to Steam at the moment.";
      serviceOutput.getVisualOutput().setTitle("No friends online...");
      serviceOutput.getVisualOutput().setText(message);
      serviceOutput.getVoiceOutput().setSsmltext(message);
    }else{
      serviceOutput.getVisualOutput().setTitle("Friends online...");
      serviceOutput.getVisualOutput().setText(visualOutputTextBuilder.toString());
      serviceOutput.getVoiceOutput().setSsmltext(voiceOutputSsmlBuilder.build().getSsml());
    }
  }

  public List<String> getListOfFriendIdsByUserId(String steamId) throws DerpwizardException {
    GetFriendListRequest friendListRequest = SteamWebApiRequestFactory.createGetFriendListRequest(steamId);
    List<String> friends = new LinkedList<String>();
    try {
      GetFriendList friendList = steamClient.<GetFriendList> processRequest(friendListRequest);
      for (Friend friend : friendList.getFriendslist().getFriends()) {
        friends.add(friend.getSteamid());
      }
    } catch (SteamApiException e) {
      String message = "Unknown Steam exception '" + e.getMessage() + "'.";
      LOG.warn(message);
      throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, "Unknown Steam exception.");
    }
    return friends;
  }
  
  private void findTwitchStreams(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    UserAccount user = getUser(serviceInput, InterfaceName.TWITCH);
    ExternalAccountLink accountLink = getExternalAccountLink(user.getUserId(), InterfaceName.TWITCH);
    
    TwitchFollowedStreamsResponse response = null;
    try {
      response = twitchClient.getFollowedStreams(accountLink.getAuthToken());
    } catch (AuthenticationException e) {
      String message = "Unknown Twitch exception '" + e.getMessage() + "'.";
      LOG.warn(message);
      throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, message);
    }
    if(response == null){
      String message = "Twitch did not return a valid list of streams.";
      LOG.warn(message);
      throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, message);
    }else if(response.getStreams() == null || response.getStreams().length < 1){
      String message = "None of the streams you follow are currently live.";
      LOG.warn(message);
      throw new DerpwizardException(new SsmlDocumentBuilder().text(message).build().getSsml(), message, message);
    }
    
    StringBuilder visualText = new StringBuilder();
    visualText.append("Current live streams:");
    StringBuilder voiceOutput = new StringBuilder();
    voiceOutput.append("Current <phoneme alphabet=\"ipa\" ph=\"laɪv\">live</phoneme> streams. <break time=\"500ms\" />");
    for(TwitchStream stream : response.getStreams()){
      if(stream.getChannel() == null || stream.getChannel().getDisplayName() == null){
        LOG.warn("No valid channel object for stream '" + stream.getId() + "'.");
        continue;
      }
      if(stream.getLinks() == null || stream.getLinks().getSelf() == null){
        LOG.warn("No valid self link object for stream '" + stream.getId() + "'.");
        continue;
      }
      
      voiceOutput.append("<break time=\"500ms\" />");
      voiceOutput.append(stream.getChannel().getDisplayName());
      visualText.append("\n\n");
      visualText.append(stream.getChannel().getDisplayName());
      visualText.append("\n");
      visualText.append(stream.getChannel().getUrl());
    }
    
    serviceOutput.getVoiceOutput().setSsmltext(voiceOutput.toString());
    serviceOutput.getVisualOutput().setText(visualText.toString());
    serviceOutput.getVisualOutput().setTitle("Active Twitch Streams");
  }
  
  public ExternalAccountLink getExternalAccountLink(String userId, InterfaceName interfaceName) throws AccountLinkingNotLinkedException{
    ExternalAccountLink accountLink = accountLinkingDAO.getAccountLinkByUserIdAndExternalSystemName(userId, interfaceName.name());
    if(accountLink == null || StringUtils.isEmpty(accountLink.getExternalUserId())){
      throw new AccountLinkingNotLinkedException(interfaceName);
    }
    return accountLink;
  }

  public UserAccount getUser(ServiceInput serviceInput, InterfaceName interfaceName) throws DerpwizardException {
    String userId = ((LiveFinderMetadata)serviceInput.getMetadata()).getUserId();
    
    if(StringUtils.isEmpty(userId)){
      String message = "Missing userId.";
      LOG.error(message);
      throw new DerpwizardException(message);
    }
    
    UserAccount user = accountLinkingDAO.getUserByUserId(userId);
    if(user == null){
      throw new AccountLinkingNotLinkedException(interfaceName);
    }
    return user;
  }

}
