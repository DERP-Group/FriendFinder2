package com.derpgroup.livefinder.model.accountlinking;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class UserAccount {

  private String userId;
  private TwitchUser twitchUser;
  private String steamId;
  private String alexaId;
  private String firstName;
  private long dateCreated;
  private Map<String,ExternalAccountLink> externalAccountLinks;
  
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public TwitchUser getTwitchUser() {
    return twitchUser;
  }
  
  public void setTwitchUser(TwitchUser twitchUser) {
    this.twitchUser = twitchUser;
  }

  public String getSteamId() {
    return steamId;
  }

  public void setSteamId(String steamId) {
    this.steamId = steamId;
  }

  public String getAlexaId() {
    return alexaId;
  }

  public void setAlexaId(String alexaId) {
    this.alexaId = alexaId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public long getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(long dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Map<String, ExternalAccountLink> getExternalAccountLinks() {
    return externalAccountLinks;
  }

  public void setExternalAccountLinks(
      Map<String, ExternalAccountLink> externalAccountLinks) {
    this.externalAccountLinks = externalAccountLinks;
  }

  @Override
  public String toString() {
    return "UserAccount [userId=" + userId + ", twitchUser=" + twitchUser
        + ", steamId=" + steamId + ", alexaId=" + alexaId + ", firstName="
        + firstName + ", dateCreated=" + dateCreated
        + ", externalAccountLinks=" + externalAccountLinks + "]";
  }
}
