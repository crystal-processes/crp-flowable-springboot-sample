package org.crp.flowable.springboot.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AcmeApplicationContextTest {

	@Autowired
	private AcmeApplication application;

	@Test
	void contextLoads() {
		assertThat(application).isNotNull();
	}

}
