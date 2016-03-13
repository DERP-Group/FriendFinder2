package com.derpgroup.livefinder.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.h2.jdbcx.JdbcDataSource;

import com.derpgroup.livefinder.dao.AccountLinkingDAO;
import com.derpgroup.livefinder.model.accountlinking.ExternalAccountLink;
import com.derpgroup.livefinder.model.accountlinking.InterfaceName;
import com.derpgroup.livefinder.model.accountlinking.UserAccount;

public class H2EmbeddedAccountLinkingDAO implements AccountLinkingDAO {
  
  private Connection conn;
  private JdbcDataSource ds;
  
  public H2EmbeddedAccountLinkingDAO(){
    ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:mem:");
    ds.setUser("sa");
    ds.setPassword("sa");
    try {
      init();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  protected void init() throws SQLException {
    try {
      conn = ds.getConnection();
      setupFixtureData();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  public void shutdown() throws SQLException{
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
  }
  protected ResultSet executeStatement(String sql){
    return executeStatement(sql, null);
  }
  
  protected ResultSet executeStatement(String sql, ArrayList<? extends Object> parameters){

    CachedRowSet crs = null;
    
    try(
        PreparedStatement unpreparedStatement = conn.prepareStatement(sql);
        PreparedStatement statement = prepStatement(unpreparedStatement, parameters);
        ResultSet rs = statement.getResultSet();
        ){
      boolean resultSetRows = statement.execute();
      if(resultSetRows){
        crs = RowSetProvider.newFactory().createCachedRowSet();
        crs.populate(rs);
        return crs;
      }
    }catch(SQLException e){
      e.printStackTrace();
    }
    return null;
  }
  
  protected ResultSet executeQuery(String sql){
    return executeQuery(sql, null);
  }
  
  protected ResultSet executeQuery(String sql, ArrayList<? extends Object> parameters){

    CachedRowSet crs = null;
    
    try(
        PreparedStatement unpreparedStatement = conn.prepareStatement(sql);
        PreparedStatement statement = prepStatement(unpreparedStatement, parameters);
        ResultSet rs = statement.executeQuery();
        ){
      crs = RowSetProvider.newFactory().createCachedRowSet();
      crs.populate(rs);
      return crs;
    }catch(SQLException e){
      e.printStackTrace();
    }
    return null;
  }
  
  protected void setupFixtureData() throws SQLException{
    conn.setAutoCommit(false);
    String userTableCreation = "CREATE TABLE User(id varchar(255) PRIMARY KEY NOT NULL,"
        + "firstName varchar(255) NULL,"
        + "dateCreated TIMESTAMP NOT NULL DEFAULT(NOW()));";
    executeStatement(userTableCreation);
    
    String accountLinkTableCreation = "CREATE TABLE AccountLink("
        + "userId varchar(255) NOT NULL,"
        + "externalUserId varchar(255) NOT NULL,"
        + "externalSystemName varchar(64) NOT NULL,"
        + "externalSystemToken varchar(255) NULL"
        + ");";
    executeStatement(accountLinkTableCreation);
    
    String linkingTokenTableCreation = "CREATE TABLE LinkingToken("
        + "token UUID NOT NULL DEFAULT(RANDOM_UUID()),"
        + "userId varchar(255) NOT NULL,"
        + "dateCreated TIMESTAMP NOT NULL DEFAULT(NOW())"
        + ");";
    executeStatement(linkingTokenTableCreation);
    
    String authorizationTableCreation = "CREATE TABLE Authorization("
        + "token UUID NOT NULL DEFAULT(RANDOM_UUID()),"
        + "userId varchar(255) NOT NULL,"
        + "dateCreated TIMESTAMP NOT NULL DEFAULT(NOW())"
        + ");";
    executeStatement(authorizationTableCreation);
    conn.commit();
    conn.setAutoCommit(true);
  }
  
  protected Connection getConn(){
    return conn;
  }

  @Override
  public UserAccount getUserByUserId(String alexaUserId) {
    String userSelect = "SELECT id, firstName FROM User WHERE id = ?;";
    ArrayList<String> parameters = new ArrayList<String>();
    parameters.add(alexaUserId);
    ResultSet response = executeQuery(userSelect, parameters);
    
    UserAccount user = new UserAccount();
    try {
      if(!response.first()){
        return null;
      }
      user.setUserId(response.getString("id"));
      user.setFirstName(response.getString("firstName"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return user;
  }

  @Override
  public UserAccount updateUser(UserAccount user) {

    String userCreate = "MERGE INTO User(id, firstName) KEY(id) VALUES(?,?);";
    
    ArrayList<String> parameters = new ArrayList<String>();
    parameters.add(user.getUserId());
    parameters.add(user.getFirstName());

    executeStatement(userCreate, parameters);
    
    return getUserByUserId(user.getUserId());
  }

  @Override
  public String generateMappingTokenForUserId(String userId) {

    String linkingTokenCreate = "INSERT INTO LinkingToken(userId) VALUES(?);";

    ArrayList<String> parameters = new ArrayList<String>();
    parameters.add(userId);
    executeStatement(linkingTokenCreate, parameters);

    ResultSet response;
    String linkingTokenRetrieve = "SELECT TOP 1 token FROM LinkingToken WHERE userId = ? ORDER BY dateCreated DESC";
    try {

      ArrayList<String> retrievalParameters = new ArrayList<String>();
      retrievalParameters.add(userId);
      response = executeQuery(linkingTokenRetrieve, retrievalParameters);
      if(!response.next()){
        return null;
      }
      return response.getString("token");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String getUserIdByMappingToken(String token) {

    ResultSet response;
    String linkingTokenRetrieve = "SELECT TOP 1 userId FROM LinkingToken WHERE token = ? ORDER BY dateCreated DESC";
    try {
      ArrayList<String> parameters = new ArrayList<String>();
      parameters.add(token);
      response = executeQuery(linkingTokenRetrieve, parameters);
      if(response == null || !response.next()){
        return null;
      }
      return response.getString("userId");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void expireMappingToken(String token) {
    String linkingTokenDelete = "DELETE FROM LinkingToken WHERE token = ?;";

//    try {
      ArrayList<String> parameters = new ArrayList<String>();
      parameters.add(token);
      executeStatement(linkingTokenDelete, parameters);
    /*} catch (SQLException e) {
      e.printStackTrace();
    }*/
  }

  @Override
  public String generateAuthToken(String userId) {

    String accessTokenCreate = "INSERT INTO Authorization(userId) VALUES(?);";

//    try {
      ArrayList<String> parameters = new ArrayList<String>();
      parameters.add(userId);
      executeStatement(accessTokenCreate, parameters);
    /*} catch (SQLException e) {
      e.printStackTrace();
      return null;
    }*/

    ResultSet response;
    String accessTokenRetrieve = "SELECT TOP 1 token FROM Authorization WHERE userId = ? ORDER BY dateCreated DESC";
    try {
      ArrayList<String> retrievalParameters = new ArrayList<String>();
      retrievalParameters.add(userId);
      response = executeQuery(accessTokenRetrieve, retrievalParameters);
      response.first();
      return response.getString("token");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String getUserIdByAuthToken(String token) {

    ResultSet response;
    String accessTokenRetrieve = "SELECT TOP 1 userId FROM Authorization WHERE token = ? ORDER BY dateCreated DESC";
    try {
      ArrayList<String> parameters = new ArrayList<String>();
      parameters.add(token);
      response = executeQuery(accessTokenRetrieve, parameters);
      if(!response.next()){
        return null;
      }
      return response.getString("userId");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void expireGrantedToken(String token) {
    String accessTokenDelete = "DELETE FROM Authorization WHERE token = ?;";

    ArrayList<String> parameters = new ArrayList<String>();
    parameters.add(token);
    executeStatement(accessTokenDelete, parameters);
  }
  
  protected PreparedStatement prepStatement(PreparedStatement statement, ArrayList<? extends Object> parameters) throws SQLException{
    if(parameters != null){
      for(int i = 0; i < parameters.size(); i++){
        statement.setObject(i + 1, parameters.get(i));
      }
    }
    return statement;
  }
  
  @Override
  public ExternalAccountLink createAccountLink(ExternalAccountLink link){
    ArrayList<Object> parameters = new ArrayList<Object>();
    parameters.add(link.getUserId());
    parameters.add(link.getExternalUserId());
    parameters.add(link.getInterfaceName().name());
    parameters.add(link.getAuthToken());
    String createAccountLink = "MERGE INTO AccountLink(userId, externalUserId, externalSystemName, externalSystemToken)"
        + " KEY(userId, externalUserId, externalSystemName) VALUES(?,?,?,?)";
    //Should externalSystemToken be a different table with a FK relationship to this one?

    executeStatement(createAccountLink, parameters);
    
    return getAccountLinkByUserIdAndExternalSystemName(link.getUserId(), link.getInterfaceName().name());
  }
  
  @Override
  public ExternalAccountLink getAccountLinkByUserIdAndExternalSystemName(String userId, String externalSystemName){
    ArrayList<Object> parameters = new ArrayList<Object>();
    parameters.add(userId);
    parameters.add(externalSystemName);
    String createAccountLink = "SELECT userId, externalUserId, externalSystemName, externalSystemToken"
        + " FROM AccountLink"
        + " WHERE userId = ?"
        + " AND externalSystemName = ?";

    ResultSet response;
    response = executeQuery(createAccountLink, parameters);
    try{
      if(!response.next()){
        return null;
      }
    
      ExternalAccountLink link = new ExternalAccountLink();
      link.setUserId(response.getString("userId"));
      link.setExternalUserId(response.getString("externalUserId"));
      link.setInterfaceName(InterfaceName.valueOf(response.getString("externalSystemName")));
      link.setAuthToken(response.getString("externalSystemToken"));
      return link;
    }catch(SQLException e){
      e.printStackTrace();
      return null;
    }
  }
  
  @Override
  public ExternalAccountLink getAccountLinkByExternalUserIdAndExternalSystemName(String externalUserId, String externalSystemName){
    ArrayList<Object> parameters = new ArrayList<Object>();
    parameters.add(externalUserId);
    parameters.add(externalSystemName);
    String createAccountLink = "SELECT userId, externalUserId, externalSystemName, externalSystemToken"
        + " FROM AccountLink"
        + " WHERE externalUserId = ?"
        + " AND externalSystemName = ?";

    ResultSet response;
    response = executeQuery(createAccountLink, parameters);
    try{
      if(!response.next()){
        return null;
      }
    
      ExternalAccountLink link = new ExternalAccountLink();
      link.setUserId(response.getString("userId"));
      link.setExternalUserId(response.getString("externalUserId"));
      link.setInterfaceName(InterfaceName.valueOf(response.getString("externalSystemName")));
      link.setAuthToken(response.getString("externalSystemToken"));
      return link;
    }catch(SQLException e){
      e.printStackTrace();
      return null;
    }
  }
}
