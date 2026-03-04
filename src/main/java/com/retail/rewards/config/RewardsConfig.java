package com.retail.rewards.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rewards")
@Data
public class RewardsConfig {
    
    // Threshold for tier one rewards ($50)
    private int tierOneThreshold;
    
    // Threshold for tier two rewards ($100)
    private int tierTwoThreshold;
    
    // Multiplier for tier one (1 point per dollar)
    private int tierOneMultiplier;
    
    // Multiplier for tier two (2 points per dollar)
    private int tierTwoMultiplier;
    
    // Number of months for calculation (3 months)
    private int calculationMonths;
    
    // Format for month display (yyyy-MM)
    private String monthFormat;
}
