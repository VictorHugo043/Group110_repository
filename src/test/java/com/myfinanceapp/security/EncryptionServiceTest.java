package com.myfinanceapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the EncryptionService.
 * This class contains comprehensive tests for encryption and decryption functionality including:
 * - Basic encryption and decryption operations
 * - Key management and derivation
 * - Edge cases (empty data, large data)
 * - Security validation (different keys)
 *
 * @author SE_Group110
 * @version 4.0
 */
class EncryptionServiceTest {
    private SecretKey testKey;
    private String testData;

    /**
     * Sets up test environment before each test.
     * Initializes a test key and test data with Chinese characters and special symbols.
     *
     * @throws EncryptionService.EncryptionException if key derivation fails
     */
    @BeforeEach
    void setUp() throws EncryptionService.EncryptionException {
        // Generate test key
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        testKey = EncryptionService.deriveKey("testPassword", salt);
        
        // Test data with Chinese characters and special symbols
        testData = "这是一段测试数据，包含中文和特殊字符：!@#$%^&*()";
    }

    /**
     * Tests basic encryption and decryption functionality.
     * Verifies that:
     * - Data can be encrypted successfully
     * - Encrypted data contains all required components
     * - Data can be decrypted back to original form
     *
     * @throws EncryptionService.EncryptionException if encryption or decryption fails
     */
    @Test
    void testEncryptDecrypt() throws EncryptionService.EncryptionException {
        // Encrypt data
        EncryptionService.EncryptedData encryptedData = EncryptionService.encrypt(testData, testKey);
        
        // Verify encryption result
        assertNotNull(encryptedData);
        assertNotNull(encryptedData.data());
        assertNotNull(encryptedData.iv());
        assertNotNull(encryptedData.keyId());
        
        // Decrypt data
        String decryptedData = EncryptionService.decrypt(encryptedData, testKey);
        
        // Verify decryption result
        assertEquals(testData, decryptedData);
    }

    /**
     * Tests encryption security with different keys.
     * Verifies that data encrypted with one key cannot be decrypted with a different key.
     *
     * @throws EncryptionService.EncryptionException if encryption fails
     */
    @Test
    void testEncryptWithDifferentKeys() throws EncryptionService.EncryptionException {
        // Generate two different keys
        byte[] salt1 = new byte[16];
        byte[] salt2 = new byte[16];
        new SecureRandom().nextBytes(salt1);
        new SecureRandom().nextBytes(salt2);
        
        SecretKey key1 = EncryptionService.deriveKey("password1", salt1);
        SecretKey key2 = EncryptionService.deriveKey("password2", salt2);
        
        // Encrypt with first key
        EncryptionService.EncryptedData encryptedData = EncryptionService.encrypt(testData, key1);
        
        // Attempt to decrypt with second key
        assertThrows(EncryptionService.EncryptionException.class, () -> {
            EncryptionService.decrypt(encryptedData, key2);
        });
    }

    /**
     * Tests encryption of empty data.
     * Verifies that empty strings can be encrypted and decrypted correctly.
     *
     * @throws EncryptionService.EncryptionException if encryption or decryption fails
     */
    @Test
    void testEncryptEmptyData() throws EncryptionService.EncryptionException {
        String emptyData = "";
        EncryptionService.EncryptedData encryptedData = EncryptionService.encrypt(emptyData, testKey);
        String decryptedData = EncryptionService.decrypt(encryptedData, testKey);
        assertEquals(emptyData, decryptedData);
    }

    /**
     * Tests encryption of large data.
     * Verifies that large amounts of data can be encrypted and decrypted correctly.
     *
     * @throws EncryptionService.EncryptionException if encryption or decryption fails
     */
    @Test
    void testEncryptLargeData() throws EncryptionService.EncryptionException {
        // Generate large test data
        StringBuilder largeData = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeData.append("这是一段测试数据，包含中文和特殊字符：!@#$%^&*()");
        }
        
        // Encrypt and decrypt
        EncryptionService.EncryptedData encryptedData = EncryptionService.encrypt(largeData.toString(), testKey);
        String decryptedData = EncryptionService.decrypt(encryptedData, testKey);
        
        assertEquals(largeData.toString(), decryptedData);
    }

    /**
     * Tests key derivation functionality.
     * Verifies that:
     * - Same password and salt produce identical keys
     * - Different salts produce different keys
     *
     * @throws EncryptionService.EncryptionException if key derivation fails
     */
    @Test
    void testKeyDerivation() throws EncryptionService.EncryptionException {
        String password = "testPassword";
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        
        // Generate two keys with same password and salt
        SecretKey key1 = EncryptionService.deriveKey(password, salt);
        SecretKey key2 = EncryptionService.deriveKey(password, salt);
        
        // Verify keys are identical
        assertArrayEquals(key1.getEncoded(), key2.getEncoded());
        
        // Generate key with different salt
        byte[] differentSalt = new byte[16];
        new SecureRandom().nextBytes(differentSalt);
        SecretKey key3 = EncryptionService.deriveKey(password, differentSalt);
        
        // Verify keys are different
        assertFalse(java.util.Arrays.equals(key1.getEncoded(), key3.getEncoded()));
    }
} 