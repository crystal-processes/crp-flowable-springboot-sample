package org.crp.flowable.springboot.sample.entities.jpa;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "NUMBER")
    private String number;

    @Column(name = "OWNER")
    private String owner;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_CONTRACT_SEQ")
    @SequenceGenerator(name="APP_CONTRACT_SEQ", allocationSize = 1)
    private Integer id;
}
