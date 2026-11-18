package org.crp.flowable.springboot.sample.entities.jpa;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.CascadeType.MERGE;

/**
 * JPA entity representing an insurance event in the database.
 */
@Entity
@Table(
        name = "APP_INSURANCE_EVENT"
)
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class InsuranceEventEntity {

    /**
     * The unique identifier of the insurance event.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_CONTRACT_SEQ")
    @SequenceGenerator(name="APP_CONTRACT_SEQ", allocationSize = 1)
    private Integer id;

    /**
     * The contract involved in this insurance event.
     */
    @ManyToOne(cascade = MERGE)
    @PrimaryKeyJoinColumn(name="INVOLVED_CONTRACT_ID")
    private ContractEntity involvedContract;

    /**
     * The amount requested in this insurance event.
     */
    private int amountRequested;

    /**
     * The amount assessed in this insurance event.
     */
    private Integer amountAssessed;

    /**
     * The description of this insurance event.
     */
    private String description;
}


