package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SettingsTopBarFactory;
import com.myfinanceapp.ui.loginscene.ResetPassword;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * A user options interface for the Finanger application.
 * This scene allows users to manage their account settings including:
 * - Username changes
 * - Security question updates
 * - Password reset functionality
 * The interface features theme customization, internationalization support,
 * and a responsive layout design.
 */
public class UserOptions {
    private static final LanguageService languageService = LanguageService.getInstance();

    // Current logged-in user information
    private static User currentUser;

    /**
     * Creates and returns a user options scene with default theme and currency settings.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @param loggedUser The currently logged-in user
     * @return A configured Scene object for the user options interface
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService(), new CurrencyService("CNY"));
    }

    /**
     * Creates and returns a user options scene with specified theme settings.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @param loggedUser The currently logged-in user
     * @param themeService The theme service to use for styling
     * @return A configured Scene object for the user options interface
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService) {
        return createScene(stage, width, height, loggedUser, themeService, new CurrencyService("CNY"));
    }

    /**
     * Creates and returns a user options scene with specified theme and currency settings.
     * The scene includes various user account management options and navigation controls.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @param loggedUser The currently logged-in user
     * @param themeService The theme service to use for styling
     * @param currencyService The currency service to use for the application
     * @return A configured Scene object for the user options interface
     * @throws IllegalStateException if no user is logged in
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        // 把当前登录用户保存，供下文使用
        currentUser = loggedUser;
        if (currentUser == null) {
            throw new IllegalStateException("No logged user!");
        }

        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // 左侧边栏: "Settings" 选中
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Settings", loggedUser, themeService, currencyService);
        root.setLeft(sideBar);

        // 中心：与 SystemSettings/ About 等相同
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);

        // 外部容器
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);

        // 顶部Tab栏: "User Options" 选中
        HBox topBar = SettingsTopBarFactory.createTopBar(stage, "User Options", loggedUser, themeService, currencyService);

        // 下部圆角容器
        VBox outerBox = new VBox(20);
        outerBox.setPadding(new Insets(25));
        outerBox.setAlignment(Pos.TOP_LEFT);
        outerBox.setMaxWidth(600);
        outerBox.setMaxHeight(400);
        outerBox.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 0 0 12 12;" +
                        "-fx-background-radius: 0 0 12 12;" +
                        themeService.getCurrentFormBackgroundStyle()
        );

        // ========== 右上角显示当前用户名 ===========
        Label currentUserLabel = new Label(languageService.getTranslation("current_username") + ": " + (loggedUser != null ? loggedUser.getUsername() : "N/A"));
        currentUserLabel.setStyle("-fx-text-fill: #3282FA; -fx-font-weight: bold;");
        currentUserLabel.setFont(new Font(14));

        HBox topRightBox = new HBox(currentUserLabel);
        topRightBox.setAlignment(Pos.TOP_RIGHT);

        // ========== 1) Reset Username ==========
        ImageView userIcon = new ImageView();
        userIcon.setFitWidth(24);
        userIcon.setFitHeight(24);
        try {
            Image icon = new Image(Objects.requireNonNull(UserOptions.class.getResource("/pictures/user_icon.png")).toExternalForm());
            userIcon.setImage(icon);
        } catch (Exception e) {
            // fallback: do nothing
        }
        Label resetUserLabel = new Label(languageService.getTranslation("reset_username"));
        resetUserLabel.setStyle(
                "-fx-text-fill: #3282FA;" +
                        "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;"
        );

        HBox resetUserHeader = new HBox(10, userIcon, resetUserLabel);
        resetUserHeader.setAlignment(Pos.CENTER_LEFT);

        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText(languageService.getTranslation("new_username"));
        // Apply theme style class for TextField
        newUsernameField.getStyleClass().add(themeService.isDayMode() ? "day-theme-text-field" : "night-theme-text-field");
        // Force refresh to ensure style is applied
        newUsernameField.setVisible(false);
        newUsernameField.setVisible(true);

        Button saveUserBtn = new Button(languageService.getTranslation("save"));
        saveUserBtn.setStyle(themeService.getButtonStyle());

        // 创建 UserService 实例
        UserService userService = new UserService();

        saveUserBtn.setOnAction(e -> {
            String newU = newUsernameField.getText().trim();
            if (newU.isEmpty()) {
                showAlert(languageService.getTranslation("error"), languageService.getTranslation("username_empty"));
                return;
            }
            String oldName = currentUser.getUsername();
            boolean success = userService.updateUserName(oldName, newU);

            if (success) {
                currentUser.setUsername(newU);
                showAlert(languageService.getTranslation("success"), languageService.getTranslation("username_updated"));
                currentUserLabel.setText(languageService.getTranslation("current_username") + ": " + newU);
            } else {
                showAlert(languageService.getTranslation("error"), languageService.getTranslation("username_update_failed"));
            }
        });

        HBox resetUserRow = new HBox(10, newUsernameField, saveUserBtn);
        resetUserRow.setAlignment(Pos.CENTER_LEFT);

        // ========== 2) Reset Security Question ==========
        ImageView secIcon = new ImageView();
        secIcon.setFitWidth(24);
        secIcon.setFitHeight(24);
        try {
            Image icon = new Image(Objects.requireNonNull(UserOptions.class.getResource("/pictures/security_icon.png")).toExternalForm());
            secIcon.setImage(icon);
        } catch (Exception e) {
            // fallback
        }
        Label resetSecLabel = new Label(languageService.getTranslation("reset_security_question"));
        resetSecLabel.setStyle(
                "-fx-text-fill: #3282FA;" +
                        "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;"
        );

        HBox resetSecHeader = new HBox(10, secIcon, resetSecLabel);

        ComboBox<String> questionCombo = new ComboBox<>();
        questionCombo.getItems().addAll(
                languageService.getTranslation("security_question_1"),
                languageService.getTranslation("security_question_2"),
                languageService.getTranslation("security_question_3"),
                languageService.getTranslation("security_question_4")
        );
        questionCombo.setValue(currentUser.getSecurityQuestion());
        // Apply theme style class for ComboBox
        questionCombo.getStyleClass().add(themeService.isDayMode() ? "day-theme-combo-box" : "night-theme-combo-box");
        // Force refresh to ensure style is applied
        questionCombo.setVisible(false);
        questionCombo.setVisible(true);

        Label ansLabel = new Label(languageService.getTranslation("your_answer"));
        ansLabel.setStyle("-fx-text-fill: #3282FA; -fx-font-weight: bold;");

        TextField ansField = new TextField(currentUser.getSecurityAnswer());
        // Apply theme style class for TextField
        ansField.getStyleClass().add(themeService.isDayMode() ? "day-theme-text-field" : "night-theme-text-field");
        // Force refresh to ensure style is applied
        ansField.setVisible(false);
        ansField.setVisible(true);

        Button saveSecBtn = new Button(languageService.getTranslation("save"));
        saveSecBtn.setStyle(themeService.getButtonStyle());

        saveSecBtn.setOnAction(e -> {
            String q = questionCombo.getValue();
            String a = ansField.getText().trim();
            if (a.isEmpty()) {
                showAlert(languageService.getTranslation("error"), languageService.getTranslation("answer_empty"));
                return;
            }

            boolean updated = userService.updateSecurityQuestion(currentUser.getUid(), q, a);

            if (updated) {
                currentUser.setSecurityQuestion(q);
                currentUser.setSecurityAnswer(a);
                showAlert(languageService.getTranslation("success"), languageService.getTranslation("security_question_updated"));
            } else {
                showAlert(languageService.getTranslation("error"), languageService.getTranslation("security_question_update_failed"));
            }
        });

        VBox questionBox = new VBox(10);
        questionBox.getChildren().addAll(questionCombo, ansLabel, ansField);

        HBox secRow = new HBox(10, questionBox, saveSecBtn);
        secRow.setAlignment(Pos.CENTER_LEFT);

        // ========== 3) Reset Password -> (click => ResetPassword scene) ==========
        ImageView passIcon = new ImageView();
        passIcon.setFitWidth(24);
        passIcon.setFitHeight(24);
        try {
            Image icon = new Image(Objects.requireNonNull(UserOptions.class.getResource("/pictures/key_icon.png")).toExternalForm());
            passIcon.setImage(icon);
        } catch (Exception e) {
        }

        Label resetPassLabel = new Label(languageService.getTranslation("reset_password") + " ➜");
        resetPassLabel.setStyle(
                "-fx-text-fill: #3282FA;" +
                        "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;"
        );
        HBox resetPassRow = new HBox(10, passIcon, resetPassLabel);
        resetPassRow.setAlignment(Pos.CENTER_LEFT);

        resetPassRow.setOnMouseClicked(e -> {
            Scene resetScene = ResetPassword.createScene(stage, width, height);
            stage.setScene(resetScene);
            stage.setTitle(languageService.getTranslation("reset_password"));
        });

        // ========== Bottom: Back to Status ==========
        Button backBtn = new Button(languageService.getTranslation("back_to_status"));
        backBtn.setStyle(themeService.getButtonStyle());
        backBtn.setOnAction(e -> {
            StatusScene statusScene = new StatusScene(stage, width, height, loggedUser);
            stage.setScene(statusScene.createScene(themeService, currencyService));
            StatusService statusService = new StatusService(statusScene, loggedUser, currencyService, languageService);
            stage.setTitle("Finanger - " + languageService.getTranslation("status"));
        });

        // ========== 组装outerBox内容 ==========
        outerBox.getChildren().addAll(
                topRightBox,
                resetUserHeader,
                resetUserRow,
                resetSecHeader,
                secRow,
                resetPassRow,
                backBtn
        );

        container.getChildren().addAll(topBar, outerBox);
        centerBox.getChildren().add(container);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, width, height);
        // Add the custom theme stylesheet for ComboBox styles
        scene.getStylesheets().add("data:text/css," + themeService.getThemeStylesheet());
        // Add a local stylesheet for TextField styles to ensure night mode adaptation
        String textFieldStylesheet = """
                .day-theme-text-field {
                    -fx-background-color: white;
                    -fx-text-fill: black;
                    -fx-border-color: #3282FA;
                    -fx-border-width: 1;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                }
                .night-theme-text-field {
                    -fx-background-color: #3C3C3C;
                    -fx-text-fill: white;
                    -fx-border-color: #3282FA;
                    -fx-border-width: 1;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                }
                .day-theme-text-field .text {
                    -fx-fill: black;
                }
                .night-theme-text-field .text {
                    -fx-fill: white;
                }
                .day-theme-text-field .prompt-text {
                    -fx-fill: gray;
                }
                .night-theme-text-field .prompt-text {
                    -fx-fill: lightgray;
                }
                """;
        scene.getStylesheets().add("data:text/css," + textFieldStylesheet);

        return scene;
    }

    /**
     * Displays an alert dialog with the specified title and message.
     * Used for showing feedback to users after operations like username changes or security question updates.
     *
     * @param title The title of the alert dialog
     * @param msg The message to display in the alert dialog
     */
    private static void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}