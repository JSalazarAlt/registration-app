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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suyos.registration.dto.UserLoginDTO;
import com.suyos.registration.dto.UserRegistrationDTO;
import com.suyos.registration.repository.UserRepository;

/**
 * Integration tests for security features.
 * 
 * Tests JWT authentication, token blacklisting, CORS configuration,
 * and other security-related functionality. Validates complete security
 * workflows including token lifecycle management.
 * 
 * @author Joel Salazar
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTest {

    /** MockMvc instance for simulating HTTP requests */
    @Autowired
    private MockMvc mockMvc;

    /** ObjectMapper for JSON serialization and deserialization */
    @Autowired
    private ObjectMapper objectMapper;

    /** UserRepository for database cleanup and user setup */
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void jwtAuthentication_ValidToken() throws Exception {
        // Register user
        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .email("security@example.com")
                .password("password123")
                .username("securityuser")
                .firstName("Security")
                .lastName("Test")
                .termsAccepted(true)
                .privacyPolicyAccepted(true)
                .build();

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode registerResponse = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        Long userId = registerResponse.get("id").asLong();

        // Login to get token
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("security@example.com")
                .password("password123")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String jwtToken = loginResponse.get("accessToken").asText();

        // Access protected endpoint with valid token
        mockMvc.perform(get("/api/v1/users/" + userId + "/profile")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void jwtAuthentication_InvalidToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/profile")
                .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void jwtAuthentication_NoToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/1/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_TokenBlacklisting() throws Exception {
        // Register and login
        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .email("logout@example.com")
                .password("password123")
                .username("logoutuser")
                .firstName("Logout")
                .lastName("Test")
                .termsAccepted(true)
                .privacyPolicyAccepted(true)
                .build();

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode registerResponse = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        Long userId = registerResponse.get("id").asLong();

        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("logout@example.com")
                .password("password123")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String jwtToken = loginResponse.get("accessToken").asText();

        // Verify token works
        mockMvc.perform(get("/api/v1/users/" + userId + "/profile")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        // Logout
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        // Verify token is blacklisted
        mockMvc.perform(get("/api/v1/users/" + userId + "/profile")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void corsConfiguration_AllowedOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/auth/login")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}