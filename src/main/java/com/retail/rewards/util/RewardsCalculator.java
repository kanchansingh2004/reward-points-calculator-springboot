package com.retail.rewards.util;

import java.math.BigDecimal;

/**
 * Utility class for calculating reward points based on transaction amounts.
 * 
 * Calculation rules:
 * - 2 points for every dollar spent over $100
 * - 1 point for every dollar spent between $50 and $100
 * - 0 points for amounts $50 or below
 */
public class RewardsCalculator {
    
    private static final BigDecimal TIER_ONE_THRESHOLD = new BigDecimal("50");
    private static final BigDecimal TIER_TWO_THRESHOLD = new BigDecimal("100");
    private static final int TIER_ONE_MULTIPLIER = 1;
    private static final int TIER_TWO_MULTIPLIER = 2;
    
    /**
     * Calculate reward points for a given transaction amount.
     * 
     * @param amount the transaction amount
     * @return the calculated reward points
     */
    public static int calculatePoints(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        
        int points = 0;
        
        // Calculate points for amount over $100 (2 points per dollar)
        if (amount.compareTo(TIER_TWO_THRESHOLD) > 0) {
            BigDecimal amountOverHundred = amount.subtract(TIER_TWO_THRESHOLD);
            points += amountOverHundred.intValue() * TIER_TWO_MULTIPLIER;
        }
        
        // Calculate points for amount between $50 and $100 (1 point per dollar)
        if (amount.compareTo(TIER_ONE_THRESHOLD) > 0) {
            BigDecimal amountForTierOne = amount.min(TIER_TWO_THRESHOLD).subtract(TIER_ONE_THRESHOLD);
            points += amountForTierOne.intValue() * TIER_ONE_MULTIPLIER;
        }
        
        return points;
    }
}
