package com.example.wallet.service;

import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.BadRequestException;
import com.example.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        userRepo.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new BadRequestException("Email already exists");
                });

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        user.setWallet(wallet);

        return userRepo.save(user);
    }
}
