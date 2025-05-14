package com.myfinanceapp.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 * Service class for automatically categorizing financial transactions using AI.
 * This service utilizes an external AI API to analyze transaction descriptions
 * and determine the appropriate category for each transaction.
 * 
 * The service supports the following transaction categories:
 * - Housing
 * - Shopping
 * - Gift
 * - Food & Drink
 * - Freelance
 * - Transport
 * - Groceries
 * - Debt
 * - Leisure
 * - Healthcare
 * - Utilities
 * - Investment
 * - Bonus
 * - Salary
 * - Others
 *
 * @author SE_Group110
 * @version 4.0
 */
public class AISortingService {

    /**
     * Analyzes a transaction description and determines its appropriate category using AI.
     * This method makes an API call to an AI service to analyze the description and
     * returns the most suitable category from the predefined list.
     *
     * @param description The transaction description to analyze
     * @return The determined category for the transaction, or an error message if the analysis fails
     * @throws RuntimeException If there is an error during the API request
     */
    public static String sort(String description) {
        try {
            // Set API request URL
            String urlString = "https://api.siliconflow.cn/v1/chat/completions";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization",
                    "Bearer sk-ijbuhipcfqfnwdbpmxqvsxgvhkktafpvxoizivjhwblqlent");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String prompt = description
                    + " Based on the above transaction description, categorize it into one of the following types: Housing, Shopping, Gift, Food & Drink, Freelance, Transport, Groceries, Debt, Leisure, Healthcare, Utilities, Investment, Bonus, Salary, Others. Your response should only include the determined category.";

            // Build request JSON data
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

            // Send request data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response content
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response and extract answer content
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
