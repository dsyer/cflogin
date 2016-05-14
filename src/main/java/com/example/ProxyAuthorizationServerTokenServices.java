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

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Dave Syer
 *
 */
@Service
public class ProxyAuthorizationServerTokenServices
		implements AuthorizationServerTokenServices {

	private CloudFoundryProperties properties;
	private RestOperations restTemplate = new RestTemplate();

	public ProxyAuthorizationServerTokenServices(CloudFoundryProperties properties) {
		this.properties = properties;
	}

	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication)
			throws AuthenticationException {
		Authentication user = authentication.getUserAuthentication();
		if (user instanceof CloudFoundryAuthentication) {
			return ((CloudFoundryAuthentication) user).getToken();
		}
		throw new AuthenticationCredentialsNotFoundException(
				"No Cloud Foundy authentication found");
	}

	@Override
	public OAuth2AccessToken refreshAccessToken(String refreshToken,
			TokenRequest tokenRequest) throws AuthenticationException {
		MultiValueMap<String, String> form = createForm(refreshToken, tokenRequest);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
				form, createHeaders());
		String url = properties.getAccessTokenUrl();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = restTemplate.postForEntity(url, request, Map.class)
				.getBody();
		OAuth2AccessToken token = ectractAccessToken(map);
		return token;
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization",
				"Basic " + Base64Utils.encodeToString("cf:".getBytes()));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return headers;
	}

	private MultiValueMap<String, String> createForm(String refreshToken,
			TokenRequest tokenRequest) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.set(OAuth2Utils.GRANT_TYPE, "refresh_token");
		form.set("refresh_token", refreshToken);
		if (!tokenRequest.getScope().isEmpty()) {
			form.set(OAuth2Utils.SCOPE,
					OAuth2Utils.formatParameterList(tokenRequest.getScope()));
		}
		return form;
	}

	private DefaultOAuth2AccessToken ectractAccessToken(Map<String, Object> map) {
		DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(
				(String) map.get("access_token"));
		token.setRefreshToken(
				new DefaultOAuth2RefreshToken((String) map.get("refresh_token")));
		token.setScope(OAuth2Utils.parseParameterList((String) map.get("scope")));
		return token;
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		Authentication user = authentication.getUserAuthentication();
		if (user instanceof CloudFoundryAuthentication) {
			return ((CloudFoundryAuthentication) user).getToken();
		}
		return null;
	}

}
