package com.example;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration
		extends AuthorizationServerConfigurerAdapter {

	private ProxyAuthorizationServerTokenServices tokenServices;
	private CloudFoundryAuthenticationProvider authenticationProvider;
	private DataSource dataSource;

	public AuthorizationServerConfiguration(
			ProxyAuthorizationServerTokenServices tokenServices,
			CloudFoundryAuthenticationProvider authenticationProvider, DataSource dataSource) {
		this.tokenServices = tokenServices;
		this.authenticationProvider = authenticationProvider;
		this.dataSource = dataSource;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)
			throws Exception {
		endpoints.tokenServices(tokenServices);
		endpoints.authenticationManager(
				new ProviderManager(Arrays.asList(authenticationProvider)));
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// N.B. the scopes are notional. The tokens are all generated with scopes that are
		// passed through from the UAA.
		clients.jdbc(dataSource);
	}
	
}
