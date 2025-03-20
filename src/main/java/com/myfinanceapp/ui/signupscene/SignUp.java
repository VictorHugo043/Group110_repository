package com.myfinanceapp.ui.signupscene;

import com.myfinanceapp.ui.registrationterms.PrivacyPolicy;
import com.myfinanceapp.ui.registrationterms.TermofUse;
import com.myfinanceapp.ui.loginscene.LoginScene;
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
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.myfinanceapp.service.UserService;
import javafx.scene.control.Alert;
import com.myfinanceapp.ui.registrationterms.PrivacyPolicy;
import com.myfinanceapp.ui.registrationterms.TermofUse;
import java.io.IOException;
import java.util.Objects;


public class SignUp {

    public static Scene createScene(Stage stage, double width, double height) {
        // 根节点：Group，承载多边形 + 容器
        Group root = new Group();

        // ============== 1. 左侧图片 (Polygon) ==============
        Image bgImage = new Image(
                Objects.requireNonNull(SignUp.class.getResource("/pictures/signupbg.png")).toExternalForm()
        );
        // 多边形来实现斜线分割，顶点坐标可根据需求调整
        Polygon leftPolygon = new Polygon(
                0,    0,
                480,  0,
                280,  height,
                0,    height
        );
        leftPolygon.setFill(new ImagePattern(bgImage, 0, 0, 1, 1, true));

        // ============== 2. 右侧蓝色 (Polygon) ==============
        Polygon rightPolygon = new Polygon(
                480,  0,
                width, 0,
                width, height,
                280,  height
        );
        rightPolygon.setFill(Color.web("#93D2F3"));

        // 将两个多边形加到根节点
        root.getChildren().addAll(leftPolygon, rightPolygon);

        // ============== 3. 在右侧放置 Pane + VBox ==============
        Pane rightPane = new Pane();
        rightPane.setLayoutX(440);  // 紧贴多边形的右边起点
        rightPane.setPrefSize(width - 440, height);

        // VBox 用于垂直布局各元素
        VBox vbox = new VBox(20); // 20px 间隔
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        vbox.setPrefSize(rightPane.getPrefWidth(), rightPane.getPrefHeight());

        // ============== 4. 标题区 ==============
        // "Sign Up to" + 下方 "Finanger" 带一条横线（可用CSS或分两个Label实现）
        Label mainTitle = new Label("Sign Up to");
        mainTitle.setFont(Font.font("PingFang SC",FontWeight.BOLD, 32));
        mainTitle.setTextFill(Color.WHITE);
        mainTitle.setStyle("-fx-underline: true;");
        mainTitle.setMaxWidth(Double.MAX_VALUE);
        mainTitle.setAlignment(Pos.CENTER_LEFT);

        Label subTitle = new Label("Finanger");
        subTitle.setFont(Font.font("Arial",FontWeight.BOLD ,18));
        subTitle.setTextFill(Color.WHITE);
        // 也可添加下划线、或在底部画一条线
        //subTitle.setStyle("-fx-underline: true;");
        subTitle.setMaxWidth(Double.MAX_VALUE);
        subTitle.setAlignment(Pos.CENTER_LEFT);

        vbox.getChildren().addAll(mainTitle, subTitle);

        VBox titleBox = new VBox(2, mainTitle, subTitle);
        titleBox.setAlignment(Pos.CENTER_RIGHT);
        titleBox.setMaxWidth(Region.USE_PREF_SIZE);      // 宽度自适应文字
        // 把文本添加进 titleBox
        //titleBox.getChildren().addAll(mainTitle, subTitle);

        // 2) 将 titleBox 加进原先的 vbox
        //vbox.getChildren().add(titleBox);

        // ============== 5. 用户名/密码输入框 ==============
        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        userLabel.setTextFill(Color.WHITE);
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(180);

        HBox userBox = new HBox(10, userLabel, usernameField);
        userBox.setAlignment(Pos.CENTER_LEFT);

        Label passLabel = new Label("Password: ");
        passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        passLabel.setTextFill(Color.WHITE);
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(180);

        HBox passBox = new HBox(10, passLabel, passwordField);
        passBox.setAlignment(Pos.CENTER_LEFT);

        // ============== 6. “Next” 按钮 ==============
        Button nextBtn = new Button("Next ➜");
        nextBtn.setPrefWidth(140);
        nextBtn.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        CheckBox agreeCheckBox = new CheckBox("Agree to ");
        agreeCheckBox.setFont(Font.font("Arial", 11));
        agreeCheckBox.setTextFill(Color.WHITE);

        nextBtn.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            // 1) 先判断是否勾选了协议
            if (!agreeCheckBox.isSelected()) {
                showAlert("Error", "You must agree to the Terms of use and Privacy Policy to register!");
                return;
            }

            // 简单判空
            if (user.isEmpty() || pass.isEmpty()) {
                showAlert("Error", "Username or Password cannot be empty!");
                return;
            }

            // 调用 UserService 注册
            UserService userService = new UserService();
            boolean success = userService.registerUser(user, pass);
            if (success) {
                showAlert("Success", "User registered successfully!");
                // 也可自动跳转回登录界面
                stage.setScene(LoginScene.createScene(stage, 800, 450));
            } else {
                showAlert("Error", "Username already exists!");
            }
        });


        // ============== 7. 复选框 + 超链接 ==============
        //CheckBox agreeCheckBox = new CheckBox("Agree to ");
        //agreeCheckBox.setFont(Font.font("Arial", 11));
        //agreeCheckBox.setTextFill(Color.WHITE);

        Hyperlink termsLink = new Hyperlink("Terms of use");
        termsLink.setFont(Font.font("Arial", 11));
        termsLink.setTextFill(Color.DARKBLUE);
        termsLink.setOnAction(e -> {
            // 跳到 TermsOfUse 场景
            Scene termsScene = TermofUse.createScene(stage, 800, 450);
            stage.setScene(termsScene);
            stage.setTitle("Terms of Use");
        });
        Hyperlink privacyLink = new Hyperlink("Privacy Policy");
        privacyLink.setFont(Font.font("Arial", 11));
        privacyLink.setTextFill(Color.DARKBLUE);
        privacyLink.setOnAction(e -> {
            // 跳到 PrivacyPolicy 场景
            Scene policyScene = PrivacyPolicy.createScene(stage, 800, 450);
            stage.setScene(policyScene);
            stage.setTitle("Privacy Policy");
        });

        // 可以将 " and " 做成一个 Label
        Label andLabel = new Label(" and ");
        andLabel.setFont(Font.font("Arial", 11));
        andLabel.setTextFill(Color.WHITE);

        HBox agreeBox = new HBox(2, agreeCheckBox, termsLink, andLabel, privacyLink);
        agreeBox.setAlignment(Pos.CENTER);

        // ============== 8. “Already have an account?” 链接 ==============
        Hyperlink alreadyLink = new Hyperlink("Already have an account?");
        alreadyLink.setTextFill(Color.DARKBLUE);
        // 点击后返回到登录界面
        alreadyLink.setOnAction(e -> {
            // 假设有 LoginScene，可以跳转回登录
            Scene loginScene = LoginScene.createScene(stage, 800, 450);
            stage.setScene(loginScene);
            stage.setTitle("Finanger - Login");
            //stage.setScene(LoginScene.createScene(stage, width, height));
        });

        // ============== 9. 将所有元素加入 VBox ==============
        vbox.getChildren().addAll(
                titleBox,
                userBox,
                passBox,
                nextBtn,
                agreeBox,
                alreadyLink
        );
        rightPane.getChildren().add(vbox);
        root.getChildren().add(rightPane);

        // 生成场景
        return new Scene(root, width, height);
    }

// ... 其他代码不变 ...

    /** 新增一个 showAlert 方法，用于弹窗提醒 */
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
