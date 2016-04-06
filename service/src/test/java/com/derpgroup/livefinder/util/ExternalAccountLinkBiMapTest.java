package com.derpgroup.livefinder.util;

import java.util.Map;

import org.junit.Before;

import com.derpgroup.livefinder.model.accountlinking.ExternalAccountLink;

public class ExternalAccountLinkBiMapTest {

  public Map<String,ExternalAccountLink> accountLinks;
  
  @Before
  public void setup(){
    accountLinks = new ExternalAccountLinkBiMap<String,ExternalAccountLink>();
  }
  
  public void testPut(){
    ExternalAccountLink link = new ExternalAccountLink();
    link.setUserId("asdf");
  }
}
