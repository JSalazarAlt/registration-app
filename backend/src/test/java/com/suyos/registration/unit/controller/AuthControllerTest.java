package com.suyos.registration.unit.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suyos.registration.controller.AuthController;
import com.suyos.registration.dto.AuthenticationResponseDTO;
import com.suyos.registration.dto.UserLoginDTO;
import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserRegistrationDTO;
import com.suyos.registration.service.AuthService;
import com.suyos.registration.service.TokenBlacklistService;

/**
 * Unit tests for AuthController.
 * 
 * Tests REST API endpoints for user authentication including registration,
 * login, and logout operations. Uses MockMvc for HTTP request simulation
 * and mocked service dependencies.
 * 
 * @author Joel Salazar
 */
@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    /** MockMvc instance for simulating HTTP requests */
    @Autowired
    private MockMvc mockMvc;

    /** Mock authentication service for business logic operations */
    @Mock
    private AuthService authService;

    /** Mock token blacklist service for logout functionality */
    @Mock
    private TokenBlacklistService tokenBlacklistService;

    /** ObjectMapper for JSON serialization and deserialization */
    @Autowired
    private ObjectMapper objectMapper;

    /** Test data for user registration requests */
    private UserRegistrationDTO registrationDTO;
    
    /** Test data for user login requests */
    private UserLoginDTO loginDTO;
    
    /** Test data for user profile responses */
    private UserProfileDTO profileDTO;
    
    /** Test data for authentication responses with JWT tokens */
    private AuthenticationResponseDTO authResponseDTO;

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

        profileDTO = UserProfileDTO.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .build();

        authResponseDTO = AuthenticationResponseDTO.builder()
                .accessToken("jwt-token")
                .expiresIn(86400L)
                .user(profileDTO)
                .build();
    }

    @Test
    void registerUser_Success() throws Exception {
        when(authService.registerUser(any(UserRegistrationDTO.class), any())).thenReturn(profileDTO);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(authService).registerUser(any(UserRegistrationDTO.class), any());
    }

    @Test
    void registerUser_EmailAlreadyExists() throws Exception {
        when(authService.registerUser(any(UserRegistrationDTO.class), any()))
                .thenThrow(new RuntimeException("Email already registered"));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Registration failed: Email already registered"));
    }

    @Test
    void loginUser_Success() throws Exception {
        when(authService.authenticateUser(any(UserLoginDTO.class), any())).thenReturn(authResponseDTO);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.expiresIn").value(86400))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));

        verify(authService).authenticateUser(any(UserLoginDTO.class), any());
    }

    @Test
    void loginUser_InvalidCredentials() throws Exception {
        when(authService.authenticateUser(any(UserLoginDTO.class), any()))
                .thenThrow(new RuntimeException("Invalid email or password"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Authentication failed: Invalid email or password"));
    }

    @Test
    void logoutUser_Success() throws Exception {
        doNothing().when(tokenBlacklistService).blacklistToken(anyString());

        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));

        verify(tokenBlacklistService).blacklistToken("jwt-token");
    }

    @Test
    void logoutUser_NoToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No valid token found"));

        verify(tokenBlacklistService, never()).blacklistToken(anyString());
    }
}