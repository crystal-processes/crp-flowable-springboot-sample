package org.crp.flowable.springboot.sample.entities.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "APP_CONTRACT"
)
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_CONTRACT_SEQ")
    @SequenceGenerator(name="APP_CONTRACT_SEQ", allocationSize = 1)
    private Integer id;

    private String contractId;

    private String account;

    private int maxAmount;
}


