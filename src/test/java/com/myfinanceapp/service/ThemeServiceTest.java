package com.myfinanceapp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThemeServiceTest {

    @Test
    void setTheme() {
        ThemeService themeService = new ThemeService();
        
        // 默认应该是日间模式
        assertTrue(themeService.isDayMode());
        
        // 设置为夜间模式
        themeService.setTheme(false);
        assertFalse(themeService.isDayMode());
        
        // 再次设置为日间模式
        themeService.setTheme(true);
        assertTrue(themeService.isDayMode());
    }

    @Test
    void isDayMode() {
        ThemeService themeService = new ThemeService();
        // 默认应该是日间模式
        assertTrue(themeService.isDayMode());
        
        // 设置为夜间模式
        themeService.setTheme(false);
        assertFalse(themeService.isDayMode());
    }

    @Test
    void getCurrentThemeStyle() {
        ThemeService themeService = new ThemeService();
        
        // 测试日间模式
        assertEquals("-fx-background-color: white;", themeService.getCurrentThemeStyle());
        
        // 测试夜间模式
        themeService.setTheme(false);
        assertEquals("-fx-background-color: #2E2E2E;", themeService.getCurrentThemeStyle());
    }

    @Test
    void getCurrentFormBackgroundStyle() {
        ThemeService themeService = new ThemeService();
        
        // 测试日间模式
        assertEquals("-fx-background-color: white;", themeService.getCurrentFormBackgroundStyle());
        
        // 测试夜间模式
        themeService.setTheme(false);
        assertEquals("-fx-background-color: #3C3C3C;", themeService.getCurrentFormBackgroundStyle());
    }

    @Test
    void getTextColorStyle() {
        ThemeService themeService = new ThemeService();
        
        // 测试日间模式
        assertEquals("-fx-text-fill: black;", themeService.getTextColorStyle());
        
        // 测试夜间模式
        themeService.setTheme(false);
        assertEquals("-fx-text-fill: white;", themeService.getTextColorStyle());
    }

    @Test
    void getButtonStyle() {
        ThemeService themeService = new ThemeService();
        
        // 测试日间模式
        String expectedDayButtonStyle = "-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;";
        assertEquals(expectedDayButtonStyle, themeService.getButtonStyle());
        
        // 测试夜间模式
        themeService.setTheme(false);
        String expectedNightButtonStyle = "-fx-background-color: #4A6FA5; -fx-text-fill: white; -fx-font-weight: bold;";
        assertEquals(expectedNightButtonStyle, themeService.getButtonStyle());
    }

    @Test
    void getTableStyle() {
        ThemeService themeService = new ThemeService();
        
        // 测试日间模式
        String expectedDayTableStyle = ".table-view { -fx-background-color: white; } " +
                ".table-view .table-cell { -fx-text-fill: black; -fx-background-color: white; -fx-border-color: #D3D3D3; } " +
                ".table-row-cell { -fx-background-color: white; }";
        assertEquals(expectedDayTableStyle, themeService.getTableStyle());
        
        // 测试夜间模式
        themeService.setTheme(false);
        String expectedNightTableStyle = ".table-view { -fx-background-color: #3C3C3C; } " +
                ".table-view .table-cell { -fx-text-fill: white; -fx-background-color: #3C3C3C; -fx-border-color: #555555; } " +
                ".table-row-cell { -fx-background-color: #3C3C3C; }";
        assertEquals(expectedNightTableStyle, themeService.getTableStyle());
    }

    @Test
    void getTableHeaderStyle() {
        ThemeService themeService = new ThemeService();
        
        // 测试日间模式
        String expectedDayHeaderStyle = ".table-view .column-header { -fx-background-color: #F0F0F0; -fx-border-color: #D3D3D3; } " +
                ".table-view .column-header .label { -fx-text-fill: black; }";
        assertEquals(expectedDayHeaderStyle, themeService.getTableHeaderStyle());
        
        // 测试夜间模式
        themeService.setTheme(false);
        String expectedNightHeaderStyle = ".table-view .column-header { -fx-background-color: #4A4A4A; -fx-border-color: #555555; } " +
                ".table-view .column-header .label { -fx-text-fill: white; }";
        assertEquals(expectedNightHeaderStyle, themeService.getTableHeaderStyle());
    }
}
