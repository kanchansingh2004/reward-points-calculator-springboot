package com.charter.retail.rewards.util;

import com.charter.retail.rewards.config.RewardsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Utility class for calculating customer reward points based on transaction amounts.
 * Implements the tiered reward calculation logic.
 */
@Component
@RequiredArgsConstructor
public class RewardsCalculator {
    
    private final RewardsConfig config;
    
    /**
     * Calculates reward points based on transaction amount using a tiered system.
     * Points are awarded in tiers:
     * - 2x points for every dollar above the tier two threshold
     * - 1x point for every dollar between tier one and tier two thresholds
     * - No points for amounts at or below tier one threshold
     */
    public int calculatePoints(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        
        int points = 0;
        
        // Calculate points for amount over tier two threshold ($100)
        if (amount.compareTo(BigDecimal.valueOf(config.getTierTwoThreshold())) > 0) {
            BigDecimal amountOverHundred = amount.subtract(BigDecimal.valueOf(config.getTierTwoThreshold()));
            points += amountOverHundred.intValue() * config.getTierTwoMultiplier();
        }
        
        // Calculate points for amount between tier one and tier two ($50-$100)
        if (amount.compareTo(BigDecimal.valueOf(config.getTierOneThreshold())) > 0) {
            BigDecimal amountForTierOne = amount.min(BigDecimal.valueOf(config.getTierTwoThreshold())).subtract(BigDecimal.valueOf(config.getTierOneThreshold()));
            points += amountForTierOne.intValue() * config.getTierOneMultiplier();
        }
        
        return points;
    }
}
