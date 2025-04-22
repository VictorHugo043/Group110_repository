package com.myfinanceapp.service;

public class ThemeService {
    private boolean isDayMode;

    public ThemeService() {
        this.isDayMode = true; // Default to Daytime mode
    }

    public void setTheme(boolean isDay) {
        this.isDayMode = isDay;
    }

    public boolean isDayMode() {
        return isDayMode;
    }

    public String getCurrentThemeStyle() {
        return isDayMode ? "-fx-background-color: white;" : "-fx-background-color: #2E2E2E;";
    }

    public String getCurrentFormBackgroundStyle() {
        return isDayMode ? "-fx-background-color: white;" : "-fx-background-color: #3C3C3C;";
    }

    public String getTextColorStyle() {
        return isDayMode ? "-fx-text-fill: black;" : "-fx-text-fill: white;";
    }

    public String getButtonStyle() {
        if (isDayMode) {
            return "-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;";
        } else {
            return "-fx-background-color: #4A6FA5; -fx-text-fill: white; -fx-font-weight: bold;";
        }
    }

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

    public String getTableHeaderStyle() {
        if (isDayMode) {
            return ".table-view .column-header { -fx-background-color: #F0F0F0; -fx-border-color: #D3D3D3; } " +
                    ".table-view .column-header .label { -fx-text-fill: black; }";
        } else {
            return ".table-view .column-header { -fx-background-color: #4A4A4A; -fx-border-color: #555555; } " +
                    ".table-view .column-header .label { -fx-text-fill: white; }";
        }
    }

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