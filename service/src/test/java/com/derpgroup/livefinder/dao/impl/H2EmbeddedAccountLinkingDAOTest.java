package com.derpgroup.livefinder.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.After;

import com.derpgroup.livefinder.model.accountlinking.UserAccount;

//@Ignore
public class H2EmbeddedAccountLinkingDAOTest {

  private H2EmbeddedAccountLinkingDAO dao;
  
  @Before
  public void setup() throws SQLException{
    dao = new H2EmbeddedAccountLinkingDAO();
    dao.init();
  }
  
  @Test
  public void testInit() throws SQLException{
    dao.shutdown();
    dao = new H2EmbeddedAccountLinkingDAO();
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
  
  @After
  public void shutdown() throws SQLException{
    Connection conn = dao.getConn();
    if(!conn.isClosed()){
      conn.close();
    }
  }
}
