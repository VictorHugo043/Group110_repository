package com.myfinanceapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 加密服务的单元测试类
 */
class EncryptionServiceTest {
    private SecretKey testKey;
    private String testData;

    @BeforeEach
    void setUp() throws EncryptionService.EncryptionException {
        // 生成测试密钥
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        testKey = EncryptionService.deriveKey("testPassword", salt);
        
        // 测试数据
        testData = "这是一段测试数据，包含中文和特殊字符：!@#$%^&*()";
    }

    @Test
    void testEncryptDecrypt() throws EncryptionService.EncryptionException {
        // 加密数据
        EncryptionService.EncryptedData encryptedData = EncryptionService.encrypt(testData, testKey);
        
        // 验证加密结果
        assertNotNull(encryptedData);
        assertNotNull(encryptedData.data());
        assertNotNull(encryptedData.iv());
        assertNotNull(encryptedData.keyId());
        
        // 解密数据
        String decryptedData = EncryptionService.decrypt(encryptedData, testKey);
        
        // 验证解密结果
        assertEquals(testData, decryptedData);
    }

    @Test
    void testEncryptWithDifferentKeys() throws EncryptionService.EncryptionException {
        // 生成两个不同的密钥
        byte[] salt1 = new byte[16];
        byte[] salt2 = new byte[16];
        new SecureRandom().nextBytes(salt1);
        new SecureRandom().nextBytes(salt2);
        
        SecretKey key1 = EncryptionService.deriveKey("password1", salt1);
        SecretKey key2 = EncryptionService.deriveKey("password2", salt2);
        
        // 使用第一个密钥加密
        EncryptionService.EncryptedData encryptedData = EncryptionService.encrypt(testData, key1);
        
        // 尝试使用第二个密钥解密
        assertThrows(EncryptionService.EncryptionException.class, () -> {
            EncryptionService.decrypt(encryptedData, key2);
        });
    }

    @Test
    void testEncryptEmptyData() throws EncryptionService.EncryptionException {
        String emptyData = "";
        EncryptionService.EncryptedData encryptedData = EncryptionService.encrypt(emptyData, testKey);
        String decryptedData = EncryptionService.decrypt(encryptedData, testKey);
        assertEquals(emptyData, decryptedData);
    }

    @Test
    void testEncryptLargeData() throws EncryptionService.EncryptionException {
        // 生成大量测试数据
        StringBuilder largeData = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeData.append("这是一段测试数据，包含中文和特殊字符：!@#$%^&*()");
        }
        
        // 加密和解密
        EncryptionService.EncryptedData encryptedData = EncryptionService.encrypt(largeData.toString(), testKey);
        String decryptedData = EncryptionService.decrypt(encryptedData, testKey);
        
        assertEquals(largeData.toString(), decryptedData);
    }

    @Test
    void testKeyDerivation() throws EncryptionService.EncryptionException {
        String password = "testPassword";
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        
        // 使用相同的密码和盐生成两个密钥
        SecretKey key1 = EncryptionService.deriveKey(password, salt);
        SecretKey key2 = EncryptionService.deriveKey(password, salt);
        
        // 验证两个密钥相同
        assertArrayEquals(key1.getEncoded(), key2.getEncoded());
        
        // 使用不同的盐生成密钥
        byte[] differentSalt = new byte[16];
        new SecureRandom().nextBytes(differentSalt);
        SecretKey key3 = EncryptionService.deriveKey(password, differentSalt);
        
        // 验证密钥不同
        assertFalse(java.util.Arrays.equals(key1.getEncoded(), key3.getEncoded()));
    }
} 