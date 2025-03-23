package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Status {

    private static User currentUser;

    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        Status.currentUser = loggedUser;
        // 主容器
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // ============ 左侧导航 sideBar ============
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage,"Status",loggedUser);
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

}


