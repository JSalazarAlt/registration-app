package com.suyos.registration.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.suyos.registration.dto.AuthenticationResponseDTO;
import com.suyos.registration.dto.UserLoginDTO;
import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserRegistrationDTO;
import com.suyos.registration.mapper.UserMapper;
import com.suyos.registration.model.User;
import com.suyos.registration.repository.UserRepository;
import com.suyos.registration.service.AuthService;
import com.suyos.registration.service.JwtService;
import com.suyos.registration.service.LoginAttemptService;
import com.suyos.registration.service.SecurityAuditService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Unit tests for AuthService.
 * 
 * Tests user registration and authentication operations with comprehensive
 * mocking of dependencies. Validates business logic for account creation,
 * login validation, and security features like account locking.
 * 
 * @author Joel Salazar
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    /** Mock service for handling failed login attempts and account locking */
    @Mock
    private LoginAttemptService loginAttemptService;
    
    /** Mock repository for user data access operations */
    @Mock
    private UserRepository userRepository;
    
    /** Mock mapper for converting between User entities and DTOs */
    @Mock
    private UserMapper userMapper;
    
    /** Mock password encoder for secure password hashing */
    @Mock
    private PasswordEncoder passwordEncoder;
    
    /** Mock JWT service for token generation and validation */
    @Mock
    private JwtService jwtService;
    
    /** Mock security audit service for logging security events */
    @Mock
    private SecurityAuditService securityAuditService;
    
    /** Mock HTTP servlet request for audit logging */
    @Mock
    private HttpServletRequest mockRequest;
    
    /** AuthService instance under test with injected mocks */
    @InjectMocks
    private AuthService authService;
    
    /** Test data for user registration operations */
    private UserRegistrationDTO registrationDTO;
    
    /** Test data for user login operations */
    private UserLoginDTO loginDTO;
    
    /** Test user entity for database operations */
    private User user;
    
    /** Test user profile DTO for response validation */
    private UserProfileDTO profileDTO;

    @BeforeEach
    void setUp() {
        registrationDTO = UserRegistrationDTO.builder()
                .email("test@example.com")
                .password("password123")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .termsAccepted(true)
                .privacyPolicyAccepted(true)
                .build();

        loginDTO = UserLoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .accountEnabled(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .build();

        profileDTO = UserProfileDTO.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(registrationDTO)).thenReturn(user);
        when(passwordEncoder.encode(registrationDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toProfileDTO(user)).thenReturn(profileDTO);

        UserProfileDTO result = authService.registerUser(registrationDTO, mockRequest);

        assertNotNull(result);
        assertEquals(profileDTO.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(registrationDTO.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.registerUser(registrationDTO, mockRequest));

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_Success() {
        when(userRepository.findActiveUserByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toProfileDTO(user)).thenReturn(profileDTO);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(86400L);

        AuthenticationResponseDTO result = authService.authenticateUser(loginDTO, mockRequest);

        assertNotNull(result);
        assertEquals("jwt-token", result.getAccessToken());
        assertEquals(86400L, result.getExpiresIn());
        assertEquals(profileDTO, result.getUser());
        verify(userRepository).save(user);
    }

    @Test
    void authenticateUser_InvalidEmail() {
        when(userRepository.findActiveUserByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticateUser(loginDTO, mockRequest));

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void authenticateUser_AccountLocked() {
        user.setAccountLocked(true);
        user.setLockedUntil(LocalDateTime.now().plusHours(1));
        when(userRepository.findActiveUserByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticateUser(loginDTO, mockRequest));

        assertEquals("Account is locked. Try again later.", exception.getMessage());
    }

    @Test
    void authenticateUser_InvalidPassword() {
        when(userRepository.findActiveUserByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticateUser(loginDTO, mockRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(loginAttemptService).recordFailedAttempt(user);
    }
}