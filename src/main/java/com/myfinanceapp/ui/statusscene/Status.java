package com.myfinanceapp.ui.statusscene;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import com.myfinanceapp.ui.loginscene.LoginScene;


public class Status {

    public static Scene createScene(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();

        // 左侧导航
        VBox sideBar = new VBox(20);
        sideBar.setStyle("-fx-background-color: #E0F0FF;");
        sideBar.setPrefWidth(150);
        sideBar.setAlignment(Pos.CENTER);
        Label welcomeLabel = new Label("Welcome \nback!");
        welcomeLabel.setFont(new Font(18));
        sideBar.getChildren().addAll(welcomeLabel, new Label("Status"), new Label("Goals"), new Label("New"), new Label("Settings"));
        Label logoutLabel = new Label("Log out");
        logoutLabel.setTextFill(Color.BLUE); // 或者自定义样式
        logoutLabel.setOnMouseClicked(event -> {
            // 点击后，弹出确认对话框
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?");
            confirm.setHeaderText(null);
            confirm.setTitle("Confirm Logout");

            // 等待用户操作
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // 确认，则跳转回登录界面
                    stage.setScene(LoginScene.createScene(stage, 800, 450));
                    stage.setTitle("Finanger - Login");
                }
            });
        });

// 将这个 logoutLabel 加到 sideBar 里
        sideBar.getChildren().add(logoutLabel);
        root.setLeft(sideBar);

        // 中间示例：一些报表/图表面板（这里简单用Label替代）
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        Label incExpLabel = new Label("Income and Expenses for This Month");
        incExpLabel.setFont(new Font(20));
        incExpLabel.setTextFill(Color.DARKBLUE);

        // 这里可添加你的PieChart、BarChart等
        Label chartPlaceholder = new Label("[Chart Placeholder]");
        chartPlaceholder.setFont(new Font(16));

        centerBox.getChildren().addAll(incExpLabel, chartPlaceholder);
        root.setCenter(centerBox);

        return new Scene(root, width, height);
    }
}
