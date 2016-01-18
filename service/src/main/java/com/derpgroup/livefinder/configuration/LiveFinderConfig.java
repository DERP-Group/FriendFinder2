package com.derpgroup.livefinder.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LiveFinderConfig {
  
  @NotNull
  private SteamAccountLinkingConfig steamAccountLinkingConfig;
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
}
