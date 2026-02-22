@echo off
echo ========================================
echo Testing Customer Rewards API
echo ========================================
echo.

echo Test Case 1: $120.00 - Expected 90 points
echo Calculation: (120-100)x2 + 50 = 40 + 50 = 90
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":120.00,\"transactionDate\":\"2024-12-20\"}"
echo.
echo.

echo Test Case 2: $75.00 - Expected 25 points
echo Calculation: (75-50)x1 = 25
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":75.00,\"transactionDate\":\"2024-12-21\"}"
echo.
echo.

echo Test Case 3: $200.00 - Expected 250 points
echo Calculation: (200-100)x2 + 50 = 200 + 50 = 250
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":200.00,\"transactionDate\":\"2024-12-22\"}"
echo.
echo.

echo Test Case 4: $45.00 - Expected 0 points
echo Calculation: Below $50 threshold = 0
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"customerId\":1,\"amount\":45.00,\"transactionDate\":\"2024-12-23\"}"
echo.
echo.

echo ========================================
echo Fetching updated rewards for Customer 1
echo ========================================
curl http://localhost:8080/api/rewards/customer/1
echo.
echo.

echo ========================================
echo Test Complete!
echo ========================================
pause
