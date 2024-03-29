package org.crp.flowable.springboot.sample.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.crp.flowable.springboot.sample.entities.jpa.ContractEntity;
import org.crp.flowable.springboot.sample.utils.RuntimeUsage;

/**
 * Demo service to provide contracts for the insurance event process.
 */
public interface ContractService {

    /**
     * Get contract by Id.
     *
     * @param contractId contract identifier to get.
     * @return contract
     * @throws RuntimeException when contract is not found
     */
    @RuntimeUsage("Used in the expressions to get contract by `id`")
    JsonNode getContract(String contractId);

    /**
     * Get JPA contract by contract Id.
     *
     * @param contractId contract identifier to get.
     * @return contract jpa entity
     * @throws RuntimeException when contract is not found
     */
    @RuntimeUsage("Used in the expressions to get contract by `contractId`")
    ContractEntity getJpaContract(String contractId);
}
