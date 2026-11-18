package org.crp.flowable.springboot.sample.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an account entity in the system.
 * This is a DTO (Data Transfer Object) class used for data transfer.
 */
@Builder
@Getter @Setter
@AllArgsConstructor
public class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = -1524895032576145883L;

    /**
     * The owner of the account.
     */
    private final String owner;

    /**
     * The unique identifier of the account.
     */
    private final String id;
}
