package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Service class for handling AI chat interactions using the DeepSeek API.
 * This class provides functionality to communicate with an AI model for generating
 * chat completions and responses based on user input and system prompts.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class AiChatService {
    private static final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";
    private static final String API_KEY = "sk-ijbuhipcfqfnwdbpmxqvsxgvhkktafpvxoizivjhwblqlent";
    /** The AI model to use for chat completions */
    private static final String MODEL   = "deepseek-ai/DeepSeek-R1-Distill-Qwen-32B";

    private static final Gson gson = new Gson();

    /**
     * Sends a chat completion request to the AI model and retrieves the response.
     * The method handles the entire process of preparing the request, sending it to the API,
     * and processing the response.
     *
     * Example usage:
     * <pre>
     * List<Map<String,String>> messages = new ArrayList<>();
     * messages.add(Map.of("role","user","content","Hello"));
     * messages.add(Map.of("role","assistant","content","Hi there"));
     * </pre>
     *
     * @param messages List of message maps containing the conversation history.
     *                Each message should have "role" and "content" keys.
     *                Roles can be "user", "assistant", or "system".
     * @param systemPrompt Optional system prompt to guide the AI's behavior.
     *                    Can be null or empty if no system prompt is needed.
     * @return The AI's response as a String, or null if the request fails.
     * @throws RuntimeException If there are network issues or API communication problems.
     */
    public static String chatCompletion(List<Map<String, String>> messages, String systemPrompt) {
        // Create a copy of the messages list to avoid modifying the original
        List<Map<String, String>> tempMessages = new ArrayList<>(messages);

        // Add system message if provided
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            tempMessages.add(sysMsg);
        }

        // Prepare request payload
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

            // Set request headers: Authorization + Content-Type
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            // Send request payload
            try (OutputStream os = conn.getOutputStream()) {
                byte[] data = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(data, 0, data.length);
            }

            // Read response
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

                    // Parse JSON response
                    JsonObject root = gson.fromJson(sb.toString(), JsonObject.class);
                    JsonArray choices = root.getAsJsonArray("choices");
                    if (choices != null && choices.size() > 0) {
                        JsonObject firstChoice = choices.get(0).getAsJsonObject();
                        JsonObject msgObj = firstChoice.getAsJsonObject("message");
                        String content = msgObj.get("content").getAsString();
                        return content;
                    }
                }
            } else {
                // Read error stream if request failed
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
