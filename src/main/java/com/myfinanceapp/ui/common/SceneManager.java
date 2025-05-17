package com.myfinanceapp.ui.common;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Utility class for managing scene transitions in the JavaFX application.
 * This class provides functionality for:
 * - Switching between scenes while maintaining window dimensions
 * - Ensuring consistent window size during scene transitions
 * - Providing various animated transitions between scenes
 *
 * @author SE_Group110
 * @version 4.0
 */
public class SceneManager {
    /**
     * Default animation duration in milliseconds
     */
    private static final int DEFAULT_ANIMATION_DURATION = 600;
    
    /**
     * Animation type enumeration
     */
    public enum AnimationType {
        NONE,       // No animation
        FADE,       // Fade in/out
        SLIDE_LEFT, // Slide from right to left
        SLIDE_RIGHT,// Slide from left to right
        SLIDE_UP,   // Slide from bottom to top
        SLIDE_DOWN, // Slide from top to bottom
        RIPPLE,     // Ripple effect
        ROTATE      // Rotation effect
    }
    
    /**
     * Switches to a new scene while preserving the current window dimensions.
     * This method ensures that the window size remains consistent during scene transitions
     * by explicitly setting the width and height before and after the scene change.
     *
     * @param stage The current application stage
     * @param newScene The new scene to display
     */
    public static void switchScene(Stage stage, Scene newScene) {
        switchScene(stage, newScene, AnimationType.NONE, DEFAULT_ANIMATION_DURATION);
    }
    
    /**
     * Switches to a new scene with specified animation type
     * 
     * @param stage The current stage
     * @param newScene The new scene to display
     * @param animationType The type of animation to use
     */
    public static void switchScene(Stage stage, Scene newScene, AnimationType animationType) {
        switchScene(stage, newScene, animationType, DEFAULT_ANIMATION_DURATION);
    }
    
    /**
     * Switches to a new scene with specified animation type and duration
     * 
     * @param stage The current stage
     * @param newScene The new scene to display
     * @param animationType The type of animation to use
     * @param duration The animation duration in milliseconds
     */
    /**
     * Switches to a new scene with specified animation type and duration, optimized for transition handling
     */
    public static void switchScene(Stage stage, Scene newScene, AnimationType animationType, int duration) {
        // Save current window dimensions and position
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        // Set window dimensions to ensure consistent size during animation
        stage.setWidth(width);
        stage.setHeight(height);

        // Simplify animation handling for status page transitions
        if (animationType == AnimationType.SLIDE_UP &&
                newScene.getRoot() instanceof BorderPane &&
                ((BorderPane)newScene.getRoot()).getCenter() instanceof ScrollPane) {

            // Use simplified fade animation for StatusScene to avoid performance issues
            newScene.getRoot().setOpacity(0);
            stage.setScene(newScene);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(duration), newScene.getRoot());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            return;
        }

        try {
            // Execute corresponding transition effect based on animation type
            switch (animationType) {
                case FADE:
                    AnimationUtils.fadeTransition(stage, newScene, duration);
                    break;
                case SLIDE_LEFT:
                    AnimationUtils.slideTransition(stage, newScene, duration, "LEFT");
                    break;
                case SLIDE_RIGHT:
                    AnimationUtils.slideTransition(stage, newScene, duration, "RIGHT");
                    break;
                case SLIDE_UP:
                    AnimationUtils.slideTransition(stage, newScene, duration, "UP");
                    break;
                case SLIDE_DOWN:
                    AnimationUtils.slideTransition(stage, newScene, duration, "DOWN");
                    break;
                case NONE:
                default:
                    stage.setScene(newScene);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Scene transition animation failed, switching directly: " + e.getMessage());
            stage.setScene(newScene);
        }

        // Ensure window dimensions and position remain unchanged
        Platform.runLater(() -> {
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);
        });
    }
} 