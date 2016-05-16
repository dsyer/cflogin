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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Dave Syer
 *
 */
@Controller
public class ClientDetailsController {

	private JdbcTemplate template;

	private JdbcClientDetailsService clients;

	private RandomValueStringGenerator strings = new RandomValueStringGenerator(8);

	public ClientDetailsController(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		this.clients = new JdbcClientDetailsService(dataSource);
	}

	@GetMapping("/clients")
	public String clients(Map<String, Object> model, Principal user) {
		model.put("user", user.getName());
		List<ClientDetails> details = new ArrayList<>();
		List<Map<String, Object>> list = template.queryForList(
				"SELECT * from user_client_details where username=?", user.getName());
		for (ClientDetails clientDetails : clients.listClientDetails()) {
			if (isOwner(user.getName(), clientDetails.getClientId(), list)) {
				details.add(clientDetails);
			}
		}
		model.put("clients", details);
		return "clients";
	}

	@PostMapping("/clients")
	public String add(Principal user) {
		BaseClientDetails client = new BaseClientDetails(strings.generate(), null,
				"openid,cloud_controller.read,cloud_controller.write",
				"password,authorization_code,refresh_token", "ROLE_CLIENT");
		client.setClientSecret(strings.generate());
		client.setAutoApproveScopes(Arrays.asList("true"));
		clients.addClientDetails(client);
		template.update(
				"INSERT into user_client_details (username, client_id) values (?,?)",
				user.getName(), client.getClientId());
		return "redirect:/clients";
	}

	@DeleteMapping("/clients/{clientId}")
	public String delete(Principal user, @PathVariable String clientId) {
		clients.removeClientDetails(clientId);
		template.update(
				"DELETE from user_client_details where username=? and client_id=?",
				user.getName(), clientId);
		return "redirect:/clients";
	}

	private boolean isOwner(String name, String clientId,
			List<Map<String, Object>> list) {
		for (Map<String, Object> map : list) {
			if (name.equals(map.get("username"))
					&& clientId.equals(map.get("client_id"))) {
				return true;
			}
		}
		return false;
	}

}
