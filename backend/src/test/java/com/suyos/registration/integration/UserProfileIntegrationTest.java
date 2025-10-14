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
import com.suyos.registration.dto.UserUpdateDTO;
import com.suyos.registration.repository.UserRepository;

/**
 * Integration tests for user profile management.
 * 
 * Tests complete user profile workflows including authentication-protected
 * endpoints for profile retrieval and updates. Validates JWT-based security
 * and end-to-end profile management functionality.
 * 
 * @author Joel Salazar
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserProfileIntegrationTest {

    /** MockMvc instance for simulating HTTP requests */
    @Autowired
    private MockMvc mockMvc;

    /** ObjectMapper for JSON serialization and deserialization */
    @Autowired
    private ObjectMapper objectMapper;

    /** UserRepository for database cleanup and user setup */
    @Autowired
    private UserRepository userRepository;

    /** JWT token for authenticated requests */
    private String jwtToken;
    
    /** User ID for profile management operations */
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        
        // Register and login to get JWT token
        UserRegistrationDTO registrationDTO = UserRegistrationDTO.builder()
                .email("profile@example.com")
                .password("password123")
                .username("profileuser")
                .firstName("Profile")
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
        userId = registerResponse.get("id").asLong();

        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("profile@example.com")
                .password("password123")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginResponse = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        jwtToken = loginResponse.get("accessToken").asText();
    }

    @Test
    void getUserProfile_Success() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + userId + "/profile")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("profile@example.com"))
                .andExpect(jsonPath("$.firstName").value("Profile"))
                .andExpect(jsonPath("$.lastName").value("Test"));
    }

    @Test
    void updateUserProfile_Success() throws Exception {
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .phone("1234567890")
                .build();

        mockMvc.perform(put("/api/v1/users/" + userId + "/profile")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.phone").value("1234567890"));
    }

    @Test
    void getUserProfile_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + userId + "/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUserProfile_Unauthorized() throws Exception {
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .build();

        mockMvc.perform(put("/api/v1/users/" + userId + "/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnauthorized());
    }
}