package com.myfinanceapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Transaction transaction;

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

    @Test
    void getTransactionDate() {
        assertEquals("2023-05-15", transaction.getTransactionDate());
    }

    @Test
    void setTransactionDate() {
        transaction.setTransactionDate("2023-06-20");
        assertEquals("2023-06-20", transaction.getTransactionDate());
    }

    @Test
    void getTransactionType() {
        assertEquals("EXPENSE", transaction.getTransactionType());
    }

    @Test
    void setTransactionType() {
        transaction.setTransactionType("INCOME");
        assertEquals("INCOME", transaction.getTransactionType());
    }

    @Test
    void getCurrency() {
        assertEquals("CNY", transaction.getCurrency());
    }

    @Test
    void setCurrency() {
        transaction.setCurrency("USD");
        assertEquals("USD", transaction.getCurrency());
    }

    @Test
    void getAmount() {
        assertEquals(100.50, transaction.getAmount(), 0.001);
    }

    @Test
    void setAmount() {
        transaction.setAmount(250.75);
        assertEquals(250.75, transaction.getAmount(), 0.001);
    }

    @Test
    void getCategory() {
        assertEquals("Food", transaction.getCategory());
    }

    @Test
    void setCategory() {
        transaction.setCategory("Entertainment");
        assertEquals("Entertainment", transaction.getCategory());
    }

    @Test
    void getPaymentMethod() {
        assertEquals("Cash", transaction.getPaymentMethod());
    }

    @Test
    void setPaymentMethod() {
        transaction.setPaymentMethod("Credit Card");
        assertEquals("Credit Card", transaction.getPaymentMethod());
    }

    @Test
    void getDescription() {
        assertEquals("Test description", transaction.getDescription());
    }

    @Test
    void setDescription() {
        transaction.setDescription("Updated description");
        assertEquals("Updated description", transaction.getDescription());
    }

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