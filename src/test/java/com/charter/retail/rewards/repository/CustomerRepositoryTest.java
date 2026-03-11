package com.charter.retail.rewards.repository;

import com.charter.retail.rewards.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldSaveAndFindCustomer() {
        Customer customer = new Customer(null, "John Doe", new ArrayList<>());
        Customer saved = customerRepository.save(customer);
        
        Optional<Customer> found = customerRepository.findById(saved.getId());
        
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        Optional<Customer> found = customerRepository.findById(999L);
        
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllCustomers() {
        customerRepository.save(new Customer(null, "Alice", new ArrayList<>()));
        customerRepository.save(new Customer(null, "Bob", new ArrayList<>()));
        
        List<Customer> customers = customerRepository.findAll();
        
        assertThat(customers).hasSizeGreaterThanOrEqualTo(2);
    }
}
