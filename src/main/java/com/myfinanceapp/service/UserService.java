package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myfinanceapp.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * src/main/resources/users.json 读写用户信息 (JSON)
 */
public class UserService {

    // 这里用相对路径指向 resources 下的 JSON 文件
    // 注意：在打包为jar时，可能无法直接写入resources，可改为绝对路径或用户目录
    private static final String USER_JSON_PATH = "src/main/resources/users.json";

    private static final Gson gson = new Gson();
    // 定义一个用于解析 List<User> 的类型
    private static final Type USER_LIST_TYPE = new TypeToken<List<User>>() {}.getType();

    /**
     * 注册用户：追加到JSON中
     */
    public boolean registerUser(String username, String password,String secQuestion, String secAnswer) {
        // 1. 先读取当前JSON中已有的用户列表
        List<User> users = loadUsers();
        // 2. 检查是否重名
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                // 已经存在同名用户
                return false;
            }
        }
        // 3. 不存在则添加
        users.add(new User(username, password, secQuestion, secAnswer));
        // 4. 保存回JSON
        saveUsers(users);
        return true;
    }

    /**
     * 登录校验
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
     * 加载全部用户
     */
    private static List<User> loadUsers() {
        File jsonFile = new File(USER_JSON_PATH);
        if (!jsonFile.exists()) {
            // 文件不存在则返回空列表
            return new ArrayList<>();
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)) {
            List<User> users = gson.fromJson(reader, USER_LIST_TYPE);
            if (users == null) {
                users = new ArrayList<>();
            }
            return users;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 保存用户列表到 JSON
     */
    private static void saveUsers(List<User> users) {
        File jsonFile = new File(USER_JSON_PATH);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(jsonFile), StandardCharsets.UTF_8)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public User findUserByUsername(String username) {
        List<User> users = loadUsers(); // CSV/JSON 读取
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    /*public boolean updateUser(User updatedUser) {
        List<User> users = loadUsers();
        for (int i=0; i<users.size(); i++) {
            if (users.get(i).getUsername().equalsIgnoreCase(updatedUser.getUsername())) {
                // 更新
                users.set(i, updatedUser);
                saveUsers(users); // 写回CSV/JSON
                return true;
            }
        }
        return false;
    }*/
    public static boolean updateUserName(User user, String oldName){
        List<User> users = loadUsers();
        for(int i=0; i<users.size(); i++){
            if(users.get(i).getUsername().equalsIgnoreCase(oldName)){
                // 找到 => users.set(i, user);
                users.set(i, user);
                saveUsers(users);
                return true;
            }
        }
        return false;
    }



    public User loginGetUser(String username, String password) {
        // 假设你在 CSV 或 JSON 中读出所有 User
        List<User> users = loadUsers();
        for(User u: users){
            if(u.getUsername().equalsIgnoreCase(username)
                    && u.getPassword().equals(password)) {
                return u; // 找到并返回完整 User
            }
        }
        return null; // 未找到
    }


}
