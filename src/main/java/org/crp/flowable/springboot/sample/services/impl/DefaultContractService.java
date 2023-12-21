package org.crp.flowable.springboot.sample.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.crp.flowable.springboot.sample.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * "Mock" contract service implementation.
 */
public class DefaultContractService implements ContractService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public JsonNode getContract(String contractId) {
        try {
            return objectMapper.readTree("{" +
                    "\"account\" : { \"owner\" : \"jlong\", \"id\":\"ABCD-123456789\" }," +
                    "\"maxAmount\": 10000" +
                    "}");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
