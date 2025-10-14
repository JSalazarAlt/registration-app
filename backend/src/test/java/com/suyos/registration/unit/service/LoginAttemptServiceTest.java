package com.suyos.registration.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.suyos.registration.model.User;
import com.suyos.registration.repository.UserRepository;
import com.suyos.registration.service.LoginAttemptService;

/**
 * Unit tests for LoginAttemptService.
 * 
 * Tests failed login attempt tracking and account locking functionality.
 * Validates security features that protect against brute force attacks.
 * 
 * @author Joel Salazar
 */
@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    /** Mock repository for user data access operations */
    @Mock
    private UserRepository userRepository;
    
    /** LoginAttemptService instance under test with injected mocks */
    @InjectMocks
    private LoginAttemptService loginAttemptService;
    
    /** Test user entity for failed login attempt operations */
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .failedLoginAttempts(0)
                .accountLocked(false)
                .build();
    }

    @Test
    void recordFailedAttempt_FirstAttempt() {
        loginAttemptService.recordFailedAttempt(user);

        assertEquals(1, user.getFailedLoginAttempts());
        assertFalse(user.getAccountLocked());
        assertNull(user.getLockedUntil());
        verify(userRepository).save(user);
    }

    @Test
    void recordFailedAttempt_SecondAttempt() {
        user.setFailedLoginAttempts(1);
        
        loginAttemptService.recordFailedAttempt(user);

        assertEquals(2, user.getFailedLoginAttempts());
        assertFalse(user.getAccountLocked());
        assertNull(user.getLockedUntil());
        verify(userRepository).save(user);
    }

    @Test
    void recordFailedAttempt_ThirdAttempt_AccountLocked() {
        user.setFailedLoginAttempts(2);
        
        loginAttemptService.recordFailedAttempt(user);

        assertEquals(3, user.getFailedLoginAttempts());
        assertTrue(user.getAccountLocked());
        assertNotNull(user.getLockedUntil());
        assertTrue(user.getLockedUntil().isAfter(LocalDateTime.now()));
        verify(userRepository).save(user);
    }

    @Test
    void recordFailedAttempt_AlreadyLocked() {
        user.setFailedLoginAttempts(5);
        user.setAccountLocked(true);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
        
        loginAttemptService.recordFailedAttempt(user);

        assertEquals(6, user.getFailedLoginAttempts());
        assertTrue(user.getAccountLocked());
        assertNotNull(user.getLockedUntil());
        verify(userRepository).save(user);
    }
}