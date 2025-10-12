package com.suyos.registration.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user profile information.
 * 
 * This DTO is used to transfer user profile data from the API to clients
 * for display purposes. It contains public user information that can be
 * safely exposed in API responses without sensitive security data.
 * 
 * @author Joel Salazar
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    /**
     * User's email address.
     * 
     * Primary contact method and login identifier for the user account.
     */
    private String email;

    /**
     * User's chosen username for display purposes.
     * 
     * Unique identifier that can be shown publicly in the application interface.
     */
    private String username;

    /**
     * User's first name for personalization.
     * 
     * Used for greeting and personalizing the user experience.
     */
    private String firstName;

    /**
     * User's last name for identification.
     * 
     * Combined with first name for full user identification in the interface.
     */
    private String lastName;

    /**
     * User's phone number for contact purposes.
     * 
     * Optional field that may be used for notifications or account recovery.
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

    /**
     * Timestamp of the user's last successful login.
     * 
     * Used for displaying account activity information to the user.
     */
    private LocalDateTime lastLoginAt;

    /**
     * Timestamp when the user account was created.
     * 
     * Used for displaying "member since" information in the user profile.
     */
    private LocalDateTime createdAt;

}