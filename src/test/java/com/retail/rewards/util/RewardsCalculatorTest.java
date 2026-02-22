package com.retail.rewards.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RewardsCalculator utility class.
 * Tests various scenarios for reward points calculation.
 */
class RewardsCalculatorTest {
    
    /**
     * Test reward calculation with parameterized inputs.
     * Covers various transaction amounts and expected points.
     */
    @ParameterizedTest
    @CsvSource({
        "120.00, 90",      // (120-100)*2 + (100-50)*1 = 40 + 50 = 90
        "100.00, 50",      // (100-50)*1 = 50
        "75.00, 25",       // (75-50)*1 = 25
        "50.00, 0",        // No points at threshold
        "49.99, 0",        // Below threshold
        "150.00, 150",     // (150-100)*2 + (100-50)*1 = 100 + 50 = 150
        "200.00, 250",     // (200-100)*2 + (100-50)*1 = 200 + 50 = 250
        "50.01, 0",        // Just above threshold
        "100.01, 50",      // Just above second threshold
        "25.00, 0"         // Well below threshold
    })
    void testCalculatePoints(String amount, int expectedPoints) {
        BigDecimal transactionAmount = new BigDecimal(amount);
        int actualPoints = RewardsCalculator.calculatePoints(transactionAmount);
        assertEquals(expectedPoints, actualPoints);
    }
    
    /**
     * Test calculation with null amount.
     * Should return 0 points.
     */
    @Test
    void testCalculatePointsWithNullAmount() {
        int points = RewardsCalculator.calculatePoints(null);
        assertEquals(0, points);
    }
    
    /**
     * Test calculation with zero amount.
     * Should return 0 points.
     */
    @Test
    void testCalculatePointsWithZeroAmount() {
        int points = RewardsCalculator.calculatePoints(BigDecimal.ZERO);
        assertEquals(0, points);
    }
    
    /**
     * Test calculation with negative amount.
     * Should return 0 points.
     */
    @Test
    void testCalculatePointsWithNegativeAmount() {
        int points = RewardsCalculator.calculatePoints(new BigDecimal("-10.00"));
        assertEquals(0, points);
    }
}
