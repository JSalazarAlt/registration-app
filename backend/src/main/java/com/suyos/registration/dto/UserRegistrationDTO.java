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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {
    
    /** User's chosen username for display purposes */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9]+$", 
        message = "Username must contain only alphanumeric characters"
    )
    private String username;
    
    /** User's first name for personalization */
    @NotBlank(message = "First name is required")
    @Pattern(
        regexp = "^[a-zA-Z ]+$", 
        message = "First name must contain only alphabetic characters and spaces"
    )
    private String firstName;

    /** User's last name for identification */
    @NotBlank(message = "Last name is required")
    @Pattern(
        regexp = "^[a-zA-Z ]+$", 
        message = "Last name must contain only alphabetic characters and spaces"
    )
    private String lastName;

    /** User's email address for account creation and login */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    /** User's password for account security */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /** User's phone number for contact purposes */
    @Pattern(
        regexp = "^\\+?[0-9]{7,15}$",
        message = "Phone must be 7 to 15 digits, with optional + for country code"
    )
    private String phone;
    
    /** Confirmation that user has accepted the terms of service */
    @NotNull(message = "Terms acceptance is required")
    private Boolean termsAccepted;
    
    /** Confirmation that user has accepted the privacy policy */
    @NotNull(message = "Privacy policy acceptance is required")
    private Boolean privacyPolicyAccepted;
    
}