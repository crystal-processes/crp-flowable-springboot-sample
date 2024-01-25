package org.crp.flowable.springboot.sample.entities.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "APP_INSURANCE_EVENT"
)
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class InsuranceEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_CONTRACT_SEQ")
    @SequenceGenerator(name="APP_CONTRACT_SEQ", allocationSize = 1)
    private Integer id;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="INVOLVED_CONTRACT")
    private  ContractEntity involvedContract;

    private int amountRequested;
    private Integer amountAssessed;

    private String description;
}


