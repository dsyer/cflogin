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

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

class CloudFoundryAuthentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -4233504504979317090L;
	private OAuth2AccessToken token;
	private String username;

	public CloudFoundryAuthentication(String username, OAuth2AccessToken token) {
		this(username, token,
				AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
	}

	private CloudFoundryAuthentication(String username, OAuth2AccessToken token,
			List<GrantedAuthority> authorities) {
		super(authorities);
		this.username = username;
		this.token = token;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		return username;
	}

	public OAuth2AccessToken getToken() {
		return token;
	}

}