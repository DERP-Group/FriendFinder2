/**
 * Copyright (C) 2015 David Phillips
 * Copyright (C) 2015 Eric Olson
 * Copyright (C) 2015 Rusty Gerard
 * Copyright (C) 2015 Paul Winters
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.derpgroup.livefinder.resource;

import java.net.URISyntaxException;
import java.util.UUID;

import io.dropwizard.setup.Environment;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.livefinder.configuration.MainConfig;
import com.derpgroup.livefinder.dao.AccountLinkingDAO;
import com.derpgroup.livefinder.manager.TwitchClient;
import com.derpgroup.livefinder.manager.TwitchTokenResponse;
import com.derpgroup.livefinder.manager.TwitchUserResponse;
import com.derpgroup.livefinder.model.TwitchClientWrapper;
import com.derpgroup.livefinder.model.accountlinking.AccountLinkingUser;
import com.derpgroup.livefinder.model.accountlinking.AuthenticationException;
import com.derpgroup.livefinder.model.accountlinking.TwitchUser;

/**
 * REST APIs for requests generating from authentication flows
 *
 * @author Eric
 * @since 0.0.1
 */
@Path("/livefinder/auth")
@Produces({MediaType.TEXT_PLAIN,MediaType.APPLICATION_JSON})
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

  private static final Logger LOG = LoggerFactory.getLogger(AuthResource.class);
  
  private AccountLinkingDAO accountLinkingDAO;
  private String steamLinkingFlowHostname;
  private String steamSuccessPagePath;
  private String steamErrorPagePath;
  private String alexaRedirectPath;
  private TwitchClient twitchClient;
  
  
  public AuthResource(MainConfig config, Environment env, AccountLinkingDAO accountLinkingDAO) {
    this.accountLinkingDAO = accountLinkingDAO;
    
    steamLinkingFlowHostname = config.getLiveFinderConfig().getSteamAccountLinkingConfig().getLinkingFlowHostname();
    steamSuccessPagePath = config.getLiveFinderConfig().getSteamAccountLinkingConfig().getSuccessPagePath();
    steamErrorPagePath = config.getLiveFinderConfig().getSteamAccountLinkingConfig().getErrorPagePath();
    
    alexaRedirectPath = config.getLiveFinderConfig().getAlexaAccountLinkingConfig().getAlexaRedirectPath();
    
    twitchClient = TwitchClientWrapper.getInstance().getClient();
  }
  
  @GET
  @Path("/mappingToken")
  @Produces(MediaType.APPLICATION_JSON)
  public Response authenticateUserBySessionToken(@QueryParam("token") String sessionToken){
    if(StringUtils.isEmpty(sessionToken)){
      LOG.error("No valid session token was provided.");
      throw new WebApplicationException("No valid session token was provided.",Response.Status.UNAUTHORIZED);
      //TODO: Make this not throw html errors
    }
    
    LOG.debug("Looking up userId for token '" + sessionToken + "'.");
    String userId = accountLinkingDAO.getUserIdByMappingToken(sessionToken);
    
    if(StringUtils.isEmpty(userId)){
      LOG.error("No valid user details were associated with the token provided.");
      throw new WebApplicationException("No valid user details were associated with the token provided.",Response.Status.FORBIDDEN);
    }
    
    LOG.debug("Retrieving user details for derpId '" + userId + "'.");
    AccountLinkingUser user = accountLinkingDAO.getUserByUserId(userId);
    
    if(user == null){
      LOG.error("No valid user details were associated with the token provided.");
      throw new WebApplicationException("No valid user details were associated with the token provided.",Response.Status.FORBIDDEN);
    }
    
    String accessToken = accountLinkingDAO.generateAuthToken(user.getUserId());
    
    return Response.ok(user).header("Access-Token", accessToken).build();
  }
  
  @GET
  @Path("/steam/linkIds")
  @Produces(MediaType.APPLICATION_JSON)
  public Object doSteamLinking(@QueryParam("accessToken") String accessToken, @QueryParam("externalId") String externalId){
    //TODO: Refactor to be generic?
    //TODO: Move accessToken logic into a filter
    
    AccountLinkingUser user;
    try {
      user = validateAccessToken(accessToken);
    } catch (AuthenticationException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
    }

    if(externalId == null){
      String error = "Missing required parameter 'externalId'";
      return Response.status(Response.Status.BAD_REQUEST).entity(new AuthenticationException(error)).build();
    }
    
    if(user.getSteamId() == null){
      LOG.info("User '" + user.getUserId() + "' has no steamId; setting for the first time to '" + externalId + "'.");
    }else{
      LOG.info("User '" + user.getUserId() + "' had steamId '" + user.getSteamId() + "'; updating to '" + externalId + "'.");
    }
    user.setSteamId(externalId);
    
    accountLinkingDAO.updateUser(user);

    return user;
  }
  
  @GET
  @Path("/twitch")
  @Produces(MediaType.TEXT_PLAIN)
  public Response doTwitchAuth(@QueryParam("code") String code,@QueryParam("state") String state){
    
    AccountLinkingUser user;
    try {
      user = validateAccessToken(state);
    } catch (AuthenticationException e) {
      LOG.error("Could not validate access token.");
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }

    if(StringUtils.isEmpty(code)){
      String error = "Missing required parameter 'code'";
      LOG.error(error);
      return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }
    
    LOG.info("Requesting access token for user '" + user.getUserId() + "' with code '" + code + "'.");
    TwitchTokenResponse tokenResponse;
    try {
      tokenResponse = twitchClient.redeemCode(code);
    } catch (AuthenticationException e) {
      LOG.error("Could not redeem code for access token.");
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
    
    LOG.info("Requesting twitch user info for user '" + user.getUserId() + "'.");
    TwitchUserResponse userResponse;
    try {
      userResponse = twitchClient.getUser(tokenResponse.getAccessToken());
    } catch (AuthenticationException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
    
    TwitchUser twitchUser = new TwitchUser();
    twitchUser.setAuthToken(tokenResponse.getAccessToken());
    twitchUser.setRefreshToken(tokenResponse.getRefreshToken());
    twitchUser.setName(userResponse.getDisplayName());
    user.setTwitchUser(twitchUser);
    
    accountLinkingDAO.updateUser(user);
    
    return Response.ok("Hello! \n Code: " + code + "\n UserId: " + user.toString()).build();
  }

  @GET
  @Path("/alexa")
  @Produces(MediaType.APPLICATION_JSON) 
  public Response doAlexaLinking(){
    AccountLinkingUser user = new AccountLinkingUser();
    String userId = UUID.randomUUID().toString();
    user.setUserId(userId);
    accountLinkingDAO.updateUser(user);
    
    String accessToken = accountLinkingDAO.generateAuthToken(user.getUserId());
    
    return Response.ok(user).header("Access-Token", accessToken).build();
  }
  
  public AccountLinkingUser validateAccessToken(String accessToken) throws AuthenticationException{

    String userId = null;
    if(accessToken == null){
      String error = "Missing required parameter 'accessToken'";
      throw new AuthenticationException(error);
    }else{
      LOG.debug("Looking up userId for acessToken '" + accessToken + "'.");
      userId = accountLinkingDAO.getUserIdByAuthToken(accessToken);
    }
    if(userId == null){
      String error = "Token could not be resolved to a known user.";
      throw new AuthenticationException(error);
    }
    
    LOG.debug("Looking up user for userId '" + userId + "'.");
    AccountLinkingUser user = accountLinkingDAO.getUserByUserId(userId);
    if(user == null){
      String error = "Couldn't find user with userId '" + userId + "'.";
      throw new AuthenticationException(error);
    }
    
    return user;
  }
  
  public Response buildRedirect(String path, String reason){
    return buildRedirect(path, null, reason);
  }
  
  public Response buildRedirect(String path, String host, String reason){
    return buildRedirect(path, host, reason, null);
  }
  
  public Response buildRedirect(String path, String host, String reason, String fragment){
    URIBuilder uriBuilder = new URIBuilder();
    uriBuilder.setPath(path);
    if(!StringUtils.isEmpty(host)){
      uriBuilder.setPath(host);
    }
    if(!StringUtils.isEmpty(path)){
      uriBuilder.setPath(path);
    }
    if(!StringUtils.isEmpty(reason)){
      uriBuilder.addParameter("reason", reason);
    }
    if(!StringUtils.isEmpty(fragment)){
      uriBuilder.setFragment(fragment);
    }

    try {
      return Response.seeOther(uriBuilder.build()).build();
    } catch (URISyntaxException e) {
      return Response.serverError().entity("Unknown exception.").build();
    }
  }
}
