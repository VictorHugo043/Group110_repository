package com.myfinanceapp.ui.common;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.settingscene.About;
import com.myfinanceapp.ui.settingscene.SystemSettings;
import com.myfinanceapp.ui.settingscene.UserOptions;
import com.myfinanceapp.ui.settingscene.ExportReport; // Add this import
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

public class SettingsTopBarFactory {

    public static HBox createTopBar(Stage stage, String activeTab, User loggedUser) {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(0, 0, 0, 0));
        topBar.setAlignment(Pos.BOTTOM_LEFT);

        VBox systemSettingsTab = createOneTab("System Settings", activeTab.equals("System Settings"));
        VBox userOptionsTab = createOneTab("User Options", activeTab.equals("User Options"));
        VBox exportReportTab = createOneTab("Export Report", activeTab.equals("Export Report"));
        VBox aboutTab = createOneTab("About", activeTab.equals("About"));

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

        exportReportTab.setOnMouseClicked(e -> {
            stage.setScene(ExportReport.createScene(stage, width, height, loggedUser));
        });

        topBar.getChildren().addAll(systemSettingsTab, userOptionsTab, exportReportTab, aboutTab);
        return topBar;
    }

    private static VBox createOneTab(String text, boolean isActive) {
        Label arrow = new Label("\u25BC");
        arrow.setVisible(isActive);
        arrow.setTextFill(Color.web("#3282FA"));
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