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
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponseDTO {
    
    /**
     * JWT access token for API authentication.
     * 
     * This token should be included in the Authorization header
     * as "Bearer {token}" for all subsequent API requests.
     */
    private String accessToken;
    
    /**
     * Token type identifier.
     * 
     * Always "Bearer" for JWT tokens, indicating the authentication scheme.
     */
    @Builder.Default
    private String tokenType = "Bearer";
    
    /**
     * Token expiration time in seconds.
     * 
     * Indicates how long the token remains valid from the time of issuance.
     */
    private Long expiresIn;
    
    /**
     * User profile information.
     * 
     * Contains basic user details for client-side display and personalization.
     */
    private UserProfileDTO user;

}