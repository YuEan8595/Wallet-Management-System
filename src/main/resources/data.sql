-- Users
INSERT INTO users (name, email, phone)
VALUES ('Alice', 'alice@mail.com', '0123456789');

INSERT INTO users (name, email, phone)
VALUES ('Bob', 'bob@mail.com', '0987654321');

-- Wallets
INSERT INTO wallet (user_id, balance, currency)
VALUES (1, 100.00, 'USD');

INSERT INTO wallet (user_id, balance, currency)
VALUES (2, 50.00, 'USD');

-- Transactions
INSERT INTO transaction (from_wallet_id, to_wallet_id, amount, type, status, timestamp)
VALUES (NULL, 1, 100.00, 'DEPOSIT', 'SUCCESS', CURRENT_TIMESTAMP);

INSERT INTO transaction (from_wallet_id, to_wallet_id, amount, type, status, timestamp)
VALUES (1, 2, 25.00, 'TRANSFER', 'SUCCESS', CURRENT_TIMESTAMP);
