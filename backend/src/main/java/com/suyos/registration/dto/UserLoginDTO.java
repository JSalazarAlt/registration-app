package com.suyos.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user authentication information.
 * 
 * This DTO is used to capture and validate user credentials during the
 * login process. It contains only the essential fields required for
 * user authentication and session establishment.
 * 
 * @author Joel Salazar
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDTO {

    /**
     * User's email address for account creation and login.
     * 
     * Must be a valid email format and will be used as the primary
     * identifier for authentication and communication.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

     /**
     * User's password for account security.
     * 
     * Must be at least 8 characters long for security purposes.
     * Will be hashed before storage in the database.
     */
    @NotBlank(message = "Password is required")
    private String password;

}