package com.derpgroup.livefinder.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitchAccountLinkingConfig {

  @NotNull
  private String twitchApiRootUri;
  @NotNull
  private String clientId;
  @NotNull
  private String clientSecret;
  @NotNull
  private String redirectUri;

  @JsonProperty
  public String getTwitchApiRootUri() {
    return twitchApiRootUri;
  }

  @JsonProperty
  public void setTwitchApiRootUri(String twitchApiRootUri) {
    this.twitchApiRootUri = twitchApiRootUri;
  }

  @JsonProperty
  public String getClientId() {
    return clientId;
  }

  @JsonProperty
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @JsonProperty
  public String getClientSecret() {
    return clientSecret;
  }

  @JsonProperty
  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  @JsonProperty
  public String getRedirectUri() {
    return redirectUri;
  }

  @JsonProperty
  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }
}
