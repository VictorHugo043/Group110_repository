package com.myfinanceapp.service;

import com.myfinanceapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

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

    private TestUserService userService;
    @TempDir
    Path tempDir;

    /**
     * Test-specific UserService subclass for overriding file paths
     */
    private static class TestUserService extends UserService {
        private final String testFilePath;

        public TestUserService(String testFilePath) {
            this.testFilePath = testFilePath;
        }

        @Override
        protected String getUserJsonPath() {
            return testFilePath;
        }
    }

    /**
     * Sets up the test environment before each test.
     * Initializes a new UserService instance and sets up a temporary directory for test files.
     */
    @BeforeEach
    void setUp() {
        try {
            // Create test-specific UserService instance
            File usersFile = new File(tempDir.toFile(), "users.json");
            if (!usersFile.exists()) {
                if (!usersFile.createNewFile()) {
                    throw new RuntimeException("Failed to create users.json file");
                }
                // Write empty JSON array
                java.nio.file.Files.write(usersFile.toPath(), "[]".getBytes());
            }
            
            userService = new TestUserService(usersFile.getAbsolutePath());
            
            // Verify file is writable
            if (!usersFile.canWrite()) {
                throw new RuntimeException("users.json file is not writable");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test environment", e);
        }
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
        assertTrue(result, "User registration should succeed");

        // Verify user information
        User user = userService.findUserByUsername("testuser");
        assertNotNull(user, "User should be found after registration");
        assertNotNull(user.getUid(), "UID should be generated");
        assertEquals("testuser", user.getUsername(), "Username should match");
        assertEquals("password123", user.getPassword(), "Password should match");
        assertEquals("What is your pet's name?", user.getSecurityQuestion(), "Security question should match");
        assertEquals("Fluffy", user.getSecurityAnswer(), "Security answer should match");
        assertNotNull(user.getSalt(), "Salt should be generated");

        // Test duplicate username (case-insensitive)
        result = userService.registerUser("TestUser", "password456", "What is your favorite color?", "Blue");
        assertFalse(result, "Registration with existing username (case-insensitive) should fail");
    }

    /**
     * Tests login verification functionality.
     * Verifies that:
     * - Correct credentials allow login
     * - Incorrect password is rejected
     */
    @Test
    void checkLogin() {
        // Register user
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");

        // Test successful login
        boolean result = userService.checkLogin("testuser", "password123");
        assertTrue(result, "Login with correct credentials should succeed");

        // Test wrong password
        result = userService.checkLogin("testuser", "wrongpassword");
        assertFalse(result, "Login with incorrect password should fail");

        // Test case mismatch (case-sensitive)
        result = userService.checkLogin("TestUser", "password123");
        assertFalse(result, "Login should fail with different case username");
    }

    /**
     * Tests user search by username functionality.
     * Verifies that:
     * - Existing users can be found
     * - Non-existent users return null
     */
    @Test
    void findUserByUsername() {
        // Register user
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");

        // Test finding existing user (case-insensitive)
        User user = userService.findUserByUsername("testuser");
        assertNotNull(user, "Existing user should be found");
        assertEquals("testuser", user.getUsername(), "Username should match");

        // Test finding non-existent user
        user = userService.findUserByUsername("nonexistent");
        assertNull(user, "Non-existent user should return null");

        // Test case-insensitive search
        user = userService.findUserByUsername("TestUser");
        assertNotNull(user, "User should be found with different case");
        assertEquals("testuser", user.getUsername(), "Original username should be preserved");
    }

    /**
     * Tests user search by UID functionality.
     * Verifies that:
     * - Users can be found by their UID
     * - Non-existent UIDs return null
     */
    @Test
    void findUserByUid() {
        // Register user and get UID
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        User user = userService.findUserByUsername("testuser");
        String uid = user.getUid();

        // Test finding user by UID
        User foundUser = userService.findUserByUid(uid);
        assertNotNull(foundUser, "User should be found by UID");
        assertEquals(uid, foundUser.getUid(), "UID should match");
        assertEquals("testuser", foundUser.getUsername(), "Username should match");

        // Test finding non-existent UID
        foundUser = userService.findUserByUid("nonexistent-uid");
        assertNull(foundUser, "Non-existent UID should return null");
    }

    /**
     * Tests username update functionality.
     * Verifies that:
     * - Usernames can be updated successfully
     * - Updates to existing usernames are rejected
     */
    @Test
    void updateUserName() {
        // Register user
        userService.registerUser("olduser", "password123", "What is your pet's name?", "Fluffy");

        // Test successful username update
        boolean result = userService.updateUserName("olduser", "newuser");
        assertTrue(result, "Username update should succeed");

        // Verify username is updated
        User user = userService.findUserByUsername("newuser");
        assertNotNull(user, "User should be found with new username");
        assertEquals("newuser", user.getUsername(), "Username should be updated");

        // Test updating to existing username (case-insensitive)
        userService.registerUser("otheruser", "password456", "What is your favorite color?", "Blue");
        result = userService.updateUserName("newuser", "OtherUser");
        assertFalse(result, "Update to existing username (case-insensitive) should fail");
    }

    /**
     * Tests user retrieval after login functionality.
     * Verifies that:
     * - Users can be retrieved after successful login
     * - Failed login returns null
     */
    @Test
    void loginGetUser() {
        // Register user
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");

        // Test getting user after successful login
        User user = userService.loginGetUser("testuser", "password123");
        assertNotNull(user, "User should be retrieved after successful login");
        assertEquals("testuser", user.getUsername(), "Username should match");

        // Test getting user after failed login
        user = userService.loginGetUser("testuser", "wrongpassword");
        assertNull(user, "Failed login should return null");

        // Test case mismatch (case-sensitive)
        user = userService.loginGetUser("TestUser", "password123");
        assertNull(user, "Login should fail with different case username");
    }

    /**
     * Tests password update functionality.
     * Verifies that:
     * - Passwords can be updated successfully
     * - Updates with invalid UIDs are rejected
     */
    @Test
    void updatePassword() {
        // Register user
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        User user = userService.findUserByUsername("testuser");

        // Test password update
        boolean result = userService.updatePassword(user.getUid(), "newpassword123");
        assertTrue(result, "Password update should succeed");

        // Verify password is updated
        User updatedUser = userService.findUserByUid(user.getUid());
        assertEquals("newpassword123", updatedUser.getPassword(), "Password should be updated");
    }

    /**
     * Tests security question update functionality.
     * Verifies that:
     * - Security questions can be updated successfully
     * - Updates with invalid UIDs are rejected
     */
    @Test
    void updateSecurityQuestion() {
        // Register user
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        User user = userService.findUserByUsername("testuser");

        // Test updating security question
        boolean result = userService.updateSecurityQuestion(user.getUid(), "What is your favorite color?", "Blue");
        assertTrue(result, "Security question update should succeed");

        // Verify security question is updated
        User updatedUser = userService.findUserByUid(user.getUid());
        assertEquals("What is your favorite color?", updatedUser.getSecurityQuestion(), "Security question should be updated");
        assertEquals("Blue", updatedUser.getSecurityAnswer(), "Security answer should be updated");

        // Test using invalid UID to update security question
        result = userService.updateSecurityQuestion("nonexistent-uid", "What is your favorite color?", "Blue");
        assertFalse(result, "Security question update with invalid UID should fail");
    }
}
