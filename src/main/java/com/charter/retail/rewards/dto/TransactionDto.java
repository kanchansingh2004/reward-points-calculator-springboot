package com.charter.retail.rewards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for transaction information.
 * Used for creating and returning transaction data with validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    
    private Long id;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;
}
