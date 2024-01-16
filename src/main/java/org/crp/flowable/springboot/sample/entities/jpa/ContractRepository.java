package org.crp.flowable.springboot.sample.entities.jpa;

import org.hibernate.annotations.SQLSelect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<ContractEntity, Integer> {

    @SQLSelect(sql = "select * from ContractEntity where contractId = :contractId")
    Optional<ContractEntity> findByContractId(String contractId);

}
