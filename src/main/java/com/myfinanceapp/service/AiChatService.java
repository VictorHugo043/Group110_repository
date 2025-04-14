package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AiChatService {
    private static final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";
    private static final String API_KEY = "sk-ijbuhipcfqfnwdbpmxqvsxgvhkktafpvxoizivjhwblqlent";
    // 你的模型
    private static final String MODEL   = "deepseek-ai/DeepSeek-R1-Distill-Qwen-32B";

    private static final Gson gson = new Gson();

    /**
     * messages 示例:
     *   List<Map<String,String>> messages = new ArrayList<>();
     *   messages.add(Map.of("role","user","content","你好"));
     *   messages.add(Map.of("role","assistant","content","你好呀"));
     */
    public static String chatCompletion(List<Map<String, String>> messages, String systemPrompt) {
        // 1) 先把用户这句话加入对话历史
        //messages.add(Map.of("role", "user", "content", userInput));
        // 避免修改原始消息列表
        List<Map<String, String>> tempMessages = new ArrayList<>(messages);

        // 添加系统消息
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            tempMessages.add(sysMsg);
        }

        // 2) 准备请求体
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", MODEL);
        payload.put("messages", tempMessages);
        payload.put("temperature", 0.7);
        payload.put("max_tokens", 1024);
        payload.put("response_format", Map.of("type","text"));

        String jsonPayload = gson.toJson(payload);

        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // 设置请求头: Authorization + Content-Type
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            // 3) 发送请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] data = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(data, 0, data.length);
            }

            // 4) 读取响应
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (InputStream is = conn.getInputStream();
                     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                     BufferedReader br = new BufferedReader(isr)) {

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    // 解析 JSON
                    JsonObject root = gson.fromJson(sb.toString(), JsonObject.class);
                    JsonArray choices = root.getAsJsonArray("choices");
                    if (choices != null && choices.size() > 0) {
                        JsonObject firstChoice = choices.get(0).getAsJsonObject();
                        JsonObject msgObj = firstChoice.getAsJsonObject("message");
                        String content = msgObj.get("content").getAsString();

                        // 把 AI 回答也加入 messages
                        //messages.add(Map.of("role","assistant","content", content));
                        return content;
                    }
                }
            } else {
                // 错误时读取 errorStream
                try (InputStream errStream = conn.getErrorStream()) {
                    if (errStream != null) {
                        String errMsg = new String(errStream.readAllBytes(), StandardCharsets.UTF_8);
                        System.err.println("API error response: " + errMsg);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
