package com.myfinanceapp.ui.loginscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Objects;

public class ResetPassword {

    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    // 多边形相关
    private static Group root;
    private static Polygon leftPolygon;
    private static Polygon rightPolygon;
    private static Pane rightPane;
    private static VBox vbox;
    private static Button backBtn;

    // 比例数组
    private static final double[] LEFT_POLY_FRACS = {
            0.0, 0.0,
            480.0/INITIAL_WIDTH, 0.0,
            280.0/INITIAL_WIDTH, 1.0,
            0.0, 1.0
    };
    private static final double[] RIGHT_POLY_FRACS = {
            480.0/INITIAL_WIDTH, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            280.0/INITIAL_WIDTH, 1.0
    };
    private static final double RIGHT_PANE_X_FRAC = 440.0/INITIAL_WIDTH;

    // 输入控件
    private static TextField usernameField;
    private static Label questionLabel;
    private static TextField answerField;
    private static PasswordField newPasswordField;

    // 按钮
    private static Button checkQuestionBtn;
    private static Button resetPassBtn;

    // 找到的用户对象（缓存）
    private static User foundUser = null;

    public static Scene createScene(Stage stage, double width, double height) {
        root = new Group();
        Scene scene = new Scene(root, width, height);

        stage.setMinWidth(INITIAL_WIDTH);
        stage.setMinHeight(INITIAL_HEIGHT);
        stage.setResizable(true);

        // ========== 1) 创建 back 按钮并加到root ==========
        backBtn = new Button("back");
        backBtn.setStyle(
                "-fx-background-color: #A3D1FF;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;"
        );
        backBtn.setPrefSize(60, 30);

        // 点击后回到登录界面
        backBtn.setOnAction(e->{
            stage.setScene(LoginScene.createScene(stage, 800, 450));
            stage.setTitle("Finanger - Login");
        });
        // 先把它放到root

        // =========== 左侧背景多边形 ===========
        leftPolygon = new Polygon();
        Image leftBg = new Image(
                Objects.requireNonNull(ResetPassword.class.getResource("/pictures/resetbg.png")).toExternalForm()
        );
        leftPolygon.setFill(new ImagePattern(leftBg, 0, 0, 1, 1, true));

        rightPolygon = new Polygon();
        rightPolygon.setFill(Color.web("#93D2F3"));

        root.getChildren().addAll(leftPolygon, rightPolygon);

        // =========== 右侧 Pane + VBox ===========
        rightPane = new Pane();
        root.getChildren().add(rightPane);

        vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));
        rightPane.getChildren().add(vbox);

        // =========== 标题区 ===========
        Label titleLabel = new Label("Reset Password");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);

        Label subLabel = new Label("To reset your password, please answer the\nsecurity question you set during registration.");
        subLabel.setFont(Font.font("Arial", 14));
        subLabel.setTextFill(Color.WHITE);

        VBox titleBox = new VBox(5, titleLabel, subLabel);
        titleBox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(titleBox);

        // =========== Step 1: 输入 Username, 点击 [Check Question] ===========

        // Username row (单行: label + textfield)
        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        userLabel.setTextFill(Color.WHITE);

        usernameField = new TextField();
        usernameField.setPrefWidth(180);

        HBox userHBox = new HBox(10, userLabel, usernameField);
        userHBox.setAlignment(Pos.CENTER);

        checkQuestionBtn = new Button("Check Question ➜");
        checkQuestionBtn.setPrefWidth(160);
        checkQuestionBtn.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        checkQuestionBtn.setOnAction(e -> handleCheckQuestion());

        // =========== Step 2: 显示安全问题 + 输入答案 & 新密码 ===========

        // Security question row (两行: label, then question text)
        Label questionLbl = new Label("Your Security Question:");
        questionLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        questionLbl.setTextFill(Color.WHITE);

        questionLabel = new Label("(Not loaded yet)");
        questionLabel.setFont(Font.font("Arial", 12));
        questionLabel.setTextFill(Color.WHITE);

        VBox questionContainer = new VBox(5, questionLbl, questionLabel);
        questionContainer.setAlignment(Pos.CENTER_LEFT);

        // Answer row (两行: label, then textfield)
        Label ansLbl = new Label("Your answer:");
        ansLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ansLbl.setTextFill(Color.WHITE);
        answerField = new TextField();
        answerField.setPrefWidth(180);

        VBox answerContainer = new VBox(5, ansLbl, answerField);
        answerContainer.setAlignment(Pos.CENTER_LEFT);

        // New password row (两行: label, then textfield)
        Label newPassLbl = new Label("Enter New Password:");
        newPassLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        newPassLbl.setTextFill(Color.WHITE);
        newPasswordField = new PasswordField();
        newPasswordField.setPrefWidth(180);

        VBox newPassContainer = new VBox(5, newPassLbl, newPasswordField);
        newPassContainer.setAlignment(Pos.CENTER_LEFT);

        // Reset password button
        resetPassBtn = new Button("Reset Password ➜");
        resetPassBtn.setPrefWidth(160);
        resetPassBtn.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        resetPassBtn.setOnAction(e -> handleReset(stage));

        // 初始先禁用 question, answer, newPass, resetBtn
        questionLabel.setDisable(true);
        answerField.setDisable(true);
        newPasswordField.setDisable(true);
        resetPassBtn.setDisable(true);

        // 组装
        vbox.getChildren().addAll(
                userHBox,
                checkQuestionBtn,
                questionContainer,
                answerContainer,
                newPassContainer,
                resetPassBtn
        );
        // 在所有多边形、Pane 添加完之后：
        root.getChildren().add(backBtn);// 然后
        backBtn.toFront();


        // 监听 scene大小，动态重排
        scene.widthProperty().addListener((obs,oldV,newV)-> relayout());
        scene.heightProperty().addListener((obs,oldV,newV)-> relayout());

        // 场景设置好之后，延迟一拍再调用 relayout 以获取 backBtn真实宽度
        Platform.runLater(ResetPassword::relayout);

        return scene;
    }

    /**
     * Step 1: 用户点击 "Check Question" => 验证用户名 => 显示其安全问题
     */
    private static void handleCheckQuestion() {
        String uname = usernameField.getText().trim();
        if (uname.isEmpty()) {
            showAlert("Error", "Username cannot be empty!");
            return;
        }

        UserService userService = new UserService();
        User user = userService.findUserByUsername(uname);
        if (user == null) {
            showAlert("Error", "User not found!");
            return;
        }

        // 找到用户 => 显示安全问题, 启用回答与重置密码
        foundUser = user;
        questionLabel.setText(user.getSecurityQuestion());
        questionLabel.setDisable(false);
        answerField.setDisable(false);
        newPasswordField.setDisable(false);
        resetPassBtn.setDisable(false);
    }

    /**
     * Step 2: 用户点击 "Reset Password"
     */
    private static void handleReset(Stage stage) {
        if (foundUser == null) {
            showAlert("Error", "Please check question first!");
            return;
        }

        String answer = answerField.getText().trim();
        String newPass = newPasswordField.getText().trim();
        if (answer.isEmpty() || newPass.isEmpty()) {
            showAlert("Error", "Fields cannot be empty!");
            return;
        }

        // 比对安全答案
        if (!answer.equalsIgnoreCase(foundUser.getSecurityAnswer())) {
            showAlert("Error", "Security answer mismatch!");
            return;
        }

        // 匹配成功 => 重设密码
        foundUser.setPassword(newPass);
        UserService userService = new UserService();
        boolean updated = userService.updateUserName(foundUser, foundUser.getUsername()); // 需实现
        if (updated) {
            showAlert("Success", "Password reset successfully!");
            // 回到登录界面
            stage.setScene(LoginScene.createScene(stage, INITIAL_WIDTH, INITIAL_HEIGHT));
        } else {
            showAlert("Error", "Failed to reset password!");
        }
    }

    /**
     * 动态重排多边形 + back按钮
     */
    private static void relayout() {
        double curW = root.getScene().getWidth();
        double curH = root.getScene().getHeight();

        // 多边形
        leftPolygon.getPoints().setAll(
                LEFT_POLY_FRACS[0]*curW, LEFT_POLY_FRACS[1]*curH,
                LEFT_POLY_FRACS[2]*curW, LEFT_POLY_FRACS[3]*curH,
                LEFT_POLY_FRACS[4]*curW, LEFT_POLY_FRACS[5]*curH,
                LEFT_POLY_FRACS[6]*curW, LEFT_POLY_FRACS[7]*curH
        );
        rightPolygon.getPoints().setAll(
                RIGHT_POLY_FRACS[0]*curW, RIGHT_POLY_FRACS[1]*curH,
                RIGHT_POLY_FRACS[2]*curW, RIGHT_POLY_FRACS[3]*curH,
                RIGHT_POLY_FRACS[4]*curW, RIGHT_POLY_FRACS[5]*curH,
                RIGHT_POLY_FRACS[6]*curW, RIGHT_POLY_FRACS[7]*curH
        );

        // Pane
        double paneX = RIGHT_PANE_X_FRAC * curW;
        rightPane.setLayoutX(paneX);
        rightPane.setPrefSize(curW - paneX, curH);
        vbox.setPrefSize(rightPane.getPrefWidth(), rightPane.getPrefHeight());

        // backBtn放在窗口右上角 => x= sceneWidth - backBtn.width - 10
        backBtn.applyCss();
        backBtn.layout();
        double btnW = backBtn.getWidth();
        backBtn.setLayoutX(curW - btnW - 10);
        backBtn.setLayoutY(10);
        backBtn.toFront();
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
