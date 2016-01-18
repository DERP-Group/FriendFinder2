package com.derpgroup.livefinder.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SteamAccountLinkingConfig {

  @NotNull
  private String linkingFlowHostname;
  @NotNull
  private String linkingFlowProtocol;
  @NotNull
  private String successPagePath;
  @NotNull
  private String errorPagePath;
  @NotNull
  private String landingPagePath;

  @JsonProperty
  public String getLinkingFlowHostname() {
    return linkingFlowHostname;
  }

  @JsonProperty
  public void setLinkingFlowHostname(String linkingFlowHostname) {
    this.linkingFlowHostname = linkingFlowHostname;
  }

  public String getLinkingFlowProtocol() {
    return linkingFlowProtocol;
  }

  public void setLinkingFlowProtocol(String linkingFlowProtocol) {
    this.linkingFlowProtocol = linkingFlowProtocol;
  }

  @JsonProperty
  public String getSuccessPagePath() {
    return successPagePath;
  }

  @JsonProperty
  public void setSuccessPagePath(String successPagePath) {
    this.successPagePath = successPagePath;
  }

  @JsonProperty
  public String getErrorPagePath() {
    return errorPagePath;
  }

  @JsonProperty
  public void setErrorPagePath(String errorPagePath) {
    this.errorPagePath = errorPagePath;
  }

  public String getLandingPagePath() {
    return landingPagePath;
  }

  public void setLandingPagePath(String landingPagePath) {
    this.landingPagePath = landingPagePath;
  }
}
