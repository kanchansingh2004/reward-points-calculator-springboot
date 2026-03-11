package com.charter.retail.rewards;

import com.charter.retail.rewards.config.RewardsConfig;
import com.charter.retail.rewards.controller.CustomerRewardsController;
import com.charter.retail.rewards.dto.CustomerRewardsDto;
import com.charter.retail.rewards.exception.ResourceNotFoundException;
import com.charter.retail.rewards.repository.CustomerRepository;
import com.charter.retail.rewards.repository.TransactionRepository;
import com.charter.retail.rewards.service.RewardsService;
import com.charter.retail.rewards.util.RewardsCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for CustomerRewardsController.
 */
@WebMvcTest(CustomerRewardsController.class)
class CustomerRewardsIntegrationTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private RewardsService rewardsService;
    
    @MockBean
    private RewardsConfig rewardsConfig;
    
    @MockBean
    private RewardsCalculator rewardsCalculator;
    
    @MockBean
    private CustomerRepository customerRepository;
    
    @MockBean
    private TransactionRepository transactionRepository;
    
    @Test
    void testGetCustomerRewards_Success() throws Exception {
        Long customerId = 1L;
        Map<String, Integer> monthlyPoints = new HashMap<>();
        monthlyPoints.put("2024-12", 90);
        
        CustomerRewardsDto rewardsDto = new CustomerRewardsDto(customerId, "Test Customer", monthlyPoints, 90);
        
        when(rewardsService.getRewardsForCustomer(customerId)).thenReturn(rewardsDto);
        
        mockMvc.perform(get("/api/rewards/customer/{customerId}", customerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(customerId))
            .andExpect(jsonPath("$.customerName").value("Test Customer"))
            .andExpect(jsonPath("$.totalPoints").value(90));
    }
    
    @Test
    void testGetCustomerRewards_NotFound() throws Exception {
        Long customerId = 999L;
        
        when(rewardsService.getRewardsForCustomer(customerId))
            .thenThrow(new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        mockMvc.perform(get("/api/rewards/customer/{customerId}", customerId))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetCustomerRewards_WithMultipleMonths() throws Exception {
        Long customerId = 1L;
        Map<String, Integer> monthlyPoints = new HashMap<>();
        monthlyPoints.put("2024-10", 50);
        monthlyPoints.put("2024-11", 90);
        monthlyPoints.put("2024-12", 150);
        
        CustomerRewardsDto rewardsDto = new CustomerRewardsDto(customerId, "Test Customer", monthlyPoints, 290);
        
        when(rewardsService.getRewardsForCustomer(customerId)).thenReturn(rewardsDto);
        
        mockMvc.perform(get("/api/rewards/customer/{customerId}", customerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPoints").value(290))
            .andExpect(jsonPath("$.monthlyPoints").isMap());
    }
    
    @Test
    void testGetAllCustomersRewards_Success() throws Exception {
        Map<String, Integer> monthlyPoints1 = new HashMap<>();
        monthlyPoints1.put("2024-12", 90);
        
        Map<String, Integer> monthlyPoints2 = new HashMap<>();
        monthlyPoints2.put("2024-12", 150);
        
        CustomerRewardsDto rewards1 = new CustomerRewardsDto(1L, "Customer 1", monthlyPoints1, 90);
        CustomerRewardsDto rewards2 = new CustomerRewardsDto(2L, "Customer 2", monthlyPoints2, 150);
        
        List<CustomerRewardsDto> rewardsList = Arrays.asList(rewards1, rewards2);
        Pageable pageable = PageRequest.of(0, 20);
        Page<CustomerRewardsDto> page = new PageImpl<>(rewardsList, pageable, rewardsList.size());
        
        when(rewardsService.getRewardsForAllCustomers(org.mockito.ArgumentMatchers.any(Pageable.class))).thenReturn(page);
        
        mockMvc.perform(get("/api/rewards/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].customerId").value(1))
            .andExpect(jsonPath("$.content[1].customerId").value(2))
            .andExpect(jsonPath("$.content[0].totalPoints").value(90))
            .andExpect(jsonPath("$.content[1].totalPoints").value(150));
    }
    
    @Test
    void testGetAllCustomersRewards_EmptyList() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CustomerRewardsDto> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        
        when(rewardsService.getRewardsForAllCustomers(org.mockito.ArgumentMatchers.any(Pageable.class))).thenReturn(emptyPage);
        
        mockMvc.perform(get("/api/rewards/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty());
    }
}
