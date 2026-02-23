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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private RewardsCalculator rewardsCalculator;
    
    @Mock
    private RewardsConfig config;
    
    @InjectMocks
    private RewardsService rewardsService;
    
    @BeforeEach
    void setUp() {
        when(config.getCalculationMonths()).thenReturn(3);
        when(config.getMonthFormat()).thenReturn("yyyy-MM");
    }
    
    @Test
    void testGetRewardsForCustomer_Success() {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Test Customer");
        
        LocalDate today = LocalDate.now();
        List<Transaction> transactions = Arrays.asList(
            new Transaction(1L, customerId, new BigDecimal("120.00"), today.minusDays(10)),
            new Transaction(2L, customerId, new BigDecimal("75.00"), today.minusDays(20))
        );
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(customerId), any(), any()))
            .thenReturn(transactions);
        when(rewardsCalculator.calculatePoints(new BigDecimal("120.00"))).thenReturn(90);
        when(rewardsCalculator.calculatePoints(new BigDecimal("75.00"))).thenReturn(25);
        
        CustomerRewardsDto result = rewardsService.getRewardsForCustomer(customerId);
        
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals("Test Customer", result.getCustomerName());
        assertEquals(115, result.getTotalPoints());
    }
    
    @Test
    void testGetRewardsForCustomer_NotFound() {
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            rewardsService.getRewardsForCustomer(customerId);
        });
    }
    
    @Test
    void testGetRewardsForCustomer_NoTransactions() {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Test Customer");
        
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(customerId), any(), any()))
            .thenReturn(Arrays.asList());
        
        CustomerRewardsDto result = rewardsService.getRewardsForCustomer(customerId);
        
        assertNotNull(result);
        assertEquals(0, result.getTotalPoints());
        assertTrue(result.getMonthlyPoints().isEmpty());
    }
    
    @Test
    void testGetRewardsForAllCustomers() {
        LocalDate today = LocalDate.now();
        Customer customer1 = new Customer(1L, "Customer 1");
        Customer customer2 = new Customer(2L, "Customer 2");
        
        List<Customer> customers = Arrays.asList(customer1, customer2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(customers, pageable, 2);
        
        List<Transaction> transactions1 = Arrays.asList(
            new Transaction(1L, 1L, new BigDecimal("120.00"), today.minusDays(10))
        );
        List<Transaction> transactions2 = Arrays.asList(
            new Transaction(2L, 2L, new BigDecimal("150.00"), today.minusDays(15))
        );
        
        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(1L), any(), any()))
            .thenReturn(transactions1);
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(2L), any(), any()))
            .thenReturn(transactions2);
        when(rewardsCalculator.calculatePoints(any())).thenReturn(90, 150);
        
        Page<CustomerRewardsDto> results = rewardsService.getRewardsForAllCustomers(pageable);
        
        assertNotNull(results);
        assertEquals(2, results.getContent().size());
        assertEquals(2, results.getTotalElements());
    }
    
    @Test
    void testCreateTransaction_Success() {
        Long customerId = 1L;
        TransactionDto inputDto = new TransactionDto(null, customerId, new BigDecimal("120.00"), LocalDate.now());
        Transaction savedTransaction = new Transaction(1L, customerId, new BigDecimal("120.00"), LocalDate.now());
        
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        
        TransactionDto result = rewardsService.createTransaction(inputDto);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(new BigDecimal("120.00"), result.getAmount());
    }
    
    @Test
    void testCreateTransaction_CustomerNotFound() {
        Long customerId = 999L;
        TransactionDto inputDto = new TransactionDto(null, customerId, new BigDecimal("120.00"), LocalDate.now());
        
        when(customerRepository.existsById(customerId)).thenReturn(false);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            rewardsService.createTransaction(inputDto);
        });
    }
}
