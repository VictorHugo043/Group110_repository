package com.myfinanceapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the LanguageService.
 * This class contains tests for language management functionality including:
 * - Singleton instance management
 * - Language switching
 * - Translation retrieval
 * - Default language handling
 * - Welcome message translations
 * - Goal-related translations
 *
 * @author SE_Group110
 * @version 4.0
 */
class LanguageServiceTest {
    private LanguageService languageService;

    /**
     * Sets up the test environment before each test.
     * Resets the singleton instance to ensure clean test state.
     * Initializes a new instance of LanguageService.
     */
    @BeforeEach
    void setUp() {
        // Reset singleton instance before each test
        try {
            java.lang.reflect.Field instance = LanguageService.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            fail("Failed to reset singleton instance", e);
        }
        languageService = LanguageService.getInstance();
    }

    /**
     * Tests the singleton pattern implementation.
     * Verifies that:
     * - Multiple getInstance() calls return the same instance
     * - Instance is not null
     */
    @Test
    void testGetInstance() {
        // Test singleton pattern
        LanguageService instance1 = LanguageService.getInstance();
        LanguageService instance2 = LanguageService.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }

    /**
     * Tests the default language setting.
     * Verifies that the default language is set to English.
     */
    @Test
    void testGetCurrentLanguage() {
        // Test default language
        assertEquals("English", languageService.getCurrentLanguage(), "Default language should be English");
    }

    /**
     * Tests language switching functionality.
     * Verifies that:
     * - Valid language can be set
     * - Invalid language is ignored
     * - Current language is correctly maintained
     */
    @Test
    void testSetCurrentLanguage() {
        // Test setting valid language
        languageService.setCurrentLanguage("Chinese");
        assertEquals("Chinese", languageService.getCurrentLanguage(), "Language should be changed to Chinese");

        // Test setting invalid language
        languageService.setCurrentLanguage("InvalidLanguage");
        assertEquals("Chinese", languageService.getCurrentLanguage(), "Language should not change for invalid language");
    }

    /**
     * Tests translation functionality.
     * Verifies that:
     * - English translations are correct
     * - Chinese translations are correct
     * - Non-existent keys return the key itself
     */
    @Test
    void testGetTranslation() {
        // Test English translations
        assertEquals("Languages", languageService.getTranslation("languages"), "Should return English translation");
        assertEquals("Settings", languageService.getTranslation("settings"), "Should return English translation");

        // Switch to Chinese and test Chinese translations
        languageService.setCurrentLanguage("Chinese");
        assertEquals("语言", languageService.getTranslation("languages"), "Should return Chinese translation");
        assertEquals("设置", languageService.getTranslation("settings"), "Should return Chinese translation");

        // Test non-existent translation key
        assertEquals("nonexistent_key", languageService.getTranslation("nonexistent_key"), 
            "Should return the key itself for nonexistent translations");
    }

    /**
     * Tests welcome message translations.
     * Verifies that:
     * - English welcome message is correct
     * - Chinese welcome message is correct
     */
    @Test
    void testWelcomeMessageTranslation() {
        // Test English welcome message
        String englishWelcome = "Welcome to use the financial assistant. Please feel free to ask any financial questions you have.";
        assertEquals(englishWelcome, languageService.getTranslation("welcome_message"), 
            "Should return English welcome message");

        // Switch to Chinese and test Chinese welcome message
        languageService.setCurrentLanguage("Chinese");
        String chineseWelcome = "欢迎使用财务助手。请随时询问任何财务相关的问题。";
        assertEquals(chineseWelcome, languageService.getTranslation("welcome_message"), 
            "Should return Chinese welcome message");
    }

    /**
     * Tests goal-related translations.
     * Verifies that:
     * - English goal-related translations are correct
     * - Chinese goal-related translations are correct
     */
    @Test
    void testGoalRelatedTranslations() {
        // Test English goal-related translations
        assertEquals("Goals", languageService.getTranslation("goals"));
        assertEquals("Create a new goal", languageService.getTranslation("create_new_goal"));
        assertEquals("Target Amount", languageService.getTranslation("target_amount"));

        // Switch to Chinese and test Chinese translations
        languageService.setCurrentLanguage("Chinese");
        assertEquals("目标", languageService.getTranslation("goals"));
        assertEquals("创建新目标", languageService.getTranslation("create_new_goal"));
        assertEquals("目标金额", languageService.getTranslation("target_amount"));
    }
}