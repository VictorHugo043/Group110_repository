package com.myfinanceapp.service;

/**
 * Service class for managing the application's theme and styling.
 * This service provides functionality for:
 * - Switching between day and night modes
 * - Managing consistent styling across the application
 * - Providing theme-specific styles for various UI components
 * - Handling color schemes and visual elements
 * 
 * The service supports two themes:
 * - Day mode: Light theme with white backgrounds and dark text
 * - Night mode: Dark theme with dark backgrounds and light text
 */
public class ThemeService {
    private boolean isDayMode;

    /**
     * Constructs a new ThemeService instance.
     * Initializes with day mode as the default theme.
     */
    public ThemeService() {
        this.isDayMode = true; // Default to Daytime mode
    }

    /**
     * Sets the application theme mode.
     *
     * @param isDay true for day mode, false for night mode
     */
    public void setTheme(boolean isDay) {
        this.isDayMode = isDay;
    }

    /**
     * Checks if the application is currently in day mode.
     *
     * @return true if in day mode, false if in night mode
     */
    public boolean isDayMode() {
        return isDayMode;
    }

    /**
     * Gets the background style for the main application window.
     *
     * @return CSS style string for the main background
     */
    public String getCurrentThemeStyle() {
        return isDayMode ? "-fx-background-color: white;" : "-fx-background-color: #2E2E2E;";
    }

    /**
     * Gets the background style for form elements.
     *
     * @return CSS style string for form backgrounds
     */
    public String getCurrentFormBackgroundStyle() {
        return isDayMode ? "-fx-background-color: white;" : "-fx-background-color: #3C3C3C;";
    }

    /**
     * Gets the text color style for the current theme.
     *
     * @return CSS style string for text color
     */
    public String getTextColorStyle() {
        return isDayMode ? "-fx-text-fill: black;" : "-fx-text-fill: white;";
    }

    /**
     * Gets the button style for the current theme.
     * Includes background color, text color, and font weight.
     *
     * @return CSS style string for buttons
     */
    public String getButtonStyle() {
        if (isDayMode) {
            return "-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;";
        } else {
            return "-fx-background-color: #4A6FA5; -fx-text-fill: white; -fx-font-weight: bold;";
        }
    }

    /**
     * Gets the table style for the current theme.
     * Includes styles for the table view, cells, and rows.
     *
     * @return CSS style string for tables
     */
    public String getTableStyle() {
        if (isDayMode) {
            return ".table-view { -fx-background-color: white; } " +
                    ".table-view .table-cell { -fx-text-fill: black; -fx-background-color: white; -fx-border-color: #D3D3D3; } " +
                    ".table-row-cell { -fx-background-color: white; }";
        } else {
            return ".table-view { -fx-background-color: #3C3C3C; } " +
                    ".table-view .table-cell { -fx-text-fill: white; -fx-background-color: #3C3C3C; -fx-border-color: #555555; } " +
                    ".table-row-cell { -fx-background-color: #3C3C3C; }";
        }
    }

    /**
     * Gets the table header style for the current theme.
     * Includes styles for column headers and their labels.
     *
     * @return CSS style string for table headers
     */
    public String getTableHeaderStyle() {
        if (isDayMode) {
            return ".table-view .column-header { -fx-background-color: #F0F0F0; -fx-border-color: #D3D3D3; } " +
                    ".table-view .column-header .label { -fx-text-fill: black; }";
        } else {
            return ".table-view .column-header { -fx-background-color: #4A4A4A; -fx-border-color: #555555; } " +
                    ".table-view .column-header .label { -fx-text-fill: white; }";
        }
    }

    /**
     * Gets the complete theme stylesheet for the current theme.
     * Includes styles for:
     * - Combo boxes
     * - Date pickers
     * - Popup menus
     * - Calendar components
     * - Hover and selection states
     *
     * @return Complete CSS stylesheet string for the current theme
     */
    public String getThemeStylesheet() {
        if (isDayMode) {
            return ".day-theme-combo-box { " +
                    "-fx-background-color: white; " +
                    "-fx-text-fill: black; " +
                    "-fx-control-inner-background: white; " +
                    "-fx-border-color: #D3D3D3; " +
                    "-fx-border-radius: 3; " +
                    "} " +
                    ".day-theme-combo-box .list-cell { " +
                    "-fx-text-fill: black; " +
                    "-fx-background-color: white; " +
                    "} " +
                    ".day-theme-combo-box .combo-box-popup .list-view { " +
                    "-fx-background-color: white; " +
                    "} " +
                    ".day-theme-combo-box .combo-box-popup .list-view .list-cell { " +
                    "-fx-text-fill: black; " +
                    "-fx-background-color: white; " +
                    "} " +
                    ".day-theme-combo-box .combo-box-popup .list-view .list-cell:hover { " +
                    "-fx-background-color: #E0F0FF; " +
                    "} " +
                    ".day-theme-date-picker { " +
                    "-fx-background-color: white; " +
                    "-fx-text-fill: black; " +
                    "-fx-control-inner-background: white; " +
                    "-fx-border-color: #D3D3D3; " +
                    "-fx-border-radius: 3; " +
                    "} " +
                    ".day-theme-date-picker .text-field { " +
                    "-fx-text-fill: black; " +
                    "-fx-background-color: white; " +
                    "-fx-prompt-text-fill: #555555; " +
                    "} " +
                    ".day-theme-date-picker .date-picker-popup { " +
                    "-fx-background-color: white; " +
                    "-fx-border-color: #D3D3D3; " +
                    "} " +
                    ".day-theme-date-picker .date-picker-popup .month-year-pane { " +
                    "-fx-background-color: #F0F0F0; " +
                    "-fx-text-fill: black; " +
                    "} " +
                    ".day-theme-date-picker .date-picker-popup .day-name-cell { " +
                    "-fx-text-fill: black; " +
                    "-fx-background-color: white; " +
                    "} " +
                    ".day-theme-date-picker .date-picker-popup .calendar-grid { " +
                    "-fx-background-color: white; " +
                    "} " +
                    ".day-theme-date-picker .date-picker-popup .day-cell { " +
                    "-fx-text-fill: black; " +
                    "-fx-background-color: white; " +
                    "} " +
                    ".day-theme-date-picker .date-picker-popup .day-cell:hover { " +
                    "-fx-background-color: #E0F0FF; " +
                    "} " +
                    ".day-theme-date-picker .date-picker-popup .day-cell:selected { " +
                    "-fx-background-color: #3282FA; " +
                    "-fx-text-fill: white; " +
                    "}";
        } else {
            return ".night-theme-combo-box { " +
                    "-fx-background-color: #3C3C3C; " +
                    "-fx-text-fill: white; " +
                    "-fx-control-inner-background: #3C3C3C; " +
                    "-fx-border-color: #555555; " +
                    "-fx-border-radius: 3; " +
                    "} " +
                    ".night-theme-combo-box .list-cell { " +
                    "-fx-text-fill: white; " +
                    "-fx-background-color: #3C3C3C; " +
                    "} " +
                    ".night-theme-combo-box .combo-box-popup .list-view { " +
                    "-fx-background-color: #3C3C3C; " +
                    "} " +
                    ".night-theme-combo-box .combo-box-popup .list-view .list-cell { " +
                    "-fx-text-fill: white; " +
                    "-fx-background-color: #3C3C3C; " +
                    "} " +
                    ".night-theme-combo-box .combo-box-popup .list-view .list-cell:hover { " +
                    "-fx-background-color: #4A6FA5; " +
                    "} " +
                    ".night-theme-date-picker { " +
                    "-fx-background-color: #3C3C3C; " +
                    "-fx-text-fill: white; " +
                    "-fx-control-inner-background: #3C3C3C; " +
                    "-fx-border-color: #555555; " +
                    "-fx-border-radius: 3; " +
                    "} " +
                    ".night-theme-date-picker .text-field { " +
                    "-fx-text-fill: white; " +
                    "-fx-background-color: #3C3C3C; " +
                    "-fx-prompt-text-fill: #CCCCCC; " +
                    "} " +
                    ".night-theme-date-picker .date-picker-popup { " +
                    "-fx-background-color: #3C3C3C; " +
                    "-fx-border-color: #555555; " +
                    "} " +
                    ".night-theme-date-picker .date-picker-popup .month-year-pane { " +
                    "-fx-background-color: #4A4A4A; " +
                    "-fx-text-fill: white; " +
                    "} " +
                    ".night-theme-date-picker .date-picker-popup .day-name-cell { " +
                    "-fx-text-fill: white; " +
                    "-fx-background-color: #3C3C3C; " +
                    "} " +
                    ".night-theme-date-picker .date-picker-popup .calendar-grid { " +
                    "-fx-background-color: #3C3C3C; " +
                    "} " +
                    ".night-theme-date-picker .date-picker-popup .day-cell { " +
                    "-fx-text-fill: white; " +
                    "-fx-background-color: #3C3C3C; " +
                    "} " +
                    ".night-theme-date-picker .date-picker-popup .day-cell:hover { " +
                    "-fx-background-color: #4A6FA5; " +
                    "} " +
                    ".night-theme-date-picker .date-picker-popup .day-cell:selected { " +
                    "-fx-background-color: #4A6FA5; " +
                    "-fx-text-fill: white; " +
                    "}";
        }
    }
}