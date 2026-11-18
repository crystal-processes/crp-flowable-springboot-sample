package org.crp.flowable.springboot.sample;

import java.util.ArrayList;
import java.util.Collection;

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
			Collection<ResolverFactory> resolverFactories = config.getPostDefaultResolverFactories();
			if (resolverFactories == null) {
				resolverFactories = new ArrayList<>();
				config.setPostDefaultResolverFactories(resolverFactories);
			}
			resolverFactories.add(new ProcessScriptFileNameResolverFactory());
		};
	}

}
