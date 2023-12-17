package org.crp.flowable.springboot.sample.services;

import org.crp.flowable.springboot.sample.entities.Contract;
import org.crp.flowable.springboot.sample.utils.RuntimeUsage;

/**
 * Demo service to provide contracts for the insurance event process.
 */
public interface ContractService {

    /**
     * Get contract by Id.
     * @param contractId contract identifier to get.
     * @return contract
     * @throws RuntimeException when contract is not found
     */
    @RuntimeUsage("Used in the expressions to get contract by `id`")
    Contract getContract(String contractId);
}
