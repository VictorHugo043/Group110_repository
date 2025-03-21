package com.myfinanceapp.ui.statusscene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.myfinanceapp.ui.loginscene.LoginScene;

import java.util.Objects;

public class Status {

    public static Scene createScene(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // 左侧导航
        VBox sideBar = new VBox();
        sideBar.setStyle("-fx-background-color: white;");
        sideBar.setPrefWidth(170);
        sideBar.setAlignment(Pos.TOP_CENTER);
        sideBar.setPadding(new Insets(0, 0, 0, 15));

        // 页面顶部的分割线容器（动态拉伸）
        HBox veryTopDivider = createDividerBetweenButtons();
        veryTopDivider.setStyle("-fx-background-color: transparent;");
        veryTopDivider.getChildren().get(0).setStyle("-fx-background-color: #3282FA;");
        VBox.setVgrow(veryTopDivider, Priority.ALWAYS); // 让其填充可用空间

        Label welcomeLabel = new Label("Welcome \nback!");
        welcomeLabel.setFont(new Font(18));
        welcomeLabel.setTextFill(Color.DARKBLUE);
        welcomeLabel.setPadding(new Insets(20, 0, 0, 0));
        // 为 welcomeLabel 添加右侧框线，与按钮右侧分割线对齐
        welcomeLabel.setStyle(
                "-fx-border-color: transparent #3282FA transparent transparent;" + // 上右下左
                        "-fx-border-width: 0 2px 0 0;"
        );
        welcomeLabel.setPrefWidth(150); // 确保宽度与按钮一致，以便框线对齐

        // 侧边栏按钮
        HBox statusBox = createSidebarButtonBox("Status", "status_icon_default.png", "status_icon_selected.png", true);
        HBox goalsBox = createSidebarButtonBox("Goals", "goals_icon_default.png", "goals_icon_selected.png", false);
        HBox newBox = createSidebarButtonBox("New", "new_icon_default.png", "new_icon_selected.png", false);
        HBox settingsBox = createSidebarButtonBox("Settings", "settings_icon_default.png", "settings_icon_selected.png", false);
        HBox logoutBox = createSidebarButtonBox("Log out", "logout_icon_default.png", "logout_icon_selected.png", false);

        // 页面切换逻辑
        goalsBox.getChildren().get(0).setOnMouseClicked(e -> stage.setScene(/* Goals 页面 createScene 方法 */ null));
        newBox.getChildren().get(0).setOnMouseClicked(e -> stage.setScene(/* New 页面 createScene 方法 */ null));
        settingsBox.getChildren().get(0).setOnMouseClicked(e -> stage.setScene(/* Settings 页面 createScene 方法 */ null));
        logoutBox.getChildren().get(0).setOnMouseClicked(event -> {
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

        // 侧边栏按钮布局
        double buttonSpacing = 15; // 按钮间距
        VBox buttonContainer = new VBox();
        buttonContainer.getChildren().add(statusBox);

        // 在按钮之间添加分割线段
        HBox dividerBetween1 = createDividerBetweenButtons(buttonSpacing);
        buttonContainer.getChildren().add(dividerBetween1);
        buttonContainer.getChildren().add(goalsBox);

        HBox dividerBetween2 = createDividerBetweenButtons(buttonSpacing);
        buttonContainer.getChildren().add(dividerBetween2);
        buttonContainer.getChildren().add(newBox);

        HBox dividerBetween3 = createDividerBetweenButtons(buttonSpacing);
        buttonContainer.getChildren().add(dividerBetween3);
        buttonContainer.getChildren().add(settingsBox);

        HBox dividerBetween4 = createDividerBetweenButtons(buttonSpacing);
        buttonContainer.getChildren().add(dividerBetween4);
        buttonContainer.getChildren().add(logoutBox);

        // 页面底部的分割线容器（动态拉伸）
        HBox veryBottomDivider = createDividerBetweenButtons();
        veryBottomDivider.setStyle("-fx-background-color: transparent;");
        veryBottomDivider.getChildren().get(0).setStyle("-fx-background-color: #3282FA;");
        buttonContainer.getChildren().add(veryBottomDivider);

        buttonContainer.setPadding(new Insets(30, 0, 0, 0));

        // 在 welcomeLabel 和第一个按钮之间添加分割线
        HBox topDivider = createDividerBetweenButtons(30); // welcomeLabel 和第一个按钮之间的间距
        topDivider.setStyle("-fx-background-color: transparent;");
        topDivider.getChildren().get(0).setStyle("-fx-background-color: #3282FA;");

        // 组装 sideBar
        sideBar.getChildren().addAll(
                veryTopDivider,
                welcomeLabel,
                topDivider,
                buttonContainer
        );
        root.setLeft(sideBar);

        // 中间内容区
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        Label incExpLabel = new Label("Income and Expenses for This Month");
        incExpLabel.setFont(new Font(20));
        incExpLabel.setTextFill(Color.DARKBLUE);
        Label chartPlaceholder = new Label("[Chart Placeholder]");
        chartPlaceholder.setFont(new Font(16));

        centerBox.getChildren().addAll(incExpLabel, chartPlaceholder);
        root.setCenter(centerBox);

        return new Scene(root, width, height);
    }

    private static HBox createSidebarButtonBox(String text, String defaultIcon, String selectedIcon, boolean isActive) {
        Label label = new Label(text);
        label.setFont(new Font(14));
        label.setPrefSize(150, 40);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setPadding(new Insets(0, 10, 0, 20));

        ImageView iconView;
        String iconFile = isActive ? selectedIcon : defaultIcon;
        var url = Objects.requireNonNull(
                Status.class.getResource("/pictures/" + iconFile),
                "Resource /pictures/" + iconFile + " not found!"
        );
        iconView = new ImageView(new Image(url.toExternalForm()));
        iconView.setFitWidth(18);
        iconView.setFitHeight(18);
        label.setGraphic(iconView);
        label.setGraphicTextGap(10);

        // 右侧分割线
        Pane divider = new Pane();
        divider.setPrefSize(2, 40); // 与按钮高度一致

        if (isActive) {
            // 选中状态：白色背景，无右侧边框，左侧圆角，无分割线
            label.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-text-fill: #3282FA; " +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                            "-fx-border-width: 2px 0 2px 2px; " +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
            divider.setStyle("-fx-background-color: white;"); // 选中时分割线为白色（消失）
        } else {
            // 未选中状态：浅蓝色背景，无右侧边框（由分割线替代）
            label.setStyle(
                    "-fx-background-color: #E0F0FF; " +
                            "-fx-text-fill: black; " +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                            "-fx-border-width: 2px 0 2px 2px; " +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
            divider.setStyle("-fx-background-color: #3282FA;"); // 未选中时显示蓝色分割线
        }

        HBox box = new HBox(label, divider);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(0); // 确保按钮和分割线之间没有间隙
        return box;
    }

    // 创建按钮之间的分割线段（重载方法，允许不指定高度）
    private static HBox createDividerBetweenButtons() {
        Pane divider = new Pane();
        divider.setMinWidth(2);
        divider.setMaxWidth(2);
        divider.setStyle("-fx-background-color: #3282FA;"); // 蓝色分割线

        HBox container = new HBox(divider);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 0, 0, 150)); // 将分割线移动到与按钮右侧对齐的位置
        return container;
    }

    // 创建按钮之间的分割线段（固定高度）
    private static HBox createDividerBetweenButtons(double height) {
        Pane divider = new Pane();
        divider.setPrefSize(2, height); // 宽度为 2px，高度为按钮间距
        divider.setMaxWidth(2); // 限制最大宽度，防止被拉伸
        divider.setMinWidth(2); // 限制最小宽度
        divider.setStyle("-fx-background-color: #3282FA;"); // 蓝色分割线

        HBox container = new HBox(divider);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 0, 0, 150)); // 将分割线移动到与按钮右侧对齐的位置
        return container;
    }
}