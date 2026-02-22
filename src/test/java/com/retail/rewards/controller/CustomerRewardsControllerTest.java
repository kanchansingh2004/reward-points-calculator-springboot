package com.retail.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.dto.TransactionDto;
import com.retail.rewards.exception.ResourceNotFoundException;
import com.retail.rewards.service.RewardsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CustomerRewardsController.
 * Tests REST API endpoints for rewards and transaction functionality.
 */
@WebMvcTest(CustomerRewardsController.class)
class CustomerRewardsControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private RewardsService rewardsService;
    
    /**
     * Test GET endpoint for customer rewards - success scenario.
     */
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
    
    /**
     * Test GET endpoint for customer rewards - customer not found.
     */
    @Test
    void testGetCustomerRewards_NotFound() throws Exception {
        Long customerId = 999L;
        
        when(rewardsService.getRewardsForCustomer(customerId))
            .thenThrow(new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        mockMvc.perform(get("/api/rewards/customer/{customerId}", customerId))
            .andExpect(status().isNotFound());
    }
    
    /**
     * Test GET endpoint for all customers rewards.
     */
    @Test
    void testGetAllCustomersRewards() throws Exception {
        Map<String, Integer> monthlyPoints1 = new HashMap<>();
        monthlyPoints1.put("2024-12", 90);
        
        Map<String, Integer> monthlyPoints2 = new HashMap<>();
        monthlyPoints2.put("2024-12", 150);
        
        CustomerRewardsDto rewards1 = new CustomerRewardsDto(1L, "Customer 1", monthlyPoints1, 90);
        CustomerRewardsDto rewards2 = new CustomerRewardsDto(2L, "Customer 2", monthlyPoints2, 150);
        
        when(rewardsService.getRewardsForAllCustomers()).thenReturn(Arrays.asList(rewards1, rewards2));
        
        mockMvc.perform(get("/api/rewards/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].customerId").value(1))
            .andExpect(jsonPath("$[1].customerId").value(2));
    }
    
    /**
     * Test POST endpoint for creating transaction - success scenario.
     */
    @Test
    void testCreateTransaction_Success() throws Exception {
        TransactionDto inputDto = new TransactionDto(null, 1L, new BigDecimal("120.00"), LocalDate.now());
        TransactionDto outputDto = new TransactionDto(1L, 1L, new BigDecimal("120.00"), LocalDate.now());
        
        when(rewardsService.createTransaction(any(TransactionDto.class))).thenReturn(outputDto);
        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.customerId").value(1))
            .andExpect(jsonPath("$.amount").value(120.00));
    }
    
    /**
     * Test POST endpoint with invalid data - missing required fields.
     */
    @Test
    void testCreateTransaction_ValidationError() throws Exception {
        TransactionDto invalidDto = new TransactionDto(null, null, null, null);
        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }
    
    /**
     * Test POST endpoint with negative amount.
     */
    @Test
    void testCreateTransaction_NegativeAmount() throws Exception {
        TransactionDto invalidDto = new TransactionDto(null, 1L, new BigDecimal("-50.00"), LocalDate.now());
        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }
    
    /**
     * Test POST endpoint with non-existent customer.
     */
    @Test
    void testCreateTransaction_CustomerNotFound() throws Exception {
        TransactionDto inputDto = new TransactionDto(null, 999L, new BigDecimal("120.00"), LocalDate.now());
        
        when(rewardsService.createTransaction(any(TransactionDto.class)))
            .thenThrow(new ResourceNotFoundException("Customer not found with ID: 999"));
        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
            .andExpect(status().isNotFound());
    }
}
