package com.myfinanceapp.ui.loginscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import com.myfinanceapp.ui.signupscene.SignUp;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.service.CurrencyService;
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

/**
 * 斜线分割的登录界面 (动态重排版本)
 */
public class LoginScene {

    // 原始设计尺寸
    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    // 主容器
    private static Group root;

    // 多边形: leftPolygon, rightPolygon
    private static Polygon leftPolygon;
    private static Polygon rightPolygon;

    // Pane rightPane (原先 layoutX=440)
    private static Pane rightPane;
    private static VBox vbox;

    // 表单控件
    private static Label titleLabel;
    private static HBox userBox;
    private static HBox passBox;
    private static Hyperlink forgotLink;
    private static Button loginButton;
    private static HBox signUpBox;

    // ========== 多边形的比例坐标 ==========

    // leftPolygon 原先: (0,0)->(480,0)->(280,height)->(0,height)
    // 转换成基于 (INITIAL_WIDTH, INITIAL_HEIGHT) 的 fraction:
    private static final double[] LEFT_POLY_FRACTIONS = {
            0.0, 0.0,
            480.0/INITIAL_WIDTH, 0.0,
            280.0/INITIAL_WIDTH, 1.0,
            0.0, 1.0
    };

    // rightPolygon 原先: (480,0)->(width,0)->(width,height)->(280,height)
    private static final double[] RIGHT_POLY_FRACTIONS = {
            480.0/INITIAL_WIDTH, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            280.0/INITIAL_WIDTH, 1.0
    };

    // Pane 的 layoutX 原先是 440
    // => 440/800 = 0.55
    private static final double PANE_X_FRAC = 440.0 / INITIAL_WIDTH;

    /**
     * 创建并返回此登录场景
     */
    public static Scene createScene(Stage stage, double width, double height) {
        return createScene(stage, width, height, new CurrencyService("CNY"));
    }

    public static Scene createScene(Stage stage, double width, double height, CurrencyService currencyService) {
        // 准备一个root Group
        root = new Group();

        // 生成 Scene，设初始大小
        Scene scene = new Scene(root, width, height);

        // 允许拉伸
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(450);

        // 初始化 UI 节点
        initUI(stage, currencyService);

        // 监听 scene 大小变化 -> relayout
        scene.widthProperty().addListener((obs, oldVal, newVal) -> relayout());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> relayout());

        // 初次布局
        relayout();

        return scene;
    }

    private static void initUI(Stage stage, CurrencyService currencyService) {
        // 1) 左侧多边形
        leftPolygon = new Polygon();
        // 用图片填充
        Image coinImage = new Image(
                LoginScene.class.getResource("/pictures/coin.png").toExternalForm()
        );
        leftPolygon.setFill(new ImagePattern(coinImage, 0, 0, 1, 1, true));

        // 2) 右侧多边形
        rightPolygon = new Polygon();
        rightPolygon.setFill(Color.web("#93D2F3"));

        root.getChildren().addAll(leftPolygon, rightPolygon);

        // 3) 右侧 Pane (原先 layoutX=440)
        rightPane = new Pane();
        root.getChildren().add(rightPane);

        // 在 rightPane 中放 VBox
        vbox = new VBox(25);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);
        rightPane.getChildren().add(vbox);

        // =========== 构建表单控件 ===========
        titleLabel = new Label("Finanger");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);

        // 用户名
        Label userLabel = new Label("Username:");
        userLabel.setTextFill(Color.WHITE);
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(180);
        userBox = new HBox(10, userLabel, usernameField);
        userBox.setAlignment(Pos.CENTER);

        // 密码
        Label passLabel = new Label(" Password:");
        passLabel.setTextFill(Color.WHITE);
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(180);
        passBox = new HBox(10, passLabel, passwordField);
        passBox.setAlignment(Pos.CENTER);

        // Forgot link
        forgotLink = new Hyperlink("Forgot password?");
        forgotLink.setTextFill(Color.DARKBLUE);
        forgotLink.setOnAction(e -> {
            // 跳转到 ResetPassword 界面
            Scene resetScene = ResetPassword.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
            stage.setScene(resetScene);
            stage.setTitle("Finanger - Reset Password");
        });

        // 登录按钮
        loginButton = new Button("Log in ➜");
        loginButton.setPrefWidth(160);
        loginButton.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        // 点击验证
        loginButton.setOnAction(e -> {
            String uname = usernameField.getText();
            String pass = passwordField.getText();

            if (uname.isEmpty() || pass.isEmpty()) {
                showAlert("Error", "Username or Password cannot be empty!");
                return;
            }

            UserService userService = new UserService();
            User loggedUser = userService.loginGetUser(uname, pass);
            if (loggedUser != null) {
                // Login successful, jump to Status scene
                StatusScene statusScene = new StatusScene(stage, root.getScene().getWidth(), root.getScene().getHeight(), loggedUser);
                stage.setScene(statusScene.createScene());
                StatusService statusService = new StatusService(statusScene, loggedUser, currencyService); // Pass currencyService
                stage.setTitle("Finanger - Status");
                showAlert("Success", "Login Successful!");
            } else {
                showAlert("Error", "Invalid username or password!");
            }
        });

        // SignUp
        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setTextFill(Color.WHITE);
        Hyperlink signUpLink = new Hyperlink("Sign Up");
        signUpLink.setTextFill(Color.DARKBLUE);
        signUpLink.setOnAction(e -> {
            Scene signUpScene = SignUp.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
            stage.setScene(signUpScene);
            stage.setTitle("Finanger - Sign Up");
        });
        signUpBox = new HBox(5, noAccountLabel, signUpLink);
        signUpBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(
                titleLabel,
                userBox,
                passBox,
                forgotLink,
                loginButton,
                signUpBox
        );
    }

    /**
     * 动态重排，每次窗口大小变化时调用
     */
    private static void relayout() {
        // 当前 Scene 尺寸
        double curWidth = root.getScene().getWidth();
        double curHeight = root.getScene().getHeight();

        // ========== 1) 重算 leftPolygon 顶点 ==========
        leftPolygon.getPoints().setAll(
                LEFT_POLY_FRACTIONS[0]*curWidth, LEFT_POLY_FRACTIONS[1]*curHeight,
                LEFT_POLY_FRACTIONS[2]*curWidth, LEFT_POLY_FRACTIONS[3]*curHeight,
                LEFT_POLY_FRACTIONS[4]*curWidth, LEFT_POLY_FRACTIONS[5]*curHeight,
                LEFT_POLY_FRACTIONS[6]*curWidth, LEFT_POLY_FRACTIONS[7]*curHeight
        );

        // ========== 2) 重算 rightPolygon 顶点 ==========
        rightPolygon.getPoints().setAll(
                RIGHT_POLY_FRACTIONS[0]*curWidth, RIGHT_POLY_FRACTIONS[1]*curHeight,
                RIGHT_POLY_FRACTIONS[2]*curWidth, RIGHT_POLY_FRACTIONS[3]*curHeight,
                RIGHT_POLY_FRACTIONS[4]*curWidth, RIGHT_POLY_FRACTIONS[5]*curHeight,
                RIGHT_POLY_FRACTIONS[6]*curWidth, RIGHT_POLY_FRACTIONS[7]*curHeight
        );

        // ========== 3) 重算 rightPane 的位置、大小 ==========
        // 原先 layoutX=440 => fraction=440/800=0.55
        double paneX = PANE_X_FRAC * curWidth; // left pos
        rightPane.setLayoutX(paneX);

        // Pane 的宽度 = 剩余部分
        double paneWidth = curWidth - paneX;
        double paneHeight = curHeight; // 占满高度
        rightPane.setPrefSize(paneWidth, paneHeight);

        // 让 VBox 大小也随之调整
        vbox.setPrefSize(paneWidth, paneHeight);
        // 也可以根据需要自定义，不一定要全填

        // (可选) 文字大小也可跟随变动:
        // double scale = curWidth / INITIAL_WIDTH;
        // titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36 * scale));
        // ...
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}