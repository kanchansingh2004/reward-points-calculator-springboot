package com.charter.retail.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * DTO for customer rewards information.
 * Contains customer details along with monthly and total reward points.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRewardsDto {
    
    private Long customerId;
    private String customerName;
    private Map<String, Integer> monthlyPoints;
    private Integer totalPoints;
}
