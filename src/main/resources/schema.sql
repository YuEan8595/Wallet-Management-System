DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS wallet;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50)
);

CREATE TABLE wallet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_wallet_id BIGINT,
    to_wallet_id BIGINT,
    amount DECIMAL(19,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP,
    CONSTRAINT fk_tx_from_wallet FOREIGN KEY (from_wallet_id) REFERENCES wallet(id),
    CONSTRAINT fk_tx_to_wallet FOREIGN KEY (to_wallet_id) REFERENCES wallet(id)
);
