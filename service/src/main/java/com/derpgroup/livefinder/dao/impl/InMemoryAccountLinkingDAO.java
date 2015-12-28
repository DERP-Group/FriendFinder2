package com.derpgroup.livefinder.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.livefinder.dao.AccountLinkingDAO;
import com.derpgroup.livefinder.model.accountlinking.AccountLinkingUser;
import com.derpgroup.livefinder.model.accountlinking.InterfaceMapping;
import com.derpgroup.livefinder.model.accountlinking.InterfaceName;
import com.derpgroup.livefinder.resource.AuthResource;

public class InMemoryAccountLinkingDAO implements AccountLinkingDAO {
  
  private static final Logger LOG = LoggerFactory.getLogger(InMemoryAccountLinkingDAO.class);

  Map<String,AccountLinkingUser> users;
  Map<String,InterfaceMapping> interfaceMappings;
  
  public InMemoryAccountLinkingDAO(){
    users = new HashMap<String,AccountLinkingUser>();
    interfaceMappings = new HashMap<String,InterfaceMapping>();
    
    LOG.info("Initializing pre-seeded mapping data.");
    InterfaceMapping mapping = new InterfaceMapping();
    mapping.setInterfaceName(InterfaceName.ALEXA);
    mapping.setInterfaceUserId("amzn1.echo-sdk-account.AH7YZRPVGIG7FRIG77BGROA3D66SSNAGCGKJSDBP6MQ2P3H556X5A");
    mapping.setUserId("96876148-bbcd-40f2-993d-dd3ed635367b");
    String compositeKey = mapping.getInterfaceName().name() + "." + mapping.getInterfaceUserId();
    interfaceMappings.put(compositeKey, mapping);
    interfaceMappings.put(mapping.getInterfaceName().name() + "." + "fake", mapping);
    
    LOG.info("Initializing pre-seeded user data.");
    AccountLinkingUser user = new AccountLinkingUser();
    user.userId("96876148-bbcd-40f2-993d-dd3ed635367b");
    user.setSteamId("76561198019030536");
    users.put("96876148-bbcd-40f2-993d-dd3ed635367b", user);
  }
  
  @Override
  public AccountLinkingUser getUser(String alexaUserId) {
    return users.get(alexaUserId);
  }

  @Override
  public String getUserIdByInterfaceUserIdAndInterface(String interfaceUserId, InterfaceName interfaceName) {
    String compositeKey = interfaceName.name() + "." + interfaceUserId;
    InterfaceMapping mapping = interfaceMappings.get(compositeKey);
    return mapping.getUserId();
  }

  @Override
  public void addInterfaceMapping(InterfaceMapping interfaceMapping) {
    interfaceMappings.put(interfaceMapping.getInterfaceName().name() + "." + interfaceMapping.getInterfaceUserId(), interfaceMapping);
  }

}
