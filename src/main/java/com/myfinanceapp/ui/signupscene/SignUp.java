package com.myfinanceapp.ui.signupscene;

import com.myfinanceapp.ui.common.SceneManager;
import com.myfinanceapp.ui.registrationterms.PrivacyPolicy;
import com.myfinanceapp.ui.registrationterms.TermofUse;
import com.myfinanceapp.ui.usermanual.UserManual; // Import the new UserManual scene
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

/**
 * A registration interface for the Finanger application.
 * This scene provides a user-friendly sign-up process with:
 * - Username and password creation
 * - Security question setup
 * - Terms of use and privacy policy acceptance
 * The interface features a split design with background image,
 * responsive layout, and dynamic resizing capabilities.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class SignUp {

    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    private static Group root;
    private static Polygon leftPolygon;
    private static Polygon rightPolygon;
    private static Pane rightPane;
    private static VBox vbox;

    // Convert fixed coordinates to proportions (relative to 800×450)
    // Left polygon: (0,0), (480,0), (280,450), (0,450)
    private static final double[] LEFT_POLY_FRACS = {
            0.0, 0.0,
            480.0 / INITIAL_WIDTH, 0.0,
            280.0 / INITIAL_WIDTH, 1.0,
            0.0, 1.0
    };
    // Right polygon: (480,0), (800,0), (800,450), (280,450)
    private static final double[] RIGHT_POLY_FRACS = {
            480.0 / INITIAL_WIDTH, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            280.0 / INITIAL_WIDTH, 1.0
    };

    // rightPane originally layoutX = 440, i.e., 440/800 = 0.55
    private static final double RIGHT_PANE_X_FRAC = 440.0 / INITIAL_WIDTH;

    // User input controls (no need to recalculate coordinates as they are automatically arranged in VBox)
    private static TextField usernameField;
    private static PasswordField passwordField;
    private static CheckBox agreeCheckBox;
    private static ComboBox<String> securityQuestionCombo;
    private static TextField securityAnswerField;

    /**
     * Creates and returns a sign-up scene with the specified dimensions.
     * The scene includes user registration form and navigation controls.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @return A configured Scene object for the sign-up interface
     */
    public static Scene createScene(Stage stage, double width, double height) {
        root = new Group();
        Scene scene = new Scene(root, width, height);

        // Set minimum window dimensions
        stage.setMinWidth(INITIAL_WIDTH);
        stage.setMinHeight(INITIAL_HEIGHT);
        stage.setResizable(true);

        // === Left and right polygons ===
        leftPolygon = new Polygon();
        Image bgImage = new Image(Objects.requireNonNull(SignUp.class.getResource("/pictures/signupbg.png")).toExternalForm());
        leftPolygon.setFill(new ImagePattern(bgImage, 0, 0, 1, 1, true));

        rightPolygon = new Polygon();
        rightPolygon.setFill(Color.web("#93D2F3"));

        root.getChildren().addAll(leftPolygon, rightPolygon);

        // === Right Pane and VBox ===
        rightPane = new Pane();
        root.getChildren().add(rightPane);

        vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));
        rightPane.getChildren().add(vbox);

        // === Title area ===
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

        // === Username and password input area ===
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

        // Prepare common security questions
        securityQuestionCombo = new ComboBox<>();
        securityQuestionCombo.getItems().addAll(
                "What is your favorite book?",
                "What was the name of your first pet?",
                "What is your best friend's name?",
                "What city were you born in?"
        );
        securityQuestionCombo.setValue("What is your favorite book?"); // Default

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

        // Insert after vbox.getChildren().addAll(userBox, passBox)
        vbox.getChildren().addAll(questionContainer, answerContainer);

        // === Next button and agreements ===
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
            String secAnswer = securityAnswerField.getText();
            if (!agreeCheckBox.isSelected()) {
                showAlert("Error", "You must agree to the Terms of use and Privacy Policy to register!");
                return;
            }
            if (user.isEmpty() || pass.isEmpty() || secAnswer.isEmpty()) {
                showAlert("Error", "Fields cannot be empty!");
                return;
            }
            UserService userService = new UserService();
            boolean success = userService.registerUser(user, pass, secQuestion, secAnswer);
            if (success) {
                showAlert("Success", "User registered successfully!");
                // Redirect to UserManual scene instead of LoginScene
                Scene userManualScene = UserManual.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
                SceneManager.switchScene(stage, userManualScene);  // Use SceneManager instead of direct setting
                stage.setTitle("Finanger - User Manual");
            } else {
                showAlert("Error", "Username already exists!");
            }
        });

        Hyperlink termsLink = new Hyperlink("Terms of use");
        termsLink.setFont(Font.font("Arial", 11));
        termsLink.setTextFill(Color.DARKBLUE);
        termsLink.setOnAction(e -> {
            Scene termsScene = TermofUse.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
            stage.setScene(termsScene);
            stage.setTitle("Terms of Use");
        });

        Hyperlink privacyLink = new Hyperlink("Privacy Policy");
        privacyLink.setFont(Font.font("Arial", 11));
        privacyLink.setTextFill(Color.DARKBLUE);
        privacyLink.setOnAction(e -> {
            Scene policyScene = PrivacyPolicy.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
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
            Scene loginScene = LoginScene.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
            stage.setScene(loginScene);
            stage.setTitle("Finanger - Login");
        });

        vbox.getChildren().addAll(nextBtn, agreeBox, alreadyLink);

        // Add dynamic layout listener
        scene.widthProperty().addListener((obs, oldVal, newVal) -> relayout());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> relayout());
        relayout();

        return scene;
    }

    /**
     * Dynamically recalculates the layout of UI components based on the current window size.
     * This method ensures proper positioning and scaling of all elements.
     */
    private static void relayout() {
        double curWidth = root.getScene().getWidth();
        double curHeight = root.getScene().getHeight();

        // Recalculate left polygon coordinates
        leftPolygon.getPoints().setAll(
                LEFT_POLY_FRACS[0] * curWidth, LEFT_POLY_FRACS[1] * curHeight,
                LEFT_POLY_FRACS[2] * curWidth, LEFT_POLY_FRACS[3] * curHeight,
                LEFT_POLY_FRACS[4] * curWidth, LEFT_POLY_FRACS[5] * curHeight,
                LEFT_POLY_FRACS[6] * curWidth, LEFT_POLY_FRACS[7] * curHeight
        );

        // Recalculate right polygon coordinates
        rightPolygon.getPoints().setAll(
                RIGHT_POLY_FRACS[0] * curWidth, RIGHT_POLY_FRACS[1] * curHeight,
                RIGHT_POLY_FRACS[2] * curWidth, RIGHT_POLY_FRACS[3] * curHeight,
                RIGHT_POLY_FRACS[4] * curWidth, RIGHT_POLY_FRACS[5] * curHeight,
                RIGHT_POLY_FRACS[6] * curWidth, RIGHT_POLY_FRACS[7] * curHeight
        );

        // Recalculate rightPane position and size
        double paneX = RIGHT_PANE_X_FRAC * curWidth;
        rightPane.setLayoutX(paneX);
        rightPane.setPrefSize(curWidth - paneX, curHeight);
        vbox.setPrefSize(rightPane.getPrefWidth(), rightPane.getPrefHeight());
    }

    /**
     * Displays an alert dialog with the specified title and message.
     * Used for showing feedback to users during the registration process.
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