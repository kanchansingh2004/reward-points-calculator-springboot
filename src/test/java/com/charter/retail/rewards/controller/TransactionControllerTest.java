package com.charter.retail.rewards.controller;

import com.charter.retail.rewards.dto.TransactionDto;
import com.charter.retail.rewards.exception.ResourceNotFoundException;
import com.charter.retail.rewards.service.RewardsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    
    @Mock
    private RewardsService rewardsService;
    
    @InjectMocks
    private TransactionController controller;
    
    @Test
    void testCreateTransaction_Success() {
        TransactionDto inputDto = new TransactionDto(null, 1L, new BigDecimal("120.00"), LocalDate.now());
        TransactionDto savedDto = new TransactionDto(1L, 1L, new BigDecimal("120.00"), LocalDate.now());
        
        when(rewardsService.createTransaction(inputDto)).thenReturn(savedDto);
        
        ResponseEntity<TransactionDto> response = controller.createTransaction(inputDto);
        
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        assertEquals(1L, response.getBody().getCustomerId());
        assertEquals(new BigDecimal("120.00"), response.getBody().getAmount());
    }
    
    @Test
    void testCreateTransaction_CustomerNotFound() {
        TransactionDto inputDto = new TransactionDto(null, 999L, new BigDecimal("120.00"), LocalDate.now());
        
        when(rewardsService.createTransaction(inputDto))
            .thenThrow(new ResourceNotFoundException("Customer not found with ID: 999"));
        
        assertThrows(ResourceNotFoundException.class, () -> {
            controller.createTransaction(inputDto);
        });
    }
    
    @Test
    void testCreateTransaction_WithDifferentAmounts() {
        TransactionDto inputDto = new TransactionDto(null, 1L, new BigDecimal("75.50"), LocalDate.now());
        TransactionDto savedDto = new TransactionDto(2L, 1L, new BigDecimal("75.50"), LocalDate.now());
        
        when(rewardsService.createTransaction(inputDto)).thenReturn(savedDto);
        
        ResponseEntity<TransactionDto> response = controller.createTransaction(inputDto);
        
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(new BigDecimal("75.50"), response.getBody().getAmount());
    }
    
    @Test
    void testCreateTransaction_WithPastDate() {
        LocalDate pastDate = LocalDate.now().minusDays(30);
        TransactionDto inputDto = new TransactionDto(null, 1L, new BigDecimal("200.00"), pastDate);
        TransactionDto savedDto = new TransactionDto(3L, 1L, new BigDecimal("200.00"), pastDate);
        
        when(rewardsService.createTransaction(inputDto)).thenReturn(savedDto);
        
        ResponseEntity<TransactionDto> response = controller.createTransaction(inputDto);
        
        assertNotNull(response);
        assertEquals(pastDate, response.getBody().getTransactionDate());
    }
}
