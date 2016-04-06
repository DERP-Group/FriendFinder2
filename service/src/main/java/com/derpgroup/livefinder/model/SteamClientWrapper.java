package com.derpgroup.livefinder.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;

public class SteamClientWrapper {
  private final Logger LOG = LoggerFactory.getLogger(SteamClientWrapper.class);
  
  private static SteamClientWrapper instance;
  private boolean initialized = false;
  private SteamWebApiClient client;

  private SteamClientWrapper(){}
  
  public static synchronized SteamClientWrapper getInstance(){
    if(instance == null){
      instance = new SteamClientWrapper();
    }
    return instance;
  }
  
  public synchronized void init(String apiKey){
    LOG.info("Initializing Steam Client.");
    if(initialized){
      throw new RuntimeException("SteamClient is already initialized");
    }
    client = new SteamWebApiClient.SteamWebApiClientBuilder(apiKey).build();
    initialized = true;
  }
  
  public boolean isInitialized(){return initialized;}

  public SteamWebApiClient getClient() {
    return client;
  }
}
