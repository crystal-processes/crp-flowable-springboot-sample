package org.crp.flowable.springboot.sample.entities.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for InsuranceEventEntity operations.
 */
public interface InsuranceEventRepository extends JpaRepository<InsuranceEventEntity, Integer> {

}
