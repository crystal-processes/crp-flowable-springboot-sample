package org.crp.flowable.springboot.sample.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.mail.Address;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.IOException;

public class StartProcessMessageHandler implements MessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(StartProcessMessageHandler.class);

    private final String processDefinitionKey;
    private final String inputVariableName;
    private final RuntimeService runtimeService;
    private final ObjectMapper objectMapper;

    public StartProcessMessageHandler(String processDefinitionKey, String inputVariableName, RuntimeService runtimeService, ObjectMapper objectMapper) {
        this.processDefinitionKey = processDefinitionKey;
        this.inputVariableName = inputVariableName;
        this.runtimeService = runtimeService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        LOG.debug("A new message starts a process {}", processDefinitionKey);
        try {
            ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder().processDefinitionKey(processDefinitionKey)
                    .variable(inputVariableName, convertToJson(message))
                    .start();
            LOG.debug("The process {} was started with id {}", processDefinitionKey, processInstance.getId());
        } catch (IOException e) {
            LOG.error("Unable to parse message {}.", message.getHeaders().get("id"));
            throw new RuntimeException("Unable to parse message.", e);
        }
    }

    private ObjectNode convertToJson(Message<?> message) throws JsonProcessingException {
        ObjectNode jsonMessage = objectMapper.createObjectNode();
        try {
            jsonMessage.put("body", ((jakarta.mail.Message) message.getPayload()).getContent().toString());
            jsonMessage.put("subject", ((jakarta.mail.Message) message.getPayload()).getSubject());
            ArrayNode from = jsonMessage.putArray("from");
            for (Address a : ((jakarta.mail.Message) message.getPayload()).getFrom()) {
                from.add(a.toString());
            }
        } catch (IOException | MessagingException | jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
        return jsonMessage;
    }
}
