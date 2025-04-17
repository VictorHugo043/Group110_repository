package com.myfinanceapp.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AISortingServiceTest {

    @Test
    public void testSort_ValidInput() {
        // Test case 1: Salary income
        String description1 = "Received monthly salary from company";
        String result1 = AISortingService.sort(description1);
        assertNotNull(result1, "Return value should not be null");
        assertTrue(isValidWordCount(result1), "Return value should be one or two English words");

        // Test case 2: Transportation expense
        String description2 = "Took subway to work";
        String result2 = AISortingService.sort(description2);
        assertNotNull(result2, "Return value should not be null");
        assertTrue(isValidWordCount(result2), "Return value should be one or two English words");

        // Test case 3: Food expense
        String description3 = "Had lunch at restaurant";
        String result3 = AISortingService.sort(description3);
        assertNotNull(result3, "Return value should not be null");
        assertTrue(isValidWordCount(result3), "Return value should be one or two English words");
    }

    @Test
    public void testSort_InvalidInput() {
        String result = AISortingService.sort("!@#$%^&*()");
        assertNotNull(result, "Return value should not be null");
        assertTrue(isValidWordCount(result), "Return value should be one or two English words");
    }

    // Helper method: verify if the string contains one or two English words
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
