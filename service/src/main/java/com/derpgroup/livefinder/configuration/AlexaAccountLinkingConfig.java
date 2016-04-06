package com.derpgroup.livefinder.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlexaAccountLinkingConfig {

  @NotNull
  private String alexaRedirectPath;
  
  @JsonProperty()
  public String getAlexaRedirectPath() {
    return alexaRedirectPath;
  }

  @JsonProperty()
  public void setAlexaRedirectPath(String alexaRedirectPath) {
    this.alexaRedirectPath = alexaRedirectPath;
  }
}
