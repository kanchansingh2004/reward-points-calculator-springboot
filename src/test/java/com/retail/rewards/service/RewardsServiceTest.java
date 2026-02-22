package com.retail.rewards.service;

import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.dto.TransactionDto;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.exception.ResourceNotFoundException;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RewardsService.
 * Tests business logic for reward calculations and transaction management.
 */
@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @InjectMocks
    private RewardsService rewardsService;
    
    /**
     * Test getting rewards for a valid customer with transactions.
     */
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
        
        CustomerRewardsDto result = rewardsService.getRewardsForCustomer(customerId);
        
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals("Test Customer", result.getCustomerName());
        assertEquals(115, result.getTotalPoints()); // 90 + 25
    }
    
    /**
     * Test getting rewards for non-existent customer.
     * Should throw ResourceNotFoundException.
     */
    @Test
    void testGetRewardsForCustomer_NotFound() {
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            rewardsService.getRewardsForCustomer(customerId);
        });
    }
    
    /**
     * Test getting rewards for customer with no transactions.
     */
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
    
    /**
     * Test getting rewards for all customers.
     */
    @Test
    void testGetRewardsForAllCustomers() {
        LocalDate today = LocalDate.now();
        Customer customer1 = new Customer(1L, "Customer 1");
        Customer customer2 = new Customer(2L, "Customer 2");
        
        List<Transaction> transactions = Arrays.asList(
            new Transaction(1L, 1L, new BigDecimal("120.00"), today.minusDays(10)),
            new Transaction(2L, 2L, new BigDecimal("150.00"), today.minusDays(15))
        );
        
        when(transactionRepository.findByTransactionDateBetween(any(), any())).thenReturn(transactions);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer2));
        
        List<CustomerRewardsDto> results = rewardsService.getRewardsForAllCustomers();
        
        assertNotNull(results);
        assertEquals(2, results.size());
    }
    
    /**
     * Test creating a transaction successfully.
     */
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
    
    /**
     * Test creating a transaction for non-existent customer.
     * Should throw ResourceNotFoundException.
     */
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
