package com.suyos.registration.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user profile update information.
 * 
 * This DTO is used to capture and validate user input when updating
 * profile information. It contains only the fields that users are
 * allowed to modify, excluding sensitive security-related data.
 * 
 * @author Joel Salazar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    
    /** User's chosen username for display purposes */
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9]+$", 
        message = "Username must contain only alphanumeric characters"
    )
    private String username;
    
    /** User's first name for personalization */
    @Pattern(
        regexp = "^[a-zA-Z ]+$", 
        message = "First name must contain only alphabetic characters and spaces"
    )
    private String firstName;
    
    /** User's last name for identification */
    @Pattern(
        regexp = "^[a-zA-Z ]+$", 
        message = "Last name must contain only alphabetic characters and spaces"
    )
    private String lastName;
    
    /** User's phone number for contact purposes */
    @Pattern(
        regexp = "^$|^\\+?[0-9]{7,15}$",
        message = "Phone must be 7 to 15 digits, with optional + for country code"
    )
    private String phone;
    
    /** URL to the user's profile picture */
    private String profilePictureUrl;
    
    /** User's preferred language locale */
    private String locale;
    
    /** User's timezone preference */
    private String timezone;
    
}