package com.myfinanceapp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the ThemeService.
 * This class contains tests for theme management functionality including:
 * - Theme switching between day and night modes
 * - Background color styles
 * - Text color styles
 * - Button styles
 * - Table styles
 * - Form background styles
 *
 * @author SE_Group110
 * @version 4.0
 */
class ThemeServiceTest {

    /**
     * Tests theme switching functionality.
     * Verifies that:
     * - Default theme is day mode
     * - Theme can be switched to night mode
     * - Theme can be switched back to day mode
     */
    @Test
    void setTheme() {
        ThemeService themeService = new ThemeService();
        
        // Default should be day mode
        assertTrue(themeService.isDayMode());
        
        // Switch to night mode
        themeService.setTheme(false);
        assertFalse(themeService.isDayMode());
        
        // Switch back to day mode
        themeService.setTheme(true);
        assertTrue(themeService.isDayMode());
    }

    /**
     * Tests day mode status checking.
     * Verifies that:
     * - Default mode is day mode
     * - Mode status changes correctly when theme is switched
     */
    @Test
    void isDayMode() {
        ThemeService themeService = new ThemeService();
        // Default should be day mode
        assertTrue(themeService.isDayMode());
        
        // Switch to night mode
        themeService.setTheme(false);
        assertFalse(themeService.isDayMode());
    }

    /**
     * Tests current theme style retrieval.
     * Verifies that:
     * - Day mode returns white background
     * - Night mode returns dark background
     */
    @Test
    void getCurrentThemeStyle() {
        ThemeService themeService = new ThemeService();
        
        // Test day mode
        assertEquals("-fx-background-color: white;", themeService.getCurrentThemeStyle());
        
        // Test night mode
        themeService.setTheme(false);
        assertEquals("-fx-background-color: #2E2E2E;", themeService.getCurrentThemeStyle());
    }

    /**
     * Tests form background style retrieval.
     * Verifies that:
     * - Day mode returns white background
     * - Night mode returns dark background
     */
    @Test
    void getCurrentFormBackgroundStyle() {
        ThemeService themeService = new ThemeService();
        
        // Test day mode
        assertEquals("-fx-background-color: white;", themeService.getCurrentFormBackgroundStyle());
        
        // Test night mode
        themeService.setTheme(false);
        assertEquals("-fx-background-color: #3C3C3C;", themeService.getCurrentFormBackgroundStyle());
    }

    /**
     * Tests text color style retrieval.
     * Verifies that:
     * - Day mode returns black text
     * - Night mode returns white text
     */
    @Test
    void getTextColorStyle() {
        ThemeService themeService = new ThemeService();
        
        // Test day mode
        assertEquals("-fx-text-fill: black;", themeService.getTextColorStyle());
        
        // Test night mode
        themeService.setTheme(false);
        assertEquals("-fx-text-fill: white;", themeService.getTextColorStyle());
    }

    /**
     * Tests button style retrieval.
     * Verifies that:
     * - Day mode returns light blue button style
     * - Night mode returns dark blue button style
     */
    @Test
    void getButtonStyle() {
        ThemeService themeService = new ThemeService();
        
        // Test day mode
        String expectedDayButtonStyle = "-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;";
        assertEquals(expectedDayButtonStyle, themeService.getButtonStyle());
        
        // Test night mode
        themeService.setTheme(false);
        String expectedNightButtonStyle = "-fx-background-color: #4A6FA5; -fx-text-fill: white; -fx-font-weight: bold;";
        assertEquals(expectedNightButtonStyle, themeService.getButtonStyle());
    }

    /**
     * Tests table style retrieval.
     * Verifies that:
     * - Day mode returns light table style
     * - Night mode returns dark table style
     * - All table components (view, cells, rows) are properly styled
     */
    @Test
    void getTableStyle() {
        ThemeService themeService = new ThemeService();
        
        // Test day mode
        String expectedDayTableStyle = ".table-view { -fx-background-color: white; } " +
                ".table-view .table-cell { -fx-text-fill: black; -fx-background-color: white; -fx-border-color: #D3D3D3; } " +
                ".table-row-cell { -fx-background-color: white; }";
        assertEquals(expectedDayTableStyle, themeService.getTableStyle());
        
        // Test night mode
        themeService.setTheme(false);
        String expectedNightTableStyle = ".table-view { -fx-background-color: #3C3C3C; } " +
                ".table-view .table-cell { -fx-text-fill: white; -fx-background-color: #3C3C3C; -fx-border-color: #555555; } " +
                ".table-row-cell { -fx-background-color: #3C3C3C; }";
        assertEquals(expectedNightTableStyle, themeService.getTableStyle());
    }

    /**
     * Tests table header style retrieval.
     * Verifies that:
     * - Day mode returns light header style
     * - Night mode returns dark header style
     * - Header background and text colors are properly set
     */
    @Test
    void getTableHeaderStyle() {
        ThemeService themeService = new ThemeService();
        
        // Test day mode
        String expectedDayHeaderStyle = ".table-view .column-header { -fx-background-color: #F0F0F0; -fx-border-color: #D3D3D3; } " +
                ".table-view .column-header .label { -fx-text-fill: black; }";
        assertEquals(expectedDayHeaderStyle, themeService.getTableHeaderStyle());
        
        // Test night mode
        themeService.setTheme(false);
        String expectedNightHeaderStyle = ".table-view .column-header { -fx-background-color: #4A4A4A; -fx-border-color: #555555; } " +
                ".table-view .column-header .label { -fx-text-fill: white; }";
        assertEquals(expectedNightHeaderStyle, themeService.getTableHeaderStyle());
    }
}
