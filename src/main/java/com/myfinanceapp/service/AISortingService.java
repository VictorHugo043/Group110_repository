package com.myfinanceapp.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject; // 引入 JSONObject 类

public class AISortingService {

    public static String sort(String description) {
        try {
            // 设置 API 请求的 URL
            String urlString = "https://api.siliconflow.cn/v1/chat/completions";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法和请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization",
                    "Bearer sk-ijbuhipcfqfnwdbpmxqvsxgvhkktafpvxoizivjhwblqlent");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String prompt = description
                    + " 根据以上对一次交易的描述区分一下交易类型（如Salary,Transport,Loan Repayment等等），你的所有回答仅需包含1~2个英文单词。";

            // 构建请求的 JSON 数据
            String jsonInputString = "{\n" +
                    "    \"model\": \"Qwen/Qwen2.5-72B-Instruct\",\n" +
                    "    \"messages\": [\n" +
                    "        {\n" +
                    "            \"role\": \"system\",\n" +
                    "            \"content\": \"You are a helpful assistant.\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"role\": \"user\",\n" +
                    "            \"content\": \"" + prompt + "\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";

            // 发送请求数据
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 读取响应内容
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 解析 JSON 响应并提取回答内容
            JSONObject jsonResponse = new JSONObject(response.toString());
            String content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return content;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred during API request.";
        }
    }

}
