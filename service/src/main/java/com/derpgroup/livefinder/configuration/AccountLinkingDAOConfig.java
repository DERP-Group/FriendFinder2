package com.derpgroup.livefinder.configuration;

import java.util.Map;

import javax.validation.constraints.NotNull;

public class AccountLinkingDAOConfig {

  @NotNull
  private String type;
  
  private Map<String,Object> properties;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
}
