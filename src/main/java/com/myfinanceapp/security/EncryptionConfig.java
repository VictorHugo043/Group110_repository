package com.myfinanceapp.security;

/**
 * 加密配置类，包含所有加密相关的常量配置。
 * 该类定义了AES和Argon2加密算法所需的参数。
 * 
 * @author Finanger Team
 * @version 1.0
 */
public final class EncryptionConfig {
    private EncryptionConfig() {
        // 私有构造函数，防止实例化
    }

    /**
     * AES加密算法配置
     */
    public static final class AES {
        private AES() {
            // 私有构造函数，防止实例化
        }

        /** AES加密算法名称 */
        public static final String ALGORITHM = "AES/GCM/NoPadding";
        
        /** AES密钥长度（位） */
        public static final int KEY_LENGTH = 256;
        
        /** GCM认证标签长度（位） */
        public static final int GCM_TAG_LENGTH = 128;
        
        /** GCM初始化向量长度（字节） */
        public static final int GCM_IV_LENGTH = 12;
        
        /** 密钥派生函数迭代次数 */
        public static final int PBKDF2_ITERATIONS = 65536;
    }

    /**
     * Argon2密码哈希配置
     */
    public static final class Argon2 {
        private Argon2() {
            // 私有构造函数，防止实例化
        }

        /** 内存使用量（KB） */
        public static final int MEMORY = 65536;
        
        /** 迭代次数 */
        public static final int ITERATIONS = 4;
        
        /** 并行度 */
        public static final int PARALLELISM = 2;
        
        /** 哈希长度（字节） */
        public static final int HASH_LENGTH = 32;
        
        /** 盐长度（字节） */
        public static final int SALT_LENGTH = 16;
    }
} 