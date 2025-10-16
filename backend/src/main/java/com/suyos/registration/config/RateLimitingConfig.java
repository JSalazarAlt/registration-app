package com.suyos.registration.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitingConfig {
    
    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    
    public Bucket getIpBucket(String ip) {
        return ipBuckets.computeIfAbsent(ip, this::createIpBucket);
    }
    
    public Bucket getUserBucket(String username) {
        return userBuckets.computeIfAbsent(username, this::createUserBucket);
    }
    
    private Bucket createIpBucket(String ip) {
        // 10 requests per minute per IP
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillIntervally(10, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
    
    private Bucket createUserBucket(String username) {
        // 5 login attempts per 15 minutes per user
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(15))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
    
}