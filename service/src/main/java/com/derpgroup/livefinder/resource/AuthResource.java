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

import java.util.HashMap;
import java.util.Map;

import io.dropwizard.setup.Environment;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.livefinder.configuration.MainConfig;
import com.derpgroup.livefinder.dao.AccountLinkingDAO;
import com.derpgroup.livefinder.manager.LiveFinderManager;
import com.derpgroup.livefinder.model.accountlinking.AccountLinkingUser;

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

//  private LiveFinderManager manager;
  
  AccountLinkingDAO accountLinkingDAO;
  
  public AuthResource(MainConfig config, Environment env, AccountLinkingDAO accountLinkingDAO) {
//    manager = new LiveFinderManager();
    this.accountLinkingDAO = accountLinkingDAO;
  }
  
  @GET
  @Path("/twitch")
  @Produces(MediaType.TEXT_PLAIN)
  public String doTwitchAuth(){
    return "Hello!";
  }
  
  @GET
  @Path("/steam/linkIds")
  @Produces(MediaType.TEXT_PLAIN)
  public String doSteamLinking(@QueryParam("derpId") String derpId, @QueryParam("externalId") String externalId, @QueryParam("externalIdType") String externalIdType){
    Map<String,String> output = new HashMap<String,String>();
    
    if(derpId == null){
      return "Error - missing required parameter 'derpId'";
    }
    if(externalId == null){
      return "Error - missing required parameter 'externalId'";
    }
    if(externalIdType == null){
      return "Error - missing required parameter 'externalIdType'";
    }
    
    AccountLinkingUser user = accountLinkingDAO.getUserByUserId(derpId);
    if(user == null){
      return "Error - couldn't find user with derpId '" + derpId + "'.";
    }
    
    switch(externalIdType.toLowerCase()){
    case "steam":
      if(user.getSteamId() == null){
        LOG.info("User '" + derpId + "' has no steamId; setting for the first time to '" + externalId + "'.");
      }else{
        LOG.info("User '" + derpId + "' had steamId '" + user.getSteamId() + "'; updating to '" + externalId + "'.");
      }
      user.setSteamId(externalId);
      break;
    default:
      return "Error - unknown externalIdType";
    }
    
    accountLinkingDAO.updateUser(user);
    
    return user.toString();
  }
}
