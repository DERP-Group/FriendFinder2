package com.derpgroup.livefinder.manager;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchFollowedStreamsResponse {

  private TwitchStream[] streams;

  public TwitchStream[] getStreams() {
    return streams.clone();
  }

  public void setStreams(TwitchStream[] streams) {
    this.streams = streams.clone();
  }

  @Override
  public String toString() {
    return "TwitchFollowedStreamsResponse [streams=" + Arrays.toString(streams)
        + "]";
  }
}
