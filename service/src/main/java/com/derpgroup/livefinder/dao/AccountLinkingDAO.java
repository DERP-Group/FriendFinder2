package com.derpgroup.livefinder.dao;

import com.derpgroup.livefinder.model.accountlinking.AccountLinkingUser;

public interface AccountLinkingDAO {

  public AccountLinkingUser getUserByUserId(String alexaUserId);
  
  public AccountLinkingUser updateUser(AccountLinkingUser user);
  
  public String generateMappingTokenForUserId(String userId);
  
  public String getUserIdByMappingToken(String token);
  
  public void expireMappingToken(String token);
  
  public String generateAuthToken(String userId);
  
  public String getUserIdByAuthToken(String token);
  
  public void expireGrantedToken(String token);
}
