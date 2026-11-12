package org.crp.flowable.springboot.sample;

import java.util.ArrayList;
import java.util.List;

import org.crp.flowable.groovy.script.ProcessScriptFileNameResolverFactory;
import org.flowable.common.engine.impl.scripting.ResolverFactory;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AcmeApplicationTestConfiguration {

	@Bean EngineConfigurationConfigurer<SpringProcessEngineConfiguration> addDebuggerFileNameResolver() {
		return config -> {
			List<ResolverFactory> resolverFactories = config.getResolverFactories();
			if (resolverFactories == null) {
				resolverFactories = new ArrayList<>();
				config.setResolverFactories(resolverFactories);
			}
			resolverFactories.add(new ProcessScriptFileNameResolverFactory());
		};
	}

}
