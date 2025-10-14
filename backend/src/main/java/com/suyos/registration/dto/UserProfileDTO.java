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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    /** User's unique identifier */
    private Long id;

    /** User's email address */
    private String email;

    /** User's chosen username for display purposes */
    private String username;

    /** User's first name for personalization */
    private String firstName;

    /** User's last name for identification */
    private String lastName;

    /** User's phone number for contact purposes */
    private String phone;

    /** URL to the user's profile picture */
    private String profilePictureUrl;

    /** User's preferred language locale */
    private String locale;

    /** User's timezone preference */
    private String timezone;

    /** Timestamp of the user's last successful login */
    private LocalDateTime lastLoginAt;

    /** Timestamp when the user account was created */
    private LocalDateTime createdAt;

}