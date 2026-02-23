package com.retail.rewards.controller;

import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.dto.TransactionDto;
import com.retail.rewards.service.RewardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for customer rewards and transaction management.
 * Provides endpoints to calculate rewards and create transactions.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "Endpoints for customer rewards and transactions")
public class CustomerRewardsController {
    
    private final RewardsService rewardsService;

    /**
     * Retrieves reward points for a specific customer.
     * Calculates points earned over the last 3 months with monthly breakdown.
     **/
    @GetMapping("/rewards/customer/{customerId}")
    @Operation(summary = "Get rewards for a customer", description = "Returns reward points breakdown for the last 3 months")
    public ResponseEntity<CustomerRewardsDto> getCustomerRewards(@PathVariable Long customerId) {
        return ResponseEntity.ok(rewardsService.getRewardsForCustomer(customerId));
    }
    
    /**
     * Retrieves reward points for all customers with pagination support.
     **/
    @GetMapping("/rewards/customers")
    @Operation(summary = "Get all customer rewards", description = "Returns paginated list of all customers with their reward points")
    public ResponseEntity<Page<CustomerRewardsDto>> getAllCustomersRewards(Pageable pageable) {
        return ResponseEntity.ok(rewardsService.getRewardsForAllCustomers(pageable));
    }
    
    /**
     * Creates a new transaction for a customer.
     **/
    @PostMapping("/transactions")
    @Operation(summary = "Create a transaction", description = "Records a new purchase transaction for a customer")
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        return new ResponseEntity<>(rewardsService.createTransaction(transactionDto), HttpStatus.CREATED);
    }
}
