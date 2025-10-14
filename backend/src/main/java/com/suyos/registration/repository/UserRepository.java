package com.suyos.registration.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.suyos.registration.model.User;

/**
 * Repository interface for User entity data access operations.
 * 
 * This interface extends JpaRepository to provide standard CRUD operations
 * and authentication-specific query methods for User entities. Spring Data JPA
 * automatically generates the implementation at runtime.
 * 
 * Automatically available operations include:
 * - findAll() - retrieve all users
 * - findById() - find user by ID
 * - save() - create or update user
 * - deleteById() - delete user by ID
 * 
 * Additional authentication operations include login validation,
 * account security management, and user verification queries.
 * 
 * @author Joel Salazar
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their email address.
     * 
     * @param email The email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their username.
     * 
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if an email address is already registered.
     * 
     * @param email The email address to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Checks if a username is already taken.
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Finds an active user by email address.
     * 
     * Returns user only if account is enabled, not locked, and email is verified.
     * Used for login validation to ensure account is in good standing.
     * 
     * @param email The email address to search for
     * @return Optional containing the active user if found, empty otherwise
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.accountEnabled = true AND u.accountLocked = false")
    Optional<User> findActiveUserByEmail(@Param("email") String email);
    
    /**
     * Updates the failed login attempts count for a user.
     * 
     * Used for tracking consecutive failed login attempts for security purposes.
     * 
     * @param email The email of the user to update
     * @param attempts The new failed attempts count
     */
    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.email = :email")
    void updateFailedLoginAttempts(@Param("email") String email, @Param("attempts") Integer attempts);
    
    /**
     * Locks a user account until the specified time.
     * 
     * Sets account as locked and defines when the lock expires.
     * Used for temporary account suspension due to security violations.
     * 
     * @param email The email of the user to lock
     * @param lockedUntil The timestamp when the lock expires
     */
    @Modifying
    @Query("UPDATE User u SET u.accountLocked = true, u.lockedUntil = :lockedUntil WHERE u.email = :email")
    void lockAccount(@Param("email") String email, @Param("lockedUntil") LocalDateTime lockedUntil);
    
    /**
     * Unlocks a user account and resets failed login attempts.
     * 
     * Removes account lock, clears lock expiration time, and resets
     * failed login attempts counter to zero.
     * 
     * @param email The email of the user to unlock
     */
    @Modifying
    @Query("UPDATE User u SET u.accountLocked = false, u.lockedUntil = null, u.failedLoginAttempts = 0 WHERE u.email = :email")
    void unlockAccount(@Param("email") String email);
    
    /**
     * Finds a user by OAuth2 provider and provider ID.
     * 
     * Used to locate existing OAuth2 users during Google authentication flow.
     * 
     * @param provider The OAuth2 provider name (google)
     * @param providerId The unique identifier from the OAuth2 provider
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByOauth2ProviderAndOauth2ProviderId(String provider, String providerId);
    
}