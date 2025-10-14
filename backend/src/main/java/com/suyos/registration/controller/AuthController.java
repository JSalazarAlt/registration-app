package com.suyos.registration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyos.registration.dto.AuthenticationResponseDTO;
import com.suyos.registration.dto.UserLoginDTO;
import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserRegistrationDTO;
import com.suyos.registration.service.AuthService;
import com.suyos.registration.service.TokenBlacklistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for authentication operations.
 * 
 * Handles user registration and login endpoints.
 * Provides JWT-based authentication for the application.
 * 
 * @author Joel Salazar
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login operations")
public class AuthController {
    
    /** Service layer for authentication business logic */
    private final AuthService authService;
    
    /** Service for managing blacklisted JWT tokens */
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Registers a new user account.
     * 
     * @param registrationDTO the user registration data
     * @return ResponseEntity containing the created user's profile or error message
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data or email already exists")
    })
    public ResponseEntity<UserProfileDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserProfileDTO userProfile = authService.registerUser(registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfile);
    }

    /**
     * Authenticates a user login attempt and returns JWT token.
     * 
     * @param loginDTO the user login credentials
     * @return ResponseEntity containing JWT token and user profile or error message
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user credentials and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or account locked")
    })
    public ResponseEntity<AuthenticationResponseDTO> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        AuthenticationResponseDTO authResponse = authService.authenticateUser(loginDTO);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Logs out a user by blacklisting their JWT token.
     * 
     * @param request the HTTP request containing the Authorization header
     * @return ResponseEntity indicating logout success
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates the user's JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "400", description = "Invalid or missing token")
    })
    public ResponseEntity<String> logoutUser(jakarta.servlet.http.HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
            return ResponseEntity.ok("Logout successful");
        }
        return ResponseEntity.badRequest().body("No valid token found");
    }
}