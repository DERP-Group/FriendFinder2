package com.derpgroup.livefinder.model.accountlinking;

public class AccountLinkingUser {

  private String userId;
  private TwitchUser twitchUser;
  private String steamId;
  
  public String userId() {
    return userId;
  }

  public void userId(String userId) {
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
}
