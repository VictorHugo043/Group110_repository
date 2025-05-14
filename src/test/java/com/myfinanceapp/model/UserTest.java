package com.myfinanceapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the User model.
 * This class contains unit tests for all User class functionality including:
 * - Getters and setters for all properties
 * - Constructor variations
 * - UID generation and uniqueness
 * - Default values handling
 *
 * @author SE_Group110
 * @version 4.0
 */
class UserTest {
    
    private User user;
    private final String testUid = "test-uid-123";
    private final String testUsername = "testuser";
    private final String testPassword = "P@ssw0rd";
    private final String testSecurityQuestion = "What is your pet's name?";
    private final String testSecurityAnswer = "Fluffy";
    
    /**
     * Sets up a new User instance before each test.
     * Initializes the user with test data for all properties.
     */
    @BeforeEach
    void setUp() {
        user = new User(testUid, testUsername, testPassword, testSecurityQuestion, testSecurityAnswer, null);
    }

    /**
     * Tests the getUid method.
     * Verifies that the user ID is correctly returned.
     */
    @Test
    void getUid() {
        assertEquals(testUid, user.getUid());
    }

    /**
     * Tests the setUid method.
     * Verifies that the user ID can be updated.
     */
    @Test
    void setUid() {
        String newUid = "new-uid-456";
        user.setUid(newUid);
        assertEquals(newUid, user.getUid());
    }

    /**
     * Tests the getUsername method.
     * Verifies that the username is correctly returned.
     */
    @Test
    void getUsername() {
        assertEquals(testUsername, user.getUsername());
    }

    /**
     * Tests the setUsername method.
     * Verifies that the username can be updated.
     */
    @Test
    void setUsername() {
        String newUsername = "newuser";
        user.setUsername(newUsername);
        assertEquals(newUsername, user.getUsername());
    }

    /**
     * Tests the getPassword method.
     * Verifies that the password is correctly returned.
     */
    @Test
    void getPassword() {
        assertEquals(testPassword, user.getPassword());
    }

    /**
     * Tests the setPassword method.
     * Verifies that the password can be updated.
     */
    @Test
    void setPassword() {
        String newPassword = "NewP@ssw0rd";
        user.setPassword(newPassword);
        assertEquals(newPassword, user.getPassword());
    }

    /**
     * Tests the getSecurityQuestion method.
     * Verifies that the security question is correctly returned.
     */
    @Test
    void getSecurityQuestion() {
        assertEquals(testSecurityQuestion, user.getSecurityQuestion());
    }

    /**
     * Tests the setSecurityQuestion method.
     * Verifies that the security question can be updated.
     */
    @Test
    void setSecurityQuestion() {
        String newQuestion = "What is your mother's maiden name?";
        user.setSecurityQuestion(newQuestion);
        assertEquals(newQuestion, user.getSecurityQuestion());
    }

    /**
     * Tests the getSecurityAnswer method.
     * Verifies that the security answer is correctly returned.
     */
    @Test
    void getSecurityAnswer() {
        assertEquals(testSecurityAnswer, user.getSecurityAnswer());
    }

    /**
     * Tests the setSecurityAnswer method.
     * Verifies that the security answer can be updated.
     */
    @Test
    void setSecurityAnswer() {
        String newAnswer = "Spot";
        user.setSecurityAnswer(newAnswer);
        assertEquals(newAnswer, user.getSecurityAnswer());
    }
    
    /**
     * Tests the default constructor.
     * Verifies that all properties are initialized with null values.
     */
    @Test
    void testDefaultConstructor() {
        User defaultUser = new User();
        assertNull(defaultUser.getUid());
        assertNull(defaultUser.getUsername());
        assertNull(defaultUser.getPassword());
        assertNull(defaultUser.getSecurityQuestion());
        assertNull(defaultUser.getSecurityAnswer());
    }
    
    /**
     * Tests the full constructor.
     * Verifies that all properties are correctly initialized with provided values.
     */
    @Test
    void testFullConstructor() {
        User fullUser = new User(testUid, testUsername, testPassword, testSecurityQuestion, testSecurityAnswer, null);
        assertEquals(testUid, fullUser.getUid());
        assertEquals(testUsername, fullUser.getUsername());
        assertEquals(testPassword, fullUser.getPassword());
        assertEquals(testSecurityQuestion, fullUser.getSecurityQuestion());
        assertEquals(testSecurityAnswer, fullUser.getSecurityAnswer());
    }
    
    /**
     * Tests the constructor with auto-generated UID.
     * Verifies that:
     * - A valid UUID is generated
     * - The UUID has the correct format and length
     * - All other properties are correctly initialized
     */
    @Test
    void testConstructorWithAutoGeneratedUid() {
        User autoUidUser = new User(testUsername, testPassword, testSecurityQuestion, testSecurityAnswer);
        assertNotNull(autoUidUser.getUid());
        assertFalse(autoUidUser.getUid().isEmpty());
        assertEquals(36, autoUidUser.getUid().length()); // UUID standard length
        assertEquals(testUsername, autoUidUser.getUsername());
        assertEquals(testPassword, autoUidUser.getPassword());
        assertEquals(testSecurityQuestion, autoUidUser.getSecurityQuestion());
        assertEquals(testSecurityAnswer, autoUidUser.getSecurityAnswer());
    }
    
    /**
     * Tests the uniqueness of auto-generated UIDs.
     * Verifies that multiple users created with the same data have different UIDs.
     */
    @Test
    void testUniqueUidGeneration() {
        User user1 = new User(testUsername, testPassword, testSecurityQuestion, testSecurityAnswer);
        User user2 = new User(testUsername, testPassword, testSecurityQuestion, testSecurityAnswer);
        assertNotEquals(user1.getUid(), user2.getUid());
    }
}