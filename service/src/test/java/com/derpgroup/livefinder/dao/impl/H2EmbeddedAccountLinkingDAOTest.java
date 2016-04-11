package com.derpgroup.livefinder.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.After;

import com.derpgroup.livefinder.configuration.AccountLinkingDAOConfig;
import com.derpgroup.livefinder.model.accountlinking.ExternalAccountLink;
import com.derpgroup.livefinder.model.accountlinking.UserAccount;

@Ignore
public class H2EmbeddedAccountLinkingDAOTest {

  private H2EmbeddedAccountLinkingDAO dao;
  private AccountLinkingDAOConfig accountLinkingDAOConfig;
  
  @Before
  public void setup() throws SQLException{
    HashMap<String,Object> properties = new HashMap<String,Object>();
    properties.put("url", "jdbc:h2:mem:");
    properties.put("user", "sa");
    properties.put("password", "sa");
    accountLinkingDAOConfig = new AccountLinkingDAOConfig();
    accountLinkingDAOConfig.setProperties(properties);
    dao = new H2EmbeddedAccountLinkingDAO(accountLinkingDAOConfig);
    dao.init();
  }
  
  @Test
  public void testInit() throws SQLException{
    dao.shutdown();
    dao = new H2EmbeddedAccountLinkingDAO(accountLinkingDAOConfig);
    dao.init();
  }
  
  @Test
  public void testFixtureDataSetup() throws SQLException{
    dao.executeStatement("SELECT * FROM AccountLink"); //Confirms that table exists
  }
  
  @Test
  public void testFixtureDataSetup_badTableName() throws SQLException{
    ResultSet rs = dao.executeStatement("SELECT * FROM badTable");
    assertNull(rs);
  }
  
  @Test
  public void testShutdown() throws SQLException{
    dao.shutdown();
  }
  
  @Test
  public void testCreateUser(){
    UserAccount user = new UserAccount();
    user.setUserId("asdf");
    UserAccount createdUser =  dao.updateUser(user);
    assertNotNull(createdUser);
    assertNotNull(createdUser.getUserId());
    assertNotNull(createdUser.getDateCreated());
    assertEquals(createdUser.getUserId(),user.getUserId());
  }
  
  @Test
  public void testUpdateUser(){
    testCreateUser();
    
    UserAccount user = new UserAccount();
    user.setUserId("asdf");
    user.setFirstName("1234");
    
    UserAccount updatedUser = dao.updateUser(user);
    assertNotNull(updatedUser);
    assertNotNull(updatedUser.getUserId());
    assertNotNull(updatedUser.getDateCreated());
    assertNotNull(updatedUser.getFirstName());
    assertEquals(updatedUser.getFirstName(),user.getFirstName());
    assertEquals(updatedUser.getUserId(),user.getUserId());
    
    UserAccount retrievedUser = dao.getUserByUserId(user.getUserId());
    assertNotNull(retrievedUser);
    assertNotNull(retrievedUser.getUserId());
    assertNotNull(retrievedUser.getDateCreated());
    assertNotNull(retrievedUser.getFirstName());
    assertEquals(retrievedUser.getFirstName(),user.getFirstName());
    assertEquals(retrievedUser.getUserId(),user.getUserId());
  }
  
  @Test
  public void testRetrieveUserById(){
    UserAccount user = new UserAccount();
    user.setUserId("asdf");
    dao.updateUser(user);

    UserAccount retrievedUser =  dao.getUserByUserId(user.getUserId());
    assertNotNull(retrievedUser);
    assertNotNull(retrievedUser.getUserId());
    assertNotNull(retrievedUser.getDateCreated());
    assertEquals(retrievedUser.getUserId(),user.getUserId());
  }
  
  @Test
  public void testCreateLinkingToken(){
    String userId = "asdf";
    String responseToken = dao.generateMappingTokenForUserId(userId);
    assertNotNull(responseToken);
    assertEquals(36,responseToken.length());
  }
  
  @Test
  public void testRetrieveLinkingToken(){
    String userId = "asdf";
    String responseToken = dao.generateMappingTokenForUserId(userId);
    String userIdRetrieved = dao.getUserIdByMappingToken(responseToken);
    assertNotNull(userIdRetrieved);
    assertEquals(userIdRetrieved, userId);
  }
  
  @Test
  public void testDeleteLinkingToken(){
    String userId = "asdf";
    String responseToken = dao.generateMappingTokenForUserId(userId);
    String userIdRetrieved = dao.getUserIdByMappingToken(responseToken);
    assertNotNull(userIdRetrieved);
    assertEquals(userIdRetrieved, userId);
    
    dao.expireMappingToken(responseToken);
    userIdRetrieved = dao.getUserIdByMappingToken(responseToken);
    assertNull(userIdRetrieved);
  }
  
  @Test
  public void testCreateAccessToken(){
    String userId = "asdf";
    String responseToken = dao.generateAuthToken(userId);
    assertNotNull(responseToken);
    assertEquals(36,responseToken.length());
  }
  
  @Test
  public void testRetrieveAccessToken(){
    String userId = "asdf";
    String responseToken = dao.generateAuthToken(userId);
    String userIdRetrieved = dao.getUserIdByAuthToken(responseToken);
    assertNotNull(userIdRetrieved);
    assertEquals(userIdRetrieved, userId);
  }
  
  @Test
  public void testDeleteAccessToken(){
    String userId = "asdf";
    String responseToken = dao.generateAuthToken(userId);
    String userIdRetrieved = dao.getUserIdByAuthToken(responseToken);
    assertNotNull(userIdRetrieved);
    assertEquals(userIdRetrieved, userId);
    
    dao.expireGrantedToken(responseToken);
    userIdRetrieved = dao.getUserIdByAuthToken(responseToken);
    assertNull(userIdRetrieved);
  }
  
  @Test
  public void testCreateAccountLink(){
    ExternalAccountLink accountLink = new ExternalAccountLink();
    accountLink.setUserId("asdf");
    accountLink.setExternalUserId("1234");
    accountLink.setExternalSystemName("ALEXA");
    accountLink.setAuthToken("qwerty");
    accountLink.setRefreshToken("asdf");
    
    ExternalAccountLink accountLinkRetrieved = dao.createAccountLink(accountLink);
    
    validateAccountLinkNotNull(accountLinkRetrieved);
    validateAccountLinksEqual(accountLink, accountLinkRetrieved);
  }
  
  @Test
  public void testRetrieveAccountLink(){
    ExternalAccountLink accountLinkToCreate = new ExternalAccountLink();
    accountLinkToCreate.setUserId("asdf");
    accountLinkToCreate.setExternalUserId("1234");
    accountLinkToCreate.setExternalSystemName("ALEXA");
    accountLinkToCreate.setAuthToken("qwerty");
    accountLinkToCreate.setRefreshToken("asdf");
    
    dao.createAccountLink(accountLinkToCreate);
    
    ExternalAccountLink accountLinkRetrieved = dao.getAccountLinkByUserIdAndExternalSystemName(
        accountLinkToCreate.getUserId(), accountLinkToCreate.getExternalSystemName());
    ExternalAccountLink accountLinkRetrieved2 = dao.getAccountLinkByExternalUserIdAndExternalSystemName(
        accountLinkToCreate.getExternalUserId(), accountLinkToCreate.getExternalSystemName());
    
    validateAccountLinkNotNull(accountLinkRetrieved);
    validateAccountLinksEqual(accountLinkRetrieved, accountLinkRetrieved2);
  }
  
  @Test
  public void testRetrieveAccountLink_noExternalUserId(){
    ExternalAccountLink accountLinkToCreate = new ExternalAccountLink();
    accountLinkToCreate.setUserId("asdf");
    accountLinkToCreate.setExternalSystemName("ALEXA");
    accountLinkToCreate.setAuthToken("qwerty");
    
    dao.createAccountLink(accountLinkToCreate);
    
    ExternalAccountLink accountLinkRetrieved = dao.getAccountLinkByUserIdAndExternalSystemName(
        accountLinkToCreate.getUserId(), accountLinkToCreate.getExternalSystemName());
    
    validateAccountLinksEqual(accountLinkRetrieved, accountLinkToCreate);
  }
  
  @Test
  public void testRetrieveAllLinks(){
    ExternalAccountLink accountLink1 = new ExternalAccountLink();
    accountLink1.setUserId("userId1");
    accountLink1.setExternalUserId("externalUserId1");
    accountLink1.setExternalSystemName("ALEXA");
    accountLink1.setAuthToken("qwerty");
    accountLink1.setRefreshToken("asdf");
    
    dao.createAccountLink(accountLink1);
    
    ExternalAccountLink accountLink2 = new ExternalAccountLink();
    accountLink2.setUserId("userId1");
    accountLink2.setExternalUserId("externalUserId2");
    accountLink2.setExternalSystemName("STEAM");
    accountLink2.setAuthToken("qwerty");
    accountLink2.setRefreshToken("asdf");
    
    dao.createAccountLink(accountLink2);
    
    List<ExternalAccountLink> links = dao.getAccountLinksByUserId(accountLink1.getUserId());
    assertNotNull(links);
    assertEquals(2,links.size());
    for(ExternalAccountLink link : links){
      validateAccountLinkNotNull(link);
      if(link.getExternalUserId().equals(accountLink1.getExternalUserId())){
        validateAccountLinksEqual(link, accountLink1);
      }else{
        validateAccountLinksEqual(link, accountLink2);
      }
    }
  }
  
  @Test
  public void testCreateAccountLink_noAuthToken(){
    ExternalAccountLink accountLink = new ExternalAccountLink();
    accountLink.setUserId("asdf");
    accountLink.setExternalUserId("1234");
    accountLink.setExternalSystemName("ALEXA");
    
    ExternalAccountLink accountLinkRetrieved = dao.createAccountLink(accountLink);
    assertNotNull(accountLinkRetrieved);
    assertNotNull(accountLinkRetrieved.getUserId());
    assertNotNull(accountLinkRetrieved.getExternalUserId());
    assertNotNull(accountLinkRetrieved.getExternalSystemName());
    assertNull(accountLinkRetrieved.getAuthToken());
    
    validateAccountLinksEqual(accountLink, accountLinkRetrieved);
  }
  
  public void validateAccountLinkNotNull(ExternalAccountLink accountLink){
    assertNotNull(accountLink);
    assertNotNull(accountLink.getUserId());
    assertNotNull(accountLink.getExternalUserId());
    assertNotNull(accountLink.getExternalSystemName());
    assertNotNull(accountLink.getAuthToken());
    assertNotNull(accountLink.getRefreshToken());
  }
  
  public void validateAccountLinksEqual(ExternalAccountLink accountLink1, ExternalAccountLink accountLink2){
    assertEquals(accountLink1.getUserId(), accountLink2.getUserId());
    assertEquals(accountLink1.getExternalUserId(), accountLink2.getExternalUserId());
    assertEquals(accountLink1.getExternalSystemName(), accountLink2.getExternalSystemName());
    assertEquals(accountLink1.getAuthToken(), accountLink2.getAuthToken());
    assertEquals(accountLink1.getRefreshToken(), accountLink2.getRefreshToken());
  }
  
  @After
  public void shutdown() throws SQLException{
    Connection conn = dao.getConn();
    if(!conn.isClosed()){
      conn.close();
    }
  }
}
