package com.derpgroup.livefinder.manager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchUserResponse {

  private String displayName;
  private String id;
  
  @JsonProperty("display_name")
  public String getDisplayName() {
    return displayName;
  }

  @JsonProperty("display_name")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  @JsonProperty("_id")
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "TwitchUserResponse [displayName=" + displayName
        + ", id=" + id + "]";
  }
}
