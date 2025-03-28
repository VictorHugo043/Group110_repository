package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.SettingsTopBarFactory;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class SystemSettings {

    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // ===== 左侧导航栏：与 Status 一致，但 Settings 选中 =====
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Settings", loggedUser);
        root.setLeft(sideBar);

        // ===== 中心容器：包含顶部选项栏 + 设置表单，共用同一个圆角边框 =====
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);

        // container：垂直叠放 topBar(顶部直线圆角) + outerBox(底部圆角)
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);

        // outerBox：顶部直线、底部圆角的外框
        // 通过 border-radius: 0 0 12 12; background-radius: 0 0 12 12; 实现
        VBox outerBox = new VBox(0); // 不要间距，让Tab栏和表单紧贴
        outerBox.setMaxWidth(600);
        outerBox.setMaxHeight(400);
        outerBox.setAlignment(Pos.TOP_CENTER);
        outerBox.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 0 0 12 12;" +
                        "-fx-background-radius: 0 0 12 12;" +
                        "-fx-background-color: white;"
        );

        // 1) 顶部 Tab 栏 (与外Box同背景)
        HBox topBar = SettingsTopBarFactory.createTopBar(stage, "System Settings", loggedUser);
        // 2) 表单
        Pane settingsForm = createSettingsForm(stage, loggedUser);

        outerBox.getChildren().addAll(settingsForm);
        container.getChildren().addAll(topBar, outerBox);
        centerBox.getChildren().add(container);
        root.setCenter(centerBox);

        return new Scene(root, width, height);
    }

    /**
     * 中心的设置表单
     */
    private static Pane createSettingsForm(Stage stage, User loggedUser) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(30));

        // 语言
        ImageView languagesIcon = new ImageView();
        languagesIcon.setFitWidth(20);
        languagesIcon.setFitHeight(20);
        try {
            Image icon = new Image(Objects.requireNonNull(SystemSettings.class.getResource("/pictures/languages_icon.png")).toExternalForm());
            languagesIcon.setImage(icon);
        } catch (Exception e) {
            // fallback: do nothing
        }
        HBox langBox = new HBox(20);
        Label langLabel = new Label("Languages");
        langLabel.setFont(Font.font("Arial", 14));
        ComboBox<String> langCombo = new ComboBox<>();
        langCombo.getItems().addAll("English", "Chinese", "Spanish");
        langCombo.setValue("English");
        langBox.getChildren().addAll(languagesIcon, langLabel, langCombo);

        // Night/Daytime
        ImageView dayIcon = new ImageView();
        dayIcon.setFitWidth(20);
        dayIcon.setFitHeight(20);
        try {
            Image icon = new Image(Objects.requireNonNull(SystemSettings.class.getResource("/pictures/day_icon.png")).toExternalForm());
            dayIcon.setImage(icon);
        } catch (Exception e) {
            // fallback: do nothing
        }
        HBox nightBox = new HBox(20);
        Label nightLabel = new Label("Night/Daytime Mode");
        nightLabel.setFont(Font.font("Arial", 14));
        ComboBox<String> nightCombo = new ComboBox<>();
        nightCombo.getItems().addAll("Daytime", "Nighttime");
        nightCombo.setValue("Daytime");
        nightBox.getChildren().addAll(dayIcon, nightLabel, nightCombo);

        // Window Size
        ImageView windowIcon = new ImageView();
        windowIcon.setFitWidth(20);
        windowIcon.setFitHeight(20);
        try {
            Image icon = new Image(Objects.requireNonNull(SystemSettings.class.getResource("/pictures/window_icon.png")).toExternalForm());
            windowIcon.setImage(icon);
        } catch (Exception e) {
            // fallback: do nothing
        }
        HBox sizeBox = new HBox(20);
        Label sizeLabel = new Label("Window Size");
        sizeLabel.setFont(Font.font("Arial", 14));
        ComboBox<String> sizeCombo = new ComboBox<>();
        sizeCombo.getItems().addAll("1920x1080", "1366x768", "1280x720");
        sizeCombo.setValue("1920x1080");
        sizeBox.getChildren().addAll(windowIcon, sizeLabel, sizeCombo);

        // 按钮区
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        Button resetBtn = new Button("Reset to Default");
        resetBtn.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;");
        Button backBtn = new Button("Back to Mainpage");
        backBtn.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> {
            // 回到 Status
            StatusScene statusScene = new StatusScene(stage, 800, 450, loggedUser);
            stage.setScene(statusScene.createScene());
            StatusService statusService = new StatusService(statusScene, loggedUser); // 初始化服务
            stage.setTitle("Finanger - Status"); // 可选：设置标题
        });

        buttonBox.getChildren().addAll(resetBtn, backBtn);

        container.getChildren().addAll(langBox, nightBox, sizeBox, buttonBox);
        return container;
    }
}