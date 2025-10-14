package com.suyos.registration.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user in the expense tracking system.
 * 
 * This class maps to the 'users' table in the database and contains
 * all the necessary fields to track individual user information.
 * 
 * @author Joel Salazar
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique identifier for the user record.
     * 
     * Auto-generated using database identity strategy to ensure
     * unique primary key values for each user account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * User's chosen username for display purposes.
     * 
     * Must be unique across all users. Optional alternative
     * identifier that can be used for login if implemented.
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    /**
     * User's first name for personal identification.
     * 
     * Required field used for personalization and display purposes
     * throughout the application interface.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * User's last name for personal identification.
     * 
     * Required field used for personalization and display purposes
     * throughout the application interface.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * User's email address used for login and communication.
     * 
     * Must be unique across all users and serves as the primary
     * identifier for authentication. Used for password reset
     * and account verification emails.
     */
    @Column(name = "email", nullable = false,unique = true)
    private String email;

    /**
     * Flag indicating if the user's email address has been verified.
     * 
     * Unverified users may have limited access until they confirm
     * their email address through the verification process.
     */
    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    /**
     * Encrypted password hash for user authentication.
     * 
     * Never stores plain text passwords. Uses BCrypt hashing
     * algorithm for secure password storage and verification.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Flag indicating if user must change password on next login.
     * 
     * Used for forced password resets due to security policies
     * or administrative requirements. Defaults to false.
     */
    @Builder.Default
    @Column(name = "must_change_password", nullable = false)
    private Boolean mustChangePassword = false;

    /**
     * Timestamp when the user's password was last changed.
     * 
     * Used for password aging policies and security auditing.
     * Helps track when users last updated their credentials.
     */
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    /**
     * User's phone number for contact purposes.
     * 
     * Optional field that can be used for two-factor authentication
     * or emergency contact information.
     */
    @Column(name = "phone")
    private String phone;

    /**
     * URL to the user's profile picture.
     * 
     * Optional field storing the location of the user's avatar image
     * for display in the application interface.
     */
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    /**
     * User's preferred language locale.
     * 
     * Used for internationalization to display the application
     * interface in the user's preferred language.
     */
    @Column(name = "locale")
    private String locale;

    /**
     * User's timezone preference.
     * 
     * Used for displaying dates and times in the user's
     * local timezone throughout the application.
     */
    @Column(name = "timezone")
    private String timezone;

    /**
     * Timestamp when the user accepted the terms of service.
     * 
     * Required for legal compliance and tracking user consent
     * to application terms and conditions.
     */
    @Column(name = "terms_accepted_at", nullable = false)
    private LocalDateTime termsAcceptedAt;

    /**
     * Timestamp when the user accepted the privacy policy.
     * 
     * Required for GDPR compliance and tracking user consent
     * to data processing and privacy terms.
     */
    @Column(name = "privacy_policy_accepted_at", nullable = false)
    private LocalDateTime privacyPolicyAcceptedAt;

    /**
     * Flag indicating if the user account is enabled.
     * 
     * Disabled accounts cannot log in or access the system.
     * Used for administrative account suspension.
     */
    @Builder.Default
    @Column(name = "account_enabled", nullable = false)
    private Boolean accountEnabled = true;

    /**
     * Flag indicating if the user account is temporarily locked.
     * 
     * Locked accounts cannot log in until the lock is removed
     * or expires. Used for security breach prevention.
     */
    @Builder.Default
    @Column(name = "account_locked", nullable = false)
    private Boolean accountLocked = false;

    /**
     * Timestamp when the account lock expires.
     * 
     * Null if account is not locked. When this time passes,
     * the account can be automatically unlocked.
     */
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    /**
     * Timestamp of the user's last successful login.
     * 
     * Used for security monitoring and user activity tracking.
     * Helps identify inactive accounts and suspicious activity.
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * Counter for consecutive failed login attempts.
     * 
     * Used to track security violations and trigger account
     * lockout when threshold is exceeded. Resets on successful login.
     */
    @Builder.Default
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;
    
    /**
     * Timestamp when the user record was first created in the system.
     * 
     * Automatically set when the entity is first persisted.
     * This field is immutable after creation (updatable = false).
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user record was last modified.
     * 
     * Automatically updated whenever the entity is saved.
     * Useful for tracking when changes were made to user data.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}