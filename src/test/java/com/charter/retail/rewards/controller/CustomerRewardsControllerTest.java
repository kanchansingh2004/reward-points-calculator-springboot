package com.charter.retail.rewards.controller;

import com.charter.retail.rewards.dto.CustomerRewardsDto;
import com.charter.retail.rewards.exception.ResourceNotFoundException;
import com.charter.retail.rewards.service.RewardsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRewardsControllerTest {
    
    @Mock
    private RewardsService rewardsService;
    
    @InjectMocks
    private CustomerRewardsController controller;
    
    @Test
    void testGetCustomerRewards_Success() {
        Long customerId = 1L;
        Map<String, Integer> monthlyPoints = new HashMap<>();
        monthlyPoints.put("2024-12", 90);
        CustomerRewardsDto rewardsDto = new CustomerRewardsDto(customerId, "Test Customer", monthlyPoints, 90);
        
        when(rewardsService.getRewardsForCustomer(customerId)).thenReturn(rewardsDto);
        
        ResponseEntity<CustomerRewardsDto> response = controller.getCustomerRewards(customerId);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(customerId, response.getBody().getCustomerId());
        assertEquals("Test Customer", response.getBody().getCustomerName());
        assertEquals(90, response.getBody().getTotalPoints());
    }
    
    @Test
    void testGetCustomerRewards_NotFound() {
        Long customerId = 999L;
        
        when(rewardsService.getRewardsForCustomer(customerId))
            .thenThrow(new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        assertThrows(ResourceNotFoundException.class, () -> {
            controller.getCustomerRewards(customerId);
        });
    }
    
    @Test
    void testGetCustomerRewards_WithMultipleMonths() {
        Long customerId = 1L;
        Map<String, Integer> monthlyPoints = new HashMap<>();
        monthlyPoints.put("2024-10", 50);
        monthlyPoints.put("2024-11", 90);
        monthlyPoints.put("2024-12", 150);
        CustomerRewardsDto rewardsDto = new CustomerRewardsDto(customerId, "Test Customer", monthlyPoints, 290);
        
        when(rewardsService.getRewardsForCustomer(customerId)).thenReturn(rewardsDto);
        
        ResponseEntity<CustomerRewardsDto> response = controller.getCustomerRewards(customerId);
        
        assertNotNull(response);
        assertEquals(290, response.getBody().getTotalPoints());
        assertEquals(3, response.getBody().getMonthlyPoints().size());
    }
    
    @Test
    void testGetAllCustomersRewards_Success() {
        Map<String, Integer> monthlyPoints1 = new HashMap<>();
        monthlyPoints1.put("2024-12", 90);
        Map<String, Integer> monthlyPoints2 = new HashMap<>();
        monthlyPoints2.put("2024-12", 150);
        
        CustomerRewardsDto rewards1 = new CustomerRewardsDto(1L, "Customer 1", monthlyPoints1, 90);
        CustomerRewardsDto rewards2 = new CustomerRewardsDto(2L, "Customer 2", monthlyPoints2, 150);
        
        Pageable pageable = PageRequest.of(0, 20);
        Page<CustomerRewardsDto> page = new PageImpl<>(Arrays.asList(rewards1, rewards2), pageable, 2);
        
        when(rewardsService.getRewardsForAllCustomers(pageable)).thenReturn(page);
        
        ResponseEntity<Page<CustomerRewardsDto>> response = controller.getAllCustomersRewards(pageable);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals(2, response.getBody().getTotalElements());
    }
    
    @Test
    void testGetAllCustomersRewards_EmptyList() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<CustomerRewardsDto> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        
        when(rewardsService.getRewardsForAllCustomers(pageable)).thenReturn(emptyPage);
        
        ResponseEntity<Page<CustomerRewardsDto>> response = controller.getAllCustomersRewards(pageable);
        
        assertNotNull(response);
        assertEquals(0, response.getBody().getContent().size());
        assertEquals(0, response.getBody().getTotalElements());
    }
}
