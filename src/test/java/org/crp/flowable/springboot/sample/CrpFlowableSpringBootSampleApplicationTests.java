package org.crp.flowable.springboot.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CrpFlowableSpringBootSampleApplicationTests {

	@Autowired
	private CrpFlowableSpringBootSampleApplication application;

	@Test
	void contextLoads() {
		assertThat(application).isNotNull();
	}

}
