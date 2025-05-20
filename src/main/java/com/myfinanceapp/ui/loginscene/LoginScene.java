package com.myfinanceapp.ui.loginscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.UserService;
import com.myfinanceapp.ui.signupscene.SignUp;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;
import com.myfinanceapp.ui.common.SceneManager;
import com.myfinanceapp.ui.common.SceneManager.AnimationType;
import com.myfinanceapp.ui.common.AnimationUtils;
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
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/**
 * A dynamic login scene for the Finanger application featuring a diagonal split design.
 * This scene provides user authentication functionality with a responsive layout that
 * automatically adjusts based on window size. It includes username/password fields,
 * login button, and links to password reset and signup functionality.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class LoginScene {

    // Original design dimensions
    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    // Main container
    private static Group root;

    // Polygons: leftPolygon, rightPolygon
    private static Polygon leftPolygon;
    private static Polygon rightPolygon;

    // Pane rightPane (original layoutX=440)
    private static Pane rightPane;
    private static VBox vbox;

    // Form controls
    private static Label titleLabel;
    private static HBox userBox;
    private static HBox passBox;
    private static Hyperlink forgotLink;
    private static Button loginButton;
    private static HBox signUpBox;

    // ========== Polygon proportional coordinates ==========

    // leftPolygon original: (0,0)->(480,0)->(280,height)->(0,height)
    // Convert to fractions based on (INITIAL_WIDTH, INITIAL_HEIGHT):
    private static final double[] LEFT_POLY_FRACTIONS = {
            0.0, 0.0,
            480.0/INITIAL_WIDTH, 0.0,
            280.0/INITIAL_WIDTH, 1.0,
            0.0, 1.0
    };

    // rightPolygon original: (480,0)->(width,0)->(width,height)->(280,height)
    private static final double[] RIGHT_POLY_FRACTIONS = {
            480.0/INITIAL_WIDTH, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            280.0/INITIAL_WIDTH, 1.0
    };

    // Pane's layoutX was originally 440
    // => 440/800 = 0.55
    private static final double PANE_X_FRAC = 440.0 / INITIAL_WIDTH;

    /**
     * Creates and returns a login scene with default currency settings.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @return A configured Scene object for the login interface
     */
    public static Scene createScene(Stage stage, double width, double height) {
        return createScene(stage, width, height, new CurrencyService("CNY"));
    }

    /**
     * Creates and returns a login scene with specified currency settings.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @param currencyService The currency service to use for the application
     * @return A configured Scene object for the login interface
     */
    public static Scene createScene(Stage stage, double width, double height, CurrencyService currencyService) {
        // Prepare a root Group
        root = new Group();

        // Generate Scene with initial size
        Scene scene = new Scene(root, width, height);

        // Allow resizing
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(450);

        // Initialize UI nodes
        initUI(stage, currencyService);

        // Listen for scene size changes -> relayout
        scene.widthProperty().addListener((obs, oldVal, newVal) -> relayout());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> relayout());

        // Initial layout
        relayout();

        return scene;
    }

    /**
     * Initializes the UI components for the login scene.
     * Sets up the visual elements, event handlers, and authentication logic.
     *
     * @param stage The stage to display the scene
     * @param currencyService The currency service to use for the application
     */
    private static void initUI(Stage stage, CurrencyService currencyService) {
        // 1) Left polygon
        leftPolygon = new Polygon();
        // Fill with image
        Image coinImage = new Image(
                LoginScene.class.getResource("/pictures/coin.png").toExternalForm()
        );
        leftPolygon.setFill(new ImagePattern(coinImage, 0, 0, 1, 1, true));

        // 2) Right polygon
        rightPolygon = new Polygon();
        rightPolygon.setFill(Color.web("#93D2F3"));

        root.getChildren().addAll(leftPolygon, rightPolygon);

        // 3) Right Pane (original layoutX=440)
        rightPane = new Pane();
        root.getChildren().add(rightPane);

        // Place VBox in rightPane
        vbox = new VBox(25);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);
        rightPane.getChildren().add(vbox);

        // =========== Build form controls ===========
        titleLabel = new Label("Finanger");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);

        // Username
        Label userLabel = new Label("Username:");
        userLabel.setTextFill(Color.WHITE);
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(180);
        userBox = new HBox(10, userLabel, usernameField);
        userBox.setAlignment(Pos.CENTER);

        // Password
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
            // Navigate to ResetPassword interface
            Scene resetScene = ResetPassword.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
            SceneManager.switchScene(stage, resetScene, AnimationType.FADE);
            stage.setTitle("Finanger - Reset Password");
        });

        // Login button
        loginButton = new Button("Log in ➜");
        loginButton.setPrefWidth(160);
        loginButton.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        // Click to verify
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
                Scene statusSceneObj = statusScene.createScene();
                
                // 使用向上滑动动画切换到状态页面
                SceneManager.switchScene(stage, statusSceneObj, AnimationType.SLIDE_UP);
                
                // 创建StatusService
                StatusService statusService = new StatusService(statusScene, loggedUser, currencyService, LanguageService.getInstance());
                stage.setTitle("Finanger - Status");
                
                // 在场景切换后启动状态页面内部元素的动画效果
                AnimationUtils.animateStatusSceneEntrance(statusSceneObj);
                
                // 不再显示弹出框，改为顺畅的过渡体验
                // showAlert("Success", "Login Successful!");
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
            SceneManager.switchScene(stage, signUpScene, AnimationType.FADE);
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
        
        // 初始化时将控件设为透明
        titleLabel.setOpacity(0);
        userBox.setOpacity(0);
        passBox.setOpacity(0);
        forgotLink.setOpacity(0);
        loginButton.setOpacity(0);
        signUpBox.setOpacity(0);
        
        // 直接启动动画，无需等待鼠标移动事件
        // 使用Timeline确保组件完全加载后再执行动画
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(500), // 延迟500毫秒
            e -> {
                // 添加元素依次渐入效果
                AnimationUtils.fadeInNode(titleLabel, 100, 600);
                AnimationUtils.slideInNode(userBox, 200, 500, "RIGHT", 50);
                AnimationUtils.slideInNode(passBox, 300, 500, "RIGHT", 50);
                AnimationUtils.fadeInNode(forgotLink, 400, 400);
                AnimationUtils.slideInNode(loginButton, 500, 500, "UP", 30);
                AnimationUtils.fadeInNode(signUpBox, 600, 400);
            }
        ));
        timeline.setCycleCount(1);
        timeline.play();
    }

    /**
     * Dynamically recalculates the layout of all UI components based on the current window size.
     * This method is called whenever the window is resized to maintain proper proportions
     * and positioning of all visual elements.
     */
    private static void relayout() {
        // Current Scene dimensions
        double curWidth = root.getScene().getWidth();
        double curHeight = root.getScene().getHeight();

        // ========== 1) Recalculate leftPolygon vertices ==========
        leftPolygon.getPoints().setAll(
                LEFT_POLY_FRACTIONS[0]*curWidth, LEFT_POLY_FRACTIONS[1]*curHeight,
                LEFT_POLY_FRACTIONS[2]*curWidth, LEFT_POLY_FRACTIONS[3]*curHeight,
                LEFT_POLY_FRACTIONS[4]*curWidth, LEFT_POLY_FRACTIONS[5]*curHeight,
                LEFT_POLY_FRACTIONS[6]*curWidth, LEFT_POLY_FRACTIONS[7]*curHeight
        );

        // ========== 2) Recalculate rightPolygon vertices ==========
        rightPolygon.getPoints().setAll(
                RIGHT_POLY_FRACTIONS[0]*curWidth, RIGHT_POLY_FRACTIONS[1]*curHeight,
                RIGHT_POLY_FRACTIONS[2]*curWidth, RIGHT_POLY_FRACTIONS[3]*curHeight,
                RIGHT_POLY_FRACTIONS[4]*curWidth, RIGHT_POLY_FRACTIONS[5]*curHeight,
                RIGHT_POLY_FRACTIONS[6]*curWidth, RIGHT_POLY_FRACTIONS[7]*curHeight
        );

        // ========== 3) Recalculate rightPane position and size ==========
        // Original layoutX=440 => fraction=440/800=0.55
        double paneX = PANE_X_FRAC * curWidth; // left pos
        rightPane.setLayoutX(paneX);

        // Pane width = remaining space
        double paneWidth = curWidth - paneX;
        double paneHeight = curHeight; // Full height
        rightPane.setPrefSize(paneWidth, paneHeight);

        // Adjust VBox size accordingly
        vbox.setPrefSize(paneWidth, paneHeight);
        // Can be customized as needed, doesn't have to fill completely

        // (Optional) Text size can also scale:
        // double scale = curWidth / INITIAL_WIDTH;
        // titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36 * scale));
        // ...
    }

    /**
     * Displays an alert dialog with the specified title and message.
     *
     * @param title The title of the alert dialog
     * @param message The message to display in the alert dialog
     */
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}