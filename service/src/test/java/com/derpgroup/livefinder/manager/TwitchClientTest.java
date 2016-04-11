package com.derpgroup.livefinder.manager;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.derpgroup.derpwizard.model.accountlinking.AuthenticationException;

public class TwitchClientTest {

  private TwitchClient twitchClient;
  
  @Before
  public void setup(){
    twitchClient = new TwitchClient("https://api.twitch.tv/kraken","31yn7mmsohzw3pyrvere5biycw4wbv9"
        ,"fake","http://twitch.oauth.derpgroup.com:9080/livefinder/auth/twitch");
  }
  
  //Test exists for purpose of working with the real Twitch API - ignoring to not trigger at build time 
  @Test
  @Ignore
  public void testRedeemCode() throws AuthenticationException{
    TwitchTokenResponse tokenResponse = twitchClient.redeemCode("q1zdzfb06qjc5ub0nmxkwfgz8nohuy");
    assertNotNull(tokenResponse);
    assertNotNull(tokenResponse.getAccessToken());
    assertNotNull(tokenResponse.getRefreshToken());
    
    System.out.println(tokenResponse.toString());
  }
  
  //Test exists for purpose of working with the real Twitch API - ignoring to not trigger at build time 
  @Test
  @Ignore
  public void testGetUser() throws AuthenticationException{
    TwitchUserResponse userResponse = twitchClient.getUser("gbpkdf3qpw5vfdvx69mbzdaqeajrna");

    assertNotNull(userResponse);
    assertNotNull(userResponse.getId());
    
    System.out.println(userResponse.toString());
  }
  
  //Test exists for purpose of working with the real Twitch API - ignoring to not trigger at build time 
  @Test
  @Ignore
  public void testGetFollowedStreams() throws AuthenticationException{
    TwitchFollowedStreamsResponse followedStreamsResponse = twitchClient.getFollowedStreams("gbpkdf3qpw5vfdvx69mbzdaqeajrna");

    assertNotNull(followedStreamsResponse);
    assertNotNull(followedStreamsResponse.getStreams());
    assertNotNull(followedStreamsResponse.getStreams());
    
    for(TwitchStream stream : followedStreamsResponse.getStreams()){
      assertNotNull(stream.getId());
      assertNotNull(stream.getLinks());
      assertNotNull(stream.getLinks().getSelf());
      assertNotNull(stream.getChannel());
      assertNotNull(stream.getChannel().getId());
      assertNotNull(stream.getChannel().getDisplayName());
      assertNotNull(stream.getChannel().getUrl());
    }
    
    System.out.println(followedStreamsResponse.toString());
  }
}
