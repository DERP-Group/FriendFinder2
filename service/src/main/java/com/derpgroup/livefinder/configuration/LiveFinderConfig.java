package com.derpgroup.livefinder.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LiveFinderConfig {
  
  @NotNull
  private SteamAccountLinkingConfig steamAccountLinkingConfig;
  @NotNull
  private AlexaAccountLinkingConfig alexaAccountLinkingConfig;
  @NotNull
  private TwitchAccountLinkingConfig twitchAccountLinkingConfig;
  @NotNull
  private String apiKey;

  @JsonProperty
  public String getApiKey() {
    return apiKey;
  }

  @JsonProperty
  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  @JsonProperty
  public SteamAccountLinkingConfig getSteamAccountLinkingConfig() {
    return steamAccountLinkingConfig;
  }

  @JsonProperty
  public void setSteamAccountLinkingConfig(
      SteamAccountLinkingConfig steamAccountLinkingConfig) {
    this.steamAccountLinkingConfig = steamAccountLinkingConfig;
  }

  @JsonProperty
  public AlexaAccountLinkingConfig getAlexaAccountLinkingConfig() {
    return alexaAccountLinkingConfig;
  }

  @JsonProperty
  public void setAlexaAccountLinkingConfig(
      AlexaAccountLinkingConfig alexaAccountLinkingConfig) {
    this.alexaAccountLinkingConfig = alexaAccountLinkingConfig;
  }

  @JsonProperty
  public TwitchAccountLinkingConfig getTwitchAccountLinkingConfig() {
    return twitchAccountLinkingConfig;
  }

  @JsonProperty
  public void setTwitchAccountLinkingConfig(
      TwitchAccountLinkingConfig twitchAccountLinkingConfig) {
    this.twitchAccountLinkingConfig = twitchAccountLinkingConfig;
  }
}
