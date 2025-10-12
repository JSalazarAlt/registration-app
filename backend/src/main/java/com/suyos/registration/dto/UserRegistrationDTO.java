package com.suyos.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration information.
 * 
 * This DTO is used to capture and validate user input during the account
 * registration process. It includes all required fields for creating a new
 * user account along with legal compliance fields for terms and privacy policy.
 * 
 * @author Joel Salazar
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {
    
    /**
     * User's chosen username for display purposes.
     * 
     * Must be unique across the system and between 3-20 characters.
     * Used for public identification within the application.
     */
    /**
     * User's chosen username for display purposes.
     * 
     * Must be unique across the system and between 3-20 characters.
     * Used for public identification within the application.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9]+$", 
        message = "Username must contain only alphanumeric characters"
    )
    private String username;
    
    /**
     * User's first name for personalization.
     * 
     * Required field used for greeting and personalizing the user experience
     * throughout the application interface.
     */
    @NotBlank(message = "First name is required")
    @Pattern(
        regexp = "^[a-zA-Z ]+$", 
        message = "First name must contain only alphabetic characters and spaces"
    )
    private String firstName;

    /**
     * User's last name for identification.
     * 
     * Required field combined with first name for full user identification
     * and formal communication purposes.
     */
    @NotBlank(message = "Last name is required")
    @Pattern(
        regexp = "^[a-zA-Z ]+$", 
        message = "Last name must contain only alphabetic characters and spaces"
    )
    private String lastName;

    /**
     * User's email address for account creation and login.
     * 
     * Must be a valid email format and will be used as the primary
     * identifier for authentication and communication.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    /**
     * User's password for account security.
     * 
     * Must be at least 8 characters long for security purposes.
     * Will be hashed before storage in the database.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * User's phone number for contact purposes.
     * 
     * Optional field that may be used for account recovery,
     * two-factor authentication, or important notifications.
     */
    @Pattern(
        regexp = "^\\+?[0-9]{7,15}$",
        message = "Phone must be 7 to 15 digits, with optional + for country code"
    )
    private String phone;
    
    /**
     * Confirmation that user has accepted the terms of service.
     * 
     * Required for legal compliance. Must be true to proceed with
     * account creation and use of the application services.
     */
    @NotNull(message = "Terms acceptance is required")
    private Boolean termsAccepted;
    
    /**
     * Confirmation that user has accepted the privacy policy.
     * 
     * Required for GDPR compliance and data processing consent.
     * Must be true to proceed with account creation.
     */
    @NotNull(message = "Privacy policy acceptance is required")
    private Boolean privacyPolicyAccepted;
    
}