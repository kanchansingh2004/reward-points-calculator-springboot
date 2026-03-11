package com.charter.retail.rewards.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for reward calculation.
 * Binds application.properties values with 'rewards' prefix to this class.
 */
@Configuration
@ConfigurationProperties(prefix = "rewards")
@Data
public class RewardsConfig {
    
    private int tierOneThreshold;
    private int tierTwoThreshold;
    private int tierOneMultiplier;
    private int tierTwoMultiplier;
    private int calculationMonths;
    private String monthFormat;
}
