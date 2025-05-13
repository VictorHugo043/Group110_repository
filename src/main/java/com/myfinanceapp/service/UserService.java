package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myfinanceapp.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Service class for managing user accounts and authentication.
 * This service provides functionality for:
 * - User registration with secure password handling
 * - User authentication and login
 * - User profile management (username, password, security questions)
 * - User data persistence in JSON format
 * 
 * The service ensures that user UIDs remain constant throughout the application
 * and handles all user-related data operations securely.
 */
public class UserService {

    /** Path to the JSON file storing user data */
    static final String USER_JSON_PATH = "src/main/resources/users.json";

    private static final Gson gson = new Gson();
    private static final Type USER_LIST_TYPE = new TypeToken<List<User>>() {}.getType();

    /**
     * Registers a new user in the system.
     * Generates a unique UID and salt for the user, and stores their information
     * in the user database.
     *
     * @param username The desired username for the new account
     * @param password The user's password
     * @param secQuestion The security question for account recovery
     * @param secAnswer The answer to the security question
     * @return true if registration is successful, false if username already exists
     */
    public boolean registerUser(String username, String password, String secQuestion, String secAnswer) {
        List<User> users = loadUsers();

        // 检查是否存在相同用户名
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return false; // 用户名已存在
            }
        }

        // 生成唯一 UID
        String uid = UUID.randomUUID().toString();

        // 创建新用户时生成盐值
        byte[] saltBytes = new byte[16];
        new java.security.SecureRandom().nextBytes(saltBytes);
        String salt = java.util.Base64.getEncoder().encodeToString(saltBytes);

        users.add(new User(uid, username, password, secQuestion, secAnswer, salt));
        saveUsers(users);
        return true;
    }

    /**
     * Validates user login credentials.
     *
     * @param username The username to check
     * @param password The password to verify
     * @return true if credentials are valid, false otherwise
     */
    public boolean checkLogin(String username, String password) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a user by their username.
     * The search is case-insensitive.
     *
     * @param username The username to search for
     * @return The User object if found, null otherwise
     */
    public User findUserByUsername(String username) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Finds a user by their unique identifier (UID).
     * This method ensures that user identification remains consistent
     * throughout the application.
     *
     * @param uid The unique identifier to search for
     * @return The User object if found, null otherwise
     */
    public User findUserByUid(String uid) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUid().equals(uid)) {  // 修正：u.getUid() 而不是 getUid()
                return u;
            }
        }
        return null;
    }

    /**
     * Updates a user's username while maintaining their UID.
     * Ensures the new username is not already taken.
     *
     * @param oldUsername The current username
     * @param newUsername The desired new username
     * @return true if the update is successful, false if the new username is taken
     */
    public boolean updateUserName(String oldUsername, String newUsername) {
        List<User> users = loadUsers();

        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(oldUsername)) {
                // 确保新用户名没有被占用
                for (User existingUser : users) {
                    if (existingUser.getUsername().equalsIgnoreCase(newUsername)) {
                        return false; // 新用户名已存在
                    }
                }

                // 修改用户名但保持 UID 不变
                u.setUsername(newUsername);
                saveUsers(users);
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a complete User object after successful login.
     * This method is used to get the full user profile after authentication.
     *
     * @param username The username used for login
     * @param password The password used for login
     * @return The complete User object if login is successful, null otherwise
     */
    public User loginGetUser(String username, String password) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Loads all users from the JSON storage file.
     * Creates a new empty list if the file doesn't exist.
     * Ensures all users have a salt value for password security.
     *
     * @return List of all registered users
     */
    private static List<User> loadUsers() {
        File jsonFile = new File(USER_JSON_PATH);
        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)) {
            List<User> users = gson.fromJson(reader, USER_LIST_TYPE);
            if (users != null) {
                // 确保每个用户都有盐值
                for (User user : users) {
                    if (user.getSalt() == null) {
                        user.setSalt(user.generateSalt());
                    }
                }
                return users;
            }
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Saves the current list of users to the JSON storage file.
     * Uses UTF-8 encoding to ensure proper character handling.
     *
     * @param users The list of users to save
     */
    private static void saveUsers(List<User> users) {
        File jsonFile = new File(USER_JSON_PATH);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(jsonFile), StandardCharsets.UTF_8)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a user's password.
     * The user is identified by their UID to ensure consistency.
     *
     * @param uid The unique identifier of the user
     * @param newPassword The new password to set
     * @return true if the password was updated successfully, false if user not found
     */
    public boolean updatePassword(String uid, String newPassword) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUid().equals(uid)) {
                user.setPassword(newPassword);
                saveUsers(users);
                return true;
            }
        }
        return false;
    }

    /**
     * Updates a user's security question and answer.
     * The user is identified by their UID to ensure consistency.
     *
     * @param uid The unique identifier of the user
     * @param newQuestion The new security question
     * @param newAnswer The new answer to the security question
     * @return true if the update was successful, false if user not found
     */
    public boolean updateSecurityQuestion(String uid, String newQuestion, String newAnswer) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUid().equals(uid)) {
                user.setSecurityQuestion(newQuestion);
                user.setSecurityAnswer(newAnswer);
                saveUsers(users);
                return true;
            }
        }
        return false;
    }
}
