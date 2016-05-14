package com.example;

import java.util.Arrays;

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

	public AuthorizationServerConfiguration(
			ProxyAuthorizationServerTokenServices tokenServices,
			CloudFoundryAuthenticationProvider authenticationProvider) {
		this.tokenServices = tokenServices;
		this.authenticationProvider = authenticationProvider;
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
		clients.inMemory().withClient("cf").secret("")
				.scopes("openid", "cloud_controller.read", "cloud_controller.write")
				.authorizedGrantTypes("password", "authorization_code", "refresh_token")
				.autoApprove(true);
	}
	
}
