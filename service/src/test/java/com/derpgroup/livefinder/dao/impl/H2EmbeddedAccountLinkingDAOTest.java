package com.derpgroup.livefinder.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import com.derpgroup.livefinder.model.accountlinking.UserAccount;

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
  
  @Test(expected = SQLException.class)
  public void testFixtureDataSetup_badTableName() throws SQLException{
    dao.executeStatement("SELECT * FROM badTable");
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
  
  @After
  public void shutdown() throws SQLException{
    Connection conn = dao.getConn();
    if(!conn.isClosed()){
      conn.close();
    }
  }
}
