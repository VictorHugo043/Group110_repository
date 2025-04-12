package com.myfinanceapp.ui.common;
import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.settingscene.About;
import com.myfinanceapp.ui.settingscene.SystemSettings;
import com.myfinanceapp.ui.settingscene.OtherSettings;
import com.myfinanceapp.ui.settingscene.UserOptions;
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

/**
 * 工厂类：创建顶部Tab栏（System Settings, User Options, Other Settings, About）
 * 并可指定哪一个 Tab 选中。
 */
public class SettingsTopBarFactory {

    /**
     * 创建顶部Tab栏，4个选项：
     * @param activeTab 传入 "System Settings", "User Options", "Other Settings", "About" 表示哪个被选中
     */
    public static HBox createTopBar(Stage stage, String activeTab, User loggedUser) {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 0, 0));
        topBar.setAlignment(Pos.BOTTOM_LEFT);

        // 4个Tab
        VBox systemSettingsTab = createOneTab("System Settings",   activeTab.equals("System Settings"));
        VBox userOptionsTab    = createOneTab("User Options",      activeTab.equals("User Options"));
        VBox otherSettingsTab  = createOneTab("Other Settings",    activeTab.equals("Other Settings"));
        VBox aboutTab          = createOneTab("About",             activeTab.equals("About"));

        // 获取当前场景的尺寸
        Scene currentScene = stage.getScene();
        double width = currentScene.getWidth();
        double height = currentScene.getHeight();

        aboutTab.setOnMouseClicked(e -> {
            stage.setScene(About.createScene(stage, width, height, loggedUser));
        });
        
        systemSettingsTab.setOnMouseClicked(e -> {
            stage.setScene(SystemSettings.createScene(stage, width, height, loggedUser));
        });
        
        userOptionsTab.setOnMouseClicked(e -> {
            stage.setScene(UserOptions.createScene(stage, width, height, loggedUser));
        });
        
        otherSettingsTab.setOnMouseClicked(e -> {
            // TODO
            //stage.setScene(OtherSettings.createScene(stage, width, height));
        });

        topBar.getChildren().addAll(systemSettingsTab, userOptionsTab, otherSettingsTab, aboutTab);
        return topBar;
    }

    /**
     * 创建单个Tab(带倒三角▼)
     */
    private static VBox createOneTab(String text, boolean isActive) {
        // 小三角
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
}
