package com.myfinanceapp.model;

import java.util.UUID;
import java.util.Base64;
import java.security.SecureRandom;

public class User {
    private String uid;  // 新增 UID 字段
    private String username;
    private String password;
    private String securityQuestion;
    private String securityAnswer;
    private String salt;  // 新增盐值字段

    // 必须保留无参构造给 Gson 反序列化使用
    public User() {
        this.salt = generateSalt();  // 初始化盐值
    }

    // 带 UID 的构造函数（用于从 JSON 读取数据时）
    public User(String uid, String username, String password, String securityQuestion, String securityAnswer, String salt) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.salt = salt;
    }

    // 无 UID 的构造函数（新注册用户时自动生成 UID）
    public User(String username, String password, String securityQuestion, String securityAnswer) {
        this.uid = UUID.randomUUID().toString(); // 生成唯一 UID
        this.username = username;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.salt = generateSalt();  // 生成随机盐值
    }

    // 生成随机盐值
    public String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    // Getter & Setter
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
