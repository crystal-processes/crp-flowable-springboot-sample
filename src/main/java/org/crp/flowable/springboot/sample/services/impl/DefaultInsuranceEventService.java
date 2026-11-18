package org.crp.flowable.springboot.sample.services.impl;

import jakarta.transaction.Transactional;
import org.crp.flowable.springboot.sample.entities.jpa.InsuranceEventEntity;
import org.crp.flowable.springboot.sample.entities.jpa.InsuranceEventRepository;
import org.crp.flowable.springboot.sample.services.ContractService;
import org.crp.flowable.springboot.sample.services.InsuranceEventService;
import org.crp.flowable.springboot.sample.services.MoneyService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Default implementation of the InsuranceEventService.
 */
public class DefaultInsuranceEventService implements InsuranceEventService {

    /**
     * Service for contract operations.
     */
    @Autowired
    ContractService contractService;

    /**
     * Service for money operations.
     */
    @Autowired
    MoneyService moneyService;

    /**
     * Repository for insurance event entity operations.
     */
    @Autowired
    InsuranceEventRepository insuranceEventRepository;

    /**
     * Default constructor for DefaultInsuranceEventService.
     */
    public DefaultInsuranceEventService() {
    }

    @Override
    @Transactional
    public Integer createInstance(String contractId, String description, Integer requestedAmount) {
        return insuranceEventRepository.save(InsuranceEventEntity.builder()
                .involvedContract( contractService.getJpaContract(contractId))
                .description(description)
                .amountRequested(requestedAmount)
                .build()
        ).getId();
    }

    @Override
    @Transactional
    public void sendMoney(Integer insuranceEventId, Integer amountToSend) {
        InsuranceEventEntity insuranceEventEntity = insuranceEventRepository.findById(insuranceEventId)
                .orElseThrow(() -> new RuntimeException("Object not found "+insuranceEventId));

        if (amountToSend == null) {
            throw new RuntimeException("No Amount assessed");
        }
        insuranceEventEntity.setAmountAssessed(amountToSend);
        insuranceEventRepository.save(insuranceEventEntity);
        moneyService.sendMoney(
                insuranceEventEntity.getInvolvedContract().getAccount().getNumber(),
                amountToSend
        );
    }
}
