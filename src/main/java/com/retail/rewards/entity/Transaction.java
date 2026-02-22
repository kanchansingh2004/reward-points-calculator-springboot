package com.retail.rewards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class representing a customer transaction.
 * Each transaction is associated with a customer and contains purchase details.
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    // Unique identifier for the transaction
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ID of the customer who made the transaction
    @Column(nullable = false)
    private Long customerId;
    
    // Transaction amount in dollars
    @Column(nullable = false)
    private BigDecimal amount;
    
    // Date when the transaction occurred
    @Column(nullable = false)
    private LocalDate transactionDate;
}
