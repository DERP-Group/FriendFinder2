package com.derpgroup.livefinder.model.accountlinking;

public class TwitchUser {

  private String name;
  private String authToken;
  private String refreshToken;
  private String id;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getAuthToken() {
    return authToken;
  }
  
  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }
  
  public String getRefreshToken() {
    return refreshToken;
  }
  
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "TwitchUser [name=" + name + ", authToken=" + authToken
        + ", refreshToken=" + refreshToken + ", id=" + id + "]";
  }
}
