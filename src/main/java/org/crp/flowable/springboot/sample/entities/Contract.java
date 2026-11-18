package org.crp.flowable.springboot.sample.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a contract entity in the system.
 * This is a DTO (Data Transfer Object) class used for data transfer.
 */
@Builder
@Getter @Setter @AllArgsConstructor
public class Contract implements Serializable {
    @Serial
    private static final long serialVersionUID = -2948855108722785389L;

    /**
     * The unique identifier of the contract.
     */
    private final String id;

    /**
     * The account associated with this contract.
     */
    private final Account account;

    /**
     * The maximum amount for this contract.
     */
    private int maxAmount;
}
