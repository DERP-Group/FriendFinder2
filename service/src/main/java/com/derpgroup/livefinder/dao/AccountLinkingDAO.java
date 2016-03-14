package com.derpgroup.livefinder.dao;

import java.util.List;

import com.derpgroup.livefinder.model.accountlinking.ExternalAccountLink;
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

  public ExternalAccountLink createAccountLink(ExternalAccountLink link);

  public ExternalAccountLink getAccountLinkByUserIdAndExternalSystemName(String userId, String externalSystemName);

  public ExternalAccountLink getAccountLinkByExternalUserIdAndExternalSystemName(String externalUserId, String externalSystemName);

  public List<ExternalAccountLink> getAccountLinksByUserId(String userId);
}
