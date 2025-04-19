package com.myfinanceapp.ui.common;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.settingscene.About;
import com.myfinanceapp.ui.settingscene.SystemSettings;
import com.myfinanceapp.ui.settingscene.UserOptions;
import com.myfinanceapp.ui.settingscene.ExportReport;
import com.myfinanceapp.service.ThemeService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import com.myfinanceapp.ui.common.SceneManager;

public class SettingsTopBarFactory {
    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 450;

    // 存储窗口大小监听器的静态引用，以便能够移除它们
    private static ChangeListener<Number> widthListener;
    private static ChangeListener<Number> heightListener;

    // 重载方法，兼容旧的调用方式
    public static HBox createTopBar(Stage stage, String activeTab, User loggedUser) {
        return createTopBar(stage, activeTab, loggedUser, new ThemeService());
    }

    public static HBox createTopBar(Stage stage, String activeTab, User loggedUser, ThemeService themeService) {
        // 确保窗口有最小尺寸限制
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);

        // 如果还没有设置监听器，添加监听器
        if (widthListener == null) {
            widthListener = (obs, oldVal, newVal) -> {
                if (newVal.doubleValue() < MIN_WINDOW_WIDTH) {
                    stage.setWidth(MIN_WINDOW_WIDTH);
                }
            };
            stage.widthProperty().addListener(widthListener);
        }

        if (heightListener == null) {
            heightListener = (obs, oldVal, newVal) -> {
                if (newVal.doubleValue() < MIN_WINDOW_HEIGHT) {
                    stage.setHeight(MIN_WINDOW_HEIGHT);
                }
            };
            stage.heightProperty().addListener(heightListener);
        }

        // 创建顶部栏
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 0, 0));
        topBar.setAlignment(Pos.BOTTOM_LEFT);

        VBox systemSettingsTab = createOneTab("System Settings", activeTab.equals("System Settings"), themeService);
        VBox userOptionsTab = createOneTab("User Options", activeTab.equals("User Options"), themeService);
        VBox exportReportTab = createOneTab("Export Report", activeTab.equals("Export Report"), themeService);
        VBox aboutTab = createOneTab("About", activeTab.equals("About"), themeService);

        // 获取当前窗口尺寸
        double currentWidth = Math.max(stage.getWidth(), MIN_WINDOW_WIDTH);
        double currentHeight = Math.max(stage.getHeight(), MIN_WINDOW_HEIGHT);

        // 设置点击事件
        aboutTab.setOnMouseClicked(e -> {
            Scene newScene = About.createScene(stage, currentWidth, currentHeight, loggedUser, themeService);
            SceneManager.switchScene(stage, newScene);
        });

        systemSettingsTab.setOnMouseClicked(e -> {
            Scene newScene = SystemSettings.createScene(stage, currentWidth, currentHeight, loggedUser);
            SceneManager.switchScene(stage, newScene);
        });

        userOptionsTab.setOnMouseClicked(e -> {
            Scene newScene = UserOptions.createScene(stage, currentWidth, currentHeight, loggedUser, themeService);
            SceneManager.switchScene(stage, newScene);
        });

        exportReportTab.setOnMouseClicked(e -> {
            Scene newScene = ExportReport.createScene(stage, currentWidth, currentHeight, loggedUser, themeService);
            SceneManager.switchScene(stage, newScene);
        });

        topBar.getChildren().addAll(systemSettingsTab, userOptionsTab, exportReportTab, aboutTab);
        return topBar;
    }

    private static VBox createOneTab(String text, boolean isActive, ThemeService themeService) {
        Label arrow = new Label("\u25BC");
        arrow.setVisible(isActive);
        arrow.setTextFill(Color.web(themeService.isDayMode() ? "#3282FA" : "#E0F0FF")); // Blue in Daytime, Light Blue in Nighttime for visibility
        arrow.setStyle("-fx-font-size: 14;");

        Label tabLabel = new Label(text);
        tabLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        tabLabel.setPrefWidth(120);
        tabLabel.setAlignment(Pos.CENTER);

        if (isActive) {
            tabLabel.setStyle(
                    "-fx-background-color: #3282FA;" +
                            "-fx-text-fill: white;" +
                            "-fx-border-radius: 8 8 0 0;" +
                            "-fx-background-radius: 8 8 0 0;"
            );
        } else {
            tabLabel.setStyle(
                    "-fx-background-color: " + (themeService.isDayMode() ? "#E0F0FF" : "#4A6FA5") + ";" +
                            themeService.getTextColorStyle() +
                            "-fx-border-radius: 8 8 0 0;" +
                            "-fx-background-radius: 8 8 0 0;"
            );
        }

        VBox tab = new VBox(2, arrow, tabLabel);
        tab.setAlignment(Pos.BOTTOM_CENTER);
        return tab;
    }
}