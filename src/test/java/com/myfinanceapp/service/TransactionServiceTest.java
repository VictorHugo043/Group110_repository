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

/**
 * Unit test class for the TransactionService.
 * This class contains tests for transaction management functionality including:
 * - Transaction addition
 * - Duplicate transaction handling
 * - Transaction loading
 * - CSV import functionality
 *
 * @author SE_Group110
 * @version 4.0
 */
class TransactionServiceTest {

    private TransactionService transactionService;
    private User testUser;

    /**
     * Sets up the test environment before each test.
     * Initializes the TransactionService and test user.
     * Cleans up any existing test data files.
     */
    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
        testUser = new User();
        testUser.setUid("testUser123");

        // Clean up test data
        File testFile = new File("src/main/resources/transaction/" + testUser.getUid() + ".json");
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    /**
     * Helper method to create a test transaction with specified parameters.
     *
     * @param date Transaction date
     * @param type Transaction type (Income/Expense)
     * @param currency Currency code
     * @param amount Transaction amount
     * @param category Transaction category
     * @param paymentMethod Payment method
     * @return A new Transaction object with the specified parameters
     */
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

    /**
     * Tests successful transaction addition.
     * Verifies that:
     * - Transaction is successfully added
     * - Transaction list contains the added transaction
     * - Transaction details are correctly stored
     */
    @Test
    void addTransaction_Success() {
        Transaction tx = createTransaction("2025-03-30", "Income", "USD", 1000, "Salary", "Bank Transfer");

        boolean result = transactionService.addTransaction(testUser, tx);
        assertTrue(result, "Transaction should be added successfully");

        List<Transaction> transactions = transactionService.loadTransactions(testUser);
        assertEquals(1, transactions.size(), "Transaction list should contain 1 record");
        assertEquals(tx, transactions.get(0), "Transaction details do not match");
    }

    /**
     * Tests duplicate transaction handling.
     * Verifies that:
     * - First transaction is added successfully
     * - Duplicate transaction is rejected
     * - Only one transaction record is stored
     */
    @Test
    void addDuplicateTransaction_Fail() {
        Transaction tx1 = createTransaction("2025-03-30", "Income", "USD", 1000, "Salary", "Bank Transfer");
        Transaction tx2 = createTransaction("2025-03-30", "Income", "USD", 1000, "Salary", "Bank Transfer");

        transactionService.addTransaction(testUser, tx1);
        boolean result = transactionService.addTransaction(testUser, tx2);

        assertFalse(result, "Duplicate transaction should not be added");

        List<Transaction> transactions = transactionService.loadTransactions(testUser);
        assertEquals(1, transactions.size(), "Should only store one transaction record");
    }

    /**
     * Tests loading transactions from an empty file.
     * Verifies that an empty transaction list is returned when no transactions exist.
     */
    @Test
    void loadTransactions_EmptyFile() {
        List<Transaction> transactions = transactionService.loadTransactions(testUser);
        assertTrue(transactions.isEmpty(), "Initial transaction list should be empty");
    }

    /**
     * Tests CSV import functionality.
     * Verifies that:
     * - CSV file is correctly read
     * - Transactions are properly imported
     * - Imported transactions match the CSV data
     *
     * @throws Exception if file operations fail
     */
    @Test
    void importTransactionsFromCSV() throws Exception {
        File csvFile = new File("src/test/resources/sample_transactions.csv");

        // Ensure the directory exists
        csvFile.getParentFile().mkdirs();

        // Create CSV file if it doesn't exist
        if (!csvFile.exists()) {
            String csvContent = "2025-03-30,Income,USD,1000,Salary,Bank Transfer\n" +
                    "2025-03-31,Expense,USD,200,Groceries,Credit Card";
            Files.write(csvFile.toPath(), csvContent.getBytes(StandardCharsets.UTF_8));
        }

        transactionService.importTransactionsFromCSV(testUser, csvFile);
        List<Transaction> transactions = transactionService.loadTransactions(testUser);

        assertEquals(2, transactions.size(), "Should have 2 transactions after CSV import");

        Transaction tx1 = createTransaction("2025-03-30", "Income", "USD", 1000, "Salary", "Bank Transfer");
        Transaction tx2 = createTransaction("2025-03-31", "Expense", "USD", 200, "Groceries", "Credit Card");

        assertTrue(transactions.contains(tx1), "Transaction list should contain first transaction");
        assertTrue(transactions.contains(tx2), "Transaction list should contain second transaction");
    }
}
