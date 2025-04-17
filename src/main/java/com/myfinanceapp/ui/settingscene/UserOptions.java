package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SettingsTopBarFactory;
import com.myfinanceapp.ui.loginscene.ResetPassword;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
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
 * "User Options" 界面：允许更改用户名、更改安全问题、重置密码等
 */
public class UserOptions {

    // 假设当前已登录用户信息在此记录
    private static User currentUser;

    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        // 把当前登录用户保存，供下文使用
        currentUser = loggedUser;
        if (currentUser == null) {
            throw new IllegalStateException("No logged user!");
        }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // 左侧边栏: "Settings" 选中
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Settings", loggedUser);
        root.setLeft(sideBar);

        // 中心：与 SystemSettings/ About 等相同
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);

        // 外部容器
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);

        // 顶部Tab栏: "User Options" 选中
        HBox topBar = SettingsTopBarFactory.createTopBar(stage, "User Options", loggedUser);

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
                        "-fx-background-color: white;"
        );

        // ========== 右上角显示当前用户名 ===========
        Label currentUserLabel = new Label("Current Username: " + (loggedUser != null ? loggedUser.getUsername() : "N/A"));
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
        Label resetUserLabel = new Label("Reset Username");
        resetUserLabel.setStyle("-fx-text-fill: #3282FA; -fx-font-size: 16; -fx-font-weight: bold;");

        HBox resetUserHeader = new HBox(10, userIcon, resetUserLabel);
        resetUserHeader.setAlignment(Pos.CENTER_LEFT);

        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("New username");

        Button saveUserBtn = new Button("save");
        saveUserBtn.setStyle("-fx-background-color: #BEE3F8; -fx-text-fill: #3282FA; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;");

        // 创建 UserService 实例
        UserService userService = new UserService();

        saveUserBtn.setOnAction(e -> {
            String newU = newUsernameField.getText().trim();
            if (newU.isEmpty()) {
                showAlert("Error", "Username cannot be empty!");
                return;
            }
            String oldName = currentUser.getUsername();
            boolean success = userService.updateUserName(oldName, newU);

            if (success) {
                currentUser.setUsername(newU);
                showAlert("Success", "Username updated!");
                currentUserLabel.setText("Current Username: " + newU);
            } else {
                showAlert("Error", "Failed to update username!");
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
        Label resetSecLabel = new Label("Reset Security Question");
        resetSecLabel.setStyle("-fx-text-fill: #3282FA; -fx-font-size: 16; -fx-font-weight: bold;");

        HBox resetSecHeader = new HBox(10, secIcon, resetSecLabel);

        ComboBox<String> questionCombo = new ComboBox<>();
        questionCombo.getItems().addAll(
                "What is your favorite book?",
                "What is your mother's maiden name?",
                "What is your best friend's name?",
                "What city were you born in?"
        );
        questionCombo.setValue(currentUser.getSecurityQuestion());

        Label ansLabel = new Label("Your answer:");
        ansLabel.setStyle("-fx-text-fill: #3282FA; -fx-font-weight: bold;");
        TextField ansField = new TextField(currentUser.getSecurityAnswer());

        Button saveSecBtn = new Button("save");
        saveSecBtn.setStyle("-fx-background-color: #BEE3F8; -fx-text-fill: #3282FA; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;");

        saveSecBtn.setOnAction(e -> {
            String q = questionCombo.getValue();
            String a = ansField.getText().trim();
            if (a.isEmpty()) {
                showAlert("Error", "Answer cannot be empty!");
                return;
            }

            boolean updated = userService.updateSecurityQuestion(currentUser.getUid(), q, a);

            if (updated) {
                currentUser.setSecurityQuestion(q);
                currentUser.setSecurityAnswer(a);
                showAlert("Success", "Security question updated!");
            } else {
                showAlert("Error", "Failed to update question!");
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

        Label resetPassLabel = new Label("Reset Password ➜");
        resetPassLabel.setStyle("-fx-text-fill: #3282FA; -fx-font-size: 16; -fx-font-weight: bold;");
        HBox resetPassRow = new HBox(10, passIcon, resetPassLabel);
        resetPassRow.setAlignment(Pos.CENTER_LEFT);

        resetPassRow.setOnMouseClicked(e -> {
            Scene resetScene = ResetPassword.createScene(stage, width, height);
            stage.setScene(resetScene);
            stage.setTitle("Reset Password");
        });

        // ========== Bottom: Back to Status ==========
        Button backBtn = new Button("Back to Status");
        backBtn.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> {
            StatusScene statusScene = new StatusScene(stage, width, height, loggedUser);
            stage.setScene(statusScene.createScene());
            StatusService statusService = new StatusService(statusScene, loggedUser);
            stage.setTitle("Finanger - Status");
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

        return new Scene(root, width, height);
    }

    /**
     * 简单的 Alert
     */
    private static void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}