package com.derpgroup.livefinder.dao;

import com.derpgroup.livefinder.model.accountlinking.UserAccount;

public interface AccountLinkingDAO {

  public UserAccount getUserByUserId(String alexaUserId);
  
  public UserAccount updateUser(UserAccount user);
  
  public String generateMappingTokenForUserId(String userId);
  
  public String getUserIdByMappingToken(String token);
  
  public void expireMappingToken(String token);
  
  public String generateAuthToken(String userId);
  
  public String getUserIdByAuthToken(String token);
  
  public void expireGrantedToken(String token);
}
