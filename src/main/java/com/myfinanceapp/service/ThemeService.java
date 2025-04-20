package com.myfinanceapp.service;

public class ThemeService {
    private boolean isDayMode;

    public ThemeService() {
        this.isDayMode = true; // Default to Daytime mode
    }

    /**
     * Sets the theme based on the day mode flag.
     * @param isDay true for Daytime mode, false for Nighttime mode
     */
    public void setTheme(boolean isDay) {
        this.isDayMode = isDay;
    }

    /**
     * Returns whether the current theme is Daytime mode.
     * @return true if Daytime mode, false if Nighttime mode
     */
    public boolean isDayMode() {
        return isDayMode;
    }

    /**
     * Gets the current theme style for the root pane.
     * @return CSS style string for the root pane
     */
    public String getCurrentThemeStyle() {
        return isDayMode ? "-fx-background-color: white;" : "-fx-background-color: #2E2E2E;";
    }

    /**
     * Gets the current background style for the settings form.
     * @return CSS style string for the form background
     */
    public String getCurrentFormBackgroundStyle() {
        return isDayMode ? "-fx-background-color: white;" : "-fx-background-color: #3C3C3C;";
    }

    /**
     * Gets the text color style based on the current theme.
     * @return CSS style string for text color
     */
    public String getTextColorStyle() {
        return isDayMode ? "-fx-text-fill: black;" : "-fx-text-fill: white;";
    }

    /**
     * Gets the button style based on the current theme.
     * @return CSS style string for buttons
     */
    public String getButtonStyle() {
        if (isDayMode) {
            return "-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;";
        } else {
            return "-fx-background-color: #4A6FA5; -fx-text-fill: white; -fx-font-weight: bold;";
        }
    }
}