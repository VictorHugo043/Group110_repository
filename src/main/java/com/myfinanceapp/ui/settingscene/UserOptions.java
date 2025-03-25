package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SettingsTopBarFactory;
import com.myfinanceapp.ui.loginscene.ResetPassword;
import com.myfinanceapp.ui.statusscene.Status;
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
            // 或 showAlert + return
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
        container.setAlignment(Pos.TOP_CENTER);

        // 顶部Tab栏: "User Options" 选中
        HBox topBar = SettingsTopBarFactory.createTopBar(stage, "User Options", loggedUser);

        // 下部圆角容器
        VBox outerBox = new VBox(20);
        outerBox.setPadding(new Insets(25));
        outerBox.setAlignment(Pos.TOP_LEFT);
        outerBox.setMaxWidth(600);
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

        // 图标
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

        // 点击后更新用户名
        saveUserBtn.setOnAction(e -> {
            String newU = newUsernameField.getText().trim();
            if (newU.isEmpty()) {
                showAlert("Error", "Username cannot be empty!");
                return;
            }
            // 获取旧用户名
            String oldName = currentUser.getUsername();

            // 调用 UserService 来更新用户名
            boolean success = userService.updateUserName(oldName, newU);

            if (success) {
                // 更新当前用户对象的用户名
                currentUser.setUsername(newU);
                showAlert("Success", "Username updated!");
                currentUserLabel.setText("Current Username: " + newU); // 更新 UI
            } else {
                showAlert("Error", "Failed to update username!");
            }
        });

        HBox resetUserRow = new HBox(10, newUsernameField, saveUserBtn);
        resetUserRow.setAlignment(Pos.CENTER_LEFT);

        // ========== 2) Reset Security Question ==========

        // 图标
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

        // 下拉框
        ComboBox<String> questionCombo = new ComboBox<>();
        questionCombo.getItems().addAll(
                "What is your favorite book?",
                "What is your mother's maiden name?",
                "What is your best friend's name?",
                "What city were you born in?"
        );
        questionCombo.setValue(currentUser.getSecurityQuestion()); // 初始显示

        // 答案
        Label ansLabel = new Label("Your answer:");
        ansLabel.setStyle("-fx-text-fill: #3282FA; -fx-font-weight: bold;");
        TextField ansField = new TextField(currentUser.getSecurityAnswer());

        Button saveSecBtn = new Button("save");
        saveSecBtn.setStyle("-fx-background-color: #BEE3F8; -fx-text-fill: #3282FA; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;");

        // 修正：调用正确的方法更新安全问题
        saveSecBtn.setOnAction(e -> {
            String q = questionCombo.getValue();
            String a = ansField.getText().trim();
            if (a.isEmpty()) {
                showAlert("Error", "Answer cannot be empty!");
                return;
            }

            // 调用 UserService 来更新安全问题
            boolean updated = userService.updateSecurityQuestion(currentUser.getUid(), q, a);

            if (updated) {
                // 更新当前用户对象的安全问题和答案
                currentUser.setSecurityQuestion(q);
                currentUser.setSecurityAnswer(a);
                showAlert("Success", "Security question updated!");
            } else {
                showAlert("Error", "Failed to update question!");
            }
        });

        // Layout
        VBox questionBox = new VBox(10);
        questionBox.getChildren().addAll(questionCombo, ansLabel, ansField);

        HBox secRow = new HBox(10, questionBox, saveSecBtn);
        secRow.setAlignment(Pos.CENTER_LEFT);

        // ========== 3) Reset Password -> (click => ResetPassword scene) ==========

        // 图标
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

        // 点击 => 跳转 ResetPassword
        resetPassRow.setOnMouseClicked(e -> {
            stage.setScene(ResetPassword.createScene(stage, 800, 450));
            stage.setTitle("Reset Password");
        });

        // ========== Bottom: Back to Mainpage ==========

        Button backBtn = new Button("Back to Mainpage");
        backBtn.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> {
            stage.setScene(Status.createScene(stage, 800, 450, loggedUser));
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

        // 组合
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
