package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.SettingsTopBarFactory;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.service.ThemeService;
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
    private static ThemeService themeService = new ThemeService();

    // Overloaded method for backward compatibility
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, themeService);
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService) {
        SystemSettings.themeService = themeService; // Update the shared instance
        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // ===== 左侧导航栏：与 Status 一致，但 Settings 选中 =====
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Settings", loggedUser, themeService);
        root.setLeft(sideBar);

        // ===== 中心容器：包含顶部选项栏 + 设置表单，共用同一个圆角边框 =====
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);

        // container：垂直叠放 topBar(顶部直线圆角) + outerBox(底部圆角)
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);

        // outerBox：顶部直线、底部圆角的外框
        VBox outerBox = new VBox(0);
        outerBox.setMaxWidth(600);
        outerBox.setMaxHeight(400);
        outerBox.setAlignment(Pos.TOP_CENTER);
        outerBox.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 0 0 12 12;" +
                        "-fx-background-radius: 0 0 12 12;" +
                        themeService.getCurrentFormBackgroundStyle()
        );

        // 1) 顶部 Tab 栏 (与外Box同背景)
        HBox topBar = SettingsTopBarFactory.createTopBar(stage, "System Settings", loggedUser, themeService);
        // 2) 表单
        Pane settingsForm = createSettingsForm(stage, width, height, loggedUser, root, outerBox, sideBar, topBar);

        outerBox.getChildren().addAll(settingsForm);
        container.getChildren().addAll(topBar, outerBox);
        centerBox.getChildren().add(container);
        root.setCenter(centerBox);

        return new Scene(root, width, height);
    }

    /**
     * 中心的设置表单
     */
    private static Pane createSettingsForm(Stage stage, double width, double height, User loggedUser, BorderPane root, VBox outerBox, VBox sideBar, HBox topBar) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(30));

        // Declare labels at method scope
        Label langLabel = new Label("Languages");
        Label nightLabel = new Label("Night/Daytime Mode");
        Label sizeLabel = new Label("Window Size");

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
        langLabel.setFont(Font.font("Arial", 14));
        langLabel.setStyle(themeService.getTextColorStyle());
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
        nightLabel.setFont(Font.font("Arial", 14));
        nightLabel.setStyle(themeService.getTextColorStyle());
        ComboBox<String> nightCombo = new ComboBox<>();
        nightCombo.getItems().addAll("Daytime", "Nighttime");
        nightCombo.setValue(themeService.isDayMode() ? "Daytime" : "Nighttime");
        nightCombo.setOnAction(e -> {
            String selectedMode = nightCombo.getValue();
            themeService.setTheme(selectedMode.equals("Daytime"));
            root.setStyle(themeService.getCurrentThemeStyle());
            outerBox.setStyle(
                    "-fx-border-color: #3282FA;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 0 0 12 12;" +
                            "-fx-background-radius: 0 0 12 12;" +
                            themeService.getCurrentFormBackgroundStyle()
            );
            langLabel.setStyle(themeService.getTextColorStyle());
            nightLabel.setStyle(themeService.getTextColorStyle());
            sizeLabel.setStyle(themeService.getTextColorStyle());
            // Update sidebar styles
            sideBar.setStyle(
                    themeService.getCurrentThemeStyle() +
                            "-fx-border-color: #3282FA;" +
                            "-fx-border-width: 0 2 0 0;"
            );
            // Update welcomeLabel style (first child of sideBar)
            Label welcomeLabel = (Label) sideBar.getChildren().get(0);
            welcomeLabel.setStyle(themeService.getTextColorStyle());
            // Update button styles in sidebar
            for (int i = 1; i < sideBar.getChildren().size(); i++) {
                HBox buttonBox = (HBox) sideBar.getChildren().get(i);
                Label buttonLabel = (Label) buttonBox.getChildren().get(0);
                boolean isActive = buttonLabel.getPrefWidth() == 172; // Active buttons have width 172
                if (isActive) {
                    buttonLabel.setStyle(
                            themeService.getCurrentThemeStyle() +
                                    "-fx-text-fill: #3282FA; " +
                                    "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                                    "-fx-border-width: 2 0 2 2; " +
                                    "-fx-border-radius: 8 0 0 8;" +
                                    "-fx-background-radius: 8 0 0 8;"
                    );
                } else {
                    buttonLabel.setStyle(
                            "-fx-background-color: " + (themeService.isDayMode() ? "#E0F0FF" : "#4A6FA5") + "; " +
                                    themeService.getTextColorStyle() +
                                    "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                                    "-fx-border-width: 2 0 2 2; " +
                                    "-fx-border-radius: 8 0 0 8;" +
                                    "-fx-background-radius: 8 0 0 8;"
                    );
                }
            }
            // Update top bar styles
            for (int i = 0; i < topBar.getChildren().size(); i++) {
                VBox tab = (VBox) topBar.getChildren().get(i);
                Label arrow = (Label) tab.getChildren().get(0);
                Label tabLabel = (Label) tab.getChildren().get(1);
                boolean isActive = arrow.isVisible();
                arrow.setTextFill(javafx.scene.paint.Color.web(themeService.isDayMode() ? "#3282FA" : "#E0F0FF"));
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
            }
        });
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
        sizeLabel.setFont(Font.font("Arial", 14));
        sizeLabel.setStyle(themeService.getTextColorStyle());
        ComboBox<String> sizeCombo = new ComboBox<>();
        sizeCombo.getItems().addAll("1920x1080", "1366x768", "1280x720");
        sizeCombo.setValue("1920x1080");
        sizeBox.getChildren().addAll(windowIcon, sizeLabel, sizeCombo);

        // 按钮区
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        Button resetBtn = new Button("Reset to Default");
        resetBtn.setStyle(themeService.getButtonStyle());
        resetBtn.setOnAction(e -> {
            langCombo.setValue("English");
            nightCombo.setValue("Daytime");
            sizeCombo.setValue("1920x1080");
            themeService.setTheme(true); // Reset to Daytime
            root.setStyle(themeService.getCurrentThemeStyle());
            outerBox.setStyle(
                    "-fx-border-color: #3282FA;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 0 0 12 12;" +
                            "-fx-background-radius: 0 0 12 12;" +
                            themeService.getCurrentFormBackgroundStyle()
            );
            langLabel.setStyle(themeService.getTextColorStyle());
            nightLabel.setStyle(themeService.getTextColorStyle());
            sizeLabel.setStyle(themeService.getTextColorStyle());
            // Update sidebar styles
            sideBar.setStyle(
                    themeService.getCurrentThemeStyle() +
                            "-fx-border-color: #3282FA;" +
                            "-fx-border-width: 0 2 0 0;"
            );
            // Update welcomeLabel style (first child of sideBar)
            Label welcomeLabel = (Label) sideBar.getChildren().get(0);
            welcomeLabel.setStyle(themeService.getTextColorStyle());
            // Update button styles in sidebar
            for (int i = 1; i < sideBar.getChildren().size(); i++) {
                HBox buttonBox1 = (HBox) sideBar.getChildren().get(i);
                Label buttonLabel = (Label) buttonBox1.getChildren().get(0);
                boolean isActive = buttonLabel.getPrefWidth() == 172; // Active buttons have width 172
                if (isActive) {
                    buttonLabel.setStyle(
                            themeService.getCurrentThemeStyle() +
                                    "-fx-text-fill: #3282FA; " +
                                    "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                                    "-fx-border-width: 2 0 2 2; " +
                                    "-fx-border-radius: 8 0 0 8;" +
                                    "-fx-background-radius: 8 0 0 8;"
                    );
                } else {
                    buttonLabel.setStyle(
                            "-fx-background-color: " + (themeService.isDayMode() ? "#E0F0FF" : "#4A6FA5") + "; " +
                                    themeService.getTextColorStyle() +
                                    "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                                    "-fx-border-width: 2 0 2 2; " +
                                    "-fx-border-radius: 8 0 0 8;" +
                                    "-fx-background-radius: 8 0 0 8;"
                    );
                }
            }
            // Update top bar styles
            for (int i = 0; i < topBar.getChildren().size(); i++) {
                VBox tab = (VBox) topBar.getChildren().get(i);
                Label arrow = (Label) tab.getChildren().get(0);
                Label tabLabel = (Label) tab.getChildren().get(1);
                boolean isActive = arrow.isVisible();
                arrow.setTextFill(javafx.scene.paint.Color.web(themeService.isDayMode() ? "#3282FA" : "#E0F0FF"));
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
            }
        });
        Button backBtn = new Button("Back to Status");
        backBtn.setStyle(themeService.getButtonStyle());
        backBtn.setOnAction(e -> {
            StatusScene statusScene = new StatusScene(stage, width, height, loggedUser);
            stage.setScene(statusScene.createScene(themeService));
            StatusService statusService = new StatusService(statusScene, loggedUser);
            stage.setTitle("Finanger - Status");
        });

        buttonBox.getChildren().addAll(resetBtn, backBtn);

        container.getChildren().addAll(langBox, nightBox, sizeBox, buttonBox);
        return container;
    }
}