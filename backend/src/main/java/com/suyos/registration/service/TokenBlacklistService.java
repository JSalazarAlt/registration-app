package com.suyos.registration.service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing blacklisted JWT tokens.
 * 
 * Provides functionality to blacklist tokens on logout and check if tokens
 * are blacklisted during authentication. Uses in-memory storage for simplicity.
 * 
 * @author Joel Salazar
 */
@Service
@Slf4j
public class TokenBlacklistService {
    
    /** Thread-safe set of blacklisted JWT tokens */
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    /** JWT service for token validation and expiration checking */
    private final JwtService jwtService;
    
    public TokenBlacklistService(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    /**
     * Blacklists a JWT token.
     * 
     * @param token the JWT token to blacklist
     */
    public void blacklistToken(String token) {
        try {
            Date expiration = jwtService.extractExpiration(token);
            if (expiration.after(new Date())) {
                blacklistedTokens.add(token);
                log.debug("Token blacklisted successfully");
            }
        } catch (Exception e) {
            log.warn("Failed to blacklist token: {}", e.getMessage());
            blacklistedTokens.add(token);
        }
    }
    
    /**
     * Checks if a token is blacklisted.
     * 
     * @param token the JWT token to check
     * @return true if token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    /**
     * Cleans up expired tokens from blacklist.
     * This method should be called periodically to prevent memory leaks.
     */
    public void cleanupExpiredTokens() {
        Date now = new Date();
        blacklistedTokens.removeIf(token -> {
            try {
                Date expiration = jwtService.extractExpiration(token);
                return expiration.before(now);
            } catch (Exception e) {
                return false;
            }
        });
        log.debug("Cleaned up expired blacklisted tokens");
    }
}