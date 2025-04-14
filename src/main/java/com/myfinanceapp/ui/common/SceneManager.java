package com.myfinanceapp.ui.common;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    /**
     * 切换场景时保持窗口大小
     * @param stage 当前舞台
     * @param newScene 新场景
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