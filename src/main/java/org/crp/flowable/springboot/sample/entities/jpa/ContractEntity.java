package org.crp.flowable.springboot.sample.entities.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.CascadeType.MERGE;

/**
 * JPA entity representing a contract in the database.
 */
@Entity
@Table(
        name = "APP_CONTRACT"
)
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ContractEntity {

    /**
     * The unique identifier of the contract.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_CONTRACT_SEQ")
    @SequenceGenerator(name="APP_CONTRACT_SEQ", allocationSize = 1)
    private Integer id;

    /**
     * The contract identifier.
     */
    private String contractId;

    /**
     * The account associated with this contract.
     */
    @ManyToOne(cascade = MERGE)
    @PrimaryKeyJoinColumn(name = "id")
    private AccountEntity account;

    /**
     * The maximum amount for this contract.
     */
    private int maxAmount;
}


