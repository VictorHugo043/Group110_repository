package com.myfinanceapp.ui.common;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class for managing scene transitions in the JavaFX application.
 * This class provides functionality for:
 * - Switching between scenes while maintaining window dimensions
 * - Ensuring consistent window size during scene transitions
 */
public class SceneManager {
    /**
     * Switches to a new scene while preserving the current window dimensions.
     * This method ensures that the window size remains consistent during scene transitions
     * by explicitly setting the width and height before and after the scene change.
     *
     * @param stage The current application stage
     * @param newScene The new scene to display
     */
    public static void switchScene(Stage stage, Scene newScene) {
        // 保存当前尺寸
        double width = stage.getWidth();
        double height = stage.getHeight();
        
        // 先设置窗口尺寸
        stage.setWidth(width);
        stage.setHeight(height);
        
        // 然后设置新场景
        stage.setScene(newScene);
        
        // 再次确保窗口尺寸
        stage.setWidth(width);
        stage.setHeight(height);
    }
} 