package com.myfinanceapp.security;

/**
 * Configuration class for encryption-related constants and parameters.
 * This class defines the configuration parameters for both AES and Argon2 encryption algorithms.
 * It provides a centralized location for all encryption-related settings to ensure consistency
 * and maintainability across the application.
 * 
 * @author SE_Group110
 * @version 4.0
 */
public final class EncryptionConfig {
    private EncryptionConfig() {
        // Private constructor to prevent instantiation
    }

    /**
     * Configuration class for AES encryption parameters.
     * Defines the settings for AES-256-GCM encryption, including algorithm specifications,
     * key lengths, and initialization vector parameters.
     */
    public static final class AES {
        private AES() {
            // Private constructor to prevent instantiation
        }

        /** AES encryption algorithm specification using GCM mode with no padding */
        public static final String ALGORITHM = "AES/GCM/NoPadding";
        
        /** AES key length in bits (256 bits for AES-256) */
        public static final int KEY_LENGTH = 256;
        
        /** GCM authentication tag length in bits (128 bits for strong authentication) */
        public static final int GCM_TAG_LENGTH = 128;
        
        /** GCM initialization vector length in bytes (12 bytes = 96 bits) */
        public static final int GCM_IV_LENGTH = 12;
        
        /** Number of iterations for PBKDF2 key derivation (65536 for strong key derivation) */
        public static final int PBKDF2_ITERATIONS = 65536;
    }

    /**
     * Configuration class for Argon2 password hashing parameters.
     * Defines the settings for Argon2id password hashing, including memory usage,
     * iteration count, and parallelism parameters for secure password storage.
     */
    public static final class Argon2 {
        private Argon2() {
            // Private constructor to prevent instantiation
        }

        /** Memory usage in KB (65536 KB = 64 MB) for Argon2 hashing */
        public static final int MEMORY = 65536;
        
        /** Number of iterations for Argon2 hashing (4 iterations for balanced security) */
        public static final int ITERATIONS = 4;
        
        /** Degree of parallelism for Argon2 hashing (2 threads) */
        public static final int PARALLELISM = 2;
        
        /** Length of the hash output in bytes (32 bytes = 256 bits) */
        public static final int HASH_LENGTH = 32;
        
        /** Length of the salt in bytes (16 bytes = 128 bits) */
        public static final int SALT_LENGTH = 16;
    }
} 