package org.crp.flowable.springboot.sample.services.impl;

import org.crp.flowable.springboot.sample.entities.Contract;
import org.crp.flowable.springboot.sample.services.ContractService;
import org.crp.flowable.springboot.sample.utils.RuntimeUsage;

/**
 * "Mock" contract service implementation.
 */
public class DefaultContractService implements ContractService {

    @Override
    public Contract getContract(String contractId) {
        return Contract.builder().id(contractId).account("ABCD-123456789").maxAmount(10_000).build();
    }
}
