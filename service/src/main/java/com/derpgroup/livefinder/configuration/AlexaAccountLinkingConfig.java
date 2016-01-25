package com.derpgroup.livefinder.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlexaAccountLinkingConfig {

  @NotNull
  private String alexaRedirectUri;
  
  @JsonProperty()
  public String getAlexaRedirectUri() {
    // TODO Auto-generated method stub
    return alexaRedirectUri;
  }

  @JsonProperty()
  public void setAlexaRedirectUri(String alexaRedirectUri) {
    this.alexaRedirectUri = alexaRedirectUri;
  }
}
