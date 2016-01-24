package com.derpgroup.livefinder.model.accountlinking;

public class AccountLinkingUser {

  private String userId;
  private TwitchUser twitchUser;
  private String steamId;
  private String alexaId;
  
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

  @Override
  public String toString() {
    return "AccountLinkingUser [userId=" + userId + ", twitchUser="
        + twitchUser + ", steamId=" + steamId + ", alexaId=" + alexaId + "]";
  }
}
