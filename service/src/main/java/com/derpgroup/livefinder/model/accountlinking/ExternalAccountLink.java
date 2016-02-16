package com.derpgroup.livefinder.model.accountlinking;

public class ExternalAccountLink {

  private String userId;
  private String externalUserId;
  private String authToken;
  private InterfaceName interfaceName;
  
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
  
  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public InterfaceName getInterfaceName() {
    return interfaceName;
  }
  
  public void setInterfaceName(InterfaceName interfaceName) {
    this.interfaceName = interfaceName;
  }
}
