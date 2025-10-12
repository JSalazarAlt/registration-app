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
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    
    /**
     * User's chosen username for display purposes.
     * 
     * Must be unique across the system and between 3-20 characters.
     * Only alphanumeric characters are allowed.
     * Used for public identification within the application.
     */
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only alphanumeric characters")
    private String username;
    
    /**
     * User's first name for personalization.
     * 
     * Only alphabetic characters are allowed.
     * Used for greeting and personalizing the user experience
     * throughout the application interface.
     */
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only alphabetic characters")
    private String firstName;
    
    /**
     * User's last name for identification.
     * 
     * Only alphabetic characters are allowed.
     * Combined with first name for full user identification
     * and formal communication purposes.
     */
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name must contain only alphabetic characters")
    private String lastName;
    
    /**
     * User's phone number for contact purposes.
     * 
     * Optional field that may be used for account recovery,
     * two-factor authentication, or important notifications.
     */
    private String phone;
    
    /**
     * URL to the user's profile picture.
     * 
     * Optional field for displaying user avatar in the application interface.
     */
    private String profilePictureUrl;
    
    /**
     * User's preferred language locale.
     * 
     * Used for internationalization to display content in user's language.
     */
    private String locale;
    
    /**
     * User's timezone preference.
     * 
     * Used for displaying dates and times in the user's local timezone.
     */
    private String timezone;
    
}