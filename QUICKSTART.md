# Quick Start Guide

## Build and Run

```bash
# Using Maven
mvn clean install
mvn spring-boot:run

# Using Maven Wrapper (if Maven not installed)
./mvnw clean install
./mvnw spring-boot:run

# On Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

## Test the Application

```bash
# Run all tests
mvn test

# Or with Maven wrapper
./mvnw test
mvnw.cmd test
```

## Access the Application

- **API Base URL**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

## Quick API Tests

```bash
# Get rewards for customer 1
curl http://localhost:8080/api/rewards/customer/1

# Get rewards for all customers
curl http://localhost:8080/api/rewards/customers

# Create a new transaction
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"amount":120.00,"transactionDate":"2024-12-15"}'
```

## Sample Data

The application loads 3 customers on startup:
1. Kanchan (ID: 1)
2. Priya (ID: 2)
3. Shivam (ID: 3)

Each customer has 5 transactions spread across the last 3 months.
