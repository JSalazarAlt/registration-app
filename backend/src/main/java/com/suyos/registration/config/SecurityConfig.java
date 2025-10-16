package com.suyos.registration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.suyos.registration.filter.RateLimitingFilter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Security configuration for hybrid JWT and OAuth2 authentication.
 * 
 * Configures security filter chains, CORS settings, authentication providers,
 * and password encoding for the application. Supports both traditional JWT 
 * authentication and Google OAuth2 authentication. 
 * 
 * SecurityConfig wires JwtAuthenticationFilter (so that incoming requests 
 * get a JWT checked) and OAuth2AuthenticationSuccessHandler (so OAuth2 logins 
 * get processed into application tokens).
 * 
 * @author Joel Salazar
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    /** JWT authentication filter for processing JWT tokens in requests */
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    /** OAuth2 success handler for processing successful Google OAuth2 authentication */
    private final OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;
    
    /** Rate limiting filter for auth endpoints */
    private final RateLimitingFilter rateLimitingFilter;

    /**
     * Configures the main security rules for the application.
     * 
     * Supports both traditional JWT authentication and Google OAuth2 authentication.
     * 
     * Purpose: (1) Sets up which endpoints require authentication and which are
     *              public.
     *          (2) Configures security features like CSRF, CORS, and HTTP headers.
     *          (3) Specifies session management (stateless for JWT).
     *          (4) Integrates custom filters (rate limiting, JWT authentication).
     *          (5) Sets up OAuth2 login handling.
     * 
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Disable CSRF protection (suitable for stateless APIs using JWT)
            .csrf(csrf -> csrf.disable())
            // Enable CORS with custom configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Configure HTTP headers for security
            .headers(headers -> {
                // Prevent the site from being loaded in a frame (clickjacking protection)
                headers.frameOptions(frame -> frame.deny());
                // Disable content type sniffing (optional, enabled by default)
                headers.contentTypeOptions(contentType -> contentType.disable());
                // Enforce HTTP Strict Transport Security (HSTS)
                headers.httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000) // 1 year
                    .includeSubDomains(true));
            })
            // Define authorization rules for endpoints
            .authorizeHttpRequests(auth -> auth
                // Allow anyone to register or login
                .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                // Require authentication for logout
                .requestMatchers("/api/v1/auth/logout").authenticated()
                // Allow anyone to use OAuth2 endpoints
                .requestMatchers("/oauth2/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated())
            // Use stateless session management (no HTTP session, suitable for JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Configure OAuth2 login
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                    .baseUri("/oauth2/authorize"))
                .redirectionEndpoint(redirection -> redirection
                    .baseUri("/oauth2/callback/*"))
                .successHandler(oauth2SuccessHandler))
            // Add rate limiting filter before authentication filter
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            // Add JWT authentication filter before authentication filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    /**
     * Configures CORS settings. Cross-Origin Resource Sharing (CORS) is a 
     * security feature that restricts webpages from making requests to a 
     * different domain than the one that served the webpage.
     * 
     * Purpose: (1) Prevents malicious sites from reading sensitive data from 
     *              another site via the browser.
     * 
     * @return the CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allows requests from the frontend
        configuration.addAllowedOrigin("http://localhost:5173");
        // Allows all HTTP methods
        configuration.addAllowedMethod("*");
        // Allows all headers
        configuration.addAllowedHeader("*");
        // Allows cookies and credentials
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the authentication manager. 
     * (1) Exposes the authentication manager as a Spring bean allowing 
     *     other components to inject and use it.
     * (2) Retrieves the default authentication manager from the Spring Security
     *     context, which is configured based on our authentication providers 
     *     (e.g., password encoder, user details service).
     * 
     * Purpose: (1) Central interface for authenticating user credentials.
     *          (2) Takes an Authentication object containing user credentials
     *              (e.g., UsernamePasswordAuthenticationToken), checks the
     *              credentials against the configured user details service and
     *              returns an authenticated Authentication object if successful.
     * 
     * @param config the authentication configuration
     * @return the authentication manager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the password encoder.
     * 
     * @return the BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}