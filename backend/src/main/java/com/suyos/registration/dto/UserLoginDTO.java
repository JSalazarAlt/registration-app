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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDTO {

    /** User's email address for authentication */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /** User's password for authentication */
    @NotBlank(message = "Password is required")
    private String password;

}