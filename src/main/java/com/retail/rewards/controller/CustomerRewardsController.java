package com.retail.rewards.controller;

import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.service.RewardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class CustomerRewardsController {
    
    private final RewardsService rewardsService;

    /**
     * Retrieves reward points for a specific customer.
     * Calculates points earned over the last 3 months with monthly breakdown.
     **/
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerRewardsDto> getCustomerRewards(@PathVariable Long customerId) {
        return ResponseEntity.ok(rewardsService.getRewardsForCustomer(customerId));
    }
    
    /**
     * Retrieves reward points for all customers with pagination support.
     **/
    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerRewardsDto>> getAllCustomersRewards(Pageable pageable) {
        return ResponseEntity.ok(rewardsService.getRewardsForAllCustomers(pageable));
    }
}
