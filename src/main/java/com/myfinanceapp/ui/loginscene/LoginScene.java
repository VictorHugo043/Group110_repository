package com.myfinanceapp.ui.loginscene;
import com.myfinanceapp.ui.statusscene.Status;
import com.myfinanceapp.ui.signupscene.SignUp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
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


/**
 * 斜线分割的登录界面
 */
public class LoginScene {

    public static Scene createScene(Stage stage, double width, double height) {
        // 使用 Group 来承载多边形与控件
        Group root = new Group();

        // ========== 1. 绘制左侧图片多边形 ==========
        // 先加载图片
        Image coinImage = new Image(
                LoginScene.class.getResource("/pictures/coin.png").toExternalForm()
        );

        // 创建一个多边形覆盖左侧区域，并呈对角线
        // 下面的点是示例，按需要微调 (x,y)
        Polygon leftPolygon = new Polygon(
                0,        0,            // 左上角
                480,      0,            // 顶边稍靠右
                280,      height,       // 底边往左
                0,        height        // 左下角
        );
        // 用图片填充多边形
        leftPolygon.setFill(new ImagePattern(coinImage, 0, 0, 1, 1, true));

        // ========== 2. 绘制右侧蓝色多边形 ==========
        Polygon rightPolygon = new Polygon(
                480, 0,
                width, 0,
                width, height,
                280, height
        );
        // 自定义颜色
        rightPolygon.setFill(Color.web("#93D2F3"));

        // 将两个多边形加入 root
        root.getChildren().addAll(leftPolygon, rightPolygon);

        // =========== 3. 在右侧添加 Pane，承载布局 ===========
        Pane rightPane = new Pane();
        rightPane.setLayoutX(440);
        rightPane.setLayoutY(0);
        // 宽 = 800 - 440 = 360 (若总宽800)
        rightPane.setPrefSize(width - 440, height);

        // 用 VBox 垂直布局，将各控件更“舒展”地分布
        VBox vbox = new VBox(25); // 25px 间距，可自行调整
        vbox.setPadding(new Insets(30, 30, 30, 30));
        vbox.setAlignment(Pos.CENTER); // 整体居中
        vbox.setPrefSize(rightPane.getPrefWidth(), rightPane.getPrefHeight());

        // =========== 4. 构建登录表单控件 ===========
        Label titleLabel = new Label("Finanger");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);

        // 用户名
        Label userLabel = new Label("Username:");
        userLabel.setTextFill(Color.WHITE);
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(180);

        HBox userBox = new HBox(10, userLabel, usernameField);
        userBox.setAlignment(Pos.CENTER_LEFT);

        // 密码
        Label passLabel = new Label(" Password:");
        passLabel.setTextFill(Color.WHITE);
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(180);

        HBox passBox = new HBox(10, passLabel, passwordField);
        passBox.setAlignment(Pos.CENTER_LEFT);

        // "Forgot password?"
        Hyperlink forgotLink = new Hyperlink("Forgot password?");
        forgotLink.setTextFill(Color.DARKBLUE);

        // 登录按钮
        Button loginButton = new Button("Log in ➜");
        loginButton.setPrefWidth(160);
        loginButton.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        // 修改setOnAction
        loginButton.setOnAction(e -> {
            String uname = usernameField.getText();
            String pass = passwordField.getText();

            if (uname.isEmpty() || pass.isEmpty()) {
                showAlert("Error", "Username or Password cannot be empty!");
                return;
            }

            UserService userService = new UserService();
            boolean valid = userService.checkLogin(uname, pass);
            if (valid) {
                // 登录成功: 跳转到你想要的场景
                // e.g. Status page or main dashboard
                 stage.setScene(Status.createScene(stage, 800, 450));
                showAlert("Success", "Login Successful!");
            } else {
                // 登录失败
                showAlert("Error", "Invalid username or password!");
            }
        });


        // 最底部：Don't have an account? Sign Up
        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setTextFill(Color.WHITE);
        Hyperlink signUpLink = new Hyperlink("Sign Up");
        signUpLink.setTextFill(Color.DARKBLUE);
        signUpLink.setOnAction(e -> {
            // 调用 SignUp.createScene(...) 切换到注册页面
            Scene signUpScene = SignUp.createScene(stage, 800, 450);
            stage.setScene(signUpScene);
            stage.setTitle("Finanger - Sign Up");
        });


        HBox signUpBox = new HBox(5, noAccountLabel, signUpLink);
        signUpBox.setAlignment(Pos.CENTER);

        // 将所有子控件按顺序放进 VBox
        vbox.getChildren().addAll(
                titleLabel,
                userBox,
                passBox,
                forgotLink,
                loginButton,
                signUpBox
        );

        // 加入 rightPane，并将其放进根节点
        rightPane.getChildren().add(vbox);
        root.getChildren().add(rightPane);

        // =========== 5. 创建并返回场景 ===========
        return new Scene(root, width, height);
    }
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}