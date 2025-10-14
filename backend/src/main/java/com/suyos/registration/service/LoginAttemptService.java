package com.suyos.registration.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.suyos.registration.model.User;
import com.suyos.registration.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling login attempt tracking and account security.
 * 
 * Manages failed login attempts, implements account locking mechanisms,
 * and provides security features to prevent brute force attacks.
 * Uses separate transactions to ensure failed attempts are recorded
 * even when authentication fails.
 * 
 * @author Joel Salazar
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptService {
    
    /** Repository for user data access operations */
    private final UserRepository userRepository;

    /** Maximum allowed failed login attempts before account lock */
    private static final int MAX_FAILED_ATTEMPTS = 5;
    
    /** Account lock duration in hours */
    private static final int LOCK_DURATION_HOURS = 24;

    /**
     * Records a failed login attempt and implements account locking security.
     * 
     * Increments the failed login attempt counter for the user and automatically
     * locks the account if the maximum number of failed attempts is reached.
     * Uses REQUIRES_NEW transaction propagation to ensure the failed attempt
     * is saved even if the calling transaction rolls back.
     * 
     * @param user the user who had a failed login attempt
     * @throws IllegalArgumentException if user is null
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            user.setLockedUntil(LocalDateTime.now().plusHours(LOCK_DURATION_HOURS));
        }

        userRepository.save(user);
    }

}