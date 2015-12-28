package com.derpgroup.livefinder.dao;

import com.derpgroup.livefinder.model.accountlinking.AccountLinkingUser;
import com.derpgroup.livefinder.model.accountlinking.InterfaceMapping;
import com.derpgroup.livefinder.model.accountlinking.InterfaceName;

public interface AccountLinkingDAO {

  public AccountLinkingUser getUser(String alexaUserId);
  
  public String getUserIdByInterfaceUserIdAndInterface(String interfaceUserId, InterfaceName interfaceName);
  
  public void addInterfaceMapping(InterfaceMapping interfaceMapping);
}
