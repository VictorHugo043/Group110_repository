package com.myfinanceapp.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service class providing AES encryption and decryption functionality.
 * This class implements secure data encryption using AES-256-GCM mode, which provides
 * both confidentiality and authenticity. It includes methods for:
 * - Encrypting data with AES-256-GCM
 * - Decrypting data with authentication
 * - Key derivation using PBKDF2
 * - Secure random IV generation
 * 
 * @author SE_Group110
 * @version 4.0
 */
public final class EncryptionService {
    private static final SecureRandom secureRandom = new SecureRandom();

    private EncryptionService() {
        // Private constructor to prevent instantiation
    }

    /**
     * Encrypts the given data using AES-256-GCM encryption.
     * This method:
     * - Generates a random initialization vector (IV)
     * - Initializes the cipher in encryption mode
     * - Encrypts the data with authentication
     * - Returns the encrypted data along with the IV and key ID
     *
     * @param data The data to be encrypted
     * @param key The encryption key to use
     * @return An EncryptedData object containing the encrypted data, IV, and key ID
     * @throws EncryptionException If an error occurs during the encryption process
     */
    public static EncryptedData encrypt(String data, SecretKey key) throws EncryptionException {
        try {
            // Generate random IV
            byte[] iv = generateIV();
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance(EncryptionConfig.AES.ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(EncryptionConfig.AES.GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            
            // Perform encryption
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            
            // Return encryption result
            return new EncryptedData(
                Base64.getEncoder().encodeToString(encryptedData),
                Base64.getEncoder().encodeToString(iv),
                generateKeyId(key)
            );
        } catch (Exception e) {
            throw new EncryptionException("Error occurred during data encryption", e);
        }
    }

    /**
     * Decrypts the given encrypted data using AES-256-GCM decryption.
     * This method:
     * - Decodes the IV and encrypted data from Base64
     * - Initializes the cipher in decryption mode
     * - Verifies the authentication tag
     * - Returns the decrypted data
     *
     * @param encryptedData The EncryptedData object containing the encrypted data, IV, and key ID
     * @param key The decryption key to use
     * @return The decrypted data as a string
     * @throws EncryptionException If an error occurs during the decryption process
     */
    public static String decrypt(EncryptedData encryptedData, SecretKey key) throws EncryptionException {
        try {
            // Decode IV and encrypted data
            byte[] iv = Base64.getDecoder().decode(encryptedData.iv());
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData.data());
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance(EncryptionConfig.AES.ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(EncryptionConfig.AES.GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            
            // Perform decryption
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new EncryptionException("Error occurred during data decryption", e);
        }
    }

    /**
     * Derives a cryptographic key from a password using PBKDF2.
     * This method:
     * - Uses PBKDF2 with HMAC-SHA256 for key derivation
     * - Applies the configured number of iterations
     * - Generates a key of the specified length
     *
     * @param password The password to derive the key from
     * @param salt The salt to use in the key derivation
     * @return A SecretKey object suitable for AES encryption
     * @throws EncryptionException If an error occurs during key derivation
     */
    public static SecretKey deriveKey(String password, byte[] salt) throws EncryptionException {
        try {
            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(),
                salt,
                EncryptionConfig.AES.PBKDF2_ITERATIONS,
                EncryptionConfig.AES.KEY_LENGTH
            );
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new EncryptionException("Error occurred during key derivation", e);
        }
    }

    /**
     * Generates a cryptographically secure random initialization vector.
     * The IV length is configured in EncryptionConfig.AES.GCM_IV_LENGTH.
     *
     * @return A byte array containing the random IV
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[EncryptionConfig.AES.GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        return iv;
    }

    /**
     * Generates a unique identifier for a key.
     * The key ID is derived from the first 8 characters of the Base64-encoded key.
     *
     * @param key The key to generate an ID for
     * @return A string containing the key ID
     */
    private static String generateKeyId(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded()).substring(0, 8);
    }

    /**
     * Record class representing the result of an encryption operation.
     * Contains the encrypted data, initialization vector, and key ID.
     *
     * @param data The encrypted data in Base64 format
     * @param iv The initialization vector in Base64 format
     * @param keyId The identifier of the key used for encryption
     */
    public record EncryptedData(String data, String iv, String keyId) {
    }

    /**
     * Exception class for encryption-related errors.
     * Used to wrap and propagate encryption/decryption errors with meaningful messages.
     */
    public static class EncryptionException extends Exception {
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 