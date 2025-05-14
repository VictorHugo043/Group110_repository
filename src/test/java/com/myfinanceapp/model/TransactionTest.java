package com.myfinanceapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Transaction model.
 * This class contains unit tests for all Transaction class functionality including:
 * - Getters and setters for all properties
 * - Transaction equality comparison
 * - Property validation
 * - Default values handling
 *
 * @author SE_Group110
 * @version 4.0
 */
class TransactionTest {

    private Transaction transaction;

    /**
     * Sets up a new Transaction instance before each test.
     * Initializes the transaction with test data for all properties.
     */
    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setTransactionDate("2023-05-15");
        transaction.setTransactionType("EXPENSE");
        transaction.setCurrency("CNY");
        transaction.setAmount(100.50);
        transaction.setCategory("Food");
        transaction.setPaymentMethod("Cash");
        transaction.setDescription("Test description");
    }

    /**
     * Tests the getTransactionDate method.
     * Verifies that the transaction date is correctly returned.
     */
    @Test
    void getTransactionDate() {
        assertEquals("2023-05-15", transaction.getTransactionDate());
    }

    /**
     * Tests the setTransactionDate method.
     * Verifies that the transaction date can be updated.
     */
    @Test
    void setTransactionDate() {
        transaction.setTransactionDate("2023-06-20");
        assertEquals("2023-06-20", transaction.getTransactionDate());
    }

    /**
     * Tests the getTransactionType method.
     * Verifies that the transaction type is correctly returned.
     */
    @Test
    void getTransactionType() {
        assertEquals("EXPENSE", transaction.getTransactionType());
    }

    /**
     * Tests the setTransactionType method.
     * Verifies that the transaction type can be updated.
     */
    @Test
    void setTransactionType() {
        transaction.setTransactionType("INCOME");
        assertEquals("INCOME", transaction.getTransactionType());
    }

    /**
     * Tests the getCurrency method.
     * Verifies that the currency is correctly returned.
     */
    @Test
    void getCurrency() {
        assertEquals("CNY", transaction.getCurrency());
    }

    /**
     * Tests the setCurrency method.
     * Verifies that the currency can be updated.
     */
    @Test
    void setCurrency() {
        transaction.setCurrency("USD");
        assertEquals("USD", transaction.getCurrency());
    }

    /**
     * Tests the getAmount method.
     * Verifies that the amount is correctly returned with proper decimal precision.
     */
    @Test
    void getAmount() {
        assertEquals(100.50, transaction.getAmount(), 0.001);
    }

    /**
     * Tests the setAmount method.
     * Verifies that the amount can be updated with proper decimal precision.
     */
    @Test
    void setAmount() {
        transaction.setAmount(250.75);
        assertEquals(250.75, transaction.getAmount(), 0.001);
    }

    /**
     * Tests the getCategory method.
     * Verifies that the category is correctly returned.
     */
    @Test
    void getCategory() {
        assertEquals("Food", transaction.getCategory());
    }

    /**
     * Tests the setCategory method.
     * Verifies that the category can be updated.
     */
    @Test
    void setCategory() {
        transaction.setCategory("Entertainment");
        assertEquals("Entertainment", transaction.getCategory());
    }

    /**
     * Tests the getPaymentMethod method.
     * Verifies that the payment method is correctly returned.
     */
    @Test
    void getPaymentMethod() {
        assertEquals("Cash", transaction.getPaymentMethod());
    }

    /**
     * Tests the setPaymentMethod method.
     * Verifies that the payment method can be updated.
     */
    @Test
    void setPaymentMethod() {
        transaction.setPaymentMethod("Credit Card");
        assertEquals("Credit Card", transaction.getPaymentMethod());
    }

    /**
     * Tests the getDescription method.
     * Verifies that the description is correctly returned.
     */
    @Test
    void getDescription() {
        assertEquals("Test description", transaction.getDescription());
    }

    /**
     * Tests the setDescription method.
     * Verifies that the description can be updated.
     */
    @Test
    void setDescription() {
        transaction.setDescription("Updated description");
        assertEquals("Updated description", transaction.getDescription());
    }

    /**
     * Tests the equals method.
     * Verifies transaction equality comparison for various scenarios:
     * - Identical transactions
     * - Self-comparison
     * - Null comparison
     * - Different type comparison
     * - Different field values comparison:
     *   - Different transaction type
     *   - Different date
     *   - Different amount
     *   - Different currency
     *   - Different category
     *   - Different payment method
     *   - Different description
     */
    @Test
    void testEquals() {
        // Create an identical transaction object
        Transaction identical = new Transaction();
        identical.setTransactionDate("2023-05-15");
        identical.setTransactionType("EXPENSE");
        identical.setCurrency("CNY");
        identical.setAmount(100.50);
        identical.setCategory("Food");
        identical.setPaymentMethod("Cash");
        identical.setDescription("Test description");

        // Test equality of identical objects
        assertTrue(transaction.equals(identical));
        assertTrue(identical.equals(transaction));

        // Test equality with self
        assertTrue(transaction.equals(transaction));

        // Test inequality with null
        assertFalse(transaction.equals(null));

        // Test inequality with different type
        assertFalse(transaction.equals("Not a transaction"));

        // Test inequality with different fields
        Transaction different = new Transaction();
        different.setTransactionDate("2023-05-15");
        different.setTransactionType("INCOME"); // Different type
        different.setCurrency("CNY");
        different.setAmount(100.50);
        different.setCategory("Food");
        different.setPaymentMethod("Cash");
        different.setDescription("Test description");

        assertFalse(transaction.equals(different));

        // Test with different date
        different = new Transaction();
        different.setTransactionDate("2023-05-16"); // Different date
        different.setTransactionType("EXPENSE");
        different.setCurrency("CNY");
        different.setAmount(100.50);
        different.setCategory("Food");
        different.setPaymentMethod("Cash");
        different.setDescription("Test description");

        assertFalse(transaction.equals(different));

        // Test with different amount
        different = new Transaction();
        different.setTransactionDate("2023-05-15");
        different.setTransactionType("EXPENSE");
        different.setCurrency("CNY");
        different.setAmount(200.00); // Different amount
        different.setCategory("Food");
        different.setPaymentMethod("Cash");
        different.setDescription("Test description");

        assertFalse(transaction.equals(different));

        // Test with different currency
        different = new Transaction();
        different.setTransactionDate("2023-05-15");
        different.setTransactionType("EXPENSE");
        different.setCurrency("USD"); // Different currency
        different.setAmount(100.50);
        different.setCategory("Food");
        different.setPaymentMethod("Cash");
        different.setDescription("Test description");

        assertFalse(transaction.equals(different));

        // Test with different category
        different = new Transaction();
        different.setTransactionDate("2023-05-15");
        different.setTransactionType("EXPENSE");
        different.setCurrency("CNY");
        different.setAmount(100.50);
        different.setCategory("Transport"); // Different category
        different.setPaymentMethod("Cash");
        different.setDescription("Test description");

        assertFalse(transaction.equals(different));

        // Test with different payment method
        different = new Transaction();
        different.setTransactionDate("2023-05-15");
        different.setTransactionType("EXPENSE");
        different.setCurrency("CNY");
        different.setAmount(100.50);
        different.setCategory("Food");
        different.setPaymentMethod("Credit Card"); // Different payment method
        different.setDescription("Test description");

        assertFalse(transaction.equals(different));

        // Test with different description
        different = new Transaction();
        different.setTransactionDate("2023-05-15");
        different.setTransactionType("EXPENSE");
        different.setCurrency("CNY");
        different.setAmount(100.50);
        different.setCategory("Food");
        different.setPaymentMethod("Cash");
        different.setDescription("Different description"); // Different description

        assertFalse(transaction.equals(different));
    }
}