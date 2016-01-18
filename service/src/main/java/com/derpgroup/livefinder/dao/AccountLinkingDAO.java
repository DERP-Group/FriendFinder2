package com.derpgroup.livefinder.dao;

import com.derpgroup.livefinder.model.accountlinking.AccountLinkingUser;
import com.derpgroup.livefinder.model.accountlinking.InterfaceMapping;
import com.derpgroup.livefinder.model.accountlinking.InterfaceName;

public interface AccountLinkingDAO {

  public AccountLinkingUser getUserByUserId(String alexaUserId);
  
  public AccountLinkingUser updateUser(AccountLinkingUser user);
  
  public String getUserIdByInterfaceUserIdAndInterface(String interfaceUserId, InterfaceName interfaceName);
  
  public void addInterfaceMapping(InterfaceMapping interfaceMapping);
  
  public String generateMappingTokenForUserId(String userId);
  
  public String getUserIdByMappingToken(String token);
  
  public void expireMappingToken(String token);
}
