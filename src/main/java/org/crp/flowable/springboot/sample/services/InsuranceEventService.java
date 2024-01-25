package org.crp.flowable.springboot.sample.services;

public interface InsuranceEventService {

    Integer createInstance(String contractId, String description, Integer requestedAmount);

    void sendMoney(Integer insuranceEventEntityId, Integer amountToSend);
}
