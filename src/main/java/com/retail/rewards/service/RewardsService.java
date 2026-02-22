package com.retail.rewards.service;

import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.dto.TransactionDto;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.exception.ResourceNotFoundException;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import com.retail.rewards.util.RewardsCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for calculating customer reward points and managing transactions.
 * Handles business logic for rewards calculation and transaction creation.
 */
@Service
@RequiredArgsConstructor
public class RewardsService {
    
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    
    /**
     * Calculate rewards for a specific customer over the last 3 months.
     * 
     * @param customerId the customer ID
     * @return customer rewards DTO with monthly breakdown and total
     */
    public CustomerRewardsDto getRewardsForCustomer(Long customerId) {
        // Verify customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        // Calculate date range (last 3 months from today)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);
        
        // Fetch transactions for the customer within date range
        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
        
        // Calculate monthly points
        Map<String, Integer> monthlyPoints = calculateMonthlyPoints(transactions);
        
        // Calculate total points
        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();
        
        return new CustomerRewardsDto(customerId, customer.getName(), monthlyPoints, totalPoints);
    }
    
    /**
     * Calculate rewards for all customers over the last 3 months.
     * 
     * @return list of customer rewards DTOs
     */
    public List<CustomerRewardsDto> getRewardsForAllCustomers() {
        // Calculate date range (last 3 months from today)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);
        
        // Fetch all transactions within date range
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        
        // Group transactions by customer ID
        Map<Long, List<Transaction>> transactionsByCustomer = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomerId));
        
        // Calculate rewards for each customer
        List<CustomerRewardsDto> rewardsList = new ArrayList<>();
        for (Map.Entry<Long, List<Transaction>> entry : transactionsByCustomer.entrySet()) {
            Long customerId = entry.getKey();
            Customer customer = customerRepository.findById(customerId).orElse(null);
            
            if (customer != null) {
                Map<String, Integer> monthlyPoints = calculateMonthlyPoints(entry.getValue());
                int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();
                rewardsList.add(new CustomerRewardsDto(customerId, customer.getName(), monthlyPoints, totalPoints));
            }
        }
        
        return rewardsList;
    }
    
    /**
     * Create a new transaction for a customer.
     * 
     * @param transactionDto the transaction data
     * @return the created transaction DTO
     */
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        // Verify customer exists
        if (!customerRepository.existsById(transactionDto.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found with ID: " + transactionDto.getCustomerId());
        }
        
        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setCustomerId(transactionDto.getCustomerId());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionDate(transactionDto.getTransactionDate());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Convert to DTO and return
        return new TransactionDto(
                savedTransaction.getId(),
                savedTransaction.getCustomerId(),
                savedTransaction.getAmount(),
                savedTransaction.getTransactionDate()
        );
    }
    
    /**
     * Calculate monthly points from a list of transactions.
     * Groups transactions by month and calculates points for each month.
     * 
     * @param transactions list of transactions
     * @return map of month (YYYY-MM) to points
     */
    private Map<String, Integer> calculateMonthlyPoints(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate().format(MONTH_FORMATTER),
                        TreeMap::new,
                        Collectors.summingInt(transaction -> RewardsCalculator.calculatePoints(transaction.getAmount()))
                ));
    }
}
