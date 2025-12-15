package com.example.wallet.service;

import com.example.wallet.entity.*;
import com.example.wallet.enums.TransactionStatus;
import com.example.wallet.enums.TransactionType;
import com.example.wallet.exception.BadRequestException;
import com.example.wallet.exception.InsufficientBalanceException;
import com.example.wallet.exception.ResourceNotFoundException;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionLoggerService transactionLogger;

    @InjectMocks
    private WalletService walletService;

    @Test
    void getBalance_success() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100));

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));

        BigDecimal balance = walletService.getBalance(1L);

        assertEquals(BigDecimal.valueOf(100), balance);
        verify(walletRepository).findById(1L);
    }

    @Test
    void getBalance_walletNotFound() {
        when(walletRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> walletService.getBalance(99L)
        );
    }

    @Test
    void deposit_success() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(50));

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction tx = walletService.deposit(1L, BigDecimal.valueOf(25));

        assertEquals(BigDecimal.valueOf(75), wallet.getBalance());
        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(TransactionStatus.SUCCESS, tx.getStatus());

        verify(walletRepository).save(wallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void deposit_negativeAmount_savesFailedTransaction() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(50));

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(
                BadRequestException.class,
                () -> walletService.deposit(1L, BigDecimal.valueOf(-10))
        );

        verify(transactionRepository, times(1)).save(argThat(tx ->
                tx.getStatus() == TransactionStatus.FAILED &&
                        tx.getType() == TransactionType.DEPOSIT &&
                        tx.getAmount().equals(BigDecimal.valueOf(-10))
        ));
    }

    @Test
    void transfer_success() {
        Wallet from = new Wallet();
        from.setId(1L);
        from.setBalance(BigDecimal.valueOf(100));

        Wallet to = new Wallet();
        to.setId(2L);
        to.setBalance(BigDecimal.valueOf(50));

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(from));
        when(walletRepository.findById(2L))
                .thenReturn(Optional.of(to));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction tx = walletService.transfer(1L, 2L, BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(70), from.getBalance());
        assertEquals(BigDecimal.valueOf(80), to.getBalance());
        assertEquals(TransactionType.TRANSFER, tx.getType());
        assertEquals(TransactionStatus.SUCCESS, tx.getStatus());

        verify(walletRepository).save(from);
        verify(walletRepository).save(to);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
