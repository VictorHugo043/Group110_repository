package com.myfinanceapp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the CurrencyService.
 * This class contains tests for currency-related functionality including:
 * - Currency selection and retrieval
 * - Currency conversion between different currencies
 * - Default currency handling
 * - Null value handling
 *
 * @author SE_Group110
 * @version 4.0
 */
class CurrencyServiceTest {

    /**
     * Tests the getSelectedCurrency method.
     * Verifies that:
     * - Valid initial currency is correctly returned
     * - Null initial currency defaults to USD
     */
    @Test
    void getSelectedCurrency() {
        // Test with valid initial currency
        CurrencyService service = new CurrencyService("EUR");
        assertEquals("EUR", service.getSelectedCurrency());

        // Test with null initial currency (should default to USD)
        service = new CurrencyService(null);
        assertEquals("USD", service.getSelectedCurrency());
    }

    /**
     * Tests the setSelectedCurrency method.
     * Verifies that:
     * - Currency can be changed to a valid value
     * - Null currency is handled correctly
     */
    @Test
    void setSelectedCurrency() {
        CurrencyService service = new CurrencyService("USD");

        // Test setting to valid currency
        service.setSelectedCurrency("CNY");
        assertEquals("CNY", service.getSelectedCurrency());

        // Test setting to null (should set currency to null)
        service.setSelectedCurrency(null);
        assertNull(service.getSelectedCurrency());
    }

    /**
     * Tests the convertCurrency method.
     * Verifies currency conversion for various scenarios:
     * - Same currency conversion (no change)
     * - USD to CNY conversion
     * - USD to EUR conversion
     * - CNY to USD conversion
     * - EUR to USD conversion
     * - CNY to EUR conversion
     * - EUR to CNY conversion
     * All conversions are tested with a precision of 0.001
     */
    @Test
    void convertCurrency() {
        CurrencyService service = new CurrencyService("USD");

        // Test same currency conversion
        assertEquals(100.0, service.convertCurrency(100.0, "USD"), 0.001);

        // Test USD to CNY
        service.setSelectedCurrency("CNY");
        assertEquals(710.0, service.convertCurrency(100.0, "USD"), 0.001);

        // Test USD to EUR
        service.setSelectedCurrency("EUR");
        assertEquals(95.0, service.convertCurrency(100.0, "USD"), 0.001);

        // Test CNY to USD
        service.setSelectedCurrency("USD");
        assertEquals(14.08, service.convertCurrency(100.0, "CNY"), 0.001);

        // Test EUR to USD
        assertEquals(105.26, service.convertCurrency(100.0, "EUR"), 0.001);

        // Test CNY to EUR
        service.setSelectedCurrency("EUR");
        assertEquals(13.376, service.convertCurrency(100.0, "CNY"), 0.001);

        // Test EUR to CNY
        service.setSelectedCurrency("CNY");
        assertEquals(747.346, service.convertCurrency(100.0, "EUR"), 0.001);
    }
}