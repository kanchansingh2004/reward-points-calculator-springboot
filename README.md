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
- [Configuration](#configuration)

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
| Database | PostgreSQL | 14+ | Database (Dev/Test/Prod) |
| Build Tool | Maven | 3.6+ | Dependency management |
| Testing | JUnit 5 + Mockito | 5.x | Unit & integration testing |
| Code Reduction | Lombok | Latest | Boilerplate reduction |
| Validation | Bean Validation | 3.0 | Input validation |
| API Documentation | SpringDoc OpenAPI | 2.2.0 | Swagger UI & API docs |

### Application Layers

| Layer | Package | Responsibility | Key Classes |
|-------|---------|---------------|-------------|
| **Presentation** | `controller` | REST endpoints | `CustomerRewardsController`, `TransactionController` |
| **Business Logic** | `service` | Core business rules | `RewardsService` |
| **Data Access** | `repository` | Database operations | `CustomerRepository`, `TransactionRepository` |
| **Domain Model** | `entity` | JPA entities | `Customer`, `Transaction` |
| **Data Transfer** | `dto` | API contracts | `CustomerRewardsDto`, `TransactionDto` |
| **Configuration** | `config` | Application config | `RewardsConfig`, `SwaggerConfig` |
| **Utilities** | `util` | Helper functions | `RewardsCalculator` |
| **Error Handling** | `exception` | Exception management | `GlobalExceptionHandler` |

### Project Structure

```
customerrewardpoints/
│
├── src/main/java/com/retail/rewards/
│   ├── config/
│   │   ├── RewardsConfig.java                  # Configuration properties
│   │   └── SwaggerConfig.java                  # Swagger/OpenAPI configuration
│   ├── controller/
│   │   ├── CustomerRewardsController.java      # Rewards endpoints
│   │   └── TransactionController.java          # Transaction endpoints
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
│   └── CustomerRewardPointsApplication.java    # Main application
│
├── src/main/resources/
│   ├── application.properties                  # Application configuration
│   └── data.sql                                # Sample data initialization
│
├── src/test/java/com/retail/rewards/
│   ├── controller/
│   │   ├── CustomerRewardsControllerTest.java  # Rewards API tests
│   │   └── TransactionControllerTest.java      # Transaction API tests
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
| 3 | Create PostgreSQL databases: `CREATE DATABASE rewards_db;` and `CREATE DATABASE rewards_test_db;` | Setup databases |
| 4 | Update `application.properties` with DB credentials if needed | Configure connection |
| 5 | `mvn clean install` | Build and run tests |
| 6 | `mvn spring-boot:run` | Start the application |

### Application Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| REST API | http://localhost:8080 | N/A |
| Swagger UI | http://localhost:8080/swagger-ui.html | N/A |
| PostgreSQL (Dev) | localhost:5432/rewards_db | Username: `postgres`, Password: `postgres` |
| PostgreSQL (Test) | localhost:5432/rewards_test_db | Username: `postgres`, Password: `postgres` |

---

## API Documentation

### Endpoint Summary

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/rewards/customer/{id}` | Get rewards for specific customer | None | `CustomerRewardsDto` |
| GET | `/api/rewards/customers` | Get rewards for all customers (paginated) | None | `Page<CustomerRewardsDto>` |
| POST | `/api/transactions` | Create new transaction | `TransactionDto` | `TransactionDto` |

### 1️⃣ Get Customer Rewards

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

**cURL Example:**

```bash
curl http://localhost:8080/api/rewards/customer/1
```

---

### 2️ Get All Customers Rewards

**Endpoint:** `GET /api/rewards/customers`

**Description:** Retrieves reward points for all customers with pagination support.

**Query Parameters:**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | Integer | No | 0 | Page number (0-indexed) |
| size | Integer | No | 20 | Number of items per page |
| sort | String | No | - | Sort field and direction (e.g., `customerName,asc`) |

**Success Response (200 OK):**

```json
{
  "content": [
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
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 3,
  "totalPages": 1
}
```

**cURL Examples:**

```bash
# Default pagination
curl http://localhost:8080/api/rewards/customers

# First page with 10 items
curl "http://localhost:8080/api/rewards/customers?page=0&size=10"

# Sorted by customer name
curl "http://localhost:8080/api/rewards/customers?sort=customerName,asc"
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
| `CustomerRewardsControllerTest` | Integration | 5 | Rewards API endpoints |
| `TransactionControllerTest` | Integration | 4 | Transaction API endpoints |
| `RewardsServiceTest` | Unit | 7 | Business logic & service layer |
| `RewardsCalculatorTest` | Unit | 14 | Points calculation logic |
| **TOTAL** | - | **30** | **Complete Coverage** |

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

### Sample Data

The application initializes with sample data via `data.sql`:

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

# Testing
spring.datasource.url=jdbc:postgresql://localhost:5432/rewards_test_db
```

### Swagger/OpenAPI Configuration

API documentation is available via Swagger UI:

| Service | URL | Description |
|---------|-----|-------------|
| Swagger UI | http://localhost:8080/swagger-ui.html | Interactive API documentation |
| OpenAPI JSON | http://localhost:8080/api-docs | OpenAPI 3.0 specification |

**Configuration:**
```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
```

---