package com.charter.retail.rewards.controller;

import com.charter.retail.rewards.dto.CustomerRewardsDto;
import com.charter.retail.rewards.service.RewardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing customer reward points.
 * Provides endpoints to retrieve reward calculations for individual and all customers.
 */
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class CustomerRewardsController {
    
    private final RewardsService rewardsService;

    /**
     * Retrieves reward points for a specific customer over the last 3 months.
     *
     * @param customerId the ID of the customer
     * @return customer rewards with monthly breakdown and total points
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerRewardsDto> getCustomerRewards(@PathVariable Long customerId) {
        return ResponseEntity.ok(rewardsService.getRewardsForCustomer(customerId));
    }
    
    /**
     * Retrieves reward points for all customers with pagination support.
     *
     * @param pageable pagination and sorting parameters
     * @return paginated list of customer rewards
     */
    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerRewardsDto>> getAllCustomersRewards(Pageable pageable) {
        return ResponseEntity.ok(rewardsService.getRewardsForAllCustomers(pageable));
    }
}
