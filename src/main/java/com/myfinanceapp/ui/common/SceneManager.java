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
     * 默认动画持续时间（毫秒）
     */
    private static final int DEFAULT_ANIMATION_DURATION = 600;
    
    /**
     * 动画类型枚举
     */
    public enum AnimationType {
        NONE,       // 无动画
        FADE,       // 淡入淡出
        SLIDE_LEFT, // 从右向左滑动
        SLIDE_RIGHT,// 从左向右滑动
        SLIDE_UP,   // 从下向上滑动
        SLIDE_DOWN, // 从上向下滑动
        RIPPLE,     // 波纹展开
        ROTATE      // 旋转进入
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
     * 使用指定的动画类型切换场景
     * 
     * @param stage 当前舞台
     * @param newScene 新场景
     * @param animationType 动画类型
     */
    public static void switchScene(Stage stage, Scene newScene, AnimationType animationType) {
        switchScene(stage, newScene, animationType, DEFAULT_ANIMATION_DURATION);
    }
    
    /**
     * 使用指定的动画类型和持续时间切换场景
     * 
     * @param stage 当前舞台
     * @param newScene 新场景
     * @param animationType 动画类型
     * @param duration 动画持续时间（毫秒）
     */
    /**
     * 使用指定的动画类型和持续时间切换场景，优化过渡动画处理
     */
    public static void switchScene(Stage stage, Scene newScene, AnimationType animationType, int duration) {
        // 保存当前窗口尺寸和位置
        double width = stage.getWidth();
        double height = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        // 设置窗口尺寸，确保动画过程中尺寸不变
        stage.setWidth(width);
        stage.setHeight(height);

        // 简化切换到状态页面时的动画处理
        if (animationType == AnimationType.SLIDE_UP &&
                newScene.getRoot() instanceof BorderPane &&
                ((BorderPane)newScene.getRoot()).getCenter() instanceof ScrollPane) {

            // 对StatusScene使用简化的淡入动画，避免复杂动画导致卡顿
            newScene.getRoot().setOpacity(0);
            stage.setScene(newScene);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(duration), newScene.getRoot());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            return;
        }

        try {
            // 根据动画类型执行相应的切换效果
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
            System.err.println("场景切换动画失败，直接切换: " + e.getMessage());
            stage.setScene(newScene);
        }

        // 确保窗口尺寸和位置保持不变
        Platform.runLater(() -> {
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);
        });
    }
} 