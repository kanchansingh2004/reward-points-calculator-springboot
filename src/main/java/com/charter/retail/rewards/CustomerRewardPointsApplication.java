package com.charter.retail.rewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application for the Customer Rewards Points Calculator.
 * Manages customer transactions and calculates reward points based on purchase amounts.
 */
@SpringBootApplication
public class CustomerRewardPointsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerRewardPointsApplication.class, args);
    }
}
