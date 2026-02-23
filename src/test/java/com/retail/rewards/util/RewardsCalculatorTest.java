package com.retail.rewards.util;

import com.retail.rewards.config.RewardsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class RewardsCalculatorTest {
    
    private RewardsCalculator calculator;
    
    @BeforeEach
    void setUp() {
        RewardsConfig config = new RewardsConfig();
        calculator = new RewardsCalculator(config);
    }
    
    @ParameterizedTest
    @CsvSource({
        "120.00, 90",
        "100.00, 50",
        "75.00, 25",
        "50.00, 0",
        "49.99, 0",
        "150.00, 150",
        "200.00, 250",
        "50.01, 0",
        "100.01, 50",
        "25.00, 0"
    })
    void testCalculatePoints(String amount, int expectedPoints) {
        BigDecimal transactionAmount = new BigDecimal(amount);
        int actualPoints = calculator.calculatePoints(transactionAmount);
        assertEquals(expectedPoints, actualPoints);
    }
    
    @Test
    void testCalculatePointsWithNullAmount() {
        int points = calculator.calculatePoints(null);
        assertEquals(0, points);
    }
    
    @Test
    void testCalculatePointsWithZeroAmount() {
        int points = calculator.calculatePoints(BigDecimal.ZERO);
        assertEquals(0, points);
    }
    
    @Test
    void testCalculatePointsWithNegativeAmount() {
        int points = calculator.calculatePoints(new BigDecimal("-10.00"));
        assertEquals(0, points);
    }
}
