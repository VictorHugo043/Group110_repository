package com.myfinanceapp.service;

import com.myfinanceapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Initialize the UserService instance before each test
        userService = new UserService();
    }

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
