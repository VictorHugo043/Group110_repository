package com.myfinanceapp.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 密码哈希服务的单元测试类
 */
class PasswordHashServiceTest {
    private static final String TEST_PASSWORD = "TestPassword123!@#";
    private static final String DIFFERENT_PASSWORD = "DifferentPassword456!@#";

    @Test
    void testHashPassword() throws PasswordHashService.PasswordHashException {
        // 对密码进行哈希处理
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(TEST_PASSWORD);
        
        // 验证哈希结果
        assertNotNull(result);
        assertNotNull(result.hash());
        assertNotNull(result.salt());
        assertNotNull(result.params());
        
        // 验证参数
        assertEquals(EncryptionConfig.Argon2.MEMORY, result.params().memory());
        assertEquals(EncryptionConfig.Argon2.ITERATIONS, result.params().iterations());
        assertEquals(EncryptionConfig.Argon2.PARALLELISM, result.params().parallelism());
    }

    @Test
    void testVerifyPassword() throws PasswordHashService.PasswordHashException {
        // 对密码进行哈希处理
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(TEST_PASSWORD);
        
        // 验证正确密码
        assertTrue(PasswordHashService.verifyPassword(TEST_PASSWORD, result.hash()));
        
        // 验证错误密码
        assertFalse(PasswordHashService.verifyPassword(DIFFERENT_PASSWORD, result.hash()));
    }

    @Test
    void testHashDifferentPasswords() throws PasswordHashService.PasswordHashException {
        // 对两个不同的密码进行哈希处理
        PasswordHashService.PasswordHashResult result1 = PasswordHashService.hashPassword(TEST_PASSWORD);
        PasswordHashService.PasswordHashResult result2 = PasswordHashService.hashPassword(DIFFERENT_PASSWORD);
        
        // 验证哈希值不同
        assertNotEquals(result1.hash(), result2.hash());
    }

    @Test
    void testHashSamePassword() throws PasswordHashService.PasswordHashException {
        // 对相同的密码进行两次哈希处理
        PasswordHashService.PasswordHashResult result1 = PasswordHashService.hashPassword(TEST_PASSWORD);
        PasswordHashService.PasswordHashResult result2 = PasswordHashService.hashPassword(TEST_PASSWORD);
        
        // 验证哈希值不同（因为使用了不同的盐）
        assertNotEquals(result1.hash(), result2.hash());
        
        // 验证两个哈希值都能验证原始密码
        assertTrue(PasswordHashService.verifyPassword(TEST_PASSWORD, result1.hash()));
        assertTrue(PasswordHashService.verifyPassword(TEST_PASSWORD, result2.hash()));
    }

    @Test
    void testHashEmptyPassword() throws PasswordHashService.PasswordHashException {
        String emptyPassword = "";
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(emptyPassword);
        assertTrue(PasswordHashService.verifyPassword(emptyPassword, result.hash()));
    }

    @Test
    void testHashSpecialCharacters() throws PasswordHashService.PasswordHashException {
        String passwordWithSpecialChars = "密码123!@#$%^&*()_+{}|:\"<>?[]\\;',./";
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(passwordWithSpecialChars);
        assertTrue(PasswordHashService.verifyPassword(passwordWithSpecialChars, result.hash()));
    }

    @Test
    void testHashLongPassword() throws PasswordHashService.PasswordHashException {
        // 生成一个很长的密码
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longPassword.append("a");
        }
        
        PasswordHashService.PasswordHashResult result = PasswordHashService.hashPassword(longPassword.toString());
        assertTrue(PasswordHashService.verifyPassword(longPassword.toString(), result.hash()));
    }
} 