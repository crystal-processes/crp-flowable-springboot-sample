package org.crp.flowable.springboot.sample.services;

/**
 * Service interface for insurance event operations.
 */
public interface InsuranceEventService {

    /**
     * Creates a new insurance event instance.
     *
     * @param contractId the ID of the contract
     * @param description the description of the insurance event
     * @param requestedAmount the requested amount
     * @return the ID of the created insurance event
     */
    Integer createInstance(String contractId, String description, Integer requestedAmount);

    /**
     * Sends money for an insurance event.
     *
     * @param insuranceEventEntityId the ID of the insurance event entity
     * @param amountToSend the amount to send
     */
    void sendMoney(Integer insuranceEventEntityId, Integer amountToSend);
}
