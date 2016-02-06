package com.derpgroup.livefinder.manager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchStream {

  private String id;
  private TwitchLinks links;
  private TwitchChannel channel;
  
  @JsonProperty("_id")
  public String getId() {
    return id;
  }

  @JsonProperty("_id")
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("_links")
  public TwitchLinks getLinks() {
    return links;
  }

  @JsonProperty("_links")
  public void setLinks(TwitchLinks links) {
    this.links = links;
  }
  
  public TwitchChannel getChannel() {
    return channel;
  }
  
  public void setChannel(TwitchChannel channel) {
    this.channel = channel;
  }
  
  @Override
  public String toString() {
    return "TwitchStream [id=" + id + ", links=" + links + ", channel="
        + channel + "]";
  }
}
