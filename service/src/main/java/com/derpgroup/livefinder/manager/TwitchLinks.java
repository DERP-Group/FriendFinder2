package com.derpgroup.livefinder.manager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchLinks {

  private String self;

  public String getSelf() {
    return self;
  }

  public void setSelf(String self) {
    this.self = self;
  }

  @Override
  public String toString() {
    return "TwitchLinks [self=" + self + "]";
  }
}
