package com.example.wallet.repository;

import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Purpose: It retrieves the top 20 Transaction entities where either the fromWallet or the toWallet matches the provided Wallet entities.
     * Sorting: The results are ordered by the timestamp field in descending order (Desc), meaning the most recent transactions appear first.
     * Limit: The query limits the result to a maximum of 20 transactions.
     */
    Page<Transaction> findByFromWalletOrToWallet(
            Wallet from,
            Wallet to,
            Pageable pageable
    );
}
