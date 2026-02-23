package com.retail.rewards.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "rewards")
@Getter
@Setter
public class RewardsConfig {
    
    private BigDecimal tierOneThreshold = new BigDecimal("50");
    private BigDecimal tierTwoThreshold = new BigDecimal("100");
    private int tierOneMultiplier = 1;
    private int tierTwoMultiplier = 2;
    private int calculationMonths = 3;
    private String monthFormat = "yyyy-MM";
}
