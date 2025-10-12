package com.suyos.registration.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyos.registration.dto.AuthenticationResponseDTO;
import com.suyos.registration.dto.UserLoginDTO;
import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserRegistrationDTO;
import com.suyos.registration.dto.UserUpdateDTO;
import com.suyos.registration.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for user management operations.
 * 
 * Provides endpoints for user authentication, registration, and profile management.
 * Handles HTTP requests and delegates business logic to the UserService layer.
 * 
 * All endpoints return appropriate HTTP status codes and error messages
 * for client applications to handle authentication and user management flows.
 * 
 * @author Joel Salazar
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User authentication and profile management")
public class UserController {
    
    /** Service layer for user business logic */
    private final UserService userService;

    /**
     * Registers a new user account.
     * 
     * Creates a new user with the provided registration information.
     * Validates input data and returns the created user's profile.
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserProfileDTO userProfile = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(userProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Authenticates a user login attempt and returns JWT token.
     * 
     * Validates user credentials and returns JWT token with user profile on success.
     * Handles account locking and failed login attempts.
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
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            AuthenticationResponseDTO authResponse = userService.authenticateUser(loginDTO);
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Retrieves a user's profile information.
     * 
     * Returns the complete profile data for the specified user.
     * Used for displaying user information in the application.
     * 
     * @param userId the user's unique identifier
     * @return ResponseEntity containing the user's profile or error message
     */
    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get user profile", description = "Retrieves user profile information")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<?> getUserProfile(@Parameter(description = "User ID") @PathVariable Long userId) {
        try {
            UserProfileDTO userProfile = userService.getUserProfile(userId);
            return ResponseEntity.ok(userProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found: " + e.getMessage());
        }
    }

    /**
     * Updates a user's profile information.
     * 
     * Allows users to modify their profile data excluding sensitive fields
     * like email and password. Validates input and returns updated profile.
     * 
     * @param userId the user's unique identifier
     * @param updateDTO the updated profile information
     * @return ResponseEntity containing the updated user's profile or error message
     */
    @PutMapping("/{userId}/profile")
    @Operation(summary = "Update user profile", description = "Updates user profile information (excluding sensitive fields)")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid update data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<?> updateUserProfile(@Parameter(description = "User ID") @PathVariable Long userId, 
                                             @Valid @RequestBody UserUpdateDTO updateDTO) {
        try {
            UserProfileDTO updatedProfile = userService.updateUserProfile(userId, updateDTO);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Profile update failed: " + e.getMessage());
        }
    }

}