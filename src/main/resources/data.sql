-- Insert sample customers
INSERT INTO customers (id, name) VALUES (1, 'Jessica');
INSERT INTO customers (id, name) VALUES (2, 'Alice');
INSERT INTO customers (id, name) VALUES (3, 'Willow');

-- Insert sample transactions for customer 1 (Jessica)
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (1, 1, 120.00, CURRENT_DATE - INTERVAL '65 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (2, 1, 75.50, CURRENT_DATE - INTERVAL '70 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (3, 1, 150.00, CURRENT_DATE - INTERVAL '33 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (4, 1, 45.00, CURRENT_DATE - INTERVAL '45 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (5, 1, 200.00, CURRENT_DATE - INTERVAL '5 days');

-- Insert sample transactions for customer 2 (Alice)
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (6, 2, 89.99, CURRENT_DATE - INTERVAL '67 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (7, 2, 110.00, CURRENT_DATE - INTERVAL '80 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (8, 2, 50.00, CURRENT_DATE - INTERVAL '38 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (9, 2, 175.25, CURRENT_DATE - INTERVAL '48 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (10, 2, 95.00, CURRENT_DATE - INTERVAL '3 days');

-- Insert sample transactions for customer 3 (Willow)
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (11, 3, 250.00, CURRENT_DATE - INTERVAL '72 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (12, 3, 30.00, CURRENT_DATE - INTERVAL '85 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (13, 3, 125.75, CURRENT_DATE - INTERVAL '36 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (14, 3, 60.00, CURRENT_DATE - INTERVAL '52 days');
INSERT INTO transactions (id, customer_id, amount, transaction_date) VALUES (15, 3, 180.50, CURRENT_DATE - INTERVAL '10 days');
