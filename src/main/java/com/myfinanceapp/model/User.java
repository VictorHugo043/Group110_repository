package com.myfinanceapp.model;

import java.util.UUID;
import java.util.Base64;
import java.security.SecureRandom;

/**
 * Represents a user in the financial management system.
 * This class handles user authentication and security-related information.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class User {
    /** Unique identifier for the user */
    private String uid;
    /** Username for login */
    private String username;
    /** Hashed password */
    private String password;
    /** Security question for password recovery */
    private String securityQuestion;
    /** Answer to the security question */
    private String securityAnswer;
    /** Salt value for password hashing */
    private String salt;

    /**
     * Default constructor required for Gson deserialization.
     * Initializes a new salt value for the user.
     */
    public User() {
        this.salt = generateSalt();
    }

    /**
     * Constructor with UID for creating a user from existing data.
     * Used when reading data from JSON.
     *
     * @param uid Unique identifier for the user
     * @param username Username for login
     * @param password Hashed password
     * @param securityQuestion Security question for password recovery
     * @param securityAnswer Answer to the security question
     * @param salt Salt value for password hashing
     */
    public User(String uid, String username, String password, String securityQuestion, String securityAnswer, String salt) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.salt = salt;
    }

    /**
     * Constructor for new user registration.
     * Automatically generates a new UID and salt value.
     *
     * @param username Username for login
     * @param password Hashed password
     * @param securityQuestion Security question for password recovery
     * @param securityAnswer Answer to the security question
     */
    public User(String username, String password, String securityQuestion, String securityAnswer) {
        this.uid = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.salt = generateSalt();
    }

    /**
     * Generates a random salt value for password hashing.
     * Uses SecureRandom to ensure cryptographic security.
     *
     * @return Base64 encoded string of the generated salt
     */
    public String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    /**
     * Gets the user's unique identifier.
     * @return The user's UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the user's unique identifier.
     * @param uid The UID to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Gets the user's username.
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username.
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user's hashed password.
     * @return The hashed password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * @param password The password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's security question.
     * @return The security question
     */
    public String getSecurityQuestion() {
        return securityQuestion;
    }

    /**
     * Sets the user's security question.
     * @param securityQuestion The security question to set
     */
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    /**
     * Gets the user's security answer.
     * @return The security answer
     */
    public String getSecurityAnswer() {
        return securityAnswer;
    }

    /**
     * Sets the user's security answer.
     * @param securityAnswer The security answer to set
     */
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    /**
     * Gets the user's salt value.
     * @return The salt value
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Sets the user's salt value.
     * @param salt The salt value to set
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }
}
