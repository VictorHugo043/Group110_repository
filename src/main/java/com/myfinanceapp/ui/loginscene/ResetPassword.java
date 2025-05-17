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

/**
 * A password reset interface for the Finanger application.
 * This scene provides functionality for users to reset their password using security questions.
 * It features a responsive layout with a diagonal split design that automatically adjusts
 * based on window size.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class ResetPassword {

    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    // Polygon related
    private static Group root;
    private static Polygon leftPolygon;
    private static Polygon rightPolygon;
    private static Pane rightPane;
    private static VBox vbox;
    private static Button backBtn;

    // Proportional arrays
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

    // Input controls
    private static TextField usernameField;
    private static Label questionLabel;
    private static TextField answerField;
    private static PasswordField newPasswordField;

    // Buttons
    private static Button checkQuestionBtn;
    private static Button resetPassBtn;

    // Found user object (cached)
    private static User foundUser = null;

    /**
     * Creates and returns a password reset scene.
     * The scene includes fields for username, security question, and new password.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @return A configured Scene object for the password reset interface
     */
    public static Scene createScene(Stage stage, double width, double height) {
        root = new Group();
        Scene scene = new Scene(root, width, height);

        stage.setMinWidth(INITIAL_WIDTH);
        stage.setMinHeight(INITIAL_HEIGHT);
        stage.setResizable(true);

        // ========== 1) Create back button and add to root ==========
        backBtn = new Button("back");
        backBtn.setStyle(
                "-fx-background-color: #A3D1FF;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;"+
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);" // 添加阴影效果
        );
        backBtn.setPrefSize(60, 30);
        double initialX = Math.min(width - 80, INITIAL_WIDTH - 80);
        double initialY = 20;
        backBtn.setLayoutX(initialX);
        backBtn.setLayoutY(initialY);


        // Return to login interface when clicked
        backBtn.setOnAction(e->{
            stage.setScene(LoginScene.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight()));
            stage.setTitle("Finanger - Login");
        });

        // =========== Left background polygon ===========
        leftPolygon = new Polygon();
        Image leftBg = new Image(
                Objects.requireNonNull(ResetPassword.class.getResource("/pictures/resetbg.png")).toExternalForm()
        );
        leftPolygon.setFill(new ImagePattern(leftBg, 0, 0, 1, 1, true));

        rightPolygon = new Polygon();
        rightPolygon.setFill(Color.web("#93D2F3"));

        root.getChildren().addAll(leftPolygon, rightPolygon);

        // =========== Right Pane + VBox ===========
        rightPane = new Pane();
        root.getChildren().add(rightPane);

        vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));
        rightPane.getChildren().add(vbox);

        // =========== Title area ===========
        Label titleLabel = new Label("Reset Password");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);

        Label subLabel = new Label("To reset your password, please answer the\nsecurity question you set during registration.");
        subLabel.setFont(Font.font("Arial", 14));
        subLabel.setTextFill(Color.WHITE);

        VBox titleBox = new VBox(5, titleLabel, subLabel);
        titleBox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(titleBox);

        // =========== Step 1: Enter Username, click [Check Question] ===========

        // Username row (single line: label + textfield)
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

        // =========== Step 2: Display security question + input answer & new password ===========

        // Security question row (two lines: label, then question text)
        Label questionLbl = new Label("Your Security Question:");
        questionLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        questionLbl.setTextFill(Color.WHITE);

        questionLabel = new Label("(Not loaded yet)");
        questionLabel.setFont(Font.font("Arial", 12));
        questionLabel.setTextFill(Color.WHITE);

        VBox questionContainer = new VBox(5, questionLbl, questionLabel);
        questionContainer.setAlignment(Pos.CENTER_LEFT);

        // Answer row (two lines: label, then textfield)
        Label ansLbl = new Label("Your answer:");
        ansLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ansLbl.setTextFill(Color.WHITE);
        answerField = new TextField();
        answerField.setPrefWidth(180);

        VBox answerContainer = new VBox(5, ansLbl, answerField);
        answerContainer.setAlignment(Pos.CENTER_LEFT);

        // New password row (two lines: label, then textfield)
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

        // Initially disable question, answer, newPass, resetBtn
        questionLabel.setDisable(true);
        answerField.setDisable(true);
        newPasswordField.setDisable(true);
        resetPassBtn.setDisable(true);

        // Assemble components
        vbox.getChildren().addAll(
                userHBox,
                checkQuestionBtn,
                questionContainer,
                answerContainer,
                newPassContainer,
                resetPassBtn
        );
        // After all polygons and Pane are added:
        root.getChildren().add(backBtn);
        backBtn.toFront();

        // Listen for scene size changes, dynamically rearrange
        scene.widthProperty().addListener((obs,oldV,newV)-> relayout());
        scene.heightProperty().addListener((obs,oldV,newV)-> relayout());

        // After scene is set up, delay one frame to get backBtn's real width
        Platform.runLater(ResetPassword::relayout);

        return scene;
    }

    /**
     * Handles the "Check Question" button click event.
     * Validates the username and retrieves the user's security question.
     * If successful, enables the answer and new password fields.
     */
    private static void handleCheckQuestion() {
        String uname = usernameField.getText().trim();
        if (uname.isEmpty()) {
            showAlert("Error", "Username cannot be empty!");
            return;
        }

        UserService userService = new UserService();

        // First get user object by username
        User userByUsername = userService.findUserByUsername(uname);
        if (userByUsername == null) {
            showAlert("Error", "User not found!");
            return;
        }

        // Get UID, then use UID to find complete user information
        String uid = userByUsername.getUid();
        foundUser = userService.findUserByUid(uid);
        if (foundUser == null) {
            showAlert("Error", "User not found!");
            return;
        }

        // Display security question
        questionLabel.setText(foundUser.getSecurityQuestion());
        questionLabel.setDisable(false);
        answerField.setDisable(false);
        newPasswordField.setDisable(false);
        resetPassBtn.setDisable(false);
    }

    /**
     * Handles the password reset process.
     * Validates the security answer and updates the user's password if correct.
     *
     * @param stage The stage to display the scene
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

        // Compare security answer
        if (!answer.equalsIgnoreCase(foundUser.getSecurityAnswer())) {
            showAlert("Error", "Security answer mismatch!");
            return;
        }

        // Match successful => reset password
        foundUser.setPassword(newPass);
        UserService userService = new UserService();
        boolean updated = userService.updatePassword(foundUser.getUid(), newPass); // Needs implementation
        if (updated) {
            showAlert("Success", "Password reset successfully!");
            // Return to login interface
            stage.setScene(LoginScene.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight()));
        } else {
            showAlert("Error", "Failed to reset password!");
        }
    }

    /**
     * Dynamically recalculates the layout of all UI components based on the current window size.
     * This method is called whenever the window is resized to maintain proper proportions
     * and positioning of all visual elements.
     */
    private static void relayout() {
        double curW = root.getScene().getWidth();
        double curH = root.getScene().getHeight();

        // Polygons
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

        // Place backBtn
        final double MARGIN = 20.0;

        // 确保按钮位置在可见区域内
        backBtn.setLayoutX(Math.min(curW - backBtn.getPrefWidth() - MARGIN, curW - 70));
        backBtn.setLayoutY(MARGIN);

        // 设置按钮的样式使其更加明显
        backBtn.setStyle(
                "-fx-background-color: #A3D1FF;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);" // 添加阴影效果
        );

        // 确保按钮保持在最上层
        backBtn.toFront();
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
