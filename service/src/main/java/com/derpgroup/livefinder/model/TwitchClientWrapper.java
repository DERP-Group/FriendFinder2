package com.derpgroup.livefinder.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.livefinder.manager.TwitchClient;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;

public class TwitchClientWrapper {
  private final Logger LOG = LoggerFactory.getLogger(TwitchClientWrapper.class);
  
  private static TwitchClientWrapper instance;
  private boolean initialized = false;
  private TwitchClient client;

  private TwitchClientWrapper(){}
  
  public static synchronized TwitchClientWrapper getInstance(){
    if(instance == null){
      instance = new TwitchClientWrapper();
    }
    return instance;
  }
  
  public synchronized void init(String twitchApiRootUri, String clientId, String clientSecret, String redirectUri){
    LOG.info("Initializing Steam Client.");
    if(initialized){
      throw new RuntimeException("TwitchClient is already initialized");
    }
    client = new TwitchClient(twitchApiRootUri, clientId, clientSecret, redirectUri);
    initialized = true;
  }
  
  public boolean isInitialized(){return initialized;}

  public TwitchClient getClient() {
    return client;
  }
}
