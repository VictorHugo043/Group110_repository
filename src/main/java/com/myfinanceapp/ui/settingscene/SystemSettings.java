package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.ui.loginscene.LoginScene;
import com.myfinanceapp.ui.statusscene.Status;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Objects;

public class SystemSettings {

    public static Scene createScene(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // ===== 左侧导航栏：与 Status 一致，但 Settings 选中 =====
        VBox sideBar = createLeftSidebar(stage);
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
        HBox topBar = createTopBar(stage, width, height);
        // 2) 表单
        Pane settingsForm = createSettingsForm(stage);

        outerBox.getChildren().addAll(settingsForm);
        container.getChildren().addAll(topBar, outerBox);
        centerBox.getChildren().add(container);
        root.setCenter(centerBox);

        return new Scene(root, width, height);

    }

    /**
     * 左侧边栏，沿用和 Status 同样的样式，只是「Settings」按钮选中
     */
    private static VBox createLeftSidebar(Stage stage) {
        VBox sideBar = new VBox(15);
        sideBar.setPadding(new Insets(20, 0, 20, 15));
        sideBar.setAlignment(Pos.TOP_LEFT);
        sideBar.setPrefWidth(170);

        // 在 sideBar 的右侧画一条 2px 蓝色竖线
        sideBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 0 2 0 0;"
        );

        Label welcomeLabel = new Label("Only \nyou can do!");
        welcomeLabel.setFont(new Font(18));
        welcomeLabel.setTextFill(Color.DARKBLUE);

        // 五个按钮：Status, Goals, New, Settings(选中), Log out
        HBox statusBox   = createSidebarButtonBox("Status",   "status_icon_default.png",   "status_icon_selected.png",   false);
        HBox goalsBox    = createSidebarButtonBox("Goals",    "goals_icon_default.png",    "goals_icon_selected.png",    false);
        HBox newBox      = createSidebarButtonBox("New",      "new_icon_default.png",      "new_icon_selected.png",      false);
        HBox settingsBox = createSidebarButtonBox("Settings", "settings_icon_default.png", "settings_icon_selected.png", true);
        HBox logoutBox   = createSidebarButtonBox("Log out",  "logout_icon_default.png",   "logout_icon_selected.png",   false);

        // 示例：点击回到 Status
        statusBox.getChildren().get(0).setOnMouseClicked(e -> {
             stage.setScene(Status.createScene(stage, 800, 450));
        });
        // goalsBox, newBox, logoutBox 同理
        logoutBox.getChildren().get(0).setOnMouseClicked(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?");
            confirm.setHeaderText(null);
            confirm.setTitle("Confirm Logout");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    stage.setScene(LoginScene.createScene(stage, 800, 450));
                    stage.setTitle("Finanger - Login");
                }
            });
        });

        sideBar.getChildren().addAll(
                welcomeLabel,
                statusBox,
                goalsBox,
                newBox,
                settingsBox,
                logoutBox
        );
        return sideBar;
    }

    /**
     * 生成侧边栏按钮（与 Status 同样逻辑）
     */
    private static HBox createSidebarButtonBox(String text, String defaultIcon, String selectedIcon, boolean isActive) {
        Label label = new Label(text);
        label.setFont(new Font(14));
        label.setPrefSize(isActive ? 172 : 170, 40); // 选中时多2px
        label.setAlignment(Pos.CENTER_LEFT);
        label.setPadding(new Insets(0, 10, 0, 20));

        String iconFile = isActive ? selectedIcon : defaultIcon;
        var url = Objects.requireNonNull(
                SystemSettings.class.getResource("/pictures/" + iconFile),
                "Resource /pictures/" + iconFile + " not found!"
        );
        ImageView iconView = new ImageView(new Image(url.toExternalForm()));
        iconView.setFitWidth(18);
        iconView.setFitHeight(18);
        label.setGraphic(iconView);
        label.setGraphicTextGap(10);

        if (isActive) {
            label.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-text-fill: #3282FA; " +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                            "-fx-border-width: 2 0 2 2; " +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
        } else {
            label.setStyle(
                    "-fx-background-color: #E0F0FF; " +
                            "-fx-text-fill: black; " +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                            "-fx-border-width: 2 0 2 2; " +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
        }

        HBox box = new HBox(label);
        box.setAlignment(Pos.CENTER_LEFT);
        if (isActive) {
            box.setTranslateX(2);
        }
        return box;
    }

    /**
     * 顶部选项栏 (Tab)，4个选项：System Settings(选中), User Options, Other Settings, About
     * 但这次放在 centerContainer 顶部，而非整窗 top
     */
    private static HBox createTopBar(Stage stage, double width, double height) {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 0, 0));
        topBar.setAlignment(Pos.BOTTOM_LEFT);


        // 4个Tab
        VBox systemSettingsTab = createTopTab("System Settings", true);
        VBox userOptionsTab    = createTopTab("User Options",    false);
        VBox otherSettingsTab  = createTopTab("Other Settings",  false);
        VBox aboutTab          = createTopTab("About",           false);

        userOptionsTab.setOnMouseClicked(e -> {
            // TODO: user options
        });
        otherSettingsTab.setOnMouseClicked(e -> {
            // ...
        });
        aboutTab.setOnMouseClicked(e -> {
            // ...
        });

        topBar.getChildren().addAll(systemSettingsTab, userOptionsTab, otherSettingsTab, aboutTab);
        return topBar;
    }

    /**
     * 顶部 Tab(带倒三角) - 选中时深蓝+白字+显示三角
     */
    private static VBox createTopTab(String text, boolean isActive) {
        // 小三角 ▼
        Label arrow = new Label("\u25BC");
        arrow.setVisible(isActive);
        arrow.setTextFill(Color.web("#3282FA"));
        arrow.setStyle("-fx-font-size: 14;");

        Label tabLabel = new Label(text);
        tabLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        tabLabel.setPrefWidth(120);
        tabLabel.setAlignment(Pos.CENTER);

        if (isActive) {
            // 深蓝背景+白字
            tabLabel.setStyle(
                    "-fx-background-color: #3282FA;" +
                            "-fx-text-fill: white;" +
                            "-fx-border-radius: 8 8 0 0;" +
                            "-fx-background-radius: 8 8 0 0;"
            );
        } else {
            // 浅蓝背景+深蓝字
            tabLabel.setStyle(
                    "-fx-background-color: #E0F0FF;" +
                            "-fx-text-fill: #3282FA;" +
                            "-fx-border-radius: 8 8 0 0;" +
                            "-fx-background-radius: 8 8 0 0;"
            );
        }

        VBox tab = new VBox(2, arrow, tabLabel);
        tab.setAlignment(Pos.BOTTOM_CENTER);
        return tab;
    }

    /**
     * 中心的设置表单
     */
    private static Pane createSettingsForm(Stage stage) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(30));

        // 语言
        HBox langBox = new HBox(20);
        Label langLabel = new Label("Languages");
        langLabel.setFont(Font.font("Arial", 14));
        ComboBox<String> langCombo = new ComboBox<>();
        langCombo.getItems().addAll("English", "Chinese", "Spanish");
        langCombo.setValue("English");
        langBox.getChildren().addAll(langLabel, langCombo);

        // Night/Daytime
        HBox nightBox = new HBox(20);
        Label nightLabel = new Label("Night/Daytime Mode");
        nightLabel.setFont(Font.font("Arial", 14));
        ComboBox<String> nightCombo = new ComboBox<>();
        nightCombo.getItems().addAll("Daytime", "Nighttime");
        nightCombo.setValue("Daytime");
        nightBox.getChildren().addAll(nightLabel, nightCombo);

        // Window Size
        HBox sizeBox = new HBox(20);
        Label sizeLabel = new Label("Window Size");
        sizeLabel.setFont(Font.font("Arial", 14));
        ComboBox<String> sizeCombo = new ComboBox<>();
        sizeCombo.getItems().addAll("1920x1080", "1366x768", "1280x720");
        sizeCombo.setValue("1920x1080");
        sizeBox.getChildren().addAll(sizeLabel, sizeCombo);

        // 按钮区
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        Button resetBtn = new Button("Reset to Default");
        resetBtn.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;");
        Button backBtn = new Button("Back to Mainpage");
        backBtn.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> {
            // 回到 Status
            stage.setScene(Status.createScene(stage, 800, 450));
        });

        buttonBox.getChildren().addAll(resetBtn, backBtn);

        container.getChildren().addAll(langBox, nightBox, sizeBox, buttonBox);
        return container;
    }
}
