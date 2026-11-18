package org.crp.flowable.springboot.sample.entities.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for AccountEntity operations.
 */
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
}
