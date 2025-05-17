package com.myfinanceapp.ui.common;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;

/**
 * Utility class providing elegant scene transition animations.
 * Includes various transition effects such as fade, slide, scale, and more.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class AnimationUtils {

    /**
     * Creates a fade transition to the new scene
     * 
     * @param stage The stage
     * @param newScene The new scene to transition to
     * @param duration Animation duration in milliseconds
     */
    public static void fadeTransition(Stage stage, Scene newScene, int duration) {
        Scene oldScene = stage.getScene();
        
        // Set initial opacity of new scene to 0
        newScene.getRoot().setOpacity(0);
        
        // Create fade out animation for old scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(duration), oldScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> {
            // Switch to new scene
            stage.setScene(newScene);
            
            // Create fade in animation for new scene
            FadeTransition fadeIn = new FadeTransition(Duration.millis(duration), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        
        fadeOut.play();
    }
    
    /**
     * Creates a slide transition animation
     * 
     * @param stage The stage
     * @param newScene The new scene to transition to
     * @param duration Animation duration in milliseconds
     * @param direction Direction of slide ("LEFT", "RIGHT", "UP", "DOWN")
     */
    public static void slideTransition(Stage stage, Scene newScene, int duration, String direction) {
        Scene oldScene = stage.getScene();
        double width = oldScene.getWidth();
        double height = oldScene.getHeight();
        
        // Create a container to hold both scenes
        Pane container = new Pane();
        
        // Add old scene to container
        Node oldRoot = oldScene.getRoot();
        container.getChildren().add(oldRoot);
        
        // Add new scene to container
        Node newRoot = newScene.getRoot();
        container.getChildren().add(newRoot);
        
        // Ensure both scenes match current window size
        oldRoot.resize(width, height);
        newRoot.resize(width, height);
        
        // Temporary transition scene
        Scene transitionScene = new Scene(container, width, height);
        
        // Set initial position based on slide direction
        switch (direction) {
            case "LEFT": // New scene slides from right to left
                newRoot.setTranslateX(width);
                break;
            case "RIGHT": // New scene slides from left to right
                newRoot.setTranslateX(-width);
                break;
            case "UP": // New scene slides from bottom to top
                newRoot.setTranslateY(height);
                break;
            case "DOWN": // New scene slides from top to bottom
                newRoot.setTranslateY(-height);
                break;
        }
        
        // Switch to transition scene
        stage.setScene(transitionScene);
        
        // Create animations
        TranslateTransition oldTransition = new TranslateTransition(Duration.millis(duration), oldRoot);
        TranslateTransition newTransition = new TranslateTransition(Duration.millis(duration), newRoot);
        
        // Set animation target positions
        switch (direction) {
            case "LEFT":
                oldTransition.setToX(-width);
                newTransition.setToX(0);
                break;
            case "RIGHT":
                oldTransition.setToX(width);
                newTransition.setToX(0);
                break;
            case "UP":
                oldTransition.setToY(-height);
                newTransition.setToY(0);
                break;
            case "DOWN":
                oldTransition.setToY(height);
                newTransition.setToY(0);
                break;
        }
        
        // Create parallel animation
        ParallelTransition transition = new ParallelTransition(oldTransition, newTransition);
        
        // After animation completes, switch to new scene
        transition.setOnFinished(e -> {
            // Reset new scene position and size
            newRoot.setTranslateX(0);
            newRoot.setTranslateY(0);
            
            // Final switch to new scene
            stage.setScene(newScene);
            
            // Ensure new scene root fills entire scene
            newRoot.resize(width, height);
            newRoot.autosize();
        });
        
        // Play animation
        transition.play();
    }
    
    /**
     * Creates a radial ripple animation effect
     * 
     * @param stage The stage
     * @param newScene The new scene to transition to
     * @param duration Animation duration in milliseconds
     */
    public static void rippleTransition(Stage stage, Scene newScene, int duration) {
        Scene oldScene = stage.getScene();
        
        // Save dimensions of old and new scenes
        double width = oldScene.getWidth();
        double height = oldScene.getHeight();
        
        // Copy background color from old scene to new scene to prevent flickering
        Pane oldRoot = (Pane) oldScene.getRoot();
        
        // Set initial state of new scene
        newScene.getRoot().setScaleX(0.1);
        newScene.getRoot().setScaleY(0.1);
        newScene.getRoot().setOpacity(0.1);
        
        // Create container to hold both scene root nodes
        Pane container = new Pane();
        container.getChildren().add(oldScene.getRoot());
        container.getChildren().add(newScene.getRoot());
        
        // Ensure new scene is in correct position
        newScene.getRoot().setLayoutX((width / 2) - (width * 0.1 / 2));
        newScene.getRoot().setLayoutY((height / 2) - (height * 0.1 / 2));
        
        // Create transition scene
        Scene transitionScene = new Scene(container, width, height);
        stage.setScene(transitionScene);
        
        // Create scale animation
        ScaleTransition scale = new ScaleTransition(Duration.millis(duration), newScene.getRoot());
        scale.setFromX(0.1);
        scale.setFromY(0.1);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        // Create fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(duration), newScene.getRoot());
        fadeIn.setFromValue(0.1);
        fadeIn.setToValue(1.0);
        
        // Create fade out animation for old scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(duration), oldScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        // Create center position animation for new scene
        TranslateTransition translate = new TranslateTransition(Duration.millis(duration), newScene.getRoot());
        translate.setFromX((width / 2) - (width * 0.1 / 2));
        translate.setFromY((height / 2) - (height * 0.1 / 2));
        translate.setToX(0);
        translate.setToY(0);
        
        // Execute all animations in parallel
        ParallelTransition transition = new ParallelTransition(scale, fadeIn, fadeOut, translate);
        
        // Switch to final scene after animation completes
        transition.setOnFinished(e -> {
            newScene.getRoot().setScaleX(1.0);
            newScene.getRoot().setScaleY(1.0);
            newScene.getRoot().setOpacity(1.0);
            newScene.getRoot().setTranslateX(0);
            newScene.getRoot().setTranslateY(0);
            newScene.getRoot().setLayoutX(0);
            newScene.getRoot().setLayoutY(0);
            stage.setScene(newScene);
        });
        
        // Start animation
        transition.play();
    }
    
    /**
     * Creates a rotation fade-in animation
     * 
     * @param stage The stage
     * @param newScene The new scene to transition to
     * @param duration Animation duration in milliseconds
     */
    public static void rotateTransition(Stage stage, Scene newScene, int duration) {
        newScene.getRoot().setOpacity(0);
        newScene.getRoot().setRotate(-90);
        
        // Set new scene
        stage.setScene(newScene);
        
        // Create rotation animation
        RotateTransition rotate = new RotateTransition(Duration.millis(duration), newScene.getRoot());
        rotate.setFromAngle(-90);
        rotate.setToAngle(0);
        
        // Create fade in animation
        FadeTransition fade = new FadeTransition(Duration.millis(duration), newScene.getRoot());
        fade.setFromValue(0);
        fade.setToValue(1);
        
        // Play animations in parallel
        ParallelTransition transition = new ParallelTransition(rotate, fade);
        transition.play();
    }
    
    /**
     * Creates a fade-in animation for a single node
     * 
     * @param node The node to animate
     * @param delay Delay time in milliseconds
     * @param duration Animation duration in milliseconds
     */
    public static void fadeInNode(Node node, int delay, int duration) {
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setDelay(Duration.millis(delay));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
    
    /**
     * Creates a slide-in animation for a single node
     * 
     * @param node The node to animate
     * @param delay Delay time in milliseconds
     * @param duration Animation duration in milliseconds
     * @param direction Direction of slide ("LEFT", "RIGHT", "UP", "DOWN")
     * @param distance Distance to slide
     */
    public static void slideInNode(Node node, int delay, int duration, String direction, double distance) {
        double originalX = node.getTranslateX();
        double originalY = node.getTranslateY();
        
        switch (direction) {
            case "LEFT":
                node.setTranslateX(originalX - distance);
                break;
            case "RIGHT":
                node.setTranslateX(originalX + distance);
                break;
            case "UP":
                node.setTranslateY(originalY - distance);
                break;
            case "DOWN":
                node.setTranslateY(originalY + distance);
                break;
        }
        
        node.setOpacity(0);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(duration), node);
        translate.setDelay(Duration.millis(delay));
        translate.setToX(originalX);
        translate.setToY(originalY);
        
        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setDelay(Duration.millis(delay));
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ParallelTransition transition = new ParallelTransition(translate, fade);
        transition.play();
    }

    /**
     * Animates the entrance of the status scene
     * 
     * @param scene The scene to animate
     */
    public static void animateStatusSceneEntrance(Scene scene) {
        // First ensure scene root is immediately visible without blocking UI thread
        Platform.runLater(() -> {
            scene.getRoot().setOpacity(1);

            // Use shorter delay to ensure scene is fully rendered
            Timeline delayTimeline = new Timeline(new KeyFrame(
                    Duration.millis(50),
                    event -> {
                        try {
                            // Make nodes directly visible without complex animations
                            if (scene.getRoot() instanceof BorderPane) {
                                BorderPane root = (BorderPane) scene.getRoot();

                                // Ensure sidebar is visible
                                if (root.getLeft() != null) {
                                    root.getLeft().setOpacity(1);
                                }

                                // Ensure center content is visible
                                if (root.getCenter() != null) {
                                    root.getCenter().setOpacity(1);
                                }
                            }

                            // Refresh layout
                            scene.getRoot().layout();
                        } catch (Exception e) {
                            System.err.println("Failed to display status page: " + e.getMessage());
                            scene.getRoot().setOpacity(1);
                        }
                    }
            ));
            delayTimeline.setCycleCount(1);
            delayTimeline.play();
        });
    }

    /**
     * Animates the entrance of the main window
     * 
     * @param scene The scene to animate
     */
    public static void animateMainWindowEntrance(Scene scene) {
        if (scene == null || scene.getRoot() == null) return;

        // Set root node initially invisible
        scene.getRoot().setOpacity(0);

        // Create fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), scene.getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Play animation after scene loads
        Platform.runLater(() -> {
            fadeIn.play();
        });
    }
} 