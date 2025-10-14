package com.suyos.registration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyos.registration.dto.UserProfileDTO;
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
 * REST controller for user profile management operations.
 * 
 * Provides endpoints for user profile retrieval and updates.
 * Handles HTTP requests and delegates business logic to the UserService layer.
 * 
 * @author Joel Salazar
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
public class UserController {
    
    /** Service layer for user business logic */
    private final UserService userService;



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
    public ResponseEntity<UserProfileDTO> getUserProfile(@Parameter(description = "User ID") @PathVariable Long userId) {
        UserProfileDTO userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
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
    public ResponseEntity<UserProfileDTO> updateUserProfile(@Parameter(description = "User ID") @PathVariable Long userId, 
                                             @Valid @RequestBody UserUpdateDTO updateDTO) {
        UserProfileDTO updatedProfile = userService.updateUserProfile(userId, updateDTO);
        return ResponseEntity.ok(updatedProfile);
    }

}