package com.retail.rewards.util;

import com.retail.rewards.config.RewardsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class RewardsCalculator {
    
    private final RewardsConfig config;
    
    /**
     * Calculates reward points for a given transaction amount.
     * 
     * @param amount the transaction amount
     * @return calculated reward points, or 0 if amount is null or non-positive
     */
    public int calculatePoints(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        
        int points = 0;
        
        if (amount.compareTo(config.getTierTwoThreshold()) > 0) {
            BigDecimal amountOverHundred = amount.subtract(config.getTierTwoThreshold());
            points += amountOverHundred.intValue() * config.getTierTwoMultiplier();
        }
        
        if (amount.compareTo(config.getTierOneThreshold()) > 0) {
            BigDecimal amountForTierOne = amount.min(config.getTierTwoThreshold()).subtract(config.getTierOneThreshold());
            points += amountForTierOne.intValue() * config.getTierOneMultiplier();
        }
        
        return points;
    }
}
