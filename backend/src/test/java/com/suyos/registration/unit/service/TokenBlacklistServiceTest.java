package com.suyos.registration.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.suyos.registration.service.JwtService;
import com.suyos.registration.service.TokenBlacklistService;

/**
 * Unit tests for TokenBlacklistService.
 * 
 * Tests JWT token blacklisting functionality for secure logout operations.
 * Validates token management and blacklist checking with mocked dependencies.
 * 
 * @author Joel Salazar
 */
@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    /** Mock JWT service for token validation and expiration checking */
    @Mock
    private JwtService jwtService;
    
    /** TokenBlacklistService instance under test */
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService(jwtService);
        
        // Mock JWT service to return future expiration date
        when(jwtService.extractExpiration(anyString()))
            .thenReturn(new Date(System.currentTimeMillis() + 86400000)); // 24 hours from now
    }

    @Test
    void blacklistToken_Success() {
        String token = "test.jwt.token";
        
        tokenBlacklistService.blacklistToken(token);
        
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token));
    }

    @Test
    void isTokenBlacklisted_NotBlacklisted() {
        String token = "test.jwt.token";
        
        boolean isBlacklisted = tokenBlacklistService.isTokenBlacklisted(token);
        
        assertFalse(isBlacklisted);
    }

    @Test
    void isTokenBlacklisted_AfterBlacklisting() {
        String token = "test.jwt.token";
        
        tokenBlacklistService.blacklistToken(token);
        boolean isBlacklisted = tokenBlacklistService.isTokenBlacklisted(token);
        
        assertTrue(isBlacklisted);
    }

    @Test
    void blacklistToken_MultipleTokens() {
        String token1 = "test.jwt.token1";
        String token2 = "test.jwt.token2";
        
        tokenBlacklistService.blacklistToken(token1);
        tokenBlacklistService.blacklistToken(token2);
        
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token1));
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token2));
    }

    @Test
    void isTokenBlacklisted_NullToken() {
        boolean isBlacklisted = tokenBlacklistService.isTokenBlacklisted(null);
        
        assertFalse(isBlacklisted);
    }

    @Test
    void blacklistToken_NullToken() {
        assertDoesNotThrow(() -> {
            tokenBlacklistService.blacklistToken(null);
        });
    }
}