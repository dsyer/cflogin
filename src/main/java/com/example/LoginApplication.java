package com.example;

import java.security.Principal;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableConfigurationProperties(CloudFoundryProperties.class)
@Controller
public class LoginApplication extends WebMvcConfigurerAdapter {

	private ServerProperties server;
	private CloudFoundryProperties cloud;

	public LoginApplication(ServerProperties server, CloudFoundryProperties cloud) {
		this.server = server;
		this.cloud = cloud;
	}

	@RequestMapping("/userinfo")
	@ResponseBody
	public User login(Principal user) {
		OAuth2Authentication auth = (OAuth2Authentication) user;
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) auth.getUserAuthentication()
				.getDetails();
		// Cloud Foundry sends a user_id (UUID) and a user_name (email). The UUID is the
		// principal name.
		return new User((String) map.get("user_name"), user.getName());
	}

	@RequestMapping("/")
	public String home(Map<String, Object> model, Principal user) {
		model.put("contextPath", server.getContextPath()==null ? "" : server.getContextPath());
		model.put("api", cloud.getApi());
		model.put("user", user.getName());
		return "index";
	}
	
	@RequestMapping("/login")
	public String login(Map<String, Object> model,
			@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout) {
		if (error != null) {
			model.put("error", "Could not log in");
		}
		if (logout != null) {
			model.put("logout", "Logged out successfully");
		}
		model.put("contextPath", server.getContextPath()==null ? "" : server.getContextPath());
		model.put("api", cloud.getApi());
		return "login";
	}

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
	}
}

class User {
	private String name;
	private String id;

	public User(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
