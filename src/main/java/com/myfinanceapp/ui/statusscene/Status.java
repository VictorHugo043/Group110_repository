package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.ui.common.LeftSidebarFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class Status {

    public static Scene createScene(Stage stage, double width, double height) {
        // 主容器
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // ============ 左侧导航 sideBar ============
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage,"Status");
        root.setLeft(sideBar);

        // ============ 中间内容区 ============
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

    /**
     * 创建一个侧边栏按钮容器 (HBox)。若 isActive=true，则按钮覆盖 sideBar 的右线。
     */
    private static HBox createSidebarButtonBox(String text, String defaultIcon, String selectedIcon, boolean isActive) {
        // 文字标签
        Label label = new Label(text);
        label.setFont(new Font(14));
        // 按钮大小：未选中时 170 与 sideBar 同宽，让蓝线可见
        // 选中时 172 + 右移2px，覆盖那2px 的蓝线
        label.setPrefSize(isActive ? 172 : 170, 40);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setPadding(new Insets(0, 10, 0, 20));

        // 加载图标
        String iconFile = isActive ? selectedIcon : defaultIcon;
        var url = Objects.requireNonNull(
                Status.class.getResource("/pictures/" + iconFile),
                "Resource /pictures/" + iconFile + " not found!"
        );
        ImageView iconView = new ImageView(new Image(url.toExternalForm()));
        iconView.setFitWidth(18);
        iconView.setFitHeight(18);
        label.setGraphic(iconView);
        label.setGraphicTextGap(10);

        if (isActive) {
            // 选中状态：白底、蓝字
            // 自身 border: left=2px蓝线, top/bottom=2px, right=透明
            label.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-text-fill: #3282FA;" +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA;" +
                            "-fx-border-width: 2 0 2 2;" +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
        } else {
            // 未选中状态：浅蓝底、黑字
            label.setStyle(
                    "-fx-background-color: #E0F0FF;" +
                            "-fx-text-fill: black;" +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA;" +
                            "-fx-border-width: 2 0 2 2;" +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
        }

        // HBox 容器：选中时向左移 2px 以覆盖 sideBar 的竖线
        HBox box = new HBox(label);
        box.setAlignment(Pos.CENTER_LEFT);
        if (isActive) {
            // 右移2px => 覆盖sideBar的右侧边线
            box.setTranslateX(2);
        }
        return box;
    }
}
