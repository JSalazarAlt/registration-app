package com.suyos.registration.unit.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.suyos.registration.model.User;
import com.suyos.registration.repository.UserRepository;

/**
 * Unit tests for UserRepository.
 * 
 * Tests JPA repository operations using @DataJpaTest with H2 in-memory database.
 * Validates custom query methods and standard CRUD operations.
 * 
 * @author Joel Salazar
 */
@DataJpaTest
class UserRepositoryTest {

    /** TestEntityManager for direct database operations in tests */
    @Autowired
    private TestEntityManager entityManager;

    /** UserRepository instance under test */
    @Autowired
    private UserRepository userRepository;

    /** Test user entity for repository operations */
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .accountEnabled(true)
                .accountLocked(false)
                .emailVerified(true)
                .failedLoginAttempts(0)
                .termsAcceptedAt(LocalDateTime.now())
                .privacyPolicyAcceptedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void existsByEmail_UserExists() {
        entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_UserDoesNotExist() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    void findActiveUserByEmail_UserExists() {
        entityManager.persistAndFlush(user);

        Optional<User> foundUser = userRepository.findActiveUserByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void findActiveUserByEmail_UserDoesNotExist() {
        Optional<User> foundUser = userRepository.findActiveUserByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void findActiveUserByEmail_UserDisabled() {
        user.setAccountEnabled(false);
        entityManager.persistAndFlush(user);

        Optional<User> foundUser = userRepository.findActiveUserByEmail("test@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void save_NewUser() {
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("testuser", savedUser.getUsername());
    }

    @Test
    void findById_UserExists() {
        User savedUser = entityManager.persistAndFlush(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }
}