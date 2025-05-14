package com.myfinanceapp.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the PasswordHashService.
 * This class contains comprehensive tests for password hashing functionality including:
 * - Basic password hashing and verification
 * - Salt generation and uniqueness
 * - Edge cases (empty passwords, special characters, long passwords)
 * - Security validation (different passwords, same password multiple hashes)
 *
 * @author SE_Group110
 * @version 4.0
 */
class PasswordHashServiceTest {
    private static final String TEST_PASSWORD = "TestPassword123!@#";
    private static final String DIFFERENT_PASSWORD = "DifferentPassword456!@#";

    /**
     * Tests basic password hashing functionality.
     * Verifies that:
     * - Password can be hashed successfully
     * - Hash result contains all required components
     * - Hash parameters match configuration
     *
     * @throws PasswordHashService.PasswordHashException if hashing fails
     */
    @Test
    void testHashPassword() throws PasswordHashService.PasswordHashException {
        // Hash the password
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(TEST_PASSWORD);
        
        // Verify hash result
        assertNotNull(result);
        assertNotNull(result.hash());
        assertNotNull(result.salt());
        assertNotNull(result.params());
        
        // Verify parameters
        assertEquals(EncryptionConfig.Argon2.MEMORY, result.params().memory());
        assertEquals(EncryptionConfig.Argon2.ITERATIONS, result.params().iterations());
        assertEquals(EncryptionConfig.Argon2.PARALLELISM, result.params().parallelism());
    }

    /**
     * Tests password verification functionality.
     * Verifies that:
     * - Correct password is verified successfully
     * - Incorrect password is rejected
     *
     * @throws PasswordHashService.PasswordHashException if hashing or verification fails
     */
    @Test
    void testVerifyPassword() throws PasswordHashService.PasswordHashException {
        // Hash the password
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(TEST_PASSWORD);
        
        // Verify correct password
        assertTrue(PasswordHashService.verifyPassword(TEST_PASSWORD, result.hash()));
        
        // Verify incorrect password
        assertFalse(PasswordHashService.verifyPassword(DIFFERENT_PASSWORD, result.hash()));
    }

    /**
     * Tests hashing of different passwords.
     * Verifies that different passwords produce different hash values.
     *
     * @throws PasswordHashService.PasswordHashException if hashing fails
     */
    @Test
    void testHashDifferentPasswords() throws PasswordHashService.PasswordHashException {
        // Hash two different passwords
        PasswordHashService.PasswordHashResult result1 = PasswordHashService.hashPassword(TEST_PASSWORD);
        PasswordHashService.PasswordHashResult result2 = PasswordHashService.hashPassword(DIFFERENT_PASSWORD);
        
        // Verify different hash values
        assertNotEquals(result1.hash(), result2.hash());
    }

    /**
     * Tests hashing of the same password multiple times.
     * Verifies that:
     * - Same password produces different hashes (due to different salts)
     * - All hashes can verify the original password
     *
     * @throws PasswordHashService.PasswordHashException if hashing or verification fails
     */
    @Test
    void testHashSamePassword() throws PasswordHashService.PasswordHashException {
        // Hash the same password twice
        PasswordHashService.PasswordHashResult result1 = PasswordHashService.hashPassword(TEST_PASSWORD);
        PasswordHashService.PasswordHashResult result2 = PasswordHashService.hashPassword(TEST_PASSWORD);
        
        // Verify different hash values (due to different salts)
        assertNotEquals(result1.hash(), result2.hash());
        
        // Verify both hashes can verify the original password
        assertTrue(PasswordHashService.verifyPassword(TEST_PASSWORD, result1.hash()));
        assertTrue(PasswordHashService.verifyPassword(TEST_PASSWORD, result2.hash()));
    }

    /**
     * Tests hashing of empty password.
     * Verifies that empty passwords can be hashed and verified correctly.
     *
     * @throws PasswordHashService.PasswordHashException if hashing or verification fails
     */
    @Test
    void testHashEmptyPassword() throws PasswordHashService.PasswordHashException {
        String emptyPassword = "";
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(emptyPassword);
        assertTrue(PasswordHashService.verifyPassword(emptyPassword, result.hash()));
    }

    /**
     * Tests hashing of password with special characters.
     * Verifies that passwords containing special characters and non-ASCII characters
     * can be hashed and verified correctly.
     *
     * @throws PasswordHashService.PasswordHashException if hashing or verification fails
     */
    @Test
    void testHashSpecialCharacters() throws PasswordHashService.PasswordHashException {
        String passwordWithSpecialChars = "密码123!@#$%^&*()_+{}|:\"<>?[]\\;',./";
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(passwordWithSpecialChars);
        assertTrue(PasswordHashService.verifyPassword(passwordWithSpecialChars, result.hash()));
    }

    /**
     * Tests hashing of very long password.
     * Verifies that passwords of significant length can be hashed and verified correctly.
     *
     * @throws PasswordHashService.PasswordHashException if hashing or verification fails
     */
    @Test
    void testHashLongPassword() throws PasswordHashService.PasswordHashException {
        // Generate a very long password
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longPassword.append("a");
        }
        
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(longPassword.toString());
        assertTrue(PasswordHashService.verifyPassword(longPassword.toString(), result.hash()));
    }
} 