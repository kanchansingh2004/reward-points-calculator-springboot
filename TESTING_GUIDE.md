# Testing Guide - Customer Rewards API

## Quick Test Commands

### 1. Test Get Rewards for Customer 1
```bash
curl http://localhost:8080/api/rewards/customer/1
```

**Expected Response:**
```json
{
  "customerId": 1,
  "customerName": "Kanchan",
  "monthlyPoints": {
    "2024-11": 115,
    "2024-12": 210
  },
  "totalPoints": 325
}
```

---

### 2. Test Get Rewards for All Customers
```bash
curl http://localhost:8080/api/rewards/customers
```

**Expected Response:**
```json
[
  {
    "customerId": 1,
    "customerName": "Kanchan",
    "monthlyPoints": {...},
    "totalPoints": 325
  },
  {
    "customerId": 2,
    "customerName": "Priya",
    "monthlyPoints": {...},
    "totalPoints": 280
  },
  {
    "customerId": 3,
    "customerName": "Shivam",
    "monthlyPoints": {...},
    "totalPoints": 390
  }
]
```

---

### 3. Test Create New Transaction
```bash
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":120.00,\"transactionDate\":\"2024-12-15\"}"
```

**Expected Response:**
```json
{
  "id": 16,
  "customerId": 1,
  "amount": 120.00,
  "transactionDate": "2024-12-15"
}
```

---

## Assignment Requirements Checklist

### ✅ Core Requirements

| Requirement | Test Method | Status |
|------------|-------------|--------|
| **2 points per dollar over $100** | Create transaction with $120 → Should get 90 points | ✓ |
| **1 point per dollar $50-$100** | Create transaction with $75 → Should get 25 points | ✓ |
| **Monthly breakdown** | Check `monthlyPoints` in response | ✓ |
| **3-month period** | Verify only last 3 months appear | ✓ |
| **Total points** | Check `totalPoints` field | ✓ |
| **Multiple customers** | GET /api/rewards/customers returns 3 customers | ✓ |
| **RESTful API** | All endpoints use proper HTTP methods | ✓ |

---

## Test Scenarios

### Scenario 1: Verify Points Calculation

**Test $120 Purchase:**
```bash
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":120.00,\"transactionDate\":\"2024-12-20\"}"
```
**Expected:** 90 points (20×2 + 50×1)

**Test $75 Purchase:**
```bash
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":75.00,\"transactionDate\":\"2024-12-20\"}"
```
**Expected:** 25 points (25×1)

**Test $50 Purchase:**
```bash
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":50.00,\"transactionDate\":\"2024-12-20\"}"
```
**Expected:** 0 points

---

### Scenario 2: Verify Monthly Breakdown

```bash
curl http://localhost:8080/api/rewards/customer/1
```

**Verify:**
- Response contains `monthlyPoints` object
- Keys are in format "YYYY-MM" (e.g., "2024-12")
- Only last 3 months are included
- Each month shows correct point total

---

### Scenario 3: Verify Multiple Customers

```bash
curl http://localhost:8080/api/rewards/customers
```

**Verify:**
- Returns array of 3 customers
- Each customer has: customerId, customerName, monthlyPoints, totalPoints
- All customers have transactions in last 3 months

---

### Scenario 4: Test Error Handling

**Test Invalid Customer:**
```bash
curl http://localhost:8080/api/rewards/customer/999
```
**Expected:** 404 Not Found

**Test Invalid Transaction (Negative Amount):**
```bash
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":-50.00,\"transactionDate\":\"2024-12-20\"}"
```
**Expected:** 400 Bad Request

**Test Missing Fields:**
```bash
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1}"
```
**Expected:** 400 Bad Request

---

## Using Browser (Alternative to cURL)

### Option 1: Direct Browser Access
Open in browser:
- http://localhost:8080/api/rewards/customer/1
- http://localhost:8080/api/rewards/customers

### Option 2: H2 Console
1. Open: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:rewardsdb`
3. Username: `sa`
4. Password: (leave empty)
5. Click "Connect"

**Run SQL Queries:**
```sql
-- View all customers
SELECT * FROM customers;

-- View all transactions
SELECT * FROM transactions;

-- View transactions with customer names
SELECT c.name, t.amount, t.transaction_date 
FROM transactions t 
JOIN customers c ON t.customer_id = c.id;
```

---

## Using Postman

### Import Collection

**1. Get Customer Rewards**
- Method: GET
- URL: http://localhost:8080/api/rewards/customer/1

**2. Get All Customers Rewards**
- Method: GET
- URL: http://localhost:8080/api/rewards/customers

**3. Create Transaction**
- Method: POST
- URL: http://localhost:8080/api/transactions
- Headers: Content-Type: application/json
- Body (raw JSON):
```json
{
  "customerId": 1,
  "amount": 120.00,
  "transactionDate": "2024-12-20"
}
```

---

## Verify Sample Data

The application loads 3 customers with 5 transactions each:

| Customer ID | Name    | Transactions | Expected Points Range |
|------------|---------|--------------|---------------------|
| 1 | Kanchan | 5 | 300-400 points |
| 2 | Priya   | 5 | 250-350 points |
| 3 | Shivam  | 5 | 350-450 points |

---

## Run Automated Tests

```bash
# Run all unit and integration tests
mvn test

# Run specific test class
mvn test -Dtest=RewardsCalculatorTest

# Run with detailed output
mvn test -X
```

**Expected Output:**
```
Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
```

---

## Assignment Compliance Verification

### ✅ Technical Requirements

| Requirement | Implementation | Verification |
|------------|----------------|--------------|
| Spring Boot | ✓ Used Spring Boot 3.2.0 | Check pom.xml |
| RESTful API | ✓ GET, POST endpoints | Test with cURL |
| No hardcoded months | ✓ Uses LocalDate.now() | Check RewardsService.java |
| Multiple customers | ✓ 3 customers in sample data | GET /api/rewards/customers |
| Multiple transactions | ✓ 15 transactions total | Check H2 console |
| Unit tests | ✓ RewardsCalculatorTest | mvn test |
| Integration tests | ✓ RewardsControllerTest | mvn test |
| Exception handling | ✓ GlobalExceptionHandler | Test with invalid ID |
| JavaDoc | ✓ All classes documented | Check source files |
| .gitignore | ✓ Excludes target/ | Check .gitignore |
| README | ✓ Comprehensive documentation | Check README.md |

---

## Quick Verification Script

Create a file `test-api.sh` (Linux/Mac) or `test-api.bat` (Windows):

**test-api.sh:**
```bash
#!/bin/bash
echo "Testing Customer Rewards API..."
echo ""
echo "1. Get Customer 1 Rewards:"
curl -s http://localhost:8080/api/rewards/customer/1 | json_pp
echo ""
echo "2. Get All Customers:"
curl -s http://localhost:8080/api/rewards/customers | json_pp
echo ""
echo "3. Create Transaction:"
curl -s -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"amount":120.00,"transactionDate":"2024-12-20"}' | json_pp
```

**test-api.bat (Windows):**
```batch
@echo off
echo Testing Customer Rewards API...
echo.
echo 1. Get Customer 1 Rewards:
curl http://localhost:8080/api/rewards/customer/1
echo.
echo 2. Get All Customers:
curl http://localhost:8080/api/rewards/customers
echo.
echo 3. Create Transaction:
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":120.00,\"transactionDate\":\"2024-12-20\"}"
```

---

## Success Criteria

Your application meets all requirements if:

1. ✅ All 3 endpoints return valid JSON responses
2. ✅ Points calculation is correct (2x over $100, 1x $50-$100)
3. ✅ Monthly breakdown shows last 3 months only
4. ✅ Multiple customers with multiple transactions exist
5. ✅ Error handling returns proper HTTP status codes
6. ✅ All tests pass (mvn test)
7. ✅ No hardcoded months in code
8. ✅ JavaDoc present on all classes/methods
9. ✅ README explains the project
10. ✅ Code follows Java standards

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused | Ensure app is running on port 8080 |
| Empty response | Check if sample data loaded successfully |
| 404 errors | Verify endpoint URLs are correct |
| Invalid JSON | Check Content-Type header is set |

---

## Next Steps

1. ✅ Test all 3 endpoints with cURL
2. ✅ Verify points calculation with different amounts
3. ✅ Run automated tests: `mvn test`
4. ✅ Check H2 console for data
5. ✅ Test error scenarios
6. ✅ Review code for JavaDoc and standards
7. ✅ Push to GitHub
8. ✅ Share repository link

**Your application is ready for submission! 🚀**
