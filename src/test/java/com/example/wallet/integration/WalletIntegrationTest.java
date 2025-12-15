package com.example.wallet.integration;

import com.example.wallet.dto.DepositRequest;
import com.example.wallet.dto.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    private static final String BASIC_AUTH = "Basic dXNlcjp1c2VyMTIz"; // user:user123

    @BeforeEach
    void setup() {
        this.webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testGetWalletBalance() {
        webTestClient.get()
                .uri("/api/wallets/1/balance")
                .header("Authorization", BASIC_AUTH)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(100.00);
    }

    @Test
    void testDeposit() {
        DepositRequest request = new DepositRequest();
        request.setAmount(BigDecimal.valueOf(50.00));

        webTestClient.post()
                .uri("/api/wallets/1/deposit")
                .header("Authorization", BASIC_AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.type").isEqualTo("DEPOSIT")
                .jsonPath("$.status").isEqualTo("SUCCESS")
                .jsonPath("$.amount").isEqualTo(50.00);
    }

    @Test
    void testTransfer() {
        TransferRequest request = new TransferRequest();
        request.setFromWalletId(1L);
        request.setToWalletId(2L);
        request.setAmount(BigDecimal.valueOf(25.00));

        webTestClient.post()
                .uri("/api/wallets/transfer")
                .header("Authorization", BASIC_AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.type").isEqualTo("TRANSFER")
                .jsonPath("$.status").isEqualTo("SUCCESS")
                .jsonPath("$.amount").isEqualTo(25.00);
    }

    @Test
    void testTransferInsufficientBalance() {
        TransferRequest request = new TransferRequest();
        request.setFromWalletId(2L);
        request.setToWalletId(1L);
        request.setAmount(BigDecimal.valueOf(1000.00));

        webTestClient.post()
                .uri("/api/wallets/transfer")
                .header("Authorization", BASIC_AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Insufficient balance");
    }
}
