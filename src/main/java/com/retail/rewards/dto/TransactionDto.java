package com.retail.rewards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for transaction information.
 * Used for creating new transactions via API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    
    // Transaction ID (populated in responses)
    private Long id;
    
    // Customer ID who made the transaction
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    // Transaction amount
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    // Date of the transaction
    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;
}
