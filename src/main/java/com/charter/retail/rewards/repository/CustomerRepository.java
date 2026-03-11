package com.charter.retail.rewards.repository;

import com.charter.retail.rewards.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Customer entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
