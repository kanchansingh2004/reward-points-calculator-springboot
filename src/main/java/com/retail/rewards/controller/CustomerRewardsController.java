package com.retail.rewards.controller;

import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.dto.TransactionDto;
import com.retail.rewards.service.RewardsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for rewards and transaction endpoints.
 * Provides APIs to retrieve customer reward points and create transactions.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerRewardsController {
    
    private final RewardsService rewardsService;
    
    /**
     * Get rewards for a specific customer.
     * Calculates points earned over the last 3 months with monthly breakdown.
     * 
     * @param customerId the customer ID
     * @return customer rewards DTO
     */
    @GetMapping("/rewards/customer/{customerId}")
    public ResponseEntity<CustomerRewardsDto> getCustomerRewards(@PathVariable Long customerId) {
        CustomerRewardsDto rewards = rewardsService.getRewardsForCustomer(customerId);
        return ResponseEntity.ok(rewards);
    }
    
    /**
     * Get rewards for all customers.
     * Calculates points for all customers over the last 3 months.
     * 
     * @return list of customer rewards DTOs
     */
    @GetMapping("/rewards/customers")
    public ResponseEntity<List<CustomerRewardsDto>> getAllCustomersRewards() {
        List<CustomerRewardsDto> rewardsList = rewardsService.getRewardsForAllCustomers();
        return ResponseEntity.ok(rewardsList);
    }
    
    /**
     * Create a new transaction.
     * 
     * @param transactionDto the transaction data
     * @return the created transaction DTO
     */
    @PostMapping("/transactions")
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        TransactionDto createdTransaction = rewardsService.createTransaction(transactionDto);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }
}
