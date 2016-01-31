package com.derpgroup.livefinder.manager;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.derpgroup.livefinder.model.accountlinking.AuthenticationException;

public class TwitchClientTest {

  private TwitchClient twitchClient;
  
  @Before
  public void setup(){
    twitchClient = new TwitchClient("https://api.twitch.tv/kraken/oauth2","31yn7mmsohzw3pyrvere5biycw4wbv9"
        ,"siile5navsqcwy0s7fozlrrjn0hokpk","http://twitch.oauth.derpgroup.com:9080/livefinder/auth/twitch");
  }
  
  
  //Test exists for purpose of working with the real Twitch API - ignoring to not trigger at build time 
  @Test
  @Ignore
  public void testRedeemCode() throws AuthenticationException{
    TwitchTokenResponse tokenResponse = twitchClient.redeemCode("xvb4ncj0f74rma5mnbycnjzv5dllg5");
    assertNotNull(tokenResponse);
    assertNotNull(tokenResponse.getAccessToken());
    assertNotNull(tokenResponse.getRefreshToken());
    
    System.out.println(tokenResponse.toString());
  }
}
