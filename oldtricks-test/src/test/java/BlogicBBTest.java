/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.io.File;

import oldtricks.test.RegressionTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BlogicBBTest extends RegressionTest {
	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(BlogicBBTest.class);
	@Value("#{jdbcTemplate}")
	private JdbcTemplate jdbcTemplate;
	@Value("#{restTemplate}")
	private RestTemplate restTemplate;

	@Override
	protected void prepareTest() {
		addJdbcTemplate("mysql", jdbcTemplate);
	}

	@Test
	public void test001() {
		LOG.info(getTestClassName() + File.separator + getTestMathodName());
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.add("accountType", "GOOGLE");
		request.add("Email", "xxxxxx@gmail.com");
		request.add("Passwd", "*******");
		request.add("source", "fromtestcode");
		request.add("service", "ac2dm");

		LOG.info("{}", restTemplate.postForEntity("https://www.google.com/accounts/ClientLogin", request, String.class));
	}

	@Test
	public void test002() {
		LOG.info(getTestClassName() + File.separator + getTestMathodName());
	}

}
