package org.crp.flowable.springboot.sample.services.impl;

import org.crp.flowable.springboot.sample.entities.Account;
import org.crp.flowable.springboot.sample.entities.Contract;
import org.crp.flowable.springboot.sample.services.ContractService;

/**
 * "Mock" contract service implementation.
 */
public class DefaultContractService implements ContractService {

    @Override
    public Contract getContract(String contractId) {
        return Contract.builder().id(contractId).account(
                Account.builder().owner("jlong").id("ABCD-123456789").build()
        ).maxAmount(10_000).build();
    }
}
