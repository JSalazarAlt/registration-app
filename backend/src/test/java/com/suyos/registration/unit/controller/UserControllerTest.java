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
import com.suyos.registration.controller.UserController;
import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserUpdateDTO;
import com.suyos.registration.service.UserService;

/**
 * Unit tests for UserController.
 * 
 * Tests REST API endpoints for user profile management including
 * profile retrieval and updates. Uses MockMvc for HTTP request
 * simulation and mocked service dependencies.
 * 
 * @author Joel Salazar
 */
@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    /** MockMvc instance for simulating HTTP requests */
    @Autowired
    private MockMvc mockMvc;

    /** Mock user service for profile management operations */
    @Mock
    private UserService userService;

    /** ObjectMapper for JSON serialization and deserialization */
    @Autowired
    private ObjectMapper objectMapper;

    /** Test data for user profile responses */
    private UserProfileDTO profileDTO;
    
    /** Test data for user profile update requests */
    private UserUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        profileDTO = UserProfileDTO.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .phone("1234567890")
                .build();

        updateDTO = UserUpdateDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .phone("0987654321")
                .build();
    }

    @Test
    void getUserProfile_Success() throws Exception {
        when(userService.getUserProfile(1L)).thenReturn(profileDTO);

        mockMvc.perform(get("/api/v1/users/1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));

        verify(userService).getUserProfile(1L);
    }

    @Test
    void getUserProfile_UserNotFound() throws Exception {
        when(userService.getUserProfile(1L)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/v1/users/1/profile"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found: User not found"));
    }

    @Test
    void updateUserProfile_Success() throws Exception {
        UserProfileDTO updatedProfile = UserProfileDTO.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Updated")
                .lastName("Name")
                .phone("0987654321")
                .build();

        when(userService.updateUserProfile(eq(1L), any(UserUpdateDTO.class))).thenReturn(updatedProfile);

        mockMvc.perform(put("/api/v1/users/1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.phone").value("0987654321"));

        verify(userService).updateUserProfile(eq(1L), any(UserUpdateDTO.class));
    }

    @Test
    void updateUserProfile_UserNotFound() throws Exception {
        when(userService.updateUserProfile(eq(1L), any(UserUpdateDTO.class)))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/api/v1/users/1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Profile update failed: User not found"));
    }

    @Test
    void updateUserProfile_InvalidData() throws Exception {
        UserUpdateDTO invalidUpdateDTO = UserUpdateDTO.builder().build(); // Empty data

        mockMvc.perform(put("/api/v1/users/1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateDTO)))
                .andExpect(status().isOk()); // Will pass validation but service might handle business logic

        verify(userService).updateUserProfile(eq(1L), any(UserUpdateDTO.class));
    }
}