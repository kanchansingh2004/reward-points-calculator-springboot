package com.retail.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * Data Transfer Object for customer rewards information.
 * Contains monthly breakdown and total points for a customer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRewardsDto {
    
    // Customer ID
    private Long customerId;
    
    // Customer name
    private String customerName;
    
    // Map of month (YYYY-MM format) to points earned in that month
    private Map<String, Integer> monthlyPoints;
    
    // Total points earned across all months
    private Integer totalPoints;
}
