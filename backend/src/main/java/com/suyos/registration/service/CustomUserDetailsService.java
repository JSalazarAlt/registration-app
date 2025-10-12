package com.suyos.registration.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.suyos.registration.model.User;
import com.suyos.registration.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Custom UserDetailsService implementation for Spring Security authentication.
 * 
 * Loads user details from the database for authentication and authorization.
 * Integrates with Spring Security's authentication mechanism.
 * 
 * @author Joel Salazar
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /** Repository for user data access */
    private final UserRepository userRepository;

    /**
     * Loads user details by email for authentication.
     * 
     * @param email the user's email address
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findActiveUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new ArrayList<>()) // No roles for now
                .accountExpired(false)
                .accountLocked(user.getAccountLocked())
                .credentialsExpired(false)
                .disabled(!user.getAccountEnabled())
                .build();
    }
    
}