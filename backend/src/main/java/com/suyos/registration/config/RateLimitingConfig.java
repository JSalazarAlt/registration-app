package com.suyos.registration.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class for rate limiting using Bucket4j. 
 * 
 * Provides methods to create and retrieve rate-limiting buckets
 * 
 * @author Joel Salazar
 */
@Configuration
public class RateLimitingConfig {

    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    /**
     * Retrieves or creates a rate-limiting bucket associated with a specific 
     * client IP address.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Looks up the existing {@link Bucket} for the given IP in the cache.</li>
     *   <li>If none exists, creates a new one via {@link #createIpBucket(String)} 
     *       and stores it.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Applies per-IP request rate limits to prevent abuse or denial-of-service 
     *       attacks.</li>
     *   <li>Ensures fair API usage by limiting excessive requests from a single 
     *       source.</li>
     * </ul>
     *
     * <hr>
     *
     * @param ip the client IP address
     * @return the {@link Bucket} corresponding to the given IP
     */
    public Bucket getIpBucket(String ip) {
        return ipBuckets.computeIfAbsent(ip, this::createIpBucket);
    }

    /**
     * Retrieves or creates a rate-limiting bucket for a specific username.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Checks if a {@link Bucket} already exists for the given username.</li>
     *   <li>If not, creates a new one using {@link #createUserBucket(String)} 
     *       and caches it.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Restricts user-specific operations such as login attempts or API 
     *       actions.</li>
     *   <li>Prevents brute-force attacks and reduces server load from repeated 
     *       user actions.</li>
     * </ul>
     *
     * <hr>
     *
     * @param username the username associated with the rate-limiting bucket
     * @return the {@link Bucket} corresponding to the given user
     */
    public Bucket getUserBucket(String username) {
        return userBuckets.computeIfAbsent(username, this::createUserBucket);
    }

    /**
     * Creates a new rate-limiting bucket for a client IP address.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Defines a limit of 10 requests per minute.</li>
     *   <li>Builds and returns a {@link Bucket} with the configured 
     *       {@link Bandwidth} constraint.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Limits general request volume per IP to protect the application 
     *       against high-frequency traffic.</li>
     *   <li>Provides a simple, configurable mechanism for IP-based throttling.</li>
     * </ul>
     *
     * <hr>
     *
     * @param ip the client IP address
     * @return a new {@link Bucket} enforcing IP-level rate limits
     */
    private Bucket createIpBucket(String ip) {
        // 10 requests per minute per IP
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillIntervally(10, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Creates a new rate-limiting bucket for a specific user.
     *
     * <p><b>Behavior:</b></p>
     * <ol>
     *   <li>Defines a limit of 5 login attempts every 15 minutes.</li>
     *   <li>Builds and returns a {@link Bucket} with the configured 
     *       {@link Bandwidth} restriction.</li>
     * </ol>
     *
     * <p><b>Purpose:</b></p>
     * <ul>
     *   <li>Controls authentication attempts to prevent brute-force login 
     *       attacks.</li>
     *   <li>Ensures fair use by limiting the frequency of user login requests.</li>
     * </ul>
     *
     * <hr>
     *
     * @param username the username for which to create a rate limit
     * @return a new {@link Bucket} enforcing user-level rate limits
     */
    private Bucket createUserBucket(String username) {
        // 5 login attempts per 15 minutes per user
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(15))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

}