package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private TransactionService transactionService;
    private User testUser;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
        testUser = new User();
        testUser.setUid("testUser123");

        // 清除测试数据
        File testFile = new File("src/main/resources/transaction/" + testUser.getUid() + ".json");
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    private Transaction createTransaction(String date, String type, String currency, double amount, String category,
            String paymentMethod) {
        Transaction tx = new Transaction();
        tx.setTransactionDate(date);
        tx.setTransactionType(type);
        tx.setCurrency(currency);
        tx.setAmount(amount);
        tx.setCategory(category);
        tx.setPaymentMethod(paymentMethod);
        tx.setDescription("Test description");
        return tx;
    }

    @Test
    void addTransaction_Success() {
        Transaction tx = createTransaction("2025-03-30", "Income", "USD", 1000, "Salary", "Bank Transfer");

        boolean result = transactionService.addTransaction(testUser, tx);
        assertTrue(result, "交易应当成功添加");

        List<Transaction> transactions = transactionService.loadTransactions(testUser);
        assertEquals(1, transactions.size(), "交易记录数量应为 1");
        assertEquals(tx, transactions.get(0), "交易记录内容不匹配");
    }

    @Test
    void addDuplicateTransaction_Fail() {
        Transaction tx1 = createTransaction("2025-03-30", "Income", "USD", 1000, "Salary", "Bank Transfer");
        Transaction tx2 = createTransaction("2025-03-30", "Income", "USD", 1000, "Salary", "Bank Transfer");

        transactionService.addTransaction(testUser, tx1);
        boolean result = transactionService.addTransaction(testUser, tx2);

        assertFalse(result, "重复交易不应当被添加");

        List<Transaction> transactions = transactionService.loadTransactions(testUser);
        assertEquals(1, transactions.size(), "应当仅存储一条交易记录");
    }

    @Test
    void loadTransactions_EmptyFile() {
        List<Transaction> transactions = transactionService.loadTransactions(testUser);
        assertTrue(transactions.isEmpty(), "初始交易列表应为空");
    }

    @Test
    void importTransactionsFromCSV() throws Exception {
        File csvFile = new File("src/test/resources/sample_transactions.csv");

        // **确保文件所在的目录存在**
        csvFile.getParentFile().mkdirs();

        // **动态创建 CSV 文件**
        if (!csvFile.exists()) {
            String csvContent = "2025-03-30,Income,USD,1000,Salary,Bank Transfer\n" +
                    "2025-03-31,Expense,USD,200,Groceries,Credit Card";
            Files.write(csvFile.toPath(), csvContent.getBytes(StandardCharsets.UTF_8));
        }

        transactionService.importTransactionsFromCSV(testUser, csvFile);
        List<Transaction> transactions = transactionService.loadTransactions(testUser);

        assertEquals(2, transactions.size(), "CSV 导入后交易记录应为 2 条");

        Transaction tx1 = createTransaction("2025-03-30", "Income", "USD", 1000, "Salary", "Bank Transfer");
        Transaction tx2 = createTransaction("2025-03-31", "Expense", "USD", 200, "Groceries", "Credit Card");

        assertTrue(transactions.contains(tx1), "交易记录应包含第 1 条");
        assertTrue(transactions.contains(tx2), "交易记录应包含第 2 条");
    }

}
