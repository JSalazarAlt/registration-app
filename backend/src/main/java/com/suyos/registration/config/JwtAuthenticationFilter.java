package com.suyos.registration.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.suyos.registration.service.JwtService;
import com.suyos.registration.service.TokenBlacklistService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT authentication filter for processing JWT tokens in HTTP requests.
 * 
 * Intercepts incoming requests to extract and validate JWT tokens from
 * Authorization headers. Sets Spring Security context for valid tokens
 * and handles token blacklisting for logout functionality.
 * 
 * @author Joel Salazar
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Service for JWT token generation, validation, and extraction operations */
    private final JwtService jwtService;
    
    /** Service for loading user details from the database */
    private final UserDetailsService userDetailsService;
    
    /** Service for managing blacklisted JWT tokens */
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Processes each HTTP request to extract and validate JWT tokens.
     * 
     * Extracts the JWT token from the Authorization header, validates it,
     * and sets the security context if the token is valid.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if servlet processing fails
     * @throws IOException if I/O operation fails
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        
        try {
            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                log.debug("Blacklisted token attempted to be used");
                filterChain.doFilter(request, response);
                return;
            }
            
            userEmail = jwtService.extractUsername(jwt);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during JWT processing: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
}