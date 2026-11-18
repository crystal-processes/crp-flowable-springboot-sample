package org.crp.flowable.springboot.sample.entities.jpa;

import org.hibernate.annotations.SQLSelect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for ContractEntity operations.
 */
public interface ContractRepository extends JpaRepository<ContractEntity, Integer> {

    /**
     * Finds a contract by its contract ID.
     *
     * @param contractId the contract identifier to search for
     * @return an Optional containing the contract if found
     */
    @SQLSelect(sql = "select * from ContractEntity where contractId = :contractId")
    Optional<ContractEntity> findByContractId(String contractId);

}
