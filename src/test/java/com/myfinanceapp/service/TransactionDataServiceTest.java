package com.myfinanceapp.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionDataServiceTest {

    private TransactionDataService transactionDataService;

    //  模拟 JSON 交易数据
    private final String mockJsonData = "["
            + "{ \"transactionType\": \"Income\", \"amount\": 1000.0, \"category\": \"Salary\", \"paymentMethod\": \"Bank Transfer\" },"
            + "{ \"transactionType\": \"Expense\", \"amount\": 200.0, \"category\": \"Groceries\", \"paymentMethod\": \"Credit Card\" },"
            + "{ \"transactionType\": \"Income\", \"amount\": 500.0, \"category\": \"Freelance\", \"paymentMethod\": \"PayPal\" },"
            + "{ \"transactionType\": \"Expense\", \"amount\": 150.0, \"category\": \"Entertainment\", \"paymentMethod\": \"Cash\" },"
            + "{ \"transactionType\": \"Expense\", \"amount\": 100.0, \"category\": \"Groceries\", \"paymentMethod\": \"Debit Card\" }"
            + "]";

    @BeforeEach
    void setUp() throws IOException {
        // 1️ 创建 TransactionDataService 实例（使用 mock UID）
        transactionDataService = Mockito.spy(new TransactionDataService("testUser"));

        // 2️ 使用 Mockito 模拟 loadTransactionData() 方法，避免真实文件操作
        doReturn(new JSONArray(mockJsonData)).when(transactionDataService).loadTransactionData();
    }

    @Test
    void calculateTotalIncome() throws IOException {
        //  1000 + 500 = 1500
        double totalIncome = transactionDataService.calculateTotalIncome();
        assertEquals(1500.0, totalIncome, 0.01, "总收入计算错误");
    }

    @Test
    void calculateTotalExpense() throws IOException {
        //  200 + 150 + 100 = 450
        double totalExpense = transactionDataService.calculateTotalExpense();
        assertEquals(450.0, totalExpense, 0.01, "总支出计算错误");
    }

    @Test
    void calculateNetBalance() throws IOException {
        //  (1500 - 450) = 1050
        double netBalance = transactionDataService.calculateNetBalance();
        assertEquals(1050.0, netBalance, 0.01, "净余额计算错误");
    }

    @Test
    void calculateTotalAmountByCategory() throws IOException {
        //  测试 "Groceries" 类别的总金额 (200 + 100)
        double groceriesTotal = transactionDataService.calculateTotalAmountByCategory("Groceries");
        assertEquals(300.0, groceriesTotal, 0.01, "类别 Groceries 总金额计算错误");

        //  测试 "Salary" 类别的总金额 (1000)
        double salaryTotal = transactionDataService.calculateTotalAmountByCategory("Salary");
        assertEquals(1000.0, salaryTotal, 0.01, "类别 Salary 总金额计算错误");
    }

    @Test
    void countTransactionsByCategory() throws IOException {
        //  期望的交易类别统计
        Map<String, Integer> categoryCounts = transactionDataService.countTransactionsByCategory();

        assertEquals(2, categoryCounts.get("Groceries"), "类别 Groceries 交易次数错误");
        assertEquals(1, categoryCounts.get("Entertainment"), "类别 Entertainment 交易次数错误");
        assertEquals(1, categoryCounts.get("Salary"), "类别 Salary 交易次数错误");
        assertEquals(1, categoryCounts.get("Freelance"), "类别 Freelance 交易次数错误");
    }

    @Test
    void countTransactionsByPaymentMethod() throws IOException {
        //  期望的支付方式统计
        Map<String, Integer> paymentMethodCounts = transactionDataService.countTransactionsByPaymentMethod();

        assertEquals(1, paymentMethodCounts.get("Bank Transfer"), "支付方式 Bank Transfer 交易次数错误");
        assertEquals(1, paymentMethodCounts.get("PayPal"), "支付方式 PayPal 交易次数错误");
        assertEquals(1, paymentMethodCounts.get("Credit Card"), "支付方式 Credit Card 交易次数错误");
        assertEquals(1, paymentMethodCounts.get("Cash"), "支付方式 Cash 交易次数错误");
        assertEquals(1, paymentMethodCounts.get("Debit Card"), "支付方式 Debit Card 交易次数错误");
    }
}
