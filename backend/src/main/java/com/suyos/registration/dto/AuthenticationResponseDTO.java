package com.suyos.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication response containing JWT token and user information.
 * 
 * This DTO is returned after successful user authentication, providing the client
 * with a JWT access token for subsequent API requests and basic user profile information.
 * 
 * @author Joel Salazar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponseDTO {
    
    /** JWT access token for API authentication */
    private String accessToken;
    
    /** Token type identifier */
    @Builder.Default
    private String tokenType = "Bearer";
    
    /** Token expiration time in seconds */
    private Long expiresIn;
    
    /** User profile information */
    private UserProfileDTO user;

}