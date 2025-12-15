package com.example.wallet.controller;

import com.example.wallet.dto.BalanceResponse;
import com.example.wallet.dto.DepositRequest;
import com.example.wallet.dto.TransferRequest;
import com.example.wallet.entity.Transaction;
import com.example.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

//@Tag(name = "Wallets", description = "Wallet operations APIs")
@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{id}/balance")
    public BalanceResponse getBalance(@PathVariable Long id) {
        BigDecimal balance = walletService.getBalance(id);
        return new BalanceResponse(balance);
    }

    @PostMapping("/{id}/deposit")
    public Transaction deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest req
    ) {
        return walletService.deposit(id, req.getAmount());
    }

    @PostMapping("/transfer")
    public Transaction transfer(@Valid @RequestBody TransferRequest req) {
        return walletService.transfer(
                req.getFromWalletId(),
                req.getToWalletId(),
                req.getAmount()
        );
    }

    @GetMapping("/{id}/transactions")
    public Page<Transaction> transactions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return walletService.getTransactions(id, page, size);
    }

}