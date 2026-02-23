package com.retail.rewards.service;

import com.retail.rewards.config.RewardsConfig;
import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.dto.TransactionDto;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.exception.ResourceNotFoundException;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import com.retail.rewards.util.RewardsCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardsService {
    
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final RewardsCalculator rewardsCalculator;
    private final RewardsConfig config;

    public CustomerRewardsDto getRewardsForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(config.getCalculationMonths());
        
        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
        
        Map<String, Integer> monthlyPoints = calculateMonthlyPoints(transactions);
        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();
        
        return new CustomerRewardsDto(customerId, customer.getName(), monthlyPoints, totalPoints);
    }
    

    // Calculates reward points for all customers with pagination support.
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
    
    //Creates a new transaction for a customer.
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        if (!customerRepository.existsById(transactionDto.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found with ID: " + transactionDto.getCustomerId());
        }
        
        Transaction transaction = new Transaction();
        transaction.setCustomerId(transactionDto.getCustomerId());
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
    
    //Calculates monthly reward points from a list of transactions.
    private Map<String, Integer> calculateMonthlyPoints(List<Transaction> transactions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(config.getMonthFormat());
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate().format(formatter),
                        TreeMap::new,
                        Collectors.summingInt(transaction -> rewardsCalculator.calculatePoints(transaction.getAmount()))
                ));
    }
}
