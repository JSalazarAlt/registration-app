package com.suyos.registration.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized error response structure for API endpoints.
 * 
 * @author Joel Salazar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    
    /** Timestamp when the error occurred */
    private LocalDateTime timestamp;
    
    /** HTTP status code of the error */
    private int status;
    
    /** Brief error category or type */
    private String error;
    
    /** Detailed error message for the user */
    private String message;
    
    /** Additional error details (e.g., field validation errors) */
    private Map<String, String> details;
    
}