package com.charter.retail.rewards.service;

import com.charter.retail.rewards.config.RewardsConfig;
import com.charter.retail.rewards.dto.CustomerRewardsDto;
import com.charter.retail.rewards.dto.TransactionDto;
import com.charter.retail.rewards.entity.Customer;
import com.charter.retail.rewards.entity.Transaction;
import com.charter.retail.rewards.exception.ResourceNotFoundException;
import com.charter.retail.rewards.repository.CustomerRepository;
import com.charter.retail.rewards.repository.TransactionRepository;
import com.charter.retail.rewards.util.RewardsCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Service layer for managing customer rewards and transactions.
 * Handles business logic for reward calculations and transaction processing.
 */
@Service
@RequiredArgsConstructor
public class RewardsService {
    
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final RewardsCalculator rewardsCalculator;
    private final RewardsConfig config;

    /**
     *Retrieves reward points for a specific customer over the configured time period.
     * 
     * @param customerId customer ID
     * @return customer rewards
     */
    public CustomerRewardsDto getRewardsForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        // Calculate date range for the last N months
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(config.getCalculationMonths());
        
        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
        
        Map<String, Integer> monthlyPoints = calculateMonthlyPoints(transactions);
        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();
        
        return new CustomerRewardsDto(customerId, customer.getName(), monthlyPoints, totalPoints);
    }

    /**
     * Gets reward points for all customers.
     * 
     * @param pageable pagination parameters
     * @return paginated customer rewards
     */
    public Page<CustomerRewardsDto> getRewardsForAllCustomers(Pageable pageable) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(config.getCalculationMonths());
        
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        
        List<CustomerRewardsDto> rewardsList = customerPage.getContent().stream()
                .map(customer -> {
                    List<Transaction> transactions = transactionRepository
                            .findByCustomerIdAndTransactionDateBetween(customer.getId(), startDate, endDate);
                    
                    Map<String, Integer> monthlyPoints = calculateMonthlyPoints(transactions);
                    int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();
                    
                    return new CustomerRewardsDto(customer.getId(), customer.getName(), monthlyPoints, totalPoints);
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(rewardsList, pageable, customerPage.getTotalElements());
    }
    
    /**
     * Creates a new transaction.
     * 
     * @param transactionDto transaction data
     * @return created transaction
     */
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        Customer customer = customerRepository.findById(transactionDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + transactionDto.getCustomerId()));
        
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionDate(transactionDto.getTransactionDate());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return new TransactionDto(
                savedTransaction.getId(),
                savedTransaction.getCustomerId(),
                savedTransaction.getAmount(),
                savedTransaction.getTransactionDate()
        );
    }
    
    /**
     * Calculates monthly points from transactions.
     * 
     * @param transactions list of transactions
     * @return monthly points map
     */
    private Map<String, Integer> calculateMonthlyPoints(List<Transaction> transactions) {
        // Using Locale.ROOT for consistent internal date formatting across all locales
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(config.getMonthFormat(), Locale.ROOT);
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate().format(formatter),
                        TreeMap::new,
                        Collectors.summingInt(transaction -> rewardsCalculator.calculatePoints(transaction.getAmount()))
                ));
    }
}
