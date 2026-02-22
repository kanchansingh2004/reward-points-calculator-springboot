package com.retail.rewards;

import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Component to initialize sample data on application startup.
 * Creates customers and transactions for demonstration purposes.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * Load sample data into the database.
     * Creates 3 customers with multiple transactions across 3 months.
     */
    @Override
    public void run(String... args) {
        // Create customers
        Customer customer1 = new Customer(null, "Kanchan");
        Customer customer2 = new Customer(null, "Abhay");
        Customer customer3 = new Customer(null, "Shivam");
        
        customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
        
        // Get current date for dynamic month calculation
        LocalDate today = LocalDate.now();
        
        // Create transactions for customer 1 (Kanchan)
        transactionRepository.save(new Transaction(null, customer1.getId(), new BigDecimal("120.00"), today.minusMonths(2).minusDays(5)));
        transactionRepository.save(new Transaction(null, customer1.getId(), new BigDecimal("75.50"), today.minusMonths(2).minusDays(10)));
        transactionRepository.save(new Transaction(null, customer1.getId(), new BigDecimal("150.00"), today.minusMonths(1).minusDays(3)));
        transactionRepository.save(new Transaction(null, customer1.getId(), new BigDecimal("45.00"), today.minusMonths(1).minusDays(15)));
        transactionRepository.save(new Transaction(null, customer1.getId(), new BigDecimal("200.00"), today.minusDays(5)));
        
        // Create transactions for customer 2 (Priya)
        transactionRepository.save(new Transaction(null, customer2.getId(), new BigDecimal("89.99"), today.minusMonths(2).minusDays(7)));
        transactionRepository.save(new Transaction(null, customer2.getId(), new BigDecimal("110.00"), today.minusMonths(2).minusDays(20)));
        transactionRepository.save(new Transaction(null, customer2.getId(), new BigDecimal("50.00"), today.minusMonths(1).minusDays(8)));
        transactionRepository.save(new Transaction(null, customer2.getId(), new BigDecimal("175.25"), today.minusMonths(1).minusDays(18)));
        transactionRepository.save(new Transaction(null, customer2.getId(), new BigDecimal("95.00"), today.minusDays(3)));
        
        // Create transactions for customer 3 (Shivam)
        transactionRepository.save(new Transaction(null, customer3.getId(), new BigDecimal("250.00"), today.minusMonths(2).minusDays(12)));
        transactionRepository.save(new Transaction(null, customer3.getId(), new BigDecimal("30.00"), today.minusMonths(2).minusDays(25)));
        transactionRepository.save(new Transaction(null, customer3.getId(), new BigDecimal("125.75"), today.minusMonths(1).minusDays(6)));
        transactionRepository.save(new Transaction(null, customer3.getId(), new BigDecimal("60.00"), today.minusMonths(1).minusDays(22)));
        transactionRepository.save(new Transaction(null, customer3.getId(), new BigDecimal("180.50"), today.minusDays(10)));
        
        System.out.println("Sample data initialized successfully!");
    }
}
