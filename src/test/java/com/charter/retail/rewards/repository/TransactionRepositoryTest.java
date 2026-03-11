package com.charter.retail.rewards.repository;

import com.charter.retail.rewards.entity.Customer;
import com.charter.retail.rewards.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    private Customer testCustomer1;
    private Customer testCustomer2;

    @BeforeEach
    void setUp() {
        testCustomer1 = customerRepository.save(new Customer(null, "Test Customer 1", new ArrayList<>()));
        testCustomer2 = customerRepository.save(new Customer(null, "Test Customer 2", new ArrayList<>()));
    }

    @Test
    void shouldSaveAndFindTransaction() {
        Transaction transaction = new Transaction(null, testCustomer1, new BigDecimal("120.00"), LocalDate.now());
        Transaction saved = transactionRepository.save(transaction);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAmount()).isEqualByComparingTo(new BigDecimal("120.00"));
    }

    @Test
    void shouldFindTransactionsByCustomerIdAndDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        
        transactionRepository.save(new Transaction(null, testCustomer1, new BigDecimal("100.00"), LocalDate.now().minusDays(10)));
        transactionRepository.save(new Transaction(null, testCustomer1, new BigDecimal("150.00"), LocalDate.now().minusDays(20)));
        transactionRepository.save(new Transaction(null, testCustomer2, new BigDecimal("200.00"), LocalDate.now().minusDays(15)));
        
        List<Transaction> transactions = transactionRepository.findByCustomerIdAndTransactionDateBetween(testCustomer1.getId(), startDate, endDate);
        
        assertThat(transactions).hasSize(2);
        assertThat(transactions).allMatch(t -> t.getCustomerId().equals(testCustomer1.getId()));
    }

    @Test
    void shouldFindTransactionsByDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        
        transactionRepository.save(new Transaction(null, testCustomer1, new BigDecimal("100.00"), LocalDate.now().minusDays(10)));
        transactionRepository.save(new Transaction(null, testCustomer2, new BigDecimal("150.00"), LocalDate.now().minusDays(50)));
        
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        
        assertThat(transactions).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsInDateRange() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(30);
        
        List<Transaction> transactions = transactionRepository.findByCustomerIdAndTransactionDateBetween(testCustomer1.getId(), startDate, endDate);
        
        assertThat(transactions).isEmpty();
    }
}
