package com.suyos.registration.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.suyos.registration.dto.UserProfileDTO;
import com.suyos.registration.dto.UserRegistrationDTO;
import com.suyos.registration.dto.UserUpdateDTO;
import com.suyos.registration.model.User;

/**
 * MapStruct mapper interface for converting between User entities and DTOs.
 * 
 * This interface defines the mapping contract between the internal User entity
 * and various user-related DTOs. MapStruct generates the implementation at compile time,
 * providing type-safe and efficient object mapping without reflection.
 * 
 * The Spring component model integration allows this mapper to be injected
 * as a Spring bean into other components.
 * 
 * @author Joel Salazar
 * @version 1.0
 * @since 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "mustChangePassword", ignore = true)
    @Mapping(target = "passwordChangedAt", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    @Mapping(target = "locale", ignore = true)
    @Mapping(target = "timezone", ignore = true)
    @Mapping(target = "termsAcceptedAt", ignore = true)
    @Mapping(target = "privacyPolicyAcceptedAt", ignore = true)
    @Mapping(target = "accountEnabled", constant = "true")
    @Mapping(target = "accountLocked", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRegistrationDTO userRegistrationDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "mustChangePassword", ignore = true)
    @Mapping(target = "passwordChangedAt", ignore = true)
    @Mapping(target = "termsAcceptedAt", ignore = true)
    @Mapping(target = "privacyPolicyAcceptedAt", ignore = true)
    @Mapping(target = "accountEnabled", ignore = true)
    @Mapping(target = "accountLocked", ignore = true)
    @Mapping(target = "lockedUntil", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserUpdateDTO userUpdateDTO);

    UserProfileDTO toProfileDTO(User user);

    UserUpdateDTO toUpdateDTO(User user);
    
}