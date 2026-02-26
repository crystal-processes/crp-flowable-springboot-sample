package org.crp.flowable.springboot.sample.config;

import org.crp.flowable.springboot.sample.config.impl.FormEngineConfiguration;
import org.crp.flowable.springboot.sample.config.impl.FormEngineConfigurator;
import org.crp.flowable.springboot.sample.config.impl.SampleFormHandlerInterceptor;
import org.crp.flowable.springboot.sample.services.form.SampleFormService;
import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.rest.service.api.FormHandlerRestApiInterceptor;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FormEngineConfig {

    @Bean
    FormHandlerRestApiInterceptor sampleFormHandlerInterceptor() {
        return new SampleFormHandlerInterceptor();
    }

    @Bean
    FormEngineConfiguration formEngineConfiguration() {
        return new FormEngineConfiguration().setFormService(new SampleFormService());
    }

    @Bean
    FormEngineConfigurator sampleFormEngineConfigurator(FormEngineConfiguration formEngineConfiguration) {
        return new FormEngineConfigurator().setFormEngineConfiguration(formEngineConfiguration);
    }

    @Bean
    EngineConfigurationConfigurer<SpringAppEngineConfiguration> addFormEngine(FormEngineConfigurator formEngineConfigurator) {
        return appEngineConfiguration -> appEngineConfiguration.addConfigurator(formEngineConfigurator);
    }

    @Bean
    EngineConfigurationConfigurer<SpringProcessEngineConfiguration> addFormService(FormEngineConfigurator formEngineConfigurator) {
        return configuration -> configuration.addConfigurator(formEngineConfigurator);

    }
}