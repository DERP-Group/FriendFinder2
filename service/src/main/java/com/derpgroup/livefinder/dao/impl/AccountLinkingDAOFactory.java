package com.derpgroup.livefinder.dao.impl;

import com.derpgroup.livefinder.configuration.AccountLinkingDAOConfig;
import com.derpgroup.livefinder.dao.AccountLinkingDAO;

public class AccountLinkingDAOFactory {

  public static AccountLinkingDAO getDAO(AccountLinkingDAOConfig config){
    AccountLinkingDAO dao = null;
    switch(config.getType().toUpperCase()){
    case "H2": 
      dao = new H2EmbeddedAccountLinkingDAO(config);
      break;
    case "INMEMORY":
      dao = new InMemoryAccountLinkingDAO(config);
      break;
      default:
        throw new RuntimeException("Unsupported AccounTLinkingDAO type.");
    }
    return dao;
  }
}
