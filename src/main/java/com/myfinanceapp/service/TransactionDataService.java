package com.myfinanceapp.service;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 交易数据服务类，提供对用户交易数据的统计和分析功能
 * 数据文件路径：src/main/resources/transaction/{用户UID}.json
 */
public class TransactionDataService {
    private final String userUid;

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
    private JSONArray loadTransactionData() throws IOException {
        String filePath = "src/main/resources/transaction/" + userUid + ".json";
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JSONArray(content);
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