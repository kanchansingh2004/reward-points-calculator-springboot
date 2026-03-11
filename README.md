# Customer Rewards Points Calculator

> A Spring Boot REST API that calculates reward points for customers based on their purchase transactions over the last three months.

## Table of Contents

- [Overview](#overview)
- [Business Requirements](#business-requirements)
- [Technology Stack](#technology-stack)
- [Application Architecture](#application-architecture)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Database Schema](#database-schema)
- [Configuration](#configuration)
- [Project Highlights](#project-highlights)

---

## Overview

The application exposes endpoints to:

**Record customer transactions** - Create new purchase records
**Calculate reward points** - Automatic calculation based on spending tiers
**View reward summaries** - Monthly breakdown and total points per customer
**Pagination support** - Efficiently retrieve large datasets

---

## Business Requirements

### Reward Calculation Rules

Reward points are calculated based on the following rules:

| Spending Range | Points Earned |
|----------------|---------------|
| $0 – $50 | 0 points |
| $50.01 – $100 | 1 point for every dollar over $50 |
| Above $100 | 2 points for every dollar over $100 + 1 point for every dollar between $50 and $100 |

### Example Calculations

| Amount | Calculation | Points |
|--------|-------------|--------|
| $25 | No points | **0** |
| $75 | (75 − 50) × 1 | **25** |
| $100 | (100 − 50) × 1 | **50** |
| $120 | (20 × 2) + (50 × 1) | **90** |
| $150 | (50 × 2) + (50 × 1) | **150** |

---
## Technology Stack

| Technology | Version | Purpose |
|------------|---------|----------|
| Java | 17 | Programming language |
| Spring Boot | 3.2.0 | Application framework |
| Spring Data JPA | 3.2.0 | ORM and database abstraction |
| Spring Validation | 3.2.0 | Request validation |
| PostgreSQL | 14+ | Application database |
| H2 | Latest | In-memory database for tests |
| Maven | 3.6+ | Build tool |
| Lombok | 1.18.30 | Reduce boilerplate code |
| JUnit 5 & Mockito | Latest | Unit testing |
| SpringDoc OpenAPI | 2.2.0 | Swagger API documentation |

---

## Application Architecture

The project follows a **layered architecture**:

| Layer | Package | Description |
|-------|---------|-------------|
| Controller | `controller` | REST endpoints |
| Service | `service` | Business logic |
| Repository | `repository` | Database access |
| Entity | `entity` | JPA entities |
| DTO | `dto` | API request/response objects |
| Util | `util` | Reward calculation logic |
| Config | `config` | Application configuration |
| Exception | `exception` | Global exception handling |
### Project Structure

```
customerrewardpoints/
│
├── src/main/java/com/charter/retail/rewards/
│   ├── config/
│   │   ├── RewardsConfig.java
│   │   └── SwaggerConfig.java
│   ├── controller/
│   │   ├── CustomerRewardsController.java
│   │   └── TransactionController.java
│   ├── service/
│   │   └── RewardsService.java
│   ├── repository/
│   │   ├── CustomerRepository.java
│   │   └── TransactionRepository.java
│   ├── entity/
│   │   ├── Customer.java
│   │   └── Transaction.java
│   ├── dto/
│   │   ├── CustomerRewardsDto.java
│   │   └── TransactionDto.java
│   ├── util/
│   │   └── RewardsCalculator.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   └── CustomerRewardPointsApplication.java
│
├── src/main/resources/
│   ├── application.properties
│   └── data.sql
│
├── src/test/java/com/charter/retail/rewards/
│   ├── controller/
│   │   ├── CustomerRewardsControllerTest.java
│   │   └── TransactionControllerTest.java
│   ├── repository/
│   │   ├── CustomerRepositoryTest.java
│   │   └── TransactionRepositoryTest.java
│   ├── service/
│   │   └── RewardsServiceTest.java
│   ├── util/
│   │   └── RewardsCalculatorTest.java
│   ├── CustomerRewardPointsApplicationTest.java
│   ├── CustomerRewardsIntegrationTests.java
│   └── TransactionIntegrationTests.java
│
├── src/test/resources/
│   └── application.properties
│
├── pom.xml
└── README.md
```

---
## Setup Instructions

### Prerequisites

Make sure the following tools are installed:

- Java 17+
- Maven 3.6+
- PostgreSQL 14+

**Check versions:**

```bash
java -version
mvn -version
psql --version
```

### Installation

**1. Clone the repository:**

```bash
git clone <repository-url>
cd customerrewardpoints
```

**2. Create the PostgreSQL database:**

```sql
CREATE DATABASE rewards_db;
```

**3. Update database credentials** (if needed) in:

```
src/main/resources/application.properties
```

**4. Build the project:**

```bash
mvn clean install
```

**5. Run the application:**

```bash
mvn spring-boot:run
```

**6. Access the application:**

-  **Application:** http://localhost:8080
-  **Swagger UI:** http://localhost:8080/swagger-ui/index.html
-  **API Docs (JSON):** http://localhost:8080/api-docs

### Alternative: Run with Maven Wrapper

If Maven is not installed globally, use the included Maven wrapper:

```bash
# On Unix/Linux/macOS
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

---
## API Documentation

### Swagger UI

Interactive API documentation is available at:

**http://localhost:8080/swagger-ui/index.html**

### API Endpoints

#### 1️ Get Rewards for a Customer

**Endpoint:** `GET /api/rewards/customer/{customerId}`

**Description:** Retrieves reward points for a specific customer over the last 3 months with monthly breakdown.

**Example:**

```bash
GET /api/rewards/customer/1
```

**Response:**

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

#### 2️ Get Rewards for All Customers

**Endpoint:** `GET /api/rewards/customers`

**Description:** Retrieves reward points for all customers with pagination support.

**Query Parameters:**
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)
- `sort` - Sort field and direction (e.g., `customerId,asc`)

**Example:**

```bash
GET /api/rewards/customers?page=0&size=10
```

**Response:**

```json
{
  "content": [
    {
      "customerId": 1,
      "customerName": "Jessica",
      "monthlyPoints": {...},
      "totalPoints": 575
    }
  ],
  "pageable": {...},
  "totalElements": 3,
  "totalPages": 1
}
```

#### 3️ Create Transaction

**Endpoint:** `POST /api/transactions`

**Description:** Creates a new purchase transaction for a customer.

**Request Body:**

```json
{
  "customerId": 1,
  "amount": 120.00,
  "transactionDate": "2024-12-15"
}
```

**Response:** `201 Created`

```json
{
  "id": 16,
  "customerId": 1,
  "amount": 120.00,
  "transactionDate": "2024-12-15"
}
```

**Validation Rules:**
- `customerId` - Required, must exist in database
- `amount` - Required, must be positive
- `transactionDate` - Required, valid date format

---
## Testing

The project includes comprehensive unit tests and integration tests.

### Test Coverage

| Test Class | Type | Purpose |
|------------|------|----------|
| `CustomerRewardPointsApplicationTest` | Unit | Application context loading |
| `RewardsCalculatorTest` | Unit | Reward calculation logic |
| `RewardsServiceTest` | Unit | Service layer testing |
| `CustomerRepositoryTest` | Unit | Repository testing |
| `TransactionRepositoryTest` | Unit | Repository testing |
| `CustomerRewardsControllerTest` | Unit | Rewards API endpoints |
| `TransactionControllerTest` | Unit | Transaction API endpoints |
| `CustomerRewardsIntegrationTests` | Integration | End-to-end rewards flow |
| `TransactionIntegrationTests` | Integration | End-to-end transaction flow |

### Running Tests

**Run all tests:**

```bash
mvn test
```

**Run specific test class:**

```bash
mvn test -Dtest=RewardsCalculatorTest
```

**Run tests with coverage report:**

```bash
mvn clean test
```

**Note:** Tests use an **H2 in-memory database** to isolate the application from PostgreSQL during test execution. Test configuration is located in `src/test/resources/application.properties`.

---

## Database Schema

### Entity Relationship

Customer and Transaction entities have a **one-to-many** relationship:

```
┌─────────────────┐
│    Customer     │
├─────────────────┤
│ id (PK)         │
│ name            │
└────────┬────────┘
         │ 1
         │
         │ N
┌────────┴────────────┐
│    Transaction      │
├─────────────────────┤
│ id (PK)             │
│ customerId (FK)     │
│ amount              │
│ transactionDate     │
└─────────────────────┘
```

### Customer Table

| Column | Type |
|--------|------|
| id | Long |
| name | String |

### Transaction Table

| Column | Type |
|--------|------|
| id | Long |
| customerId | Long |
| amount | Decimal |
| transactionDate | Date |
### Sample Data

The application automatically loads sample data on startup from:

```
src/main/resources/data.sql
```

**Dataset includes:**

-  **3 customers:** Jessica, Alice, Willow
-  **15 transactions** spread across the last 3 months
-  **Various amounts** to demonstrate different reward tiers

**Sample Customers:**

| ID | Name |
|----|------|
| 1 | Jessica |
| 2 | Alice |
| 3 | Willow |

This allows testing the reward calculation endpoints immediately after startup without manual data entry.

---

## Configuration

### Application Properties

Key configurations in `src/main/resources/application.properties`:

**Server Configuration:**
```properties
server.port=8080
```

**Database Configuration:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/rewards_db
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres
```

**JPA Configuration:**
```properties
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.defer-datasource-initialization=true
```

**Swagger/OpenAPI Configuration:**
```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
```

**Rewards Configuration:**
```properties
rewards.tier-one-threshold=50
rewards.tier-two-threshold=100
rewards.tier-one-multiplier=1
rewards.tier-two-multiplier=2
rewards.calculation-months=3
rewards.month-format=yyyy-MM
```

## Troubleshooting

**Issue: Application fails to start**
- Ensure PostgreSQL is running on port 5432
- Verify database `rewards_db` exists
- Check database credentials in `application.properties`

**Issue: Tests fail**
- Ensure H2 dependency is in `pom.xml`
- Check test configuration in `src/test/resources/application.properties`

**Issue: Swagger UI not accessible**
- Verify application is running on http://localhost:8080
- Check SpringDoc dependency in `pom.xml`
- Try accessing http://localhost:8080/swagger-ui/index.html

---
