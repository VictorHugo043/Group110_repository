package com.myfinanceapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class LanguageServiceTest {
    private LanguageService languageService;

    @BeforeEach
    void setUp() {
        // 在每个测试前重置单例实例
        try {
            java.lang.reflect.Field instance = LanguageService.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            fail("Failed to reset singleton instance", e);
        }
        languageService = LanguageService.getInstance();
    }

    @Test
    void testGetInstance() {
        // 测试单例模式
        LanguageService instance1 = LanguageService.getInstance();
        LanguageService instance2 = LanguageService.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }

    @Test
    void testGetCurrentLanguage() {
        // 测试默认语言
        assertEquals("English", languageService.getCurrentLanguage(), "Default language should be English");
    }

    @Test
    void testSetCurrentLanguage() {
        // 测试设置有效语言
        languageService.setCurrentLanguage("Chinese");
        assertEquals("Chinese", languageService.getCurrentLanguage(), "Language should be changed to Chinese");

        // 测试设置无效语言
        languageService.setCurrentLanguage("InvalidLanguage");
        assertEquals("Chinese", languageService.getCurrentLanguage(), "Language should not change for invalid language");
    }

    @Test
    void testGetTranslation() {
        // 测试英文翻译
        assertEquals("Languages", languageService.getTranslation("languages"), "Should return English translation");
        assertEquals("Settings", languageService.getTranslation("settings"), "Should return English translation");

        // 切换到中文并测试中文翻译
        languageService.setCurrentLanguage("Chinese");
        assertEquals("语言", languageService.getTranslation("languages"), "Should return Chinese translation");
        assertEquals("设置", languageService.getTranslation("settings"), "Should return Chinese translation");

        // 测试不存在的翻译键
        assertEquals("nonexistent_key", languageService.getTranslation("nonexistent_key"), 
            "Should return the key itself for nonexistent translations");
    }

    @Test
    void testWelcomeMessageTranslation() {
        // 测试欢迎消息的英文翻译
        String englishWelcome = "Welcome to use the financial assistant. Please feel free to ask any financial questions you have.";
        assertEquals(englishWelcome, languageService.getTranslation("welcome_message"), 
            "Should return English welcome message");

        // 切换到中文并测试中文欢迎消息
        languageService.setCurrentLanguage("Chinese");
        String chineseWelcome = "欢迎使用财务助手。请随时询问任何财务相关的问题。";
        assertEquals(chineseWelcome, languageService.getTranslation("welcome_message"), 
            "Should return Chinese welcome message");
    }

    @Test
    void testGoalRelatedTranslations() {
        // 测试目标相关英文翻译
        assertEquals("Goals", languageService.getTranslation("goals"));
        assertEquals("Create a new goal", languageService.getTranslation("create_new_goal"));
        assertEquals("Target Amount", languageService.getTranslation("target_amount"));

        // 切换到中文并测试中文翻译
        languageService.setCurrentLanguage("Chinese");
        assertEquals("目标", languageService.getTranslation("goals"));
        assertEquals("创建新目标", languageService.getTranslation("create_new_goal"));
        assertEquals("目标金额", languageService.getTranslation("target_amount"));
    }
}