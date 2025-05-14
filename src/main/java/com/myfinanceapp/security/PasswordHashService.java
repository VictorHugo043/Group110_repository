package com.myfinanceapp.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * Service class providing password hashing functionality using Argon2id.
 * This class implements secure password hashing with the following features:
 * - Uses Argon2id algorithm for memory-hard password hashing
 * - Provides resistance against brute-force and rainbow table attacks
 * - Includes salt generation and verification
 * - Configurable memory usage, iterations, and parallelism
 * 
 * @author SE_Group110
 * @version 4.0
 */
public final class PasswordHashService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Argon2 argon2 = Argon2Factory.create(
        Argon2Factory.Argon2Types.ARGON2id,
        EncryptionConfig.Argon2.SALT_LENGTH,
        EncryptionConfig.Argon2.HASH_LENGTH
    );

    private PasswordHashService() {
        // Private constructor to prevent instantiation
    }

    /**
     * Hashes a password using Argon2id algorithm.
     * This method:
     * - Generates a cryptographically secure random salt
     * - Applies the Argon2id hashing algorithm with configured parameters
     * - Returns the hash result along with the salt and parameters
     *
     * @param password The password to be hashed
     * @return A PasswordHashResult object containing the hash, salt, and parameters
     * @throws PasswordHashException If an error occurs during the hashing process
     */
    public static PasswordHashResult hashPassword(String password) throws PasswordHashException {
        try {
            // Generate random salt
            byte[] salt = generateSalt();
            
            // Perform password hashing
            String hash = argon2.hash(
                EncryptionConfig.Argon2.ITERATIONS,
                EncryptionConfig.Argon2.MEMORY,
                EncryptionConfig.Argon2.PARALLELISM,
                password.toCharArray(),
                StandardCharsets.UTF_8
            );
            
            // Return hash result
            return new PasswordHashResult(
                hash,
                Base64.getEncoder().encodeToString(salt),
                new Argon2Params(
                    EncryptionConfig.Argon2.MEMORY,
                    EncryptionConfig.Argon2.ITERATIONS,
                    EncryptionConfig.Argon2.PARALLELISM
                )
            );
        } catch (Exception e) {
            throw new PasswordHashException("Error occurred during password hashing", e);
        }
    }

    /**
     * Verifies a password against a stored hash.
     * This method:
     * - Uses the Argon2id algorithm to verify the password
     * - Compares the computed hash with the stored hash
     * - Returns true if the password matches, false otherwise
     *
     * @param password The password to verify
     * @param hash The stored hash to verify against
     * @return true if the password matches the hash, false otherwise
     * @throws PasswordHashException If an error occurs during the verification process
     */
    public static boolean verifyPassword(String password, String hash) throws PasswordHashException {
        try {
            return argon2.verify(hash, password.toCharArray());
        } catch (Exception e) {
            throw new PasswordHashException("Error occurred during password verification", e);
        }
    }

    /**
     * Generates a cryptographically secure random salt.
     * The salt length is configured in EncryptionConfig.Argon2.SALT_LENGTH.
     *
     * @return A byte array containing the random salt
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[EncryptionConfig.Argon2.SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Record class representing the result of a password hashing operation.
     * Contains the hash, salt, and Argon2 parameters used for hashing.
     *
     * @param hash The hashed password
     * @param salt The salt used in the hashing process
     * @param params The Argon2 parameters used for hashing
     */
    public record PasswordHashResult(String hash, String salt, Argon2Params params) {
    }

    /**
     * Record class representing the parameters used in Argon2 hashing.
     * Contains memory usage, iteration count, and parallelism settings.
     *
     * @param memory Memory usage in KB
     * @param iterations Number of iterations
     * @param parallelism Degree of parallelism
     */
    public record Argon2Params(int memory, int iterations, int parallelism) {
    }

    /**
     * Exception class for password hashing-related errors.
     * Used to wrap and propagate hashing/verification errors with meaningful messages.
     */
    public static class PasswordHashException extends Exception {
        public PasswordHashException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 