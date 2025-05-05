package org.crp.flowable.springboot.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.crp.flowable.ai.outputConverter.JsonOutputConverter;
import org.crp.flowable.springboot.sample.services.impl.StartProcessMessageHandler;
import org.flowable.engine.RuntimeService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mail.dsl.Mail;

@Configuration
@EnableConfigurationProperties(AcmeAppProperties.class)
public class AcmeAiConfiguration {
    @Bean
    IntegrationFlow mailListener(StartProcessMessageHandler startProcessMessageHandler, AcmeAppProperties properties) {
        return IntegrationFlow.from(Mail.imapInboundAdapter(properties.getImapInboundAdapterUrl())
                                .shouldDeleteMessages(false).shouldMarkMessagesAsRead(false).doGet(),
                        e -> e.poller(Pollers.fixedRate(5000).maxMessagesPerPoll(1)))
                .handle(startProcessMessageHandler)
                .get();
    }

    @Bean
    StartProcessMessageHandler startProcessMessageHandler(RuntimeService runtimeService, ObjectMapper objectMapper) {
        return new StartProcessMessageHandler("P005-aiMessageInputHandler", "message",
                runtimeService, objectMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean
    JsonOutputConverter jsonOutputConverter(ObjectMapper objectMapper) {
        return new JsonOutputConverter(objectMapper);
    }
}
