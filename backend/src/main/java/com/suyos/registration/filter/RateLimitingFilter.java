package com.suyos.registration.filter;

import com.suyos.registration.config.RateLimitingConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final RateLimitingConfig rateLimitingConfig;
    
    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request, @org.springframework.lang.NonNull HttpServletResponse response, 
                                  @org.springframework.lang.NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        // Apply rate limiting only to auth endpoints
        if (isAuthEndpoint(requestURI)) {
            String clientIp = getClientIp(request);
            Bucket ipBucket = rateLimitingConfig.getIpBucket(clientIp);
            
            if (!ipBucket.tryConsume(1)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Too many requests. Try again later.\"}");
                return;
            }
            
            // Add rate limit headers
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(ipBucket.getAvailableTokens()));
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isAuthEndpoint(String uri) {
        return uri.contains("/api/v1/auth/") || uri.contains("/oauth2/");
    }
    
    private String getClientIp(HttpServletRequest request) {
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
}