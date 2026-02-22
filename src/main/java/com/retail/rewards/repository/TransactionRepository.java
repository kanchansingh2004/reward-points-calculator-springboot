package com.retail.rewards.repository;

import com.retail.rewards.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Transaction entity.
 * Provides CRUD operations and custom queries for transaction data.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find all transactions for a specific customer within a date range.
     * 
     * @param customerId the customer ID
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return list of transactions
     */
    List<Transaction> findByCustomerIdAndTransactionDateBetween(Long customerId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all transactions within a date range.
     * 
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return list of transactions
     */
    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
}
