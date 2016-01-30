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
import com.derpgroup.livefinder.model.accountlinking.AccountLinkingUser;
import com.derpgroup.livefinder.model.accountlinking.LandingPageErrorResponse;

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
  
  
  public AuthResource(MainConfig config, Environment env, AccountLinkingDAO accountLinkingDAO) {
    this.accountLinkingDAO = accountLinkingDAO;
    
    steamLinkingFlowHostname = config.getLiveFinderConfig().getSteamAccountLinkingConfig().getLinkingFlowHostname();
    steamSuccessPagePath = config.getLiveFinderConfig().getSteamAccountLinkingConfig().getSuccessPagePath();
    steamErrorPagePath = config.getLiveFinderConfig().getSteamAccountLinkingConfig().getErrorPagePath();
    
    alexaRedirectPath = config.getLiveFinderConfig().getAlexaAccountLinkingConfig().getAlexaRedirectPath();
  }
  
  @GET
  @Path("/twitch")
  @Produces(MediaType.TEXT_PLAIN)
  public String doTwitchAuth(){
    return "Hello!";
  }
  
  @GET
  @Path("/mappingToken")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUserByMappingToken(@QueryParam("token") String mappingToken){
    if(StringUtils.isEmpty(mappingToken)){
      LOG.error("No valid mapping token was provided.");
      throw new WebApplicationException("No valid mapping token was provided.",Response.Status.UNAUTHORIZED);
      //TODO: Make this not throw html errors
    }
    
    LOG.debug("Looking up userId for mappingToken '" + mappingToken + "'.");
    String userId = accountLinkingDAO.getUserIdByMappingToken(mappingToken);
    
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
    
    String derpId;
    if(accessToken == null){
      String error = "Missing required parameter 'accessToken'";
      return Response.status(Response.Status.BAD_REQUEST).entity(new LandingPageErrorResponse(this, error)).build();
//      return new LandingPageErrorResponse(this, error);
//      return buildRedirect(steamErrorPagePath,steamLinkingFlowHostname,error);
    }else{
      LOG.debug("Looking up userId for acessToken '" + accessToken + "'.");
      derpId = accountLinkingDAO.getUserIdByAuthToken(accessToken);
    }
    if(externalId == null){
      String error = "Missing required parameter 'externalId'";
      return Response.status(Response.Status.BAD_REQUEST).entity(new LandingPageErrorResponse(this, error)).build();
//      return new LandingPageErrorResponse(this, error);
//      return buildRedirect(steamErrorPagePath,steamLinkingFlowHostname,error);
    }
    if(derpId == null){
      String error = "Token could not be resolved to a known user.";
      return Response.status(Response.Status.BAD_REQUEST).entity(new LandingPageErrorResponse(this, error)).build();
//      return new LandingPageErrorResponse(this, error);
//      return buildRedirect(steamErrorPagePath,steamLinkingFlowHostname,error);
    }
    
    LOG.debug("Looking up user for userId '" + derpId + "'.");
    AccountLinkingUser user = accountLinkingDAO.getUserByUserId(derpId);
    if(user == null){
      String error = "Couldn't find user with derpId '" + derpId + "'.";
      return Response.status(Response.Status.BAD_REQUEST).entity(new LandingPageErrorResponse(this, error)).build();
//      return new LandingPageErrorResponse(this, error);
//      return buildRedirect(steamErrorPagePath,steamLinkingFlowHostname,error);
    }
    
    if(user.getSteamId() == null){
      LOG.info("User '" + derpId + "' has no steamId; setting for the first time to '" + externalId + "'.");
    }else{
      LOG.info("User '" + derpId + "' had steamId '" + user.getSteamId() + "'; updating to '" + externalId + "'.");
    }
    user.setSteamId(externalId);
    
    accountLinkingDAO.updateUser(user);

    /*return buildRedirect(steamSuccessPagePath,null);*/
//    return Response.ok("{\"response\":\"Successfully linked account!\"}").build();
    return user;
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

    /*
    
    URIBuilder uriBuilder;
    try {
      uriBuilder = new URIBuilder(alexaRedirectPath);
    } catch (URISyntaxException e) {
      throw new WebApplicationException("Error");
    }
    uriBuilder.setFragment(urlFragment.toString());
    LOG.info("Redirect URI: " + uriBuilder.toString());*/
//    LOG.info("Access Token: " + accessToken);
//    return buildRedirect(alexaRedirectPath,null,null,urlFragment.toString());
    return Response.ok(user).header("Access-Token", accessToken).build();
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
