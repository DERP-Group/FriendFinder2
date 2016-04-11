/**
 * Copyright (C) 2015 David Phillips
 * Copyright (C) 2015 Eric Olson
 * Copyright (C) 2015 Rusty Gerard
 * Copyright (C) 2015 Paul Winters
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.derpgroup.livefinder.resource;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import io.dropwizard.setup.Environment;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletResponseEnvelope;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.LinkAccountCard;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.derpgroup.derpwizard.dao.AccountLinkingDAO;
import com.derpgroup.derpwizard.model.accountlinking.ExternalAccountLink;
import com.derpgroup.derpwizard.model.accountlinking.UserAccount;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.derpgroup.derpwizard.voice.exception.DerpwizardExceptionAlexaWrapper;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException.DerpwizardExceptionReasons;
import com.derpgroup.derpwizard.voice.alexa.AlexaUtils;
import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.derpgroup.derpwizard.voice.model.ServiceOutput;
import com.derpgroup.derpwizard.voice.model.SsmlDocumentBuilder;
import com.derpgroup.derpwizard.voice.model.ServiceInput;
import com.derpgroup.derpwizard.voice.util.ConversationHistoryUtils;
import com.derpgroup.livefinder.LiveFinderMetadata;
import com.derpgroup.livefinder.MixInModule;
import com.derpgroup.livefinder.configuration.MainConfig;
import com.derpgroup.livefinder.manager.LiveFinderManager;
import com.derpgroup.livefinder.model.accountlinking.AccountLinkingNotLinkedException;
import com.derpgroup.livefinder.model.accountlinking.InterfaceName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST APIs for requests generating from Amazon Alexa
 *
 * @author Eric
 * @since 0.0.1
 */
@Path("/livefinder/alexa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LiveFinderAlexaResource {

  private static final Logger LOG = LoggerFactory.getLogger(LiveFinderAlexaResource.class);

  private LiveFinderManager manager;
  
  private AccountLinkingDAO accountLinkingDAO;
  
  private ObjectMapper mapper;
  
  String linkingFlowHostname;
  String linkingFlowProtocol;
  String landingPagePath;
  
  public LiveFinderAlexaResource(MainConfig config, Environment env, AccountLinkingDAO accountLinkingDAO) {
    this.accountLinkingDAO = accountLinkingDAO;
    manager = new LiveFinderManager(accountLinkingDAO);
    mapper = new ObjectMapper().registerModule(new MixInModule());
    
    linkingFlowHostname = config.getLiveFinderConfig().getSteamAccountLinkingConfig().getLinkingFlowHostname();
    linkingFlowProtocol = config.getLiveFinderConfig().getSteamAccountLinkingConfig().getLinkingFlowProtocol();
    landingPagePath = config.getLiveFinderConfig().getSteamAccountLinkingConfig().getLandingPagePath();
  }

  /**
   * @return The message, never null
   */
  @POST
  public SpeechletResponseEnvelope doAlexaRequest(SpeechletRequestEnvelope request, @HeaderParam("SignatureCertChainUrl") String signatureCertChainUrl, 
      @HeaderParam("Signature") String signature, @QueryParam("testFlag") Boolean testFlag){
    LiveFinderMetadata outputMetadata = null;
    try {
      if (request.getRequest() == null) {
        throw new DerpwizardException(DerpwizardExceptionReasons.MISSING_INFO.getSsml(),"Missing request body.");
      }
  
      Map<String, Object> sessionAttributes = request.getSession().getAttributes();
      
      if(request.getSession() == null || request.getSession().getUser() == null){
        String message = "Alexa request did not contain a valid userId.";
        LOG.error(message);
        throw new DerpwizardException(message);
      } 
      
      String userId;
      String accessToken = request.getSession().getUser().getAccessToken();
      String alexaUserId = request.getSession().getUser().getUserId();
      
      if(StringUtils.isEmpty(alexaUserId)){
        String message = "Missing Alexa userId.";
        LOG.error(message);
        throw new DerpwizardException(message);
      }else if(StringUtils.isEmpty(accessToken)){
        return doAccountLinking(new LiveFinderMetadata(), "Unauthorized user - please complete account linking via the Alexa app on your mobile device or browser."); //Do account linking prompt here
      }else if(StringUtils.isEmpty(accountLinkingDAO.getUserIdByAuthToken(accessToken))){
        return doAccountLinking(new LiveFinderMetadata(), "Token was unrecognized or had no associated user - please complete account linking via the Alexa app on your mobile device or browser."); //Do account linking prompt here
      }else{
        userId = accountLinkingDAO.getUserIdByAuthToken(accessToken);
        LOG.info("Found userId '" + userId + "' for access token '" + accessToken + "'.");
      }
      sessionAttributes.put("userId", userId);
      
      if(accountLinkingDAO.getAccountLinkByExternalUserIdAndExternalSystemName(alexaUserId, InterfaceName.ALEXA.name()) == null){
        LOG.info("No Alexa account link found for this user, creating new link.");
        ExternalAccountLink accountLink = new ExternalAccountLink();
        accountLink.setUserId(userId);
        accountLink.setExternalUserId(alexaUserId);
        accountLink.setExternalSystemName(InterfaceName.ALEXA.name());
        accountLinkingDAO.createAccountLink(accountLink);
      }
      
      CommonMetadata inputMetadata = mapper.convertValue(sessionAttributes, new TypeReference<LiveFinderMetadata>(){});
      outputMetadata = mapper.convertValue(sessionAttributes, new TypeReference<LiveFinderMetadata>(){});

      ///////////////////////////////////
      // Build the ServiceInput object //
      ///////////////////////////////////
      ServiceInput serviceInput = new ServiceInput();
      serviceInput.setMetadata(inputMetadata);
      Map<String, String> messageAsMap = AlexaUtils.getMessageAsMap(request.getRequest());
      serviceInput.setMessageAsMap(messageAsMap);
      
      SpeechletRequest speechletRequest = (SpeechletRequest)request.getRequest();
      String intent = AlexaUtils.getMessageSubject(speechletRequest);
      serviceInput.setSubject(intent);
      
      ////////////////////////////////////
      // Build the ServiceOutput object //
      ////////////////////////////////////
      ServiceOutput serviceOutput = new ServiceOutput();
      serviceOutput.setMetadata(outputMetadata);
      serviceOutput.setConversationEnded(false);
      ConversationHistoryUtils.registerRequestInConversationHistory(intent, messageAsMap, outputMetadata, outputMetadata.getConversationHistory());
      
      try{
        manager.handleRequest(serviceInput, serviceOutput);
      }catch(AccountLinkingNotLinkedException e){
        return doAccountUpdateSession(e.getInterfaceName(), outputMetadata, userId);
      }
      
      SimpleCard card;
      SsmlOutputSpeech outputSpeech;
      Reprompt reprompt = null;
      boolean shouldEndSession = false;
      
      switch(serviceInput.getSubject()){
      case "END_OF_CONVERSATION":
      case "STOP":
      case "CANCEL":
        outputSpeech = null;
        card = null;
        shouldEndSession = true;
        break;
      default:
        if(StringUtils.isNotEmpty(serviceOutput.getVisualOutput().getTitle())&&
            StringUtils.isNotEmpty(serviceOutput.getVisualOutput().getText())){
          card = new SimpleCard();
          card.setTitle(serviceOutput.getVisualOutput().getTitle());
          card.setContent(serviceOutput.getVisualOutput().getText());
        }
        else{
          card = null;
        }
        if(serviceOutput.getDelayedVoiceOutput() !=null && StringUtils.isNotEmpty(serviceOutput.getDelayedVoiceOutput().getSsmltext())){
          reprompt = new Reprompt();
          SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
          repromptSpeech.setSsml("<speak>"+serviceOutput.getDelayedVoiceOutput().getSsmltext()+"</speak>");
          reprompt.setOutputSpeech(repromptSpeech);
        }

        outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>"+serviceOutput.getVoiceOutput().getSsmltext()+"</speak>");
        shouldEndSession = serviceOutput.isConversationEnded();
        break;
      }
      
      return buildOutput(outputSpeech, card, reprompt, shouldEndSession, outputMetadata);
    }catch(DerpwizardException e){
      LOG.debug(e.getMessage());
      return new DerpwizardExceptionAlexaWrapper(e, "1.0",mapper.convertValue(outputMetadata, new TypeReference<Map<String,Object>>(){}));
    }catch(Throwable t){
      LOG.debug(t.getMessage());
      return new DerpwizardExceptionAlexaWrapper(new DerpwizardException(t.getMessage()),"1.0", mapper.convertValue(outputMetadata, new TypeReference<Map<String,Object>>(){}));
    }
  }

  private SpeechletResponseEnvelope doAccountLinking(LiveFinderMetadata outputMetadata, String message) throws DerpwizardException {
   
    String cardTitle = "Please complete account linking.";
    
    SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
    outputSpeech.setSsml(new SsmlDocumentBuilder().text(message).build().getSsml());
    
    LinkAccountCard card = new LinkAccountCard();
    card.setTitle(cardTitle);
    
    return buildOutput(outputSpeech, card, null, true, outputMetadata);
  }

  private SpeechletResponseEnvelope doAccountUpdateSession(InterfaceName interfaceName, LiveFinderMetadata outputMetadata, String userId) throws DerpwizardException {

    //If this is the approach for linking to the third parties, it should be made generic and added to a manager
    //If the approach ends up being to use LinkAccount cards, however, then that is implementation specific, and can't be made generic
    String output = "Could not find a linked " + interfaceName.name() + " account.<break />  To link one, please follow the account linking instructions in the Alexa app on your phone, tablet, or browser.";
    URI linkingUri;
    try {
      linkingUri = new URIBuilder().setScheme(linkingFlowProtocol).setHost(linkingFlowHostname).setPath(landingPagePath)
          .setParameter("sessionToken", accountLinkingDAO.generateMappingTokenForUserId(userId)).build();
    } catch (URISyntaxException e) {
      throw new DerpwizardException("<speak>Could not process request.</speak>","Could not build a valid linking URI, due to exception.");
    }
    
    String cardTitle = interfaceName.name() + " account linking.";
    StringBuilder cardTextBuilder = new StringBuilder();
    cardTextBuilder.append("Please copy the link below into a browser window and follow the instructions to link your account.");
    cardTextBuilder.append("\n\n");
    cardTextBuilder.append(linkingUri.toString());
    
    SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
    outputSpeech.setSsml(new SsmlDocumentBuilder().text(output).build().getSsml());
    
    SimpleCard card = new SimpleCard();
    card.setTitle(cardTitle);
    card.setContent(cardTextBuilder.toString());
    
    return buildOutput(outputSpeech, card, null, true, outputMetadata);
  }
  
  private SpeechletResponseEnvelope buildOutput(OutputSpeech outputSpeech, Card card, Reprompt reprompt, boolean shouldEndSession, LiveFinderMetadata outputMetadata){

    Map<String,Object> sessionAttributes = mapper.convertValue(outputMetadata, new TypeReference<Map<String,Object>>(){});
    SpeechletResponseEnvelope responseEnvelope = new SpeechletResponseEnvelope();
    
    SpeechletResponse speechletResponse = new SpeechletResponse();

    speechletResponse.setOutputSpeech(outputSpeech);
    speechletResponse.setCard(card);
    speechletResponse.setReprompt(reprompt);
    speechletResponse.setShouldEndSession(shouldEndSession);
    
    responseEnvelope.setResponse(speechletResponse);
    
    responseEnvelope.setSessionAttributes(sessionAttributes);

    return responseEnvelope;
  }
}
