package com.charter.retail.rewards.controller;

import com.charter.retail.rewards.dto.TransactionDto;
import com.charter.retail.rewards.service.RewardsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing customer transactions.
 * Handles creation of new purchase transactions.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final RewardsService rewardsService;
    
    /**
     * Creates a new transaction.
     * 
     * @param transactionDto transaction data
     * @return created transaction
     */
    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        TransactionDto createdTransaction = rewardsService.createTransaction(transactionDto);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }
}
