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

package com.derpgroup.livefinder;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.IOException;

import com.derpgroup.derpwizard.configuration.AccountLinkingDAOConfig;
import com.derpgroup.derpwizard.dao.AccountLinkingDAO;
import com.derpgroup.derpwizard.dao.impl.AccountLinkingDAOFactory;
import com.derpgroup.derpwizard.dao.impl.H2EmbeddedAccountLinkingDAO;
import com.derpgroup.livefinder.configuration.LiveFinderMainConfig;
import com.derpgroup.livefinder.configuration.TwitchAccountLinkingConfig;
import com.derpgroup.livefinder.health.BasicHealthCheck;
import com.derpgroup.livefinder.model.SteamClientWrapper;
import com.derpgroup.livefinder.model.TwitchClientWrapper;
import com.derpgroup.livefinder.resource.AuthResource;
import com.derpgroup.livefinder.resource.LiveFinderAlexaResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Main method for spinning up the HTTP server.
 *
 * @author Rusty Gerard
 * @since 0.0.1
 */
public class App extends Application<LiveFinderMainConfig> {

  public static void main(String[] args) throws Exception {
    new App().run(args);
  }

  @Override
  public void initialize(Bootstrap<LiveFinderMainConfig> bootstrap) {
    
    bootstrap.addBundle(new AssetsBundle("/accountLinking", "/livefinder/accountLinking", "accountLinking.html"));
  }

  @Override
  public void run(LiveFinderMainConfig config, Environment environment) throws IOException {
    if (config.isPrettyPrint()) {
      ObjectMapper mapper = environment.getObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Health checks
    environment.healthChecks().register("basics", new BasicHealthCheck(config, environment));

    AccountLinkingDAOConfig accountLinkingDAOConfig = config.getDaoConfig().getAccountLinking();
    
    // DAO
    AccountLinkingDAO accountLinkingDAO = AccountLinkingDAOFactory.getDAO(accountLinkingDAOConfig);
    
    SteamClientWrapper wrapper = SteamClientWrapper.getInstance();
    wrapper.init(config.getLiveFinderConfig().getApiKey());
    TwitchClientWrapper twitchWrapper = TwitchClientWrapper.getInstance();
    TwitchAccountLinkingConfig twitchConfig = config.getLiveFinderConfig().getTwitchAccountLinkingConfig();
    twitchWrapper.init(twitchConfig.getTwitchApiRootUri()
        ,twitchConfig.getClientId()
        ,twitchConfig.getClientSecret()
        ,twitchConfig.getRedirectUri());
    
    // Resources
    environment.jersey().register(new LiveFinderAlexaResource(config, environment, accountLinkingDAO));
    environment.jersey().register(new AuthResource(config, environment,accountLinkingDAO));
  }
}
