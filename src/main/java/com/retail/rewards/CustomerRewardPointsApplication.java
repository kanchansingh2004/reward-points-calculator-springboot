package com.retail.rewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Customer Rewards Points API.
 * This application calculates reward points for customers based on their purchase transactions.
 * 
 * Reward calculation rules:
 * - 2 points for every dollar spent over $100
 * - 1 point for every dollar spent between $50 and $100
 * - 0 points for amounts $50 or below
 */
@SpringBootApplication
public class CustomerRewardPointsApplication {
    
    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CustomerRewardPointsApplication.class, args);
    }
}
