package com.myfinanceapp.ui.goalsscene;
import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.statusscene.Status;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
public class Goals {

    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {

        // 主容器
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // ============ 左侧导航 sideBar ============
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage,"Goals",loggedUser);
        root.setLeft(sideBar);

        // ============ 中间内容区 ============
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);

        Label incExpLabel = new Label("BlaBlaBla");
        incExpLabel.setFont(new Font(20));
        incExpLabel.setTextFill(Color.DARKBLUE);

        Label chartPlaceholder = new Label("BlaBlaBla");
        chartPlaceholder.setFont(new Font(16));

        centerBox.getChildren().addAll(incExpLabel, chartPlaceholder);
        root.setCenter(centerBox);

        return new Scene(root, width, height);
    }
}
