package com.suyos.registration.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserUpdateDTO;
import com.suyos.registration.mapper.UserMapper;
import com.suyos.registration.model.User;
import com.suyos.registration.repository.UserRepository;
import com.suyos.registration.service.UserService;

/**
 * Unit tests for UserService.
 * 
 * Tests user profile management operations including profile retrieval
 * and updates. Validates business logic with mocked dependencies.
 * 
 * @author Joel Salazar
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /** Mock repository for user data access operations */
    @Mock
    private UserRepository userRepository;
    
    /** Mock mapper for converting between User entities and DTOs */
    @Mock
    private UserMapper userMapper;
    
    /** UserService instance under test with injected mocks */
    @InjectMocks
    private UserService userService;
    
    /** Test user entity for database operations */
    private User user;
    
    /** Test user profile DTO for response validation */
    private UserProfileDTO profileDTO;
    
    /** Test user update DTO for profile modification operations */
    private UserUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .phone("1234567890")
                .build();

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
    void getUserProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toProfileDTO(user)).thenReturn(profileDTO);

        UserProfileDTO result = userService.getUserProfile(1L);

        assertNotNull(result);
        assertEquals(profileDTO.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserProfile_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserProfile(1L));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateUserProfile_Success() {
        User updatedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Updated")
                .lastName("Name")
                .phone("0987654321")
                .build();

        UserProfileDTO updatedProfileDTO = UserProfileDTO.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .firstName("Updated")
                .lastName("Name")
                .phone("0987654321")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toProfileDTO(updatedUser)).thenReturn(updatedProfileDTO);

        UserProfileDTO result = userService.updateUserProfile(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserProfile_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.updateUserProfile(1L, updateDTO));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}