package com.derpgroup.livefinder.manager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchChannel {

  private String id;
  private String displayName;
  private String url;

  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  @JsonProperty("_id")
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("display_name")
  public String getDisplayName() {
    return displayName;
  }

  @JsonProperty("display_name")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return "TwitchChannel [id=" + id + ", displayName=" + displayName
        + ", url=" + url + "]";
  }
}
