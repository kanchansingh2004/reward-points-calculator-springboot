package com.charter.retail.rewards.repository;

import com.charter.retail.rewards.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for Transaction entity.
 * Provides CRUD operations and custom query methods for transaction retrieval.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Finds all transactions for a specific customer within a date range.
     *
     * @param customerId the customer ID
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return list of transactions matching the criteria
     */
    @Query("SELECT t FROM Transaction t WHERE t.customer.id = :customerId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByCustomerIdAndTransactionDateBetween(
            @Param("customerId") Long customerId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    /**
     * Finds all transactions within a date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return list of transactions matching the criteria
     */
    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
}
