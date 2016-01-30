package com.derpgroup.livefinder.dao.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.livefinder.dao.AccountLinkingDAO;
import com.derpgroup.livefinder.model.accountlinking.AccountLinkingUser;
import com.derpgroup.livefinder.model.accountlinking.InterfaceMapping;
import com.derpgroup.livefinder.model.accountlinking.InterfaceName;
import com.derpgroup.livefinder.model.accountlinking.TwitchUser;

public class InMemoryAccountLinkingDAO implements AccountLinkingDAO {
  
  private static final Logger LOG = LoggerFactory.getLogger(InMemoryAccountLinkingDAO.class);

  Map<String,AccountLinkingUser> users;
  Map<String,String> mappingTokens;
  Map<String,String> grantedAuthTokens; //Map of token->derpUserId for tokens we have given out
  
  public InMemoryAccountLinkingDAO(){
    users = new HashMap<String,AccountLinkingUser>();
    mappingTokens = new HashMap<String,String>();
    grantedAuthTokens = new HashMap<String,String>();
    
   /* LOG.info("Initializing pre-seeded mapping data.");
    InterfaceMapping mapping = new InterfaceMapping();
    mapping.setInterfaceName(InterfaceName.ALEXA);
    mapping.setInterfaceUserId("amzn1.echo-sdk-account.AH7YZRPVGIG7FRIG77BGROA3D66SSNAGCGKJSDBP6MQ2P3H556X5A");
    mapping.setUserId("96876148-bbcd-40f2-993d-dd3ed635367b");
    
    String compositeKey = mapping.getInterfaceName().name() + "." + mapping.getInterfaceUserId();
    interfaceMappings.put(compositeKey, mapping);
    interfaceMappings.put(mapping.getInterfaceName().name() + "." + "fake", mapping);
    
    LOG.info("Initializing pre-seeded user data.");
    AccountLinkingUser user = new AccountLinkingUser(); //First user has mappings set for everything
    user.setUserId("96876148-bbcd-40f2-993d-dd3ed635367b");
    user.setSteamId("76561198019030536");
    user.setTwitchUser(new TwitchUser()); //populate twitch user
    users.put("96876148-bbcd-40f2-993d-dd3ed635367b", user);
    
    AccountLinkingUser user2 = new AccountLinkingUser(); //Second user with no mappings set, for testing the mapping flow
    user2.setUserId("47e38fdf-d5ba-4491-9c3c-1f028d02483b");
    users.put("47e38fdf-d5ba-4491-9c3c-1f028d02483b", user2);*/
    
    AccountLinkingUser user = new AccountLinkingUser();
    user.setUserId("def456");
    users.put("def456", user);
    mappingTokens.put("abc123", "def456");
  }
  
  @Override
  public AccountLinkingUser getUserByUserId(String userId) {
    LOG.info("Retrieving data for user '" + userId + "'.");
    return users.get(userId);
  }

  @Override
  public AccountLinkingUser updateUser(AccountLinkingUser user) {
    LOG.info("Updating data for user '" + user.getUserId() + "'.");
    return users.put(user.getUserId(), user);
  }

  @Override
  public String generateMappingTokenForUserId(String userId) {
    String mappingToken = UUID.randomUUID().toString();
    mappingTokens.put(mappingToken, userId);
    return mappingToken;
  }

  @Override
  public String getUserIdByMappingToken(String token) {
    return mappingTokens.get(token);
  }

  @Override
  public void expireMappingToken(String token) {
    mappingTokens.remove(token);
  }

  @Override
  public String generateAuthToken(String userId) {
    String grantedToken = UUID.randomUUID().toString();
    grantedAuthTokens.put(grantedToken, userId);
    return grantedToken;
  }

  @Override
  public String getUserIdByAuthToken(String token) {
    return grantedAuthTokens.get(token);
  }

  @Override
  public void expireGrantedToken(String token) {
    grantedAuthTokens.remove(token);
  }
}
