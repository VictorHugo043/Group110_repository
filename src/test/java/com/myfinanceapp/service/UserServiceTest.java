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
     * 测试专用的 UserService 子类，用于覆盖文件路径
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
            // 创建测试专用的 UserService 实例
            File usersFile = new File(tempDir.toFile(), "users.json");
            if (!usersFile.exists()) {
                if (!usersFile.createNewFile()) {
                    throw new RuntimeException("Failed to create users.json file");
                }
                // 写入空的 JSON 数组
                java.nio.file.Files.write(usersFile.toPath(), "[]".getBytes());
            }
            
            userService = new TestUserService(usersFile.getAbsolutePath());
            
            // 验证文件是否可写
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
        // 测试成功注册
        boolean result = userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        assertTrue(result, "User registration should succeed");

        // 验证用户信息
        User user = userService.findUserByUsername("testuser");
        assertNotNull(user, "User should be found after registration");
        assertNotNull(user.getUid(), "UID should be generated");
        assertEquals("testuser", user.getUsername(), "Username should match");
        assertEquals("password123", user.getPassword(), "Password should match");
        assertEquals("What is your pet's name?", user.getSecurityQuestion(), "Security question should match");
        assertEquals("Fluffy", user.getSecurityAnswer(), "Security answer should match");
        assertNotNull(user.getSalt(), "Salt should be generated");

        // 测试重复用户名（大小写不敏感）
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
        // 注册用户
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");

        // 测试成功登录
        boolean result = userService.checkLogin("testuser", "password123");
        assertTrue(result, "Login with correct credentials should succeed");

        // 测试密码错误
        result = userService.checkLogin("testuser", "wrongpassword");
        assertFalse(result, "Login with incorrect password should fail");

        // 测试用户名大小写不匹配（大小写敏感）
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
        // 注册用户
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");

        // 测试查找存在的用户（大小写不敏感）
        User user = userService.findUserByUsername("testuser");
        assertNotNull(user, "Existing user should be found");
        assertEquals("testuser", user.getUsername(), "Username should match");

        // 测试查找不存在的用户
        user = userService.findUserByUsername("nonexistent");
        assertNull(user, "Non-existent user should return null");

        // 测试大小写不敏感的查找
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
        // 注册用户并获取 UID
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        User user = userService.findUserByUsername("testuser");
        String uid = user.getUid();

        // 测试通过 UID 查找用户
        User foundUser = userService.findUserByUid(uid);
        assertNotNull(foundUser, "User should be found by UID");
        assertEquals(uid, foundUser.getUid(), "UID should match");
        assertEquals("testuser", foundUser.getUsername(), "Username should match");

        // 测试查找不存在的 UID
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
        // 注册用户
        userService.registerUser("olduser", "password123", "What is your pet's name?", "Fluffy");

        // 测试成功更新用户名
        boolean result = userService.updateUserName("olduser", "newuser");
        assertTrue(result, "Username update should succeed");

        // 验证用户名已更新
        User user = userService.findUserByUsername("newuser");
        assertNotNull(user, "User should be found with new username");
        assertEquals("newuser", user.getUsername(), "Username should be updated");

        // 测试更新到已存在的用户名（大小写不敏感）
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
        // 注册用户
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");

        // 测试成功登录后获取用户
        User user = userService.loginGetUser("testuser", "password123");
        assertNotNull(user, "User should be retrieved after successful login");
        assertEquals("testuser", user.getUsername(), "Username should match");

        // 测试登录失败后获取用户
        user = userService.loginGetUser("testuser", "wrongpassword");
        assertNull(user, "Failed login should return null");

        // 测试用户名大小写不匹配（大小写敏感）
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
        // 注册用户
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        User user = userService.findUserByUsername("testuser");

        // 测试更新密码
        boolean result = userService.updatePassword(user.getUid(), "newpassword123");
        assertTrue(result, "Password update should succeed");

        // 验证密码已更新
        User updatedUser = userService.findUserByUid(user.getUid());
        assertEquals("newpassword123", updatedUser.getPassword(), "Password should be updated");

        // 测试使用无效 UID 更新密码
        result = userService.updatePassword("nonexistent-uid", "newpassword123");
        assertFalse(result, "Password update with invalid UID should fail");
    }

    /**
     * Tests security question update functionality.
     * Verifies that:
     * - Security questions can be updated successfully
     * - Updates with invalid UIDs are rejected
     */
    @Test
    void updateSecurityQuestion() {
        // 注册用户
        userService.registerUser("testuser", "password123", "What is your pet's name?", "Fluffy");
        User user = userService.findUserByUsername("testuser");

        // 测试更新安全问题
        boolean result = userService.updateSecurityQuestion(user.getUid(), "What is your favorite color?", "Blue");
        assertTrue(result, "Security question update should succeed");

        // 验证安全问题已更新
        User updatedUser = userService.findUserByUid(user.getUid());
        assertEquals("What is your favorite color?", updatedUser.getSecurityQuestion(), "Security question should be updated");
        assertEquals("Blue", updatedUser.getSecurityAnswer(), "Security answer should be updated");

        // 测试使用无效 UID 更新安全问题
        result = userService.updateSecurityQuestion("nonexistent-uid", "What is your favorite color?", "Blue");
        assertFalse(result, "Security question update with invalid UID should fail");
    }
}
