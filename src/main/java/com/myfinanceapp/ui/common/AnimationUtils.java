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
 * 提供优雅的场景切换动画效果工具类
 * 包含各种过渡动画，如淡入淡出、滑动、缩放等
 */
public class AnimationUtils {

    /**
     * 创建淡入淡出动画过渡到新场景
     * 
     * @param stage 舞台
     * @param newScene 新场景
     * @param duration 动画持续时间(毫秒)
     */
    public static void fadeTransition(Stage stage, Scene newScene, int duration) {
        Scene oldScene = stage.getScene();
        
        // 设置新场景初始透明度为0
        newScene.getRoot().setOpacity(0);
        
        // 创建旧场景淡出动画
        FadeTransition fadeOut = new FadeTransition(Duration.millis(duration), oldScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> {
            // 切换到新场景
            stage.setScene(newScene);
            
            // 创建新场景淡入动画
            FadeTransition fadeIn = new FadeTransition(Duration.millis(duration), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        
        fadeOut.play();
    }
    
    /**
     * 创建滑动过渡动画
     * 
     * @param stage 舞台
     * @param newScene 新场景
     * @param duration 动画持续时间(毫秒)
     * @param direction 方向 ("LEFT", "RIGHT", "UP", "DOWN")
     */
    public static void slideTransition(Stage stage, Scene newScene, int duration, String direction) {
        Scene oldScene = stage.getScene();
        double width = oldScene.getWidth();
        double height = oldScene.getHeight();
        
        // 创建一个容器来容纳两个场景
        Pane container = new Pane();
        
        // 添加旧场景到容器中
        Node oldRoot = oldScene.getRoot();
        container.getChildren().add(oldRoot);
        
        // 添加新场景到容器中
        Node newRoot = newScene.getRoot();
        container.getChildren().add(newRoot);
        
        // 确保新老场景大小都与当前窗口匹配
        oldRoot.resize(width, height);
        newRoot.resize(width, height);
        
        // 临时过渡场景
        Scene transitionScene = new Scene(container, width, height);
        
        // 根据滑动方向设置初始位置
        switch (direction) {
            case "LEFT": // 新场景从右向左滑动
                newRoot.setTranslateX(width);
                break;
            case "RIGHT": // 新场景从左向右滑动
                newRoot.setTranslateX(-width);
                break;
            case "UP": // 新场景从下向上滑动
                newRoot.setTranslateY(height);
                break;
            case "DOWN": // 新场景从上向下滑动
                newRoot.setTranslateY(-height);
                break;
        }
        
        // 切换到过渡场景
        stage.setScene(transitionScene);
        
        // 创建动画
        TranslateTransition oldTransition = new TranslateTransition(Duration.millis(duration), oldRoot);
        TranslateTransition newTransition = new TranslateTransition(Duration.millis(duration), newRoot);
        
        // 设置动画目标位置
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
        
        // 创建并行动画
        ParallelTransition transition = new ParallelTransition(oldTransition, newTransition);
        
        // 动画结束后，切换到新场景
        transition.setOnFinished(e -> {
            // 重置新场景的位置和大小
            newRoot.setTranslateX(0);
            newRoot.setTranslateY(0);
            
            // 最终切换到新场景
            stage.setScene(newScene);
            
            // 确保新场景的根节点填满整个场景
            newRoot.resize(width, height);
            newRoot.autosize();
        });
        
        // 播放动画
        transition.play();
    }
    
    /**
     * 创建径向波纹动画效果
     * 
     * @param stage 舞台
     * @param newScene 新场景
     * @param duration 动画持续时间(毫秒)
     */
    public static void rippleTransition(Stage stage, Scene newScene, int duration) {
        Scene oldScene = stage.getScene();
        
        // 保存旧场景和新场景的尺寸
        double width = oldScene.getWidth();
        double height = oldScene.getHeight();
        
        // 从旧场景复制背景颜色到新场景，防止闪烁
        Pane oldRoot = (Pane) oldScene.getRoot();
        
        // 设置新场景初始状态
        newScene.getRoot().setScaleX(0.1);
        newScene.getRoot().setScaleY(0.1);
        newScene.getRoot().setOpacity(0.1);
        
        // 创建一个容器来保存两个场景的根节点
        Pane container = new Pane();
        container.getChildren().add(oldScene.getRoot());
        container.getChildren().add(newScene.getRoot());
        
        // 确保新场景在正确的位置
        newScene.getRoot().setLayoutX((width / 2) - (width * 0.1 / 2));
        newScene.getRoot().setLayoutY((height / 2) - (height * 0.1 / 2));
        
        // 创建过渡场景
        Scene transitionScene = new Scene(container, width, height);
        stage.setScene(transitionScene);
        
        // 创建缩放动画
        ScaleTransition scale = new ScaleTransition(Duration.millis(duration), newScene.getRoot());
        scale.setFromX(0.1);
        scale.setFromY(0.1);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        // 创建淡入动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(duration), newScene.getRoot());
        fadeIn.setFromValue(0.1);
        fadeIn.setToValue(1.0);
        
        // 创建旧场景淡出动画
        FadeTransition fadeOut = new FadeTransition(Duration.millis(duration), oldScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        // 创建新场景位置居中动画
        TranslateTransition translate = new TranslateTransition(Duration.millis(duration), newScene.getRoot());
        translate.setFromX((width / 2) - (width * 0.1 / 2));
        translate.setFromY((height / 2) - (height * 0.1 / 2));
        translate.setToX(0);
        translate.setToY(0);
        
        // 并行执行所有动画
        ParallelTransition transition = new ParallelTransition(scale, fadeIn, fadeOut, translate);
        
        // 动画完成后切换到最终场景
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
        
        // 启动动画
        transition.play();
    }
    
    /**
     * 旋转淡入动画
     * 
     * @param stage 舞台
     * @param newScene 新场景
     * @param duration 动画持续时间(毫秒)
     */
    public static void rotateTransition(Stage stage, Scene newScene, int duration) {
        newScene.getRoot().setOpacity(0);
        newScene.getRoot().setRotate(-90);
        
        // 设置新场景
        stage.setScene(newScene);
        
        // 创建旋转动画
        RotateTransition rotate = new RotateTransition(Duration.millis(duration), newScene.getRoot());
        rotate.setFromAngle(-90);
        rotate.setToAngle(0);
        
        // 创建淡入动画
        FadeTransition fade = new FadeTransition(Duration.millis(duration), newScene.getRoot());
        fade.setFromValue(0);
        fade.setToValue(1);
        
        // 并行播放动画
        ParallelTransition transition = new ParallelTransition(rotate, fade);
        transition.play();
    }
    
    /**
     * 为单个节点创建淡入动画
     * 
     * @param node 需要动画的节点
     * @param delay 延迟时间(毫秒)
     * @param duration 动画持续时间(毫秒)
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
     * 为单个节点创建滑入动画
     * 
     * @param node 需要动画的节点
     * @param delay 延迟时间(毫秒)
     * @param duration 动画持续时间(毫秒)
     * @param direction 方向 ("LEFT", "RIGHT", "UP", "DOWN")
     * @param distance 移动距离
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

    public static void animateStatusSceneEntrance(Scene scene) {
        // 首先确保场景根节点立即可见，不阻塞UI线程
        Platform.runLater(() -> {
            scene.getRoot().setOpacity(1);

            // 使用更短的延迟，确保场景已经完全渲染
            Timeline delayTimeline = new Timeline(new KeyFrame(
                    Duration.millis(50),
                    event -> {
                        try {
                            // 使节点直接可见，不应用复杂动画
                            if (scene.getRoot() instanceof BorderPane) {
                                BorderPane root = (BorderPane) scene.getRoot();

                                // 确保侧边栏可见
                                if (root.getLeft() != null) {
                                    root.getLeft().setOpacity(1);
                                }

                                // 确保中心内容可见
                                if (root.getCenter() != null) {
                                    root.getCenter().setOpacity(1);
                                }
                            }

                            // 刷新布局
                            scene.getRoot().layout();
                        } catch (Exception e) {
                            System.err.println("状态页面显示失败: " + e.getMessage());
                            scene.getRoot().setOpacity(1);
                        }
                    }
            ));
            delayTimeline.setCycleCount(1);
            delayTimeline.play();
        });
    }

    public static void animateMainWindowEntrance(Scene scene) {
        if (scene == null || scene.getRoot() == null) return;

        // 初始时设置根节点为不可见
        scene.getRoot().setOpacity(0);

        // 创建渐入动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), scene.getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // 场景加载后播放动画
        Platform.runLater(() -> {
            fadeIn.play();
        });
    }
} 