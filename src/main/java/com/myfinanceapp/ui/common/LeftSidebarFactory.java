package com.myfinanceapp.ui.common;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.loginscene.LoginScene;
import com.myfinanceapp.ui.settingscene.SystemSettings;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.ui.goalsscene.Goals;
import com.myfinanceapp.ui.transactionscene.TransactionScene;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.myfinanceapp.ui.common.SceneManager;
import javafx.scene.Scene;

import java.util.Objects;

/**
 * Factory class for creating and managing the application's left sidebar navigation.
 * This class provides functionality for:
 * - Creating a consistent sidebar layout across different scenes
 * - Managing navigation between different sections of the application
 * - Handling theme-specific styling of sidebar elements
 * - Supporting multilingual interface through LanguageService
 *
 * @author SE_Group110
 * @version 4.0
 */
public class LeftSidebarFactory {
    private static final LanguageService languageService = LanguageService.getInstance();

    /**
     * Creates a left sidebar with the specified selected button highlighted.
     * This is an overloaded method that provides default ThemeService and CurrencyService instances.
     *
     * @param stage The main application stage
     * @param selectedButton The currently selected navigation button
     * @param loggedUser The currently logged-in user
     * @return A VBox containing the sidebar layout
     */
    public static VBox createLeftSidebar(Stage stage, String selectedButton, User loggedUser) {
        return createLeftSidebar(stage, selectedButton, loggedUser, new ThemeService(), new CurrencyService("CNY"));
    }

    /**
     * Creates a left sidebar with the specified selected button highlighted.
     * Includes theme and currency service parameters for consistent styling and currency handling.
     *
     * @param stage The main application stage
     * @param selectedButton The currently selected navigation button
     * @param loggedUser The currently logged-in user
     * @param themeService The service for managing application theme
     * @param currencyService The service for handling currency conversions
     * @return A VBox containing the sidebar layout
     */
    public static VBox createLeftSidebar(Stage stage, String selectedButton, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        VBox sideBar = new VBox(15);
        sideBar.setPadding(new Insets(20, 0, 20, 15));
        sideBar.setAlignment(Pos.TOP_LEFT);
        sideBar.setPrefWidth(170);

        // Draw a 2px blue vertical line on the right side of sideBar
        sideBar.setStyle(
                themeService.getCurrentThemeStyle() +
                        "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 0 2 0 0;"
        );

        // Set top text based on selectedButton
        String labelText = getWelcomeMessage(selectedButton);
        Label welcomeLabel = new Label(labelText);
        welcomeLabel.setFont(new Font(18));
        welcomeLabel.setStyle(themeService.getTextColorStyle());

        // Create five buttons and determine which one is selected
        HBox statusBox   = createSidebarButtonBox(stage, "status",   "status_icon_default.png",   "status_icon_selected.png",   selectedButton.equals("Status"), loggedUser, themeService, currencyService);
        HBox goalsBox    = createSidebarButtonBox(stage, "goals",    "goals_icon_default.png",    "goals_icon_selected.png",    selectedButton.equals("Goals"), loggedUser, themeService, currencyService);
        HBox settingsBox = createSidebarButtonBox(stage, "settings", "settings_icon_default.png", "settings_icon_selected.png", selectedButton.equals("Settings"), loggedUser, themeService, currencyService);
        HBox newBox      = createSidebarButtonBox(stage, "new",      "new_icon_default.png",      "new_icon_selected.png",      selectedButton.equals("New"), loggedUser, themeService, currencyService);
        HBox logoutBox   = createSidebarButtonBox(stage, "logout",   "logout_icon_default.png",   "logout_icon_selected.png",   selectedButton.equals("Log out"), loggedUser, themeService, currencyService);

        sideBar.getChildren().addAll(
                welcomeLabel,
                statusBox,
                goalsBox,
                newBox,
                settingsBox,
                logoutBox
        );
        return sideBar;
    }

    /**
     * Gets the welcome message based on the currently selected section.
     *
     * @param selectedButton The currently selected navigation button
     * @return The localized welcome message for the selected section
     */
    private static String getWelcomeMessage(String selectedButton) {
        String messageKey;
        switch (selectedButton) {
            case "Status":
                messageKey = "welcome_message_status";
                break;
            case "Goals":
                messageKey = "welcome_message_goals";
                break;
            case "New":
                messageKey = "welcome_message_new";
                break;
            case "Settings":
                messageKey = "welcome_message_settings";
                break;
            default:
                messageKey = "welcome_message_default";
                break;
        }
        return languageService.getTranslation(messageKey);
    }

    /**
     * Creates a navigation button box with appropriate styling and click handling.
     * The button's appearance changes based on whether it is the currently selected section.
     *
     * @param stage The main application stage
     * @param translationKey The key for button text translation
     * @param defaultIcon The icon to show when button is not selected
     * @param selectedIcon The icon to show when button is selected
     * @param isActive Whether this button represents the currently selected section
     * @param loggedUser The currently logged-in user
     * @param themeService The service for managing application theme
     * @param currencyService The service for handling currency conversions
     * @return An HBox containing the styled navigation button
     */
    private static HBox createSidebarButtonBox(Stage stage, String translationKey, String defaultIcon, String selectedIcon, boolean isActive, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        Label label = new Label(languageService.getTranslation(translationKey));
        label.setFont(new Font(14));
        label.setPrefSize(isActive ? 172 : 170, 40); // Add 2px when selected
        label.setAlignment(Pos.CENTER_LEFT);
        label.setPadding(new Insets(0, 10, 0, 20));

        String iconFile = isActive ? selectedIcon : defaultIcon;
        var url = Objects.requireNonNull(
                LeftSidebarFactory.class.getResource("/pictures/" + iconFile),
                "Resource /pictures/" + iconFile + " not found!"
        );
        ImageView iconView = new ImageView(new Image(url.toExternalForm()));
        iconView.setFitWidth(18);
        iconView.setFitHeight(18);
        label.setGraphic(iconView);
        label.setGraphicTextGap(10);

        if (isActive) {
            // Selected style
            label.setStyle(
                    themeService.getCurrentThemeStyle() +
                            "-fx-text-fill: #3282FA; " +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                            "-fx-border-width: 2 0 2 2; " +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
        } else {
            // Unselected style
            label.setStyle(
                    "-fx-background-color: " + (themeService.isDayMode() ? "#E0F0FF" : "#4A6FA5") + "; " +
                            themeService.getTextColorStyle() +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                            "-fx-border-width: 2 0 2 2; " +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
        }

        // Click event, different navigation based on button text
        label.setOnMouseClicked(e -> {
            // Get current window size
            double currentWidth = stage.getScene().getWidth();
            double currentHeight = stage.getScene().getHeight();

            switch (translationKey) {
                case "status":
                    // Navigate to Status
                    StatusScene statusScene = new StatusScene(stage, currentWidth, currentHeight, loggedUser);
                    Scene newStatusScene = statusScene.createScene(themeService, currencyService);
                    SceneManager.switchScene(stage, newStatusScene);
                    StatusService statusService = new StatusService(statusScene, loggedUser, currencyService, languageService);
                    stage.setTitle("Finanger - " + languageService.getTranslation("status"));
                    break;
                case "goals":
                    Scene goalsScene = Goals.createScene(stage, currentWidth, currentHeight, loggedUser, themeService, currencyService);
                    SceneManager.switchScene(stage, goalsScene);
                    stage.setTitle("Finanger - " + languageService.getTranslation("goals"));
                    break;
                case "new":
                    Scene transactionScene = TransactionScene.createScene(stage, currentWidth, currentHeight, loggedUser, themeService, currencyService);
                    SceneManager.switchScene(stage, transactionScene);
                    stage.setTitle("Finanger - " + languageService.getTranslation("new"));
                    break;
                case "settings":
                    Scene settingsScene = SystemSettings.createScene(stage, currentWidth, currentHeight, loggedUser, themeService, currencyService);
                    SceneManager.switchScene(stage, settingsScene);
                    stage.setTitle("Finanger - " + languageService.getTranslation("system_settings"));
                    break;
                case "logout":
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, languageService.getTranslation("logout_confirmation"));
                    confirm.setHeaderText(null);
                    confirm.setTitle(languageService.getTranslation("logout_title"));
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            Scene loginScene = LoginScene.createScene(stage, currentWidth, currentHeight);
                            SceneManager.switchScene(stage, loginScene);
                            stage.setTitle("Finanger - " + languageService.getTranslation("login"));
                        }
                    });
                    break;
            }
        });

        HBox box = new HBox(label);
        box.setAlignment(Pos.CENTER_LEFT);
        if (isActive) {
            box.setTranslateX(2);
        }
        return box;
    }

    /**
     * Updates the language of all sidebar elements.
     * This method is called when the application language is changed.
     *
     * @param sideBar The sidebar VBox to update
     * @param languageService The service for managing application language
     */
    public static void updateLanguage(VBox sideBar, LanguageService languageService) {
        // Update welcome message
        Label welcomeLabel = (Label) sideBar.getChildren().get(0);
        String currentText = welcomeLabel.getText();
        String messageKey = getMessageKeyFromText(currentText);
        welcomeLabel.setText(languageService.getTranslation(messageKey));

        // Update navigation buttons
        for (int i = 1; i < sideBar.getChildren().size(); i++) {
            HBox buttonBox = (HBox) sideBar.getChildren().get(i);
            Label buttonLabel = (Label) buttonBox.getChildren().get(0);
            
            // Determine translation key based on button text
            String currentButtonText = buttonLabel.getText();
            String translationKey = getTranslationKeyFromText(currentButtonText);
            
            if (!translationKey.isEmpty()) {
                String newText = languageService.getTranslation(translationKey);
                buttonLabel.setText(newText);
            }
        }
    }

    /**
     * Determines the message key based on the current welcome text.
     * Used for language updates to maintain the correct welcome message.
     *
     * @param text The current welcome text
     * @return The corresponding message key for translation
     */
    private static String getMessageKeyFromText(String text) {
        if (text.contains("Welcome back") || text.contains("欢迎回来")) {
            return "welcome_message_status";
        } else if (text.contains("It's My Goal") || text.contains("这是我的目标")) {
            return "welcome_message_goals";
        } else if (text.contains("Every day") || text.contains("每一天都是")) {
            return "welcome_message_new";
        } else if (text.contains("Only you can do") || text.contains("只有你能做到")) {
            return "welcome_message_settings";
        }
        return "welcome_message_default";
    }

    /**
     * Determines the translation key based on the current button text.
     * Used for language updates to maintain the correct button labels.
     *
     * @param text The current button text
     * @return The corresponding translation key
     */
    private static String getTranslationKeyFromText(String text) {
        if (text.contains("Status") || text.contains("状态")) {
            return "status";
        } else if (text.contains("Goals") || text.contains("目标")) {
            return "goals";
        } else if (text.contains("New") || text.contains("新建")) {
            return "new";
        } else if (text.contains("Settings") || text.contains("设置")) {
            return "settings";
        } else if (text.contains("Log out") || text.contains("退出登录")) {
            return "logout";
        }
        return "";
    }
}