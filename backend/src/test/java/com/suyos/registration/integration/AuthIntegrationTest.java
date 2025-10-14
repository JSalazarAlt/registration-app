package com.suyos.registration.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suyos.registration.dto.UserLoginDTO;
import com.suyos.registration.dto.UserRegistrationDTO;
import com.suyos.registration.repository.UserRepository;

/**
 * Integration tests for authentication flow.
 * 
 * Tests complete user registration and login workflows with real database
 * interactions. Validates end-to-end authentication functionality including
 * JWT token generation and user account creation.
 * 
 * @author Joel Salazar
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    /** MockMvc instance for simulating HTTP requests */
    @Autowired
    private MockMvc mockMvc;

    /** ObjectMapper for JSON serialization and deserialization */
    @Autowired
    private ObjectMapper objectMapper;

    /** UserRepository for database cleanup and verification */
    @Autowired
    private UserRepository userRepository;

    /** Test data for user registration operations */
    private UserRegistrationDTO registrationDTO;
    
    /** Test data for user login operations */
    private UserLoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        registrationDTO = UserRegistrationDTO.builder()
                .email("integration@example.com")
                .password("password123")
                .username("integrationuser")
                .firstName("Integration")
                .lastName("Test")
                .termsAccepted(true)
                .privacyPolicyAccepted(true)
                .build();

        loginDTO = UserLoginDTO.builder()
                .email("integration@example.com")
                .password("password123")
                .build();
    }

    @Test
    void completeAuthFlow_RegisterAndLogin() throws Exception {
        // Register user
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("integration@example.com"));

        // Login with registered user
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.user.email").value("integration@example.com"));
    }

    @Test
    void registerUser_DuplicateEmail() throws Exception {
        // Register first user
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated());

        // Try to register with same email
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Registration failed: Email already registered"));
    }

    @Test
    void loginUser_InvalidCredentials() throws Exception {
        // Register user first
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated());

        // Try login with wrong password
        UserLoginDTO wrongLoginDTO = UserLoginDTO.builder()
                .email("integration@example.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongLoginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Authentication failed: Invalid email or password"));
    }
}