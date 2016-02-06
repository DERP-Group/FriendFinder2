package com.derpgroup.livefinder.manager;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitchTokenResponse {

  private String accessToken;
  private String refreshToken;
  //private String[] scope;
  
  public TwitchTokenResponse(){}
  
  @JsonProperty("access_token")
  public String getAccessToken() {
    return accessToken;
  }

  @JsonProperty("access_token")
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  @JsonProperty("refresh_token")
  public String getRefreshToken() {
    return refreshToken;
  }

  @JsonProperty("refresh_token")
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  @JsonProperty("scope")
  public String[] getScope() {
    //Doing nothing, because Findbugs hate this, and we don't need the scope right now.
    return null;
  }

  @JsonProperty("scope")
  public void setScope(String[] scope) {
    //Doing nothing, because Findbugs hate this, and we don't need the scope right now.
  }

  @Override
  public String toString() {
    return "TwitchTokenResponse [accessToken=" + accessToken
        + ", refreshToken=" + refreshToken + /*", scope="
        + Arrays.toString(scope) +*/ "]";
  }
}
