package com.myfinanceapp.ui.signupscene;


import com.myfinanceapp.ui.registrationterms.PrivacyPolicy;
import com.myfinanceapp.ui.registrationterms.TermofUse;
import com.myfinanceapp.ui.loginscene.LoginScene;
import com.myfinanceapp.service.UserService;
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


public class SignUp {

    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    private static Group root;
    private static Polygon leftPolygon;
    private static Polygon rightPolygon;
    private static Pane rightPane;
    private static VBox vbox;

    // 将固定坐标转换为比例（相对于 800×450）
    // 左侧多边形: (0,0), (480,0), (280,450), (0,450)
    private static final double[] LEFT_POLY_FRACS = {
            0.0, 0.0,
            480.0 / INITIAL_WIDTH, 0.0,
            280.0 / INITIAL_WIDTH, 1.0,
            0.0, 1.0
    };
    // 右侧多边形: (480,0), (800,0), (800,450), (280,450)
    private static final double[] RIGHT_POLY_FRACS = {
            480.0 / INITIAL_WIDTH, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            280.0 / INITIAL_WIDTH, 1.0
    };

    // rightPane 原先 layoutX = 440, 即 440/800 = 0.55
    private static final double RIGHT_PANE_X_FRAC = 440.0 / INITIAL_WIDTH;

    // 用户输入控件（后面不需要重排坐标，因为它们在 VBox 内自动排列）
    private static TextField usernameField;
    private static PasswordField passwordField;
    private static CheckBox agreeCheckBox;
    private static ComboBox<String> securityQuestionCombo;
    private static TextField securityAnswerField;

    public static Scene createScene(Stage stage, double width, double height) {
        root = new Group();
        Scene scene = new Scene(root, width, height);

        // 设置最小窗口尺寸
        stage.setMinWidth(INITIAL_WIDTH);
        stage.setMinHeight(INITIAL_HEIGHT);
        stage.setResizable(true);

        // === 左右多边形 ===
        leftPolygon = new Polygon();
        Image bgImage = new Image(Objects.requireNonNull(SignUp.class.getResource("/pictures/signupbg.png")).toExternalForm());
        leftPolygon.setFill(new ImagePattern(bgImage, 0, 0, 1, 1, true));

        rightPolygon = new Polygon();
        rightPolygon.setFill(Color.web("#93D2F3"));

        root.getChildren().addAll(leftPolygon, rightPolygon);

        // === 右侧 Pane 和 VBox ===
        rightPane = new Pane();
        root.getChildren().add(rightPane);

        vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));
        rightPane.getChildren().add(vbox);

        // === 标题区 ===
        Label mainTitle = new Label("Sign Up to");
        mainTitle.setFont(Font.font("PingFang SC", FontWeight.BOLD, 32));
        mainTitle.setTextFill(Color.WHITE);
        mainTitle.setStyle("-fx-underline: true;");
        mainTitle.setMaxWidth(Double.MAX_VALUE);
        mainTitle.setAlignment(Pos.CENTER_LEFT);

        Label subTitle = new Label("Finanger");
        subTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        subTitle.setTextFill(Color.WHITE);
        subTitle.setMaxWidth(Double.MAX_VALUE);
        subTitle.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2, mainTitle, subTitle);
        titleBox.setAlignment(Pos.CENTER_RIGHT);
        titleBox.setMaxWidth(Region.USE_PREF_SIZE);
        vbox.getChildren().add(titleBox);

        // === 用户名和密码输入区域 ===
        Label userLabel = new Label("Set Your Username:");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        userLabel.setTextFill(Color.WHITE);
        usernameField = new TextField();
        usernameField.setPrefWidth(180);
        HBox userBox = new HBox(10, userLabel, usernameField);
        userBox.setAlignment(Pos.CENTER);

        Label passLabel = new Label("Set Your Password:");
        passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        passLabel.setTextFill(Color.WHITE);
        passwordField = new PasswordField();
        passwordField.setPrefWidth(180);
        HBox passBox = new HBox(10, passLabel, passwordField);
        passBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(userBox, passBox);

        // 1) Security Question
        Label questionLabel = new Label("Set your Security Question");
        questionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        questionLabel.setTextFill(Color.WHITE);

        // 准备几个常用问题
        securityQuestionCombo = new ComboBox<>();
        securityQuestionCombo.getItems().addAll(
                "What is your favorite book?",
                "What was the name of your first pet?",
                "What is your best friend's name?",
                "What city were you born in?"
        );
        securityQuestionCombo.setValue("What is your favorite book?"); // 默认

        VBox questionContainer = new VBox(5, questionLabel, securityQuestionCombo);
        questionContainer.setAlignment(Pos.CENTER_LEFT);

        // 2) Security Answer
        Label answerLabel = new Label("Your answer:");
        answerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        answerLabel.setTextFill(Color.WHITE);

        securityAnswerField = new TextField();
        securityAnswerField.setPrefWidth(180);

        VBox answerContainer = new VBox(5, answerLabel, securityAnswerField);
        answerContainer.setAlignment(Pos.CENTER_LEFT);

        // 在 vbox.getChildren().addAll(userBox, passBox); 后面插入
        vbox.getChildren().addAll(questionContainer, answerContainer);

        // === Next 按钮和协议 ===
        Button nextBtn = new Button("Next ➜");
        nextBtn.setPrefWidth(140);
        nextBtn.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        agreeCheckBox = new CheckBox("Agree to ");
        agreeCheckBox.setFont(Font.font("Arial", 11));
        agreeCheckBox.setTextFill(Color.WHITE);

        nextBtn.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            String secQuestion = securityQuestionCombo.getValue();
            String secAnswer   = securityAnswerField.getText();
            if (!agreeCheckBox.isSelected()) {
                showAlert("Error", "You must agree to the Terms of use and Privacy Policy to register!");
                return;
            }
            if (user.isEmpty() || pass.isEmpty()|| secAnswer.isEmpty()) {
                showAlert("Error", "Fields cannot be empty!");
                return;
            }
            UserService userService = new UserService();
            boolean success = userService.registerUser(user, pass,secQuestion,secAnswer);
            if (success) {
                showAlert("Success", "User registered successfully!");
                stage.setScene(LoginScene.createScene(stage, INITIAL_WIDTH, INITIAL_HEIGHT));
            } else {
                showAlert("Error", "Username already exists!");
            }
        });

        Hyperlink termsLink = new Hyperlink("Terms of use");
        termsLink.setFont(Font.font("Arial", 11));
        termsLink.setTextFill(Color.DARKBLUE);
        termsLink.setOnAction(e -> {
            Scene termsScene = TermofUse.createScene(stage, INITIAL_WIDTH, INITIAL_HEIGHT);
            stage.setScene(termsScene);
            stage.setTitle("Terms of Use");
        });

        Hyperlink privacyLink = new Hyperlink("Privacy Policy");
        privacyLink.setFont(Font.font("Arial", 11));
        privacyLink.setTextFill(Color.DARKBLUE);
        privacyLink.setOnAction(e -> {
            Scene policyScene = PrivacyPolicy.createScene(stage, INITIAL_WIDTH, INITIAL_HEIGHT);
            stage.setScene(policyScene);
            stage.setTitle("Privacy Policy");
        });

        Label andLabel = new Label(" and ");
        andLabel.setFont(Font.font("Arial", 11));
        andLabel.setTextFill(Color.WHITE);

        HBox agreeBox = new HBox(2, agreeCheckBox, termsLink, andLabel, privacyLink);
        agreeBox.setAlignment(Pos.CENTER);

        Hyperlink alreadyLink = new Hyperlink("Already have an account?");
        alreadyLink.setTextFill(Color.DARKBLUE);
        alreadyLink.setOnAction(e -> {
            Scene loginScene = LoginScene.createScene(stage, INITIAL_WIDTH, INITIAL_HEIGHT);
            stage.setScene(loginScene);
            stage.setTitle("Finanger - Login");
        });

        vbox.getChildren().addAll(nextBtn, agreeBox, alreadyLink);

        // 添加动态重排监听
        scene.widthProperty().addListener((obs, oldVal, newVal) -> relayout());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> relayout());
        relayout();

        return scene;
    }

    private static void relayout() {
        double curWidth = root.getScene().getWidth();
        double curHeight = root.getScene().getHeight();

        // 重新计算左侧多边形坐标
        leftPolygon.getPoints().setAll(
                LEFT_POLY_FRACS[0] * curWidth, LEFT_POLY_FRACS[1] * curHeight,
                LEFT_POLY_FRACS[2] * curWidth, LEFT_POLY_FRACS[3] * curHeight,
                LEFT_POLY_FRACS[4] * curWidth, LEFT_POLY_FRACS[5] * curHeight,
                LEFT_POLY_FRACS[6] * curWidth, LEFT_POLY_FRACS[7] * curHeight
        );

        // 重新计算右侧多边形坐标
        rightPolygon.getPoints().setAll(
                RIGHT_POLY_FRACS[0] * curWidth, RIGHT_POLY_FRACS[1] * curHeight,
                RIGHT_POLY_FRACS[2] * curWidth, RIGHT_POLY_FRACS[3] * curHeight,
                RIGHT_POLY_FRACS[4] * curWidth, RIGHT_POLY_FRACS[5] * curHeight,
                RIGHT_POLY_FRACS[6] * curWidth, RIGHT_POLY_FRACS[7] * curHeight
        );

        // 重新计算 rightPane 的位置和大小
        double paneX = RIGHT_PANE_X_FRAC * curWidth;
        rightPane.setLayoutX(paneX);
        rightPane.setPrefSize(curWidth - paneX, curHeight);
        vbox.setPrefSize(rightPane.getPrefWidth(), rightPane.getPrefHeight());
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
