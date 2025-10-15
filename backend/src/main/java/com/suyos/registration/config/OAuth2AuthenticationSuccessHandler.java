package com.suyos.registration.config;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.suyos.registration.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles successful Google OAuth2 authentication.
 * 
 * Processes Google OAuth2 user information, creates or updates user,
 * generates JWT token, and redirects to frontend with token.
 * 
 * @author Joel Salazar
 */
@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    /** Service for authentication operations */
    private final AuthService authService;
    
    public OAuth2AuthenticationSuccessHandler(@Lazy AuthService authService) {
        this.authService = authService;
    }

    /**
     * Processes successful Google OAuth2 authentication.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authentication the authentication object containing OAuth2 user info
     * @throws IOException if I/O operation fails
     * @throws ServletException if servlet processing fails
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        
        try {
            // Extract Google user information
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String providerId = oauth2User.getAttribute("sub"); // Google uses 'sub'
            
            // Process Google OAuth2 user
            var authResponse = authService.processGoogleOAuth2User(email, name, providerId);
            String token = authResponse.getAccessToken();
            
            // Redirect to frontend with token and user data
            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth2/redirect")
                    .queryParam("token", token)
                    .queryParam("firstName", authResponse.getUser().getFirstName())
                    .queryParam("lastName", authResponse.getUser().getLastName())
                    .queryParam("email", authResponse.getUser().getEmail())
                    .queryParam("userId", authResponse.getUser().getId())
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            
        } catch (Exception e) {
            log.error("Google OAuth2 authentication failed", e);
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/login?error=oauth2_failed");
        }
    }
}