# Wallet-Management-System

## Overview
This application is a simple digital wallet management system which is a common backend component in FinTech  applications like mobile payments, e-wallets, or remittance services.

Built with **Spring Boot 4** and **Spring Framework 7**, providing APIs to manage wallets and transactions.

> **Note:** Swagger/OpenAPI documentation is **not included** in the running application. While code for `springdoc-openapi` was implemented, it was **not used** due to compatibility issues—`springdoc-openapi` does not yet support Spring Boot 4 / Spring Framework 7 at the time of implementation.

---

## Features
- Create user with wallet
- Perform transactions (deposit, transfer)
- View transaction history
- Unit tests for API endpoints and service logic

---

## Prerequisites
- Java 21
- Maven
- Spring Boot 4.0.0
- H2 Database (in-memory for development/testing)

---

## Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/YuEan8595/Wallet-Management-System.git
cd wallet-management-system
```

### 2. In Memory H2 Database Details
```bash
spring.datasource.url=jdbc:h2:mem:walletdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

### 3. Run the application
```bash
./mvnw spring-boot:run
```
The application will start at: http://localhost:8080

### 4. Run tests
```bash
./mvnw test
```

### 5. API Endpoints
```bash
| Method | Endpoint                              | Description                                                               |
| ------ | --------------------------------------|---------------------------------------------------------------------------|
| POST   | /api/users                            | Create a new user and automatically create a new wallet link to the user )|
| GET    | /api/wallets/{walletId}/balance       | Get current balance of a wallet                                           |
| POST   | /api/wallets/{walletId}/deposit       | Deposit money into a wallet (positive amount only)                        |
| POST   | /api/wallets/transfer                 | Transfer money from one wallet to another                                 |
| GET    | /api/wallets/{walletId}/transactions  | Get list of recent transactions (last 20, sorted by timestamp descending) |
```

### 6. Postman Collection
```bash
A Postman collection is provided in the `postman` directory for easy testing of the API endpoints.
```

### 7. Basic Auth
```bash
Basic authentication is implemented for all endpoints. Use the following credentials for testing:
| Username | Password |
|----------|----------|
| user | user123 |
|admin | admin123 |
```