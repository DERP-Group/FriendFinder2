package com.derpgroup.livefinder.model.accountlinking;

public class ExternalAccountLink {

  private String userId;
  private String externalUserId;
  private String externalSystemName;
  private String authToken;
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public String getExternalUserId() {
    return externalUserId;
  }
  
  public void setExternalUserId(String externalUserId) {
    this.externalUserId = externalUserId;
  }

  public String getExternalSystemName() {
    return externalSystemName;
  }
  
  public void setExternalSystemName(String externalSystemName) {
    this.externalSystemName = externalSystemName;
  }
  
  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  @Override
  public String toString() {
    return "ExternalAccountLink [userId=" + userId + ", externalUserId="
        + externalUserId + ", externalSystemName=" + externalSystemName
        + ", authToken=" + authToken + "]";
  }
}
