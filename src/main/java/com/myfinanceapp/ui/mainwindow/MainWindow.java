package com.myfinanceapp.ui.mainwindow;

import com.myfinanceapp.ui.common.AnimationUtils;
import com.myfinanceapp.ui.common.SceneManager;
import com.myfinanceapp.ui.common.SceneManager.AnimationType;
import com.myfinanceapp.ui.loginscene.LoginScene;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import com.myfinanceapp.service.ThemeService;

/**
 * The main window of the Finanger application, featuring a dynamic split design with responsive layout.
 * This class extends JavaFX's Application class and serves as the entry point for the application.
 * The window features a diagonal split design that automatically adjusts based on window size.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class MainWindow extends Application {

    // Original design dimensions
    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    // UI controls & polygons
    private Group root;
    private Polygon whitePolygon;
    private Polygon bluePolygon;
    private Label welcomeLabel;
    private Label sloganLabel;
    private Button arrowButton;

    // =========== Original "ratio" data for each node ===========

    // White polygon (4 vertices), original coordinates: (0,0)->(500,0)->(300,450)->(0,450)
    // Converted to (xFrac,yFrac)
    private final double[] whitePolyFractions = {
            0.0, 0.0,
            500.0/INITIAL_WIDTH, 0.0,
            300.0/INITIAL_WIDTH, 450.0/INITIAL_HEIGHT,
            0.0, 450.0/INITIAL_HEIGHT
    };

    // Blue polygon (4 vertices), original: (500,0)->(800,0)->(800,450)->(300,450)
    private final double[] bluePolyFractions = {
            500.0/INITIAL_WIDTH, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            300.0/INITIAL_WIDTH, 1.0
    };

    // welcomeLabel original (x=50, y=80)
    private final double welcomeLabelXFrac = 50.0/INITIAL_WIDTH;
    private final double welcomeLabelYFrac = 80.0/INITIAL_HEIGHT;

    // sloganLabel original (x=50, y=150)
    private final double sloganLabelXFrac = 50.0/INITIAL_WIDTH;
    private final double sloganLabelYFrac = 150.0/INITIAL_HEIGHT;

    // arrowButton original (x=570, y=170)
    private final double arrowBtnXFrac = 570.0/INITIAL_WIDTH;
    private final double arrowBtnYFrac = 170.0/INITIAL_HEIGHT;

    // ThemeService instance
    private ThemeService themeService = new ThemeService();

    /**
     * Initializes and displays the main window of the application.
     * Sets up the initial window size based on screen resolution and creates the UI components.
     *
     * @param stage The primary stage for this application, onto which the application scene can be set
     */
    @Override
    public void start(Stage stage) {
        root = new Group();
        // Adjust initial window size to 80% of screen resolution
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();
        
        // Calculate appropriate window size
        double windowWidth = Math.min(1600, screenWidth * 0.8);
        double windowHeight = Math.min(900, screenHeight * 0.8);
 
        Scene scene = new Scene(root, windowWidth, windowHeight);

        stage.setTitle("Finanger - Welcome");
        stage.setScene(scene);
        stage.setResizable(true); // Allow resizing
        stage.setMinWidth(800);
        stage.setMinHeight(450);

        // Set window icon based on theme
        try {
            String iconPath = themeService.isDayMode() ? "/pictures/logo_day.png" : "/pictures/logo_night.png";
            javafx.scene.image.Image icon = new javafx.scene.image.Image(
                Objects.requireNonNull(getClass().getResource(iconPath)).toExternalForm()
            );
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("[Warning] Failed to load window icon: " + e.getMessage());
        }

        // Initialize UI
        initUI(stage);

        // Listen for Scene size changes, real-time rearrangement
        scene.widthProperty().addListener((obs, oldVal, newVal) -> relayout());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> relayout());

        // Initial layout
        relayout();

        stage.show();
        // 添加初始化动画
        AnimationUtils.animateMainWindowEntrance(scene);
    }

    /**
     * Initializes the UI components including polygons, labels, and buttons.
     * Sets up the visual elements and their event handlers.
     *
     * @param stage The primary stage for this application
     */
    private void initUI(Stage stage) {
        // ============ Create polygons ============
        whitePolygon = new Polygon();
        whitePolygon.setFill(Color.WHITE);

        bluePolygon = new Polygon();
        bluePolygon.setFill(Color.web("#A3D1FF"));

        root.getChildren().addAll(whitePolygon, bluePolygon);

        // ============ Label: "Welcome," ============
        welcomeLabel = new Label("Welcome,");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        welcomeLabel.setTextFill(Color.web("#1A57C3"));
        root.getChildren().add(welcomeLabel);

        // ============ Label: slogan ============
        sloganLabel = new Label("Finanger is your best\npersonal financial manager");
        sloganLabel.setFont(new Font("Lobster", 26));
        sloganLabel.setTextFill(Color.web("#1A57C3"));
        root.getChildren().add(sloganLabel);

        // ============ Button: "➤" ============
        arrowButton = new Button("➤");
        arrowButton.setFont(new Font(30));
        arrowButton.setTextFill(Color.web("#1A57C3"));
        arrowButton.setStyle("-fx-background-radius: 40; -fx-min-width: 60; -fx-min-height: 70;"
                + " -fx-background-color: #FFFFFF44;");

        arrowButton.setOnAction(e -> {
            // Disable button to prevent multiple clicks
            arrowButton.setDisable(true);
            
            try {
                // Create login scene, but don't show it yet
                Scene loginScene = LoginScene.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
                
                // Use fade animation directly instead of ripple
                SceneManager.switchScene(stage, loginScene, AnimationType.FADE);
                stage.setTitle("Finanger - Login");
            } catch (Exception ex) {
                System.err.println("Failed to switch to login page: " + ex.getMessage());
                ex.printStackTrace();
                // Re-enable button if error occurs
                arrowButton.setDisable(false);
            }
        });

        root.getChildren().add(arrowButton);
    }

    /**
     * Adds initialization animation effects to MainWindow
     *
     * @param scene The main window scene
     */


    /**
     * Dynamically recalculates the layout of all UI components based on the current window size.
     * This method is called whenever the window is resized to maintain proper proportions
     * and positioning of all visual elements.
     */

    private void relayout() {
        double curWidth = root.getScene().getWidth();
        double curHeight = root.getScene().getHeight();

        // ============ Recalculate white polygon coordinates ============
        whitePolygon.getPoints().setAll(
                whitePolyFractions[0] * curWidth, whitePolyFractions[1] * curHeight,
                whitePolyFractions[2] * curWidth, whitePolyFractions[3] * curHeight,
                whitePolyFractions[4] * curWidth, whitePolyFractions[5] * curHeight,
                whitePolyFractions[6] * curWidth, whitePolyFractions[7] * curHeight
        );

        // ============ Recalculate blue polygon coordinates ============
        bluePolygon.getPoints().setAll(
                bluePolyFractions[0] * curWidth, bluePolyFractions[1] * curHeight,
                bluePolyFractions[2] * curWidth, bluePolyFractions[3] * curHeight,
                bluePolyFractions[4] * curWidth, bluePolyFractions[5] * curHeight,
                bluePolyFractions[6] * curWidth, bluePolyFractions[7] * curHeight
        );

        // ============ Recalculate Label/button coordinates ============
        welcomeLabel.setLayoutX(welcomeLabelXFrac * curWidth);
        welcomeLabel.setLayoutY(welcomeLabelYFrac * curHeight);

        sloganLabel.setLayoutX(sloganLabelXFrac * curWidth);
        sloganLabel.setLayoutY(sloganLabelYFrac * curHeight);

        arrowButton.setLayoutX(arrowBtnXFrac * curWidth);
        arrowButton.setLayoutY(arrowBtnYFrac * curHeight);
    }

    /**
     * The main entry point for the application.
     * Launches the JavaFX application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Dynamically updates the window icon based on the current theme (static method for external calls)
     * @param stage The main window stage
     * @param themeService The current theme service instance
     */
    public static void updateWindowIcon(Stage stage, ThemeService themeService) {
        try {
            String iconPath = themeService.isDayMode() ? "/pictures/logo_day.png" : "/pictures/logo_night.png";
            javafx.scene.image.Image icon = new javafx.scene.image.Image(
                Objects.requireNonNull(MainWindow.class.getResource(iconPath)).toExternalForm()
            );
            stage.getIcons().clear();
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("[Warning] Failed to update window icon: " + e.getMessage());
        }
    }
}
