package com.suyos.registration.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suyos.registration.dto.AuthenticationResponseDTO;
import com.suyos.registration.dto.UserLoginDTO;
import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserRegistrationDTO;
import com.suyos.registration.dto.UserUpdateDTO;
import com.suyos.registration.mapper.UserMapper;
import com.suyos.registration.model.User;
import com.suyos.registration.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service layer for user management operations.
 * 
 * Handles user authentication, registration, profile management, and security operations.
 * Implements business logic for user-related functionality including password validation,
 * account locking, and profile updates.
 * 
 * @author Joel Salazar
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    /** Repository for user data access operations */
    private final UserRepository userRepository;
    
    /** Mapper for converting between entities and DTOs */
    private final UserMapper userMapper;
    
    /** Password encoder for secure password hashing */
    private final PasswordEncoder passwordEncoder;
    
    /** JWT service for token operations */
    private final JwtService jwtService;
    
    /** Maximum allowed failed login attempts before account lock */
    private static final int MAX_FAILED_ATTEMPTS = 5;
    
    /** Account lock duration in hours */
    private static final int LOCK_DURATION_HOURS = 24;

    /**
     * Registers a new user account.
     * 
     * Creates a new user with encoded password and default security settings.
     * The account is enabled by default but requires email verification.
     * 
     * @param registrationDTO the registration information
     * @return the created user's profile information
     * @throws RuntimeException if email already exists
     */
    public UserProfileDTO registerUser(UserRegistrationDTO userRegistrationDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(userRegistrationDTO.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Convert DTO to entity
        User user = userMapper.toEntity(userRegistrationDTO);
        
        // Set security fields
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setAccountEnabled(true);
        user.setEmailVerified(false);
        user.setFailedLoginAttempts(0);
        user.setTermsAcceptedAt(LocalDateTime.now());
        user.setPrivacyPolicyAcceptedAt(LocalDateTime.now());
        
        // Save user
        User savedUser = userRepository.save(user);
        
        return userMapper.toProfileDTO(savedUser);
    }

    /**
     * Authenticates a user login attempt and generates JWT token.
     * 
     * Validates credentials and handles failed login attempts with account locking.
     * Updates last login time on successful authentication and returns JWT token.
     * Note: Email verification is not required for login (industry standard).
     * 
     * @param userLoginDTO the login credentials
     * @return authentication response with JWT token and user profile
     * @throws RuntimeException if authentication fails
     */
    public AuthenticationResponseDTO authenticateUser(UserLoginDTO userLoginDTO) {
        Optional<User> userOpt = userRepository.findActiveUserByEmail(userLoginDTO.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOpt.get();
        
        // Check if account is locked
        if (user.getAccountLocked() && user.getLockedUntil() != null 
            && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account is locked. Try again later.");
        }
        
        // Validate password
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new RuntimeException("Invalid email or password");
        }
        
        // Reset failed attempts and update last login
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        user.setAccountLocked(false);
        user.setLockedUntil(null);
        
        userRepository.save(user);
        
        // Generate JWT token
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new java.util.ArrayList<>())
                .build();
        
        String jwtToken = jwtService.generateToken(userDetails);
        
        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .user(userMapper.toProfileDTO(user))
                .build();
    }

    /**
     * Retrieves a user's profile information.
     * 
     * @param userId the user's ID
     * @return the user's profile information
     * @throws RuntimeException if user not found
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userMapper.toProfileDTO(user);
    }

    /**
     * Updates a user's profile information.
     * 
     * Only updates non-sensitive profile fields. Security-related fields
     * like email and password require separate operations.
     * 
     * @param userId the user's ID
     * @param updateDTO the updated profile information
     * @return the updated user's profile information
     * @throws RuntimeException if user not found
     */
    public UserProfileDTO updateUserProfile(Long userId, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update only allowed fields
        if (userUpdateDTO.getUsername() != null) {
            existingUser.setUsername(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getFirstName() != null) {
            existingUser.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.getLastName() != null) {
            existingUser.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getPhone() != null) {
            existingUser.setPhone(userUpdateDTO.getPhone());
        }
        if (userUpdateDTO.getProfilePictureUrl() != null) {
            existingUser.setProfilePictureUrl(userUpdateDTO.getProfilePictureUrl());
        }
        if (userUpdateDTO.getLocale() != null) {
            existingUser.setLocale(userUpdateDTO.getLocale());
        }
        if (userUpdateDTO.getTimezone() != null) {
            existingUser.setTimezone(userUpdateDTO.getTimezone());
        }
        
        User savedUser = userRepository.save(existingUser);
        return userMapper.toProfileDTO(savedUser);
    }

    /**
     * Extracts the current user's ID from the JWT authentication context.
     * 
     * @return The ID of the currently authenticated user
     * @throws RuntimeException if user is not authenticated or not found
     */
    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
    }

    /**
     * Handles failed login attempts and implements account locking.
     * 
     * Increments failed attempt counter and locks account if maximum
     * attempts are exceeded.
     * 
     * @param user the user with failed login attempt
     */
    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            user.setLockedUntil(LocalDateTime.now().plusHours(LOCK_DURATION_HOURS));
        }
        
        userRepository.save(user);
    }

}