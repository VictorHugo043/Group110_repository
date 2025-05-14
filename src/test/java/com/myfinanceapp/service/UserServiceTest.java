package com.myfinanceapp.service;

import com.myfinanceapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the UserService.
 * This class contains tests for user management functionality including:
 * - User registration
 * - Login verification
 * - User search by username and UID
 * - Username updates
 * - Password updates
 * - Security question management
 *
 * @author SE_Group110
 * @version 4.0
 */
class UserServiceTest {

    private UserService userService;

    /**
     * Sets up the test environment before each test.
     * Initializes a new UserService instance.
     */
    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    /**
     * Tests user registration functionality.
     * Verifies that:
     * - New users can be registered successfully
     * - UID is generated automatically
     * - Duplicate usernames are rejected
     */
    @Test
    void registerUser() {
        // Test successful registration
        boolean result = userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        assertTrue(result);

        // Verify that the user was added (UID should be generated automatically)
        User user = userService.findUserByUsername("testuser");
        assertNotNull(user);
        assertNotNull(user.getUid()); // UID should be generated
        assertEquals("testuser", user.getUsername());

        // Test registration with an existing username
        result = userService.registerUser("testuser", "password456", "What is your favorite color?", "Blue");
        assertFalse(result);
    }

    /**
     * Tests login verification functionality.
     * Verifies that:
     * - Correct credentials allow login
     * - Incorrect password is rejected
     */
    @Test
    void checkLogin() {
        // Register a user first
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");

        // Test successful login
        boolean result = userService.checkLogin("testuser", "password123");
        assertTrue(result);

        // Test failed login with incorrect password
        result = userService.checkLogin("testuser", "wrongpassword");
        assertFalse(result);
    }

    /**
     * Tests user search by username functionality.
     * Verifies that:
     * - Existing users can be found
     * - Non-existent users return null
     */
    @Test
    void findUserByUsername() {
        // Register a user
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");

        // Test finding user by username
        User user = userService.findUserByUsername("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());

        // Test finding a non-existent user
        user = userService.findUserByUsername("nonexistent");
        assertNull(user);
    }

    /**
     * Tests user search by UID functionality.
     * Verifies that:
     * - Users can be found by their UID
     * - Non-existent UIDs return null
     */
    @Test
    void findUserByUid() {
        // Register a user and get UID
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        User user = userService.findUserByUsername("testuser");
        String uid = user.getUid();

        // Test finding user by UID
        User foundUser = userService.findUserByUid(uid);
        assertNotNull(foundUser);
        assertEquals(uid, foundUser.getUid());

        // Test with a non-existent UID
        foundUser = userService.findUserByUid("nonexistent-uid");
        assertNull(foundUser);
    }

    /**
     * Tests username update functionality.
     * Verifies that:
     * - Usernames can be updated successfully
     * - Updates to existing usernames are rejected
     */
    @Test
    void updateUserName() {
        // Register a user
        userService.registerUser("olduser", "password123", "What is your pet's name?", "Fluffy");

        // Test successful username update
        boolean result = userService.updateUserName("olduser", "newuser");
        assertTrue(result);

        // Verify the username was updated
        User user = userService.findUserByUsername("newuser");
        assertNotNull(user);
        assertEquals("newuser", user.getUsername());

        // Test failed username update due to existing username
        // Register another user to simulate username conflict
        userService.registerUser("otheruser", "password456", "What is your favorite color?", "Blue");

        // Try updating to a conflicting username
        result = userService.updateUserName("newuser", "otheruser");
        assertFalse(result);  // Expecting failure due to username conflict
    }

    /**
     * Tests user retrieval after login functionality.
     * Verifies that:
     * - Users can be retrieved after successful login
     * - Failed login returns null
     */
    @Test
    void loginGetUser() {
        // Register a user
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");

        // Test getting user after login
        User user = userService.loginGetUser("testuser", "password123");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());

        // Test failed login and get user
        user = userService.loginGetUser("testuser", "wrongpassword");
        assertNull(user);
    }

    /**
     * Tests password update functionality.
     * Verifies that:
     * - Passwords can be updated successfully
     * - Updates with invalid UIDs are rejected
     */
    @Test
    void updatePassword() {
        // Register a user
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        User user = userService.findUserByUsername("testuser");

        // Test password update
        boolean result = userService.updatePassword(user.getUid(), "newpassword123");
        assertTrue(result);

        // Verify password update
        User updatedUser = userService.findUserByUid(user.getUid());
        assertEquals("newpassword123", updatedUser.getPassword());

        // Test failed password update with incorrect UID
        result = userService.updatePassword("nonexistent-uid", "newpassword123");
        assertFalse(result);
    }

    /**
     * Tests security question update functionality.
     * Verifies that:
     * - Security questions can be updated successfully
     * - Updates with invalid UIDs are rejected
     */
    @Test
    void updateSecurityQuestion() {
        // Register a user
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        User user = userService.findUserByUsername("testuser");

        // Test security question update
        boolean result = userService.updateSecurityQuestion(user.getUid(), "What is your favorite color?", "Blue");
        assertTrue(result);

        // Verify security question update
        User updatedUser = userService.findUserByUid(user.getUid());
        assertEquals("What is your favorite color?", updatedUser.getSecurityQuestion());

        // Test failed security question update with incorrect UID
        result = userService.updateSecurityQuestion("nonexistent-uid", "What is your favorite color?", "Blue");
        assertFalse(result);
    }
}
