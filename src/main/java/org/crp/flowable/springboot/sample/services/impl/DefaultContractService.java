package org.crp.flowable.springboot.sample.services.impl;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.crp.flowable.springboot.sample.entities.jpa.ContractEntity;
import org.crp.flowable.springboot.sample.entities.jpa.ContractRepository;
import org.crp.flowable.springboot.sample.services.ContractService;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * "Mock" contract service implementation.
 */
public class DefaultContractService implements ContractService {

    /**
     * Repository for contract entity operations.
     */
    @Autowired
    private ContractRepository contractRepository;

    /**
     * Jackson ObjectMapper for JSON processing.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Default constructor for DefaultContractService.
     */
    public DefaultContractService() {
    }

    @Override
    public JsonNode getContract(String contractId) {
        try {
            return objectMapper.readTree("""
                    {
                    "account" : { "owner" : "jlong", "id":"ABCD-123456789" },
                    "maxAmount": 10000
                    }""");
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ContractEntity getJpaContract(String contractId) {
        return contractRepository.findByContractId(contractId).orElseThrow(
                        () -> new FlowableObjectNotFoundException("Contract ["+contractId+"] was not found")
                );
    }
}
