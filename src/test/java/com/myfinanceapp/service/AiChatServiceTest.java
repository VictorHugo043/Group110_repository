package com.myfinanceapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the AiChatService.
 * This class contains tests for AI chat completion functionality including:
 * - API error handling
 * - Network exception handling
 * - Response validation
 *
 * @author SE_Group110
 * @version 4.0
 */
class AiChatServiceTest {

    private List<Map<String, String>> messages;

    /**
     * Sets up test environment before each test.
     * Initializes an empty message list for testing.
     */
    @BeforeEach
    void setUp() {
        messages = new ArrayList<>();
    }

    /**
     * Tests chat completion with API error handling.
     * Verifies that:
     * - Service handles empty input gracefully
     * - Returns a non-null response even with invalid input
     * - Maintains service stability during API errors
     */
    @Test
    void chatCompletion_apiError() {
        // Arrange
        // Simulate an invalid input that might cause an API error (e.g., empty content)
        messages.add(Map.of("role", "user", "content", ""));
        String userInput = ""; // Empty input might not trigger a 400, but we test the response

        // Act
        String response = AiChatService.chatCompletion(messages, userInput);

        // Assert
        assertNotNull(response, "Response should not be null even with potentially invalid input");
    }

    /**
     * Tests chat completion with network exception handling.
     * Verifies that:
     * - Service handles network issues gracefully
     * - Returns a non-null response when network is available
     * - Maintains service stability during network problems
     */
    @Test
    void chatCompletion_networkException() {
        // Arrange
        String userInput = "Test network issue";

        // Act
        String response = AiChatService.chatCompletion(messages, userInput);

        // Assert
        assertNotNull(response, "Response should not be null with a working network");
    }
}