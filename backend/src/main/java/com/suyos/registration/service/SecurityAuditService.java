package com.suyos.registration.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityAuditService {
    
    public void logLoginAttempt(String username, String ip, String userAgent, boolean success) {
        if (success) {
            log.info("LOGIN_SUCCESS: user={}, ip={}, userAgent={}", username, ip, userAgent);
        } else {
            log.warn("LOGIN_FAILED: user={}, ip={}, userAgent={}", username, ip, userAgent);
        }
    }
    
    public void logRegistration(String username, String email, String ip, String userAgent) {
        log.info("USER_REGISTERED: user={}, email={}, ip={}, userAgent={}", username, email, ip, userAgent);
    }
    
    public void logPasswordChange(String username, String ip, String userAgent) {
        log.info("PASSWORD_CHANGED: user={}, ip={}, userAgent={}", username, ip, userAgent);
    }
    
    public void logAccountLocked(String username, String ip, String userAgent) {
        log.warn("ACCOUNT_LOCKED: user={}, ip={}, userAgent={}", username, ip, userAgent);
    }
    
    public void logOAuth2Login(String username, String provider, String ip, String userAgent) {
        log.info("OAUTH2_LOGIN: user={}, provider={}, ip={}, userAgent={}", username, provider, ip, userAgent);
    }
    
    public void logLogout(String username, String ip, String userAgent) {
        log.info("USER_LOGOUT: user={}, ip={}, userAgent={}", username, ip, userAgent);
    }
    
    public String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    public String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
    
}