package com.suyos.registration.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Service for JWT token generation, validation, and extraction operations.
 * 
 * Handles all JWT-related functionality including token creation, validation,
 * and claims extraction. Uses HMAC-SHA256 algorithm for token signing.
 * 
 * @author Joel Salazar
 * @version 1.0
 * @since 1.0
 */
@Service
public class JwtService {

    /** JWT secret key from application properties */
    @Value("${jwt.secret}")
    private String secretKey;
    
    /** JWT token expiration time in milliseconds (24 hours) */
    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    /**
     * Extracts username from JWT token.
     * 
     * @param token the JWT token
     * @return the username (subject) from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts expiration date from JWT token.
     * 
     * @param token the JWT token
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from JWT token.
     * 
     * @param <T> the type of the claim
     * @param token the JWT token
     * @param claimsResolver function to extract the specific claim
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates JWT token for user authentication.
     * 
     * @param userDetails the authenticated user details
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates JWT token with additional claims.
     * 
     * @param extraClaims additional claims to include in the token
     * @param userDetails the authenticated user details
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Gets JWT token expiration time in seconds.
     * 
     * @return expiration time in seconds
     */
    public Long getExpirationTime() {
        return jwtExpiration / 1000;
    }

    /**
     * Validates JWT token against user details.
     * 
     * @param token the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if JWT token is expired.
     * 
     * @param token the JWT token
     * @return true if token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts all claims from JWT token.
     * 
     * @param token the JWT token
     * @return all claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Builds JWT token with specified claims and expiration.
     * 
     * @param extraClaims additional claims to include
     * @param userDetails the user details
     * @param expiration token expiration time in milliseconds
     * @return the built JWT token
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Gets the signing key for JWT token operations.
     * 
     * @return the signing key
     */
    private javax.crypto.SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
    
}