package com.suyos.registration.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserUpdateDTO;
import com.suyos.registration.mapper.UserMapper;
import com.suyos.registration.model.User;
import com.suyos.registration.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for user profile management operations.
 * 
 * Handles user profile retrieval, updates, and profile-related business logic.
 * Focuses on user data management excluding authentication operations.
 * 
 * @author Joel Salazar
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    /** Repository for user data access operations */
    private final UserRepository userRepository;
    
    /** Mapper for converting between entities and DTOs */
    private final UserMapper userMapper;



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

}