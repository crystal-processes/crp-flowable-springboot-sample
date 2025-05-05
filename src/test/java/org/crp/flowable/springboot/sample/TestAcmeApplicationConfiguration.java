package org.crp.flowable.springboot.sample;

import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestAcmeApplicationConfiguration {
    @Bean
    ChatClient chatClient() {
        return Mockito.mock(ChatClient.class);
    }
}
