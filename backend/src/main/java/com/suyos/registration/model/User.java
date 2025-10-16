package com.suyos.registration.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user in the registration and authentication system.
 * 
 * This class maps to the 'users' table in the database and contains
 * all the necessary fields for user account management and authentication.
 * 
 * @author Joel Salazar
 */
@Entity
@Table(name = "users", indexes = {
    @jakarta.persistence.Index(name = "idx_user_email", columnList = "email"),
    @jakarta.persistence.Index(name = "idx_user_username", columnList = "username"),
    @jakarta.persistence.Index(name = "idx_user_oauth2", columnList = "oauth2_provider, oauth2_provider_id")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Unique identifier for the user record */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** User's chosen username for display purposes */
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    /** User's first name for personal identification */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /** User's last name for personal identification */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /** User's email address used for login and communication */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /** Flag indicating if the user's email address has been verified */
    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    /** Encrypted password hash for user authentication */
    @Column(name = "password", nullable = false)
    private String password;

    /** Flag indicating if user must change password on next login */
    @Builder.Default
    @Column(name = "must_change_password", nullable = false)
    private Boolean mustChangePassword = false;

    /** Timestamp when the user's password was last changed */
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    /** User's phone number for contact purposes */
    @Column(name = "phone")
    private String phone;

    /** URL to the user's profile picture */
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    /** User's preferred language locale */
    @Column(name = "locale")
    private String locale;

    /** User's timezone preference */
    @Column(name = "timezone")
    private String timezone;

    /** Timestamp when the user accepted the terms of service */
    @Column(name = "terms_accepted_at", nullable = false)
    private LocalDateTime termsAcceptedAt;

    /** Timestamp when the user accepted the privacy policy */
    @Column(name = "privacy_policy_accepted_at", nullable = false)
    private LocalDateTime privacyPolicyAcceptedAt;

    /** Flag indicating if the user account is enabled */
    @Builder.Default
    @Column(name = "account_enabled", nullable = false)
    private Boolean accountEnabled = true;

    /** Flag indicating if the user account is temporarily locked */
    @Builder.Default
    @Column(name = "account_locked", nullable = false)
    private Boolean accountLocked = false;

    /** Timestamp when the account lock expires */
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    /** Timestamp of the user's last successful login */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /** Counter for consecutive failed login attempts */
    @Builder.Default
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;
    
    /** Timestamp when the user record was first created in the system */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Timestamp when the user record was last modified */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** OAuth2 provider name (google) - null for traditional login */
    @Column(name = "oauth2_provider")
    private String oauth2Provider;

    /** Unique identifier from OAuth2 provider - null for traditional login */
    @Column(name = "oauth2_provider_id")
    private String oauth2ProviderId;

}