package com.suyos.registration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;

/**
 * Swagger/OpenAPI configuration for API documentation.
 * 
 * Configures OpenAPI 3.0 documentation with JWT authentication support.
 * Provides comprehensive API documentation accessible via Swagger UI.
 * 
 * @author Joel Salazar
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configures OpenAPI documentation with project information and JWT security.
     * 
     * @return OpenAPI configuration object
     */
    @Bean
    public OpenAPI expenseTrackerOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Expense Tracker API")
                .description("REST API for expense tracking application with user authentication")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Joel Salazar")
                    .email("joel@example.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Enter JWT token obtained from login endpoint")));
    }
    
}