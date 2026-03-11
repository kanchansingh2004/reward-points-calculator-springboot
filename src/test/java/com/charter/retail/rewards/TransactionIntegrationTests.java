package com.charter.retail.rewards;

import com.charter.retail.rewards.controller.TransactionController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.charter.retail.rewards.config.RewardsConfig;
import com.charter.retail.rewards.dto.TransactionDto;
import com.charter.retail.rewards.exception.ResourceNotFoundException;
import com.charter.retail.rewards.repository.CustomerRepository;
import com.charter.retail.rewards.repository.TransactionRepository;
import com.charter.retail.rewards.service.RewardsService;
import com.charter.retail.rewards.util.RewardsCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for TransactionController.
 */
@WebMvcTest(TransactionController.class)
class TransactionIntegrationTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
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
    
    @Test
    void testCreateTransaction_ValidationError_MissingFields() throws Exception {
        TransactionDto invalidDto = new TransactionDto(null, null, null, null);
        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void testCreateTransaction_ValidationError_NegativeAmount() throws Exception {
        TransactionDto invalidDto = new TransactionDto(null, 1L, new BigDecimal("-50.00"), LocalDate.now());
        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }
    
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
