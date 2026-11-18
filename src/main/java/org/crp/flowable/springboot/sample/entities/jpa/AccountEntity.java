package org.crp.flowable.springboot.sample.entities.jpa;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing an account in the database.
 */
@Entity
@Table(
        name="APP_ACCOUNT",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"NUMBER"})
)
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter
@Setter
public class AccountEntity {

    /**
     * The account number.
     */
    @Column(name = "NUMBER")
    private String number;

    /**
     * The owner of the account.
     */
    @Column(name = "OWNER")
    private String owner;

    /**
     * The unique identifier of the account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_CONTRACT_SEQ")
    @SequenceGenerator(name="APP_CONTRACT_SEQ", allocationSize = 1)
    private Integer id;
}
