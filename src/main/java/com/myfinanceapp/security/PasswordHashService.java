package com.myfinanceapp.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * 提供密码哈希功能的服务类。
 * 使用Argon2id算法进行密码哈希，提供抗暴力破解和抗彩虹表攻击的保护。
 * 
 * @author Finanger Team
 * @version 1.0
 */
public final class PasswordHashService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Argon2 argon2 = Argon2Factory.create(
        Argon2Factory.Argon2Types.ARGON2id,
        EncryptionConfig.Argon2.SALT_LENGTH,
        EncryptionConfig.Argon2.HASH_LENGTH
    );

    private PasswordHashService() {
        // 私有构造函数，防止实例化
    }

    /**
     * 对密码进行哈希处理
     *
     * @param password 原始密码
     * @return 包含哈希值、盐值和参数的PasswordHashResult对象
     * @throws PasswordHashException 如果哈希过程中发生错误
     */
    public static PasswordHashResult hashPassword(String password) throws PasswordHashException {
        try {
            // 生成随机盐
            byte[] salt = generateSalt();
            
            // 执行密码哈希
            String hash = argon2.hash(
                EncryptionConfig.Argon2.ITERATIONS,
                EncryptionConfig.Argon2.MEMORY,
                EncryptionConfig.Argon2.PARALLELISM,
                password.toCharArray(),
                StandardCharsets.UTF_8
            );
            
            // 返回哈希结果
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
            throw new PasswordHashException("密码哈希处理时发生错误", e);
        }
    }

    /**
     * 验证密码
     *
     * @param password 待验证的密码
     * @param hash 存储的哈希值
     * @return 如果密码匹配返回true，否则返回false
     * @throws PasswordHashException 如果验证过程中发生错误
     */
    public static boolean verifyPassword(String password, String hash) throws PasswordHashException {
        try {
            return argon2.verify(hash, password.toCharArray());
        } catch (Exception e) {
            throw new PasswordHashException("密码验证时发生错误", e);
        }
    }

    /**
     * 生成随机盐
     *
     * @return 随机生成的盐字节数组
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[EncryptionConfig.Argon2.SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * 密码哈希结果对象
     */
    public record PasswordHashResult(String hash, String salt, Argon2Params params) {
    }

    /**
     * Argon2参数对象
     */
    public record Argon2Params(int memory, int iterations, int parallelism) {
    }

    /**
     * 密码哈希异常类
     */
    public static class PasswordHashException extends Exception {
        public PasswordHashException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 