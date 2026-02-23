# Customer Rewards Points Calculator

> A RESTful Spring Boot application that calculates reward points for customers based on their purchase transactions over a rolling 3-month period.

---

## Table of Contents

- [Business Requirements](#business-requirements)
- [Technical Architecture](#technical-architecture)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [Testing Strategy](#testing-strategy)
- [Database Schema](#database-schema)
- [Development Guide](#development-guide)

---

## Business Requirements

### Rewards Calculation Rules

| Spending Range | Points Earned | Multiplier |
|---------------|---------------|------------|
| $0 - $50 | 0 points | 0x |
| $50.01 - $100 | 1 point per dollar | 1x |
| Above $100 | 2 points per dollar | 2x |

### Calculation Examples

| Purchase Amount | Calculation Breakdown | Total Points |
|----------------|----------------------|--------------|
| $25.00 | No points (below $50) | **0** |
| $50.00 | Exactly at threshold | **0** |
| $75.00 | ($75 - $50) × 1 = 25 | **25** |
| $100.00 | ($100 - $50) × 1 = 50 | **50** |
| $120.00 | ($20 × 2) + ($50 × 1) = 40 + 50 | **90** |
| $150.00 | ($50 × 2) + ($50 × 1) = 100 + 50 | **150** |
| $200.00 | ($100 × 2) + ($50 × 1) = 200 + 50 | **250** |

---

## Technical Architecture

### Technology Stack

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Language | Java | 17 | Core programming language |
| Framework | Spring Boot | 3.2.0 | Application framework |
| ORM | Spring Data JPA | 3.2.0 | Database abstraction |
| Database (Dev) | PostgreSQL | 14+ | Development database |
| Database (Prod) | PostgreSQL | 14+ | Production database |
| Database (Test) | H2 | In-memory | Testing database |
| Build Tool | Maven | 3.6+ | Dependency management |
| Testing | JUnit 5 + Mockito | 5.x | Unit & integration testing |
| Code Reduction | Lombok | Latest | Boilerplate reduction |
| Validation | Bean Validation | 3.0 | Input validation |

### Application Layers

| Layer | Package | Responsibility | Key Classes                                  |
|-------|---------|---------------|----------------------------------------------|
| **Presentation** | `controller` | REST endpoints, request/response handling | `CustomerRewardsController`                  |
| **Business Logic** | `service` | Core business rules, calculations | `RewardsService`                             |
| **Data Access** | `repository` | Database operations | `CustomerRepository`, `TransactionRepository` |
| **Domain Model** | `entity` | JPA entities | `Customer`, `Transaction`                    |
| **Data Transfer** | `dto` | API contracts | `CustomerRewardsDto`, `TransactionDto`       |
| **Utilities** | `util` | Helper functions | `RewardsCalculator`                          |
| **Error Handling** | `exception` | Exception management | `GlobalExceptionHandler`                     |

### Project Structure

```
customerrewardpoints/
│
├── src/main/java/com/retail/rewards/
│   ├── controller/
│   │   └── CustomerRewardsController.java              # All REST endpoints
│   ├── service/
│   │   └── RewardsService.java                 # Business logic
│   ├── repository/
│   │   ├── CustomerRepository.java             # Customer data access
│   │   └── TransactionRepository.java          # Transaction data access
│   ├── entity/
│   │   ├── Customer.java                       # Customer entity
│   │   └── Transaction.java                    # Transaction entity
│   ├── dto/
│   │   ├── CustomerRewardsDto.java             # Rewards response
│   │   └── TransactionDto.java                 # Transaction request/response
│   ├── util/
│   │   └── RewardsCalculator.java              # Points calculation logic
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java         # Centralized error handling
│   │   └── ResourceNotFoundException.java      # Custom exception
│   ├── DataInitializer.java                    # Sample data loader
│   └── CustomerRewardPointsApplication.java    # Main application
│
├── src/test/java/com/retail/rewards/
│   ├── service/
│   │   └── RewardsServiceTest.java             # Service unit tests
│   └── util/
│       └── RewardsCalculatorTest.java          # Calculator unit tests
│
├── pom.xml                                      # Maven dependencies
├── README.md                                    # This file
└── .gitignore                                   # Git ignore rules
```

---

## Setup Instructions

### Prerequisites

| Requirement | Minimum Version | Check Command |
|------------|----------------|---------------|
| Java JDK | 17+ | `java -version` |
| Maven | 3.6+ | `mvn -version` |
| PostgreSQL | 14+ | `psql --version` |
| Git | 2.x | `git --version` |

### Installation Steps

| Step | Command | Description |
|------|---------|-------------|
| 1 | `git clone <repository-url>` | Clone the repository |
| 2 | `cd customerrewardpoints` | Navigate to project directory |
| 3 | Create PostgreSQL database: `CREATE DATABASE rewards_db;` | Setup database |
| 4 | Update `application.properties` with DB credentials | Configure connection |
| 5 | `mvn clean install` | Build and run tests |
| 6 | `mvn spring-boot:run` | Start the application |

### Application Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| REST API | http://localhost:8080 | N/A |
| PostgreSQL | localhost:5432/rewards_db | Username: `postgres`, Password: `postgres` |

---

## API Documentation

### Endpoint Summary

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/rewards/customer/{id}` | Get rewards for specific customer | None | `CustomerRewardsDto` |
| GET | `/api/rewards/customers` | Get rewards for all customers | None | `List<CustomerRewardsDto>` |
| POST | `/api/transactions` | Create new transaction | `TransactionDto` | `TransactionDto` |

### 1️ Get Customer Rewards

**Endpoint:** `GET /api/rewards/customer/{customerId}`

**Description:** Retrieves reward points for a specific customer over the last 3 months with monthly breakdown.

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| customerId | Long | Yes | Unique customer identifier |

**Success Response (200 OK):**

```json
{
  "customerId": 1,
  "customerName": "Jessica",
  "monthlyPoints": {
    "2024-11": 115,
    "2024-12": 210,
    "2025-01": 250
  },
  "totalPoints": 575
}
```

**Error Responses:**

| Status Code | Scenario | Response Example |
|------------|----------|------------------|
| 404 | Customer not found | `{"status": 404, "error": "Not Found", "message": "Customer not found with ID: 999"}` |
| 500 | Server error | `{"status": 500, "error": "Internal Server Error"}` |

**cURL Example:**

```bash
curl -X GET http://localhost:8080/api/rewards/customer/1
```

---

### 2️ Get All Customers Rewards

**Endpoint:** `GET /api/rewards/customers`

**Description:** Retrieves reward points for all customers in the system.

**Success Response (200 OK):**

```json
[
  {
    "customerId": 1,
    "customerName": "Jessica",
    "monthlyPoints": {
      "2024-12": 340
    },
    "totalPoints": 340
  },
  {
    "customerId": 2,
    "customerName": "Alice",
    "monthlyPoints": {
      "2024-12": 450
    },
    "totalPoints": 450
  }
]
```

**cURL Example:**

```bash
curl -X GET http://localhost:8080/api/rewards/customers
```

---

### 3️ Create Transaction

**Endpoint:** `POST /api/transactions`

**Description:** Creates a new purchase transaction for a customer.

**Request Body:**

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| customerId | Long | Yes | Must exist | Customer identifier |
| amount | BigDecimal | Yes | > 0 | Transaction amount |
| transactionDate | LocalDate | Yes | Valid date | Transaction date (YYYY-MM-DD) |

**Request Example:**

```json
{
  "customerId": 1,
  "amount": 120.00,
  "transactionDate": "2024-12-15"
}
```

**Success Response (201 Created):**

```json
{
  "id": 16,
  "customerId": 1,
  "amount": 120.00,
  "transactionDate": "2024-12-15"
}
```

**Error Responses:**

| Status Code | Scenario | Response Example |
|------------|----------|------------------|
| 400 | Validation error | `{"status": 400, "error": "Validation Failed", "errors": {"amount": "Amount must be positive"}}` |
| 404 | Customer not found | `{"status": 404, "error": "Not Found", "message": "Customer not found with ID: 999"}` |

**cURL Example:**

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "amount": 120.00,
    "transactionDate": "2024-12-15"
  }'
```

---

## Testing Strategy

### Test Coverage Overview

| Test Class | Type | Test Count | Coverage Area |
|------------|------|------------|---------------|
| `RewardsCalculatorTest` | Unit | 14 | Points calculation logic |
| `RewardsServiceTest` | Unit | 6 | Business logic & service layer |

### Test Scenarios Matrix

| Category | Scenario | Expected Outcome | Test Method |
|----------|----------|------------------|-------------|
| **Positive** | Amount = $120 | 90 points | `testCalculatePoints` |
| **Positive** | Amount = $100 | 50 points | `testCalculatePoints` |
| **Positive** | Amount = $75 | 25 points | `testCalculatePoints` |
| **Boundary** | Amount = $50.00 | 0 points | `testCalculatePoints` |
| **Boundary** | Amount = $50.01 | 0 points | `testCalculatePoints` |
| **Boundary** | Amount = $100.01 | 50 points | `testCalculatePoints` |
| **Negative** | Amount = null | 0 points | `testCalculatePointsWithNullAmount` |
| **Negative** | Amount = 0 | 0 points | `testCalculatePointsWithZeroAmount` |
| **Negative** | Amount < 0 | 0 points | `testCalculatePointsWithNegativeAmount` |
| **Exception** | Customer not found | 404 error | `testGetCustomerRewards_NotFound` |
| **Exception** | Invalid transaction | 400 error | `testCreateTransaction_ValidationError` |

### Running Tests

| Command | Purpose | Output |
|---------|---------|--------|
| `mvn test` | Run all tests | Test results in console |
| `mvn test -Dtest=RewardsCalculatorTest` | Run specific test class | Targeted test results |
| `mvn clean test` | Clean and run tests | Fresh test execution |

---

## Database Schema

### Entity Relationship Diagram

```
┌─────────────────┐         ┌──────────────────────┐
│    Customer     │         │     Transaction      │
├─────────────────┤         ├──────────────────────┤
│ id (PK)         │◄────────│ id (PK)              │
│ name            │    1:N  │ customerId (FK)      │
└─────────────────┘         │ amount               │
                            │ transactionDate      │
                            └──────────────────────┘
```

### Table: customers

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique customer identifier |
| name | VARCHAR(255) | NOT NULL | Customer name |

### Table: transactions

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique transaction identifier |
| customer_id | BIGINT | NOT NULL, FOREIGN KEY | Reference to customer |
| amount | DECIMAL(10,2) | NOT NULL | Transaction amount |
| transaction_date | DATE | NOT NULL | Date of transaction |

### Sample Data

The application initializes with the following test data:

| Customer ID | Customer Name | Transaction Count | Date Range |
|------------|---------------|-------------------|------------|
| 1 | Jessica       | 5 | Last 3 months |
| 2 | Alice         | 5 | Last 3 months |
| 3 | Willow        | 5 | Last 3 months |

**Total Transactions:** 15 across 3 customers

---

## Development Guide

### Configuration Properties

| Property | Value | Purpose |
|----------|-------|---------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/rewards_db` | PostgreSQL database connection |
| `spring.datasource.username` | `postgres` | Database username |
| `spring.datasource.password` | `postgres` | Database password |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto-update schema |
| `spring.jpa.show-sql` | `true` | Log SQL queries |

### Key Design Patterns

| Pattern | Implementation | Benefit |
|---------|---------------|---------|
| **DTO Pattern** | `CustomerRewardsDto`, `TransactionDto` | Decouples API from domain model |
| **Repository Pattern** | Spring Data JPA repositories | Abstracts data access |
| **Service Layer** | `RewardsService` | Encapsulates business logic |
| **Exception Handling** | `@RestControllerAdvice` | Centralized error handling |
| **Builder Pattern** | Lombok `@Data`, `@AllArgsConstructor` | Simplifies object creation |

### Dynamic Date Calculation

The application uses **dynamic date calculation** to avoid hardcoded months:

```java
LocalDate endDate = LocalDate.now();           // Current date
LocalDate startDate = endDate.minusMonths(3);  // 3 months ago
```

| Feature | Implementation | Benefit |
|---------|---------------|---------|
| Rolling window | `LocalDate.now().minusMonths(3)` | Always calculates last 3 months |
| Month formatting | `DateTimeFormatter.ofPattern("yyyy-MM")` | Consistent month keys |
| Timezone handling | Uses system default | Adapts to deployment environment |

---

## Notes

- **No Hardcoded Months:** All date calculations are dynamic using `LocalDate.now()`
- **Timezone Aware:** Application uses system timezone for date calculations
- **Decimal Precision:** Uses `BigDecimal` for accurate monetary calculations
- **Idempotent Operations:** GET requests are safe and idempotent
- **RESTful Design:** Follows REST principles with proper HTTP methods and status codes

---
