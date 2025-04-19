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
 * 交易数据服务类，提供对用户交易数据的统计和分析功能
 * 数据文件路径：src/main/resources/transaction/{用户UID}.json
 */
public class TransactionDataService {
    private final String userUid;
    private static final Gson gson = new Gson();
    private static final String FIXED_KEY = "MyFinanceAppSecretKey1234567890";  // 固定密钥
    private static final String TRANSACTION_DIR = "src/main/resources/transaction";

    /**
     * 构造函数
     * @param userUid 用户唯一标识符，用于定位数据文件
     */
    public TransactionDataService(String userUid) {
        this.userUid = userUid;
    }

    /**
     * 加载并解析交易数据
     * @return JSONArray形式的交易数据
     * @throws IOException 当文件读取失败时抛出
     */
    JSONArray loadTransactionData() throws IOException {
        Path filePath = Paths.get(TRANSACTION_DIR, userUid + ".json");
        
        // 如果文件不存在，返回空数组
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
     * 获取加密密钥
     */
    private SecretKey getEncryptionKey() throws EncryptionException {
        // 使用固定密钥和用户ID派生加密密钥
        byte[] salt = userUid.getBytes();  // 使用用户ID作为盐值
        return EncryptionService.deriveKey(FIXED_KEY, salt);
    }

    /**
     * 计算总收入
     * @return 总收入金额
     * @throws IOException 当文件读取失败时抛出
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
     * 计算总支出
     * @return 总支出金额
     * @throws IOException 当文件读取失败时抛出
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
     * 计算净余额（收入-支出）
     * @return 净余额
     * @throws IOException 当文件读取失败时抛出
     */
    public double calculateNetBalance() throws IOException {
        return calculateTotalIncome() - calculateTotalExpense();
    }

    /**
     * 计算指定类别的交易总金额
     * @param category 要统计的类别名称
     * @return 该类别下的总金额
     * @throws IOException 当文件读取失败时抛出
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
     * 按交易类别统计交易数量
     * @return 包含类别及其对应数量的Map
     * @throws IOException 当文件读取失败时抛出
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
     * 按支付方式统计交易数量
     * @return 包含支付方式及其对应数量的Map
     * @throws IOException 当文件读取失败时抛出
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