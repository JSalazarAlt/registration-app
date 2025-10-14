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

    /**
     * Registers a new user account.
     * 
     * @param registrationDTO the registration information
     * @return the created user's profile information
     * @throws RuntimeException if email already exists
     */
    public UserProfileDTO registerUser(UserRegistrationDTO userRegistrationDTO) {
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
        
        return userMapper.toProfileDTO(savedUser);
    }

    /**
     * Authenticates a user login attempt and generates JWT token.
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
        
        if (user.getAccountLocked() && user.getLockedUntil() != null 
            && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account is locked. Try again later.");
        }
        
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailedAttempt(user);
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
        
        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .user(userMapper.toProfileDTO(user))
                .build();
    }
}