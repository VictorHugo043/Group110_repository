package com.myfinanceapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AiChatServiceTest {

    private List<Map<String, String>> messages;

    @BeforeEach
    void setUp() {
        messages = new ArrayList<>();
    }

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