package com.suyos.registration.unit.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.suyos.registration.service.JwtService;

import io.jsonwebtoken.JwtException;

/**
 * Unit tests for JwtService.
 * 
 * Tests JWT token generation, validation, and extraction operations.
 * Uses ReflectionTestUtils to configure service properties for testing.
 * 
 * @author Joel Salazar
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    /** JWT service instance under test */
    private JwtService jwtService;
    
    /** Mock user details for token generation and validation tests */
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "mySecretKeymySecretKeymySecretKeymySecretKey");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
        
        userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities("USER")
                .build();
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void extractUsername_Success() {
        String token = jwtService.generateToken(userDetails);
        
        String username = jwtService.extractUsername(token);
        
        assertEquals("test@example.com", username);
    }

    @Test
    void extractExpiration_Success() {
        String token = jwtService.generateToken(userDetails);
        
        Date expiration = jwtService.extractExpiration(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenValid_ValidToken() {
        String token = jwtService.generateToken(userDetails);
        
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = User.builder()
                .username("different@example.com")
                .password("password")
                .authorities("USER")
                .build();
        
        boolean isValid = jwtService.isTokenValid(token, differentUser);
        
        assertFalse(isValid);
    }

    @Test
    void extractUsername_InvalidToken() {
        assertThrows(JwtException.class, () -> {
            jwtService.extractUsername("invalid.token.here");
        });
    }

    @Test
    void getExpirationTime_Success() {
        Long expirationTime = jwtService.getExpirationTime();
        
        assertEquals(86400L, expirationTime); // 24 hours in seconds
    }
}