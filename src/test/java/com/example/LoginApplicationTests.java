package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class LoginApplicationTests {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void contextLoads() {
		Map<String, Object> map = jdbcTemplate.queryForMap("SELECT * from user_client_details where client_id='cf'");
		assertThat(map).containsKey("username");
	}

}
