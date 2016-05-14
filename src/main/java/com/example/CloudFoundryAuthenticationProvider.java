/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

/**
 * @author Dave Syer
 *
 */
@Component
public class CloudFoundryAuthenticationProvider implements AuthenticationProvider {

	private CloudFoundryProperties properties;

	public CloudFoundryAuthenticationProvider(CloudFoundryProperties properties) {
		this.properties = properties;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) authentication;
		String username = user.getName();
		String password = user.getCredentials().toString();
		CloudCredentials credentials = new CloudCredentials(username, password);
		CloudFoundryClient client = new CloudFoundryClient(credentials,
				properties.getApi());
		OAuth2AccessToken login = client.login();
		CloudFoundryAuthentication result = new CloudFoundryAuthentication(username,
				login);
		return result;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}