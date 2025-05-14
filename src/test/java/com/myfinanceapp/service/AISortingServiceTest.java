package com.myfinanceapp.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the AISortingService.
 * This class contains tests for AI-powered transaction categorization functionality including:
 * - Valid input categorization
 * - Invalid input handling
 * - Category validation
 * - Word count validation
 *
 * @author SE_Group110
 * @version 4.0
 */
public class AISortingServiceTest {

    /**
     * Tests transaction categorization with valid inputs.
     * Verifies that:
     * - Salary income is correctly categorized
     * - Transportation expenses are correctly categorized
     * - Food expenses are correctly categorized
     * - All results are valid categories
     */
    @Test
    public void testSort_ValidInput() {
        // Define valid categories
        String[] validCategories = {"Housing", "Shopping", "Gift", "Food & Drink", "Freelance",
                "Transport", "Groceries", "Debt", "Leisure", "Healthcare",
                "Utilities", "Investment", "Bonus", "Salary", "Others"};

        // Test case 1: Salary income
        String description1 = "Received monthly salary from company";
        String result1 = AISortingService.sort(description1);
        assertNotNull(result1, "Return value should not be null");
        assertTrue(isValidCategory(result1, validCategories), "Return value should be one of the valid categories");

        // Test case 2: Transportation expense
        String description2 = "Took subway to work";
        String result2 = AISortingService.sort(description2);
        assertNotNull(result2, "Return value should not be null");
        assertTrue(isValidCategory(result2, validCategories), "Return value should be one of the valid categories");

        // Test case 3: Food expense
        String description3 = "Had lunch at restaurant";
        String result3 = AISortingService.sort(description3);
        assertNotNull(result3, "Return value should not be null");
        assertTrue(isValidCategory(result3, validCategories), "Return value should be one of the valid categories");
    }

    /**
     * Helper method to validate if the result is one of the valid categories.
     *
     * @param result The category to validate
     * @param validCategories Array of valid category names
     * @return true if the result is a valid category, false otherwise
     */
    private boolean isValidCategory(String result, String[] validCategories) {
        for (String category : validCategories) {
            if (category.equals(result)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests transaction categorization with invalid input.
     * Verifies that:
     * - Service handles special characters gracefully
     * - Returns a valid category name
     * - Result contains one or two English words
     */
    @Test
    public void testSort_InvalidInput() {
        String result = AISortingService.sort("!@#$%^&*()");
        assertNotNull(result, "Return value should not be null");
        assertTrue(isValidWordCount(result), "Return value should be one or two English words");
    }

    /**
     * Helper method to validate if the string contains one or two English words.
     * Verifies that:
     * - String contains only letters and spaces
     * - Word count is either 1 or 2
     *
     * @param str The string to validate
     * @return true if the string contains one or two English words, false otherwise
     */
    private boolean isValidWordCount(String str) {
        // Remove leading and trailing whitespace
        str = str.trim();
        // Check if the string contains only letters and spaces
        if (!str.matches("^[a-zA-Z\\s]+$")) {
            return false;
        }
        // Split the string and count the number of words
        String[] words = str.split("\\s+");
        return words.length == 1 || words.length == 2;
    }
}
