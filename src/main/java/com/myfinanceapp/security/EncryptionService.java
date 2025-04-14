package com.myfinanceapp.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 提供AES加密和解密功能的服务类。
 * 使用AES-256-GCM模式进行加密，提供数据完整性和认证。
 * 
 * @author Finanger Team
 * @version 1.0
 */
public final class EncryptionService {
    private static final SecureRandom secureRandom = new SecureRandom();

    private EncryptionService() {
        // 私有构造函数，防止实例化
    }

    /**
     * 加密数据
     *
     * @param data 要加密的数据
     * @param key 加密密钥
     * @return 包含加密数据、IV和密钥ID的EncryptedData对象
     * @throws EncryptionException 如果加密过程中发生错误
     */
    public static EncryptedData encrypt(String data, SecretKey key) throws EncryptionException {
        try {
            // 生成随机IV
            byte[] iv = generateIV();
            
            // 初始化加密器
            Cipher cipher = Cipher.getInstance(EncryptionConfig.AES.ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(EncryptionConfig.AES.GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            
            // 执行加密
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            
            // 返回加密结果
            return new EncryptedData(
                Base64.getEncoder().encodeToString(encryptedData),
                Base64.getEncoder().encodeToString(iv),
                generateKeyId(key)
            );
        } catch (Exception e) {
            throw new EncryptionException("加密数据时发生错误", e);
        }
    }

    /**
     * 解密数据
     *
     * @param encryptedData 包含加密数据、IV和密钥ID的EncryptedData对象
     * @param key 解密密钥
     * @return 解密后的原始数据
     * @throws EncryptionException 如果解密过程中发生错误
     */
    public static String decrypt(EncryptedData encryptedData, SecretKey key) throws EncryptionException {
        try {
            // 解码IV和加密数据
            byte[] iv = Base64.getDecoder().decode(encryptedData.iv());
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData.data());
            
            // 初始化解密器
            Cipher cipher = Cipher.getInstance(EncryptionConfig.AES.ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(EncryptionConfig.AES.GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            
            // 执行解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new EncryptionException("解密数据时发生错误", e);
        }
    }

    /**
     * 从密码派生密钥
     *
     * @param password 用户密码
     * @param salt 盐值
     * @return 派生出的SecretKey对象
     * @throws EncryptionException 如果密钥派生过程中发生错误
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
            throw new EncryptionException("派生密钥时发生错误", e);
        }
    }

    /**
     * 生成随机IV
     *
     * @return 随机生成的IV字节数组
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[EncryptionConfig.AES.GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        return iv;
    }

    /**
     * 生成密钥ID
     *
     * @param key 密钥
     * @return 密钥ID字符串
     */
    private static String generateKeyId(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded()).substring(0, 8);
    }

    /**
     * 加密数据的结果对象
     */
    public record EncryptedData(String data, String iv, String keyId) {
    }

    /**
     * 加密异常类
     */
    public static class EncryptionException extends Exception {
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 