package com.suyos.registration.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for logging security-related events for auditing purposes.
 * 
 * Provides methods to log various security events such as login attempts,
 * registrations, password changes, and account lockouts.
 * 
 * @author Joel Salazar
 */
@Service
@Slf4j
public class SecurityAuditService {

    /**
     * Logs a login attempt with details about the username, client IP address, 
     * user agent, and whether the attempt was successful.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Determines the outcome of the login attempt (success or failure).</li>
     *   <li>Logs the attempt with the appropriate severity:
     *     <ul>
     *       <li>INFO level for successful logins.</li>
     *       <li>WARN level for failed attempts.</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Provides an audit trail for login activities to assist with 
     *       security monitoring.</li>
     *   <li>Helps detect potential unauthorized access attempts or brute-force 
     *       attacks.</li>
     * </ul>
     *
     *
     * <hr>
     * 
     * @param username the username involved in the login attempt
     * @param ip the client's IP address
     * @param userAgent the client’s user agent string
     * @param success {@code true} if the login was successful, otherwise {@code false}
     */
    public void logLoginAttempt(String username, String ip, String userAgent, boolean success) {
        if (success) {
            log.info("LOGIN_SUCCESS: user={}, ip={}, userAgent={}", username, ip, userAgent);
        } else {
            log.warn("LOGIN_FAILED: user={}, ip={}, userAgent={}", username, ip, userAgent);
        }
    }

    /**
     * Logs a user registration event, including username, email, and client 
     * information.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Captures registration details such as username, email, IP address, 
     *       and user agent.</li>
     *   <li>Writes an INFO-level log entry summarizing the registration event.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Maintains a record of user registrations for auditing and security 
     *       analytics.</li>
     *   <li>Provides traceability for account creation activity.</li>
     * </ul>
     *
     *
     * <hr>
     * 
     * @param username the registered username
     * @param email the registered email address
     * @param ip the client's IP address
     * @param userAgent the client’s user agent string
     */
    public void logRegistration(String username, String email, String ip, String userAgent) {
        log.info("USER_REGISTERED: user={}, email={}, ip={}, userAgent={}", username, email, ip, userAgent);
    }

    /**
     * Logs a password change event for a user.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Records the username, client IP address, and user agent associated 
     *       with the password change.</li>
     *   <li>Writes an INFO-level log entry summarizing the event.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Tracks password changes to enhance user accountability and system 
     *       auditing.</li>
     *   <li>Supports forensic analysis in case of compromised accounts.</li>
     * </ul>
     *
     *
     * <hr>
     * 
     * @param username the username whose password was changed
     * @param ip the client's IP address
     * @param userAgent the client’s user agent string
     */
    public void logPasswordChange(String username, String ip, String userAgent) {
        log.info("PASSWORD_CHANGED: user={}, ip={}, userAgent={}", username, ip, userAgent);
    }

    /**
     * Logs when a user account is locked due to security policies or repeated 
     * failed attempts.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Captures details of the locked account, including username, IP, 
     *       and user agent.</li>
     *   <li>Writes a WARN-level log entry to indicate a potential security 
     *       concern.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Provides visibility into account lock events triggered by security 
     *       mechanisms.</li>
     *   <li>Helps administrators identify and respond to suspicious activity.</li>
     * </ul>
     *
     *
     * <hr>
     * 
     * @param username the username whose account was locked
     * @param ip the client's IP address
     * @param userAgent the client’s user agent string
     */
    public void logAccountLocked(String username, String ip, String userAgent) {
        log.warn("ACCOUNT_LOCKED: user={}, ip={}, userAgent={}", username, ip, userAgent);
    }

    /**
     * Logs a successful OAuth2 login attempt with details about the provider 
     * and client environment.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Captures the username, OAuth2 provider, IP address, and user 
     *       agent involved in the login.</li>
     *   <li>Writes an INFO-level log entry summarizing the OAuth2 login 
     *       activity.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Audits successful OAuth2-based logins for security tracking.</li>
     *   <li>Provides traceability of authentication via third-party identity 
     *       providers.</li>
     * </ul>
     *
     *
     * <hr>
     * 
     * @param username the username associated with the OAuth2 login
     * @param provider the OAuth2 provider used (e.g., Google, GitHub)
     * @param ip the client's IP address
     * @param userAgent the client’s user agent string
     */
    public void logOAuth2Login(String username, String provider, String ip, String userAgent) {
        log.info("OAUTH2_LOGIN: user={}, provider={}, ip={}, userAgent={}", username, provider, ip, userAgent);
    }

    /**
     * Logs a user logout event with client context information.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Captures logout details such as username, IP address, and user 
     *       agent.</li>
     *   <li>Writes an INFO-level log entry indicating that the user logged 
     *       out.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Maintains a record of logout actions for security auditing.</li>
     *   <li>Helps confirm that JWT tokens or sessions were properly terminated 
     *       after logout.</li>
     * </ul>
     *
     * <hr>
     * 
     * @param username the username of the user logging out
     * @param ip the client's IP address
     * @param userAgent the client’s user agent string
     */
    public void logLogout(String username, String ip, String userAgent) {
        log.info("USER_LOGOUT: user={}, ip={}, userAgent={}", username, ip, userAgent);
    }

    /**
     * Extracts the client's IP address from the given HTTP request, checking 
     * common proxy headers before falling back to the remote address.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Checks the <code>X-Forwarded-For</code> header for proxy-provided 
     *       IP information.</li>
     *   <li>If unavailable, checks the <code>X-Real-IP</code> header.</li>
     *   <li>If neither is present, returns the IP from 
     *       {@link HttpServletRequest#getRemoteAddr()}.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Retrieves the true client IP address for accurate audit logging.</li>
     *   <li>Supports environments with reverse proxies or load balancers 
     *       that forward IP information via headers.</li>
     * </ul>
     *
     * <hr>
     * 
     * @param request the HTTP request containing potential IP headers
     * @return the resolved client IP address
     */
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

    /**
     * Retrieves the client's user agent string from the HTTP request headers.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Reads the <code>User-Agent</code> header from the request.</li>
     *   <li>Returns its value or {@code null} if the header is absent.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Captures client device or browser information for audit logs.</li>
     *   <li>Assists in identifying request sources during security analysis.</li>
     * </ul>
     *
     * <hr>
     * 
     * @param request the HTTP request
     * @return the value of the <code>User-Agent</code> header, or {@code null} 
     *         if not present
     */
    public String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

}