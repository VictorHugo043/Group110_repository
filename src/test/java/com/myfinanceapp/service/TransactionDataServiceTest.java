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

/**
 * Unit test class for the TransactionDataService.
 * This class contains tests for transaction data analysis functionality including:
 * - Total income calculation
 * - Total expense calculation
 * - Net balance calculation
 * - Category-based amount calculation
 * - Transaction counting by category
 * - Transaction counting by payment method
 *
 * @author SE_Group110
 * @version 4.0
 */
class TransactionDataServiceTest {

    private TransactionDataService transactionDataService;

    /**
     * Mock JSON transaction data for testing.
     * Contains a mix of income and expense transactions with different categories and payment methods.
     */
    private final String mockJsonData = "["
            + "{ \"transactionType\": \"Income\", \"amount\": 1000.0, \"category\": \"Salary\", \"paymentMethod\": \"Bank Transfer\" },"
            + "{ \"transactionType\": \"Expense\", \"amount\": 200.0, \"category\": \"Groceries\", \"paymentMethod\": \"Credit Card\" },"
            + "{ \"transactionType\": \"Income\", \"amount\": 500.0, \"category\": \"Freelance\", \"paymentMethod\": \"PayPal\" },"
            + "{ \"transactionType\": \"Expense\", \"amount\": 150.0, \"category\": \"Entertainment\", \"paymentMethod\": \"Cash\" },"
            + "{ \"transactionType\": \"Expense\", \"amount\": 100.0, \"category\": \"Groceries\", \"paymentMethod\": \"Debit Card\" }"
            + "]";

    /**
     * Sets up the test environment before each test.
     * Creates a TransactionDataService instance with mock data and configures it to return predefined JSON data.
     *
     * @throws IOException if there is an error during setup
     */
    @BeforeEach
    void setUp() throws IOException {
        // Create TransactionDataService instance with mock UID
        transactionDataService = Mockito.spy(new TransactionDataService("testUser"));

        // Mock loadTransactionData() method to avoid real file operations
        doReturn(new JSONArray(mockJsonData)).when(transactionDataService).loadTransactionData();
    }

    /**
     * Tests total income calculation.
     * Verifies that the sum of all income transactions is calculated correctly.
     * Expected result: 1000 + 500 = 1500
     *
     * @throws IOException if there is an error during calculation
     */
    @Test
    void calculateTotalIncome() throws IOException {
        double totalIncome = transactionDataService.calculateTotalIncome();
        assertEquals(1500.0, totalIncome, 0.01, "Total income calculation error");
    }

    /**
     * Tests total expense calculation.
     * Verifies that the sum of all expense transactions is calculated correctly.
     * Expected result: 200 + 150 + 100 = 450
     *
     * @throws IOException if there is an error during calculation
     */
    @Test
    void calculateTotalExpense() throws IOException {
        double totalExpense = transactionDataService.calculateTotalExpense();
        assertEquals(450.0, totalExpense, 0.01, "Total expense calculation error");
    }

    /**
     * Tests net balance calculation.
     * Verifies that the difference between total income and total expense is calculated correctly.
     * Expected result: (1500 - 450) = 1050
     *
     * @throws IOException if there is an error during calculation
     */
    @Test
    void calculateNetBalance() throws IOException {
        double netBalance = transactionDataService.calculateNetBalance();
        assertEquals(1050.0, netBalance, 0.01, "Net balance calculation error");
    }

    /**
     * Tests category-based total amount calculation.
     * Verifies that the sum of transactions for specific categories is calculated correctly.
     * Tests both expense and income categories.
     *
     * @throws IOException if there is an error during calculation
     */
    @Test
    void calculateTotalAmountByCategory() throws IOException {
        // Test total amount for "Groceries" category (200 + 100)
        double groceriesTotal = transactionDataService.calculateTotalAmountByCategory("Groceries");
        assertEquals(300.0, groceriesTotal, 0.01, "Total amount for Groceries category calculation error");

        // Test total amount for "Salary" category (1000)
        double salaryTotal = transactionDataService.calculateTotalAmountByCategory("Salary");
        assertEquals(1000.0, salaryTotal, 0.01, "Total amount for Salary category calculation error");
    }

    /**
     * Tests transaction counting by category.
     * Verifies that the number of transactions for each category is counted correctly.
     *
     * @throws IOException if there is an error during counting
     */
    @Test
    void countTransactionsByCategory() throws IOException {
        Map<String, Integer> categoryCounts = transactionDataService.countTransactionsByCategory();

        assertEquals(2, categoryCounts.get("Groceries"), "Transaction count for Groceries category error");
        assertEquals(1, categoryCounts.get("Entertainment"), "Transaction count for Entertainment category error");
        assertEquals(1, categoryCounts.get("Salary"), "Transaction count for Salary category error");
        assertEquals(1, categoryCounts.get("Freelance"), "Transaction count for Freelance category error");
    }

    /**
     * Tests transaction counting by payment method.
     * Verifies that the number of transactions for each payment method is counted correctly.
     *
     * @throws IOException if there is an error during counting
     */
    @Test
    void countTransactionsByPaymentMethod() throws IOException {
        Map<String, Integer> paymentMethodCounts = transactionDataService.countTransactionsByPaymentMethod();

        assertEquals(1, paymentMethodCounts.get("Bank Transfer"), "Transaction count for Bank Transfer payment method error");
        assertEquals(1, paymentMethodCounts.get("PayPal"), "Transaction count for PayPal payment method error");
        assertEquals(1, paymentMethodCounts.get("Credit Card"), "Transaction count for Credit Card payment method error");
        assertEquals(1, paymentMethodCounts.get("Cash"), "Transaction count for Cash payment method error");
        assertEquals(1, paymentMethodCounts.get("Debit Card"), "Transaction count for Debit Card payment method error");
    }
}
