package com.suyos.registration.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suyos.registration.dto.AuthenticationResponseDTO;
import com.suyos.registration.dto.UserLoginDTO;
import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserRegistrationDTO;
import com.suyos.registration.mapper.UserMapper;
import com.suyos.registration.model.User;
import com.suyos.registration.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling authentication and user registration operations.
 * 
 * Manages user registration, login authentication, JWT token generation,
 * and security features like account locking and failed login tracking.
 * 
 * @author Joel Salazar
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    /** Service for handling failed login attempts and account locking */
    private final LoginAttemptService loginAttemptService;
    
    /** Repository for user data access operations */
    private final UserRepository userRepository;
    
    /** Mapper for converting between User entities and DTOs */
    private final UserMapper userMapper;
    
    /** Password encoder for secure password hashing */
    private final PasswordEncoder passwordEncoder;
    
    /** JWT service for token generation and validation */
    private final JwtService jwtService;
    
    /** Security audit service for logging security events */
    private final SecurityAuditService securityAuditService;

    /**
     * Registers a new user account.
     * 
     * @param registrationDTO the registration information
     * @return the created user's profile information
     * @throws RuntimeException if email already exists
     */
    public UserProfileDTO registerUser(UserRegistrationDTO userRegistrationDTO, jakarta.servlet.http.HttpServletRequest request) {
        if (userRepository.existsByEmail(userRegistrationDTO.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        User user = userMapper.toEntity(userRegistrationDTO);
        
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setAccountEnabled(true);
        user.setEmailVerified(false);
        user.setFailedLoginAttempts(0);
        user.setTermsAcceptedAt(LocalDateTime.now());
        user.setPrivacyPolicyAcceptedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // Log registration event
        securityAuditService.logRegistration(
            savedUser.getUsername(), 
            savedUser.getEmail(),
            securityAuditService.getClientIp(request),
            securityAuditService.getUserAgent(request)
        );
        
        return userMapper.toProfileDTO(savedUser);
    }

    /**
     * Authenticates a user login attempt and generates JWT token.
     * 
     * @param userLoginDTO the login credentials
     * @return authentication response with JWT token and user profile
     * @throws RuntimeException if authentication fails
     */
    public AuthenticationResponseDTO authenticateUser(UserLoginDTO userLoginDTO, jakarta.servlet.http.HttpServletRequest request) {
        Optional<User> userOpt = userRepository.findActiveUserByEmail(userLoginDTO.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOpt.get();
        
        if (user.getAccountLocked() && user.getLockedUntil() != null 
            && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account is locked. Try again later.");
        }
        
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailedAttempt(user);
            securityAuditService.logLoginAttempt(
                userLoginDTO.getEmail(),
                securityAuditService.getClientIp(request),
                securityAuditService.getUserAgent(request),
                false
            );
            throw new RuntimeException("Invalid email or password");
        }
        
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        user.setAccountLocked(false);
        user.setLockedUntil(null);
        
        userRepository.save(user);
        
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new java.util.ArrayList<>())
                .build();
        
        String jwtToken = jwtService.generateToken(userDetails);
        
        // Log successful login
        securityAuditService.logLoginAttempt(
            user.getEmail(),
            securityAuditService.getClientIp(request),
            securityAuditService.getUserAgent(request),
            true
        );
        
        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .user(userMapper.toProfileDTO(user))
                .build();
    }

    /**
     * Processes Google OAuth2 authentication and creates or updates user account.
     * 
     * Handles Google OAuth2 user information. Creates new user if not exists,
     * or updates existing OAuth2 user. Generates JWT token for API access.
     * 
     * @param email user's email from Google
     * @param name user's full name from Google
     * @param providerId unique identifier from Google
     * @return authentication response with JWT token and user profile
     */
    public AuthenticationResponseDTO processGoogleOAuth2User(String email, String name, String providerId) {
        // First check if user exists by OAuth2 provider and ID
        Optional<User> oauth2User = userRepository.findByOauth2ProviderAndOauth2ProviderId("google", providerId);
        
        User user;
        if (oauth2User.isPresent()) {
            user = oauth2User.get();
        } else {
            // Check if user exists by email (traditional registration)
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                // Link existing account with Google OAuth2
                user = existingUser.get();
                user.setOauth2Provider("google");
                user.setOauth2ProviderId(providerId);
                user.setEmailVerified(true); // Google emails are verified
                userRepository.save(user);
            } else {
                // Create new user
                user = createGoogleOAuth2User(email, name, providerId);
            }
        }
        
        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate JWT token (same as traditional authentication)
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
     * Creates a new user from Google OAuth2 provider information.
     * 
     * @param email user's email from Google
     * @param name user's full name from Google
     * @param providerId unique identifier from Google
     * @return newly created user entity
     */
    private User createGoogleOAuth2User(String email, String name, String providerId) {
        String[] nameParts = name != null ? name.split(" ", 2) : new String[]{"User", ""};
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        User user = User.builder()
                .email(email)
                .username(email)
                .firstName(firstName)
                .lastName(lastName)
                .password("") // No password for OAuth2 users
                .oauth2Provider("google")
                .oauth2ProviderId(providerId)
                .emailVerified(true) // Google emails are pre-verified
                .accountEnabled(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .termsAcceptedAt(LocalDateTime.now())
                .privacyPolicyAcceptedAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(user);
    }
    
}