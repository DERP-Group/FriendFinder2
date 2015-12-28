package com.derpgroup.livefinder.model.accountlinking;

public class InterfaceMapping {

  private String userId;
  private String interfaceUserId;
  private InterfaceName interfaceName;
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public String getInterfaceUserId() {
    return interfaceUserId;
  }
  
  public void setInterfaceUserId(String interfaceUserId) {
    this.interfaceUserId = interfaceUserId;
  }
  
  public InterfaceName getInterfaceName() {
    return interfaceName;
  }
  
  public void setInterfaceName(InterfaceName interfaceName) {
    this.interfaceName = interfaceName;
  }
}
