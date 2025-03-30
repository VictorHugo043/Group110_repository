package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myfinanceapp.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * UserService 负责用户的注册、登录、查找和修改用户名，并确保 UID 始终不变。
 */
public class UserService {

    // 用户数据存储路径
    static final String USER_JSON_PATH = "src/main/resources/users.json";

    private static final Gson gson = new Gson();
    private static final Type USER_LIST_TYPE = new TypeToken<List<User>>() {}.getType();

    /**
     * 注册用户：生成 UID，并追加到 JSON 文件中
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

        users.add(new User(uid, username, password, secQuestion, secAnswer));
        saveUsers(users);
        return true;
    }


    /**
     * 用户登录校验
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
     * 根据用户名查找用户
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
     * 通过 UID 查找用户（保证 UID 一直不变）
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
     * 修改用户名（UID 保持不变）
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
     * 用户登录后获取完整的 User 对象
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
     * 读取用户数据
     */
    private static List<User> loadUsers() {
        File jsonFile = new File(USER_JSON_PATH);
        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)) {
            List<User> users = gson.fromJson(reader, USER_LIST_TYPE);
            return (users != null) ? users : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 保存用户数据
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
     * 重设密码
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
