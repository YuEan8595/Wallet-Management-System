package com.example.wallet.service;

import com.example.wallet.entity.*;
import com.example.wallet.enums.TransactionStatus;
import com.example.wallet.enums.TransactionType;
import com.example.wallet.exception.*;
import com.example.wallet.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepo;
    private final TransactionRepository txRepo;
    private final TransactionLoggerService txLogger;
    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    public BigDecimal getBalance(Long walletId) {
        log.info("Fetching balance for wallet ID: {}", walletId);
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return wallet.getBalance();
    }

    public Transaction deposit(Long walletId, BigDecimal amount) {
        log.info("Depositing amount: {} to wallet ID: {}", amount, walletId);
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        Transaction tx = new Transaction();
        tx.setToWallet(wallet);
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());

        try {
            // Validation
            // Amounts must be positive
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Deposit amount must be positive");
            }

            // check for max 2 decimal places
            if (amount.scale() > 2) {
                throw new BadRequestException("Deposit amount cannot have more than 2 decimal places");
            }

            // Update wallet
            wallet.setBalance(wallet.getBalance().add(amount));
            walletRepo.save(wallet);

            tx.setStatus(TransactionStatus.SUCCESS);
        } catch (RuntimeException e) {
            tx.setStatus(TransactionStatus.FAILED);
            txRepo.save(tx);  // Save failed transaction
            throw e;           // Optional: rethrow to return error to client
        }

        return txRepo.save(tx);
    }

    @Transactional
    public Transaction transfer(Long fromId, Long toId, BigDecimal amount) {
        log.info("Transferring amount: {} from wallet ID: {} to wallet ID: {}", amount, fromId, toId);
        Wallet from = walletRepo.findById(fromId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found"));
        Wallet to = walletRepo.findById(toId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver wallet not found"));

        Transaction tx = new Transaction();
        tx.setFromWallet(from);
        tx.setToWallet(to);
        tx.setAmount(amount);
        tx.setType(TransactionType.TRANSFER);
        tx.setTimestamp(LocalDateTime.now());

        try {
            // Cannot transfer to the same wallet
            if (fromId.equals(toId)) {
                throw new BadRequestException("Cannot transfer to the same wallet");
            }

            // Amounts must be positive
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Transfer amount must be positive");
            }

            // check for max 2 decimal places
            if (amount.scale() > 2) {
                throw new BadRequestException("Transfer amount cannot have more than 2 decimal places");
            }

            // Check for sufficient balance
            if (from.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }

            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));
            walletRepo.save(from);
            walletRepo.save(to);
            tx.setStatus(TransactionStatus.SUCCESS);

        } catch (RuntimeException e) {
            // Save FAILED transaction in a separate transaction
            txLogger.saveFailedTransaction(tx);
            throw e;
        }

        return txRepo.save(tx);
    }

    public Page<Transaction> getTransactions(
            Long walletId,
            int page,
            int size
    ) {
        log.info("Fetching transactions for wallet ID: {}, page: {}, size: {}", walletId, page, size);
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // Pagination and sorting by timestamp descending
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "timestamp")
        );

        return txRepo.findByFromWalletOrToWallet(wallet, wallet, pageable);
    }

}
