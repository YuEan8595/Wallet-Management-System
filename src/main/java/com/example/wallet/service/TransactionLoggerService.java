package com.example.wallet.service;

import com.example.wallet.entity.Transaction;
import com.example.wallet.enums.TransactionStatus;
import com.example.wallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionLoggerService {

    private final TransactionRepository txRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction saveFailedTransaction(Transaction tx) {
        tx.setStatus(TransactionStatus.FAILED);
        return txRepo.save(tx);
    }
}

