package com.myfinanceapp.service;

import org.json.JSONArray;
import org.json.JSONObject;
import com.myfinanceapp.security.EncryptionService;
import com.myfinanceapp.security.EncryptionService.EncryptedData;
import com.myfinanceapp.security.EncryptionService.EncryptionException;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Service class for managing and analyzing transaction data.
 * This service provides functionality for:
 * - Loading and parsing encrypted transaction data
 * - Calculating financial summaries (income, expenses, balance)
 * - Analyzing transactions by category and payment method
 * - Secure data handling with encryption
 * 
 * The service stores transaction data in encrypted JSON files
 * located at src/main/resources/transaction/{userUID}.json
 *
 * @author SE_Group110
 * @version 4.0
 */
public class TransactionDataService {
    private final String userUid;
    private static final Gson gson = new Gson();
    private static final String FIXED_KEY = "MyFinanceAppSecretKey1234567890";  // Fixed encryption key
    private static final String TRANSACTION_DIR = "src/main/resources/transaction";

    /**
     * Constructs a new TransactionDataService instance.
     *
     * @param userUid The unique identifier of the user whose transactions to manage
     */
    public TransactionDataService(String userUid) {
        this.userUid = userUid;
    }

    /**
     * Loads and decrypts transaction data from the user's file.
     * The data is stored in an encrypted JSON format and is decrypted
     * using a key derived from the user's ID.
     *
     * @return JSONArray containing the decrypted transaction data
     * @throws IOException If there is an error reading or decrypting the file
     */
    JSONArray loadTransactionData() throws IOException {
        Path filePath = Paths.get(TRANSACTION_DIR, userUid + ".json");
        
        // Return empty array if file doesn't exist
        if (!Files.exists(filePath)) {
            return new JSONArray();
        }
        
        String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        
        try {
            // Parse encrypted data from JSON
            EncryptedData encryptedData = gson.fromJson(content, EncryptedData.class);
            
            // Get encryption key derived from user ID
            SecretKey key = getEncryptionKey();
            
            // Decrypt the content
            String decryptedContent = EncryptionService.decrypt(encryptedData, key);
            return new JSONArray(decryptedContent);
        } catch (EncryptionException e) {
            throw new IOException("Failed to decrypt transaction data", e);
        }
    }

    /**
     * Derives an encryption key for the user's transaction data.
     * Uses a fixed key and the user's ID to create a unique encryption key.
     *
     * @return A SecretKey for encrypting/decrypting the user's data
     * @throws EncryptionException If there is an error deriving the key
     */
    private SecretKey getEncryptionKey() throws EncryptionException {
        // Use fixed key and user ID to derive encryption key
        byte[] salt = userUid.getBytes();  // Use user ID as salt
        return EncryptionService.deriveKey(FIXED_KEY, salt);
    }

    /**
     * Calculates the total income from all transactions.
     * Sums up the amounts of all transactions marked as "Income".
     *
     * @return The total income amount
     * @throws IOException If there is an error reading the transaction data
     */
    public double calculateTotalIncome() throws IOException {
        JSONArray transactions = loadTransactionData();
        double totalIncome = 0.0;
        for (int i = 0; i < transactions.length(); i++) {
            JSONObject transaction = transactions.getJSONObject(i);
            if ("Income".equals(transaction.getString("transactionType"))) {
                totalIncome += transaction.getDouble("amount");
            }
        }
        return totalIncome;
    }

    /**
     * Calculates the total expenses from all transactions.
     * Sums up the amounts of all transactions marked as "Expense".
     *
     * @return The total expense amount
     * @throws IOException If there is an error reading the transaction data
     */
    public double calculateTotalExpense() throws IOException {
        JSONArray transactions = loadTransactionData();
        double totalExpense = 0.0;
        for (int i = 0; i < transactions.length(); i++) {
            JSONObject transaction = transactions.getJSONObject(i);
            if ("Expense".equals(transaction.getString("transactionType"))) {
                totalExpense += transaction.getDouble("amount");
            }
        }
        return totalExpense;
    }

    /**
     * Calculates the net balance (income minus expenses).
     *
     * @return The net balance amount
     * @throws IOException If there is an error reading the transaction data
     */
    public double calculateNetBalance() throws IOException {
        return calculateTotalIncome() - calculateTotalExpense();
    }

    /**
     * Calculates the total amount for transactions in a specific category.
     *
     * @param category The category to calculate the total for
     * @return The total amount for the specified category
     * @throws IOException If there is an error reading the transaction data
     */
    public double calculateTotalAmountByCategory(String category) throws IOException {
        JSONArray transactions = loadTransactionData();
        double totalAmount = 0.0;
        for (int i = 0; i < transactions.length(); i++) {
            JSONObject transaction = transactions.getJSONObject(i);
            if (category.equals(transaction.getString("category"))) {
                totalAmount += transaction.getDouble("amount");
            }
        }
        return totalAmount;
    }

    /**
     * Counts the number of transactions in each category.
     * Returns a map where keys are category names and values are the count
     * of transactions in that category.
     *
     * @return Map of category names to transaction counts
     * @throws IOException If there is an error reading the transaction data
     */
    public Map<String, Integer> countTransactionsByCategory() throws IOException {
        JSONArray transactions = loadTransactionData();
        Map<String, Integer> categoryCount = new HashMap<>();
        for (int i = 0; i < transactions.length(); i++) {
            JSONObject transaction = transactions.getJSONObject(i);
            String category = transaction.getString("category");
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }
        return categoryCount;
    }

    /**
     * Counts the number of transactions for each payment method.
     * Returns a map where keys are payment method names and values are the count
     * of transactions using that payment method.
     *
     * @return Map of payment method names to transaction counts
     * @throws IOException If there is an error reading the transaction data
     */
    public Map<String, Integer> countTransactionsByPaymentMethod() throws IOException {
        JSONArray transactions = loadTransactionData();
        Map<String, Integer> paymentMethodCount = new HashMap<>();
        for (int i = 0; i < transactions.length(); i++) {
            JSONObject transaction = transactions.getJSONObject(i);
            String paymentMethod = transaction.getString("paymentMethod");
            paymentMethodCount.put(paymentMethod, paymentMethodCount.getOrDefault(paymentMethod, 0) + 1);
        }
        return paymentMethodCount;
    }
}