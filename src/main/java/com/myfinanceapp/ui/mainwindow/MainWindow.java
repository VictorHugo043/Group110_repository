package com.myfinanceapp.ui.mainwindow;

import com.myfinanceapp.ui.loginscene.LoginScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * 主窗口，带斜线分割布局
 */
public class MainWindow extends Application {

    private static final double WIDTH = 800;
    private static final double HEIGHT = 450;

    @Override
    public void start(Stage stage) {
        // 创建主界面根节点
        Group root = createDiagonalLayout(stage);

        // 创建场景并设置大小
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("Finanger - Welcome");
        stage.setScene(scene);
        stage.setResizable(false);  // 不允许拉伸，可按需修改
        stage.show();
    }

    /**
     * 创建对角分割布局的 Pane
     */
    private Group createDiagonalLayout(Stage stage) {
        // 使用 Group 或 Pane 来承载形状和控件
        Group group = new Group();

        // ============ 1. 左侧白色多边形 ============
        // 这里定义一个四边形的顶点，让它呈现斜线边缘
        Polygon whitePolygon = new Polygon(
                0.0, 0.0,        // 左上角
                500.0, 0.0,      // 顶边偏右
                300.0, HEIGHT,   // 底边偏左
                0.0, HEIGHT      // 左下角
        );
        whitePolygon.setFill(Color.WHITE);

        // ============ 2. 右侧蓝色多边形 ============
        Polygon bluePolygon = new Polygon(
                500.0, 0.0,
                WIDTH, 0.0,
                WIDTH, HEIGHT,
                300.0, HEIGHT
        );
        bluePolygon.setFill(Color.web("#A3D1FF")); // 浅蓝色

        // 将多边形加入 group
        group.getChildren().addAll(whitePolygon, bluePolygon);

        // ============ 3. 在左侧白色区域放置文字 ============
        Label welcomeLabel = new Label("Welcome,");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        welcomeLabel.setTextFill(Color.web("#1A57C3"));
        // 简单设置坐标
        welcomeLabel.setLayoutX(50);
        welcomeLabel.setLayoutY(80);

        Label sloganLabel = new Label("Finanger is your best\npersonal financial manager");
        sloganLabel.setFont(new Font("Lobster", 26));
        sloganLabel.setTextFill(Color.web("#1A57C3"));
        sloganLabel.setLayoutX(50);
        sloganLabel.setLayoutY(150);

        group.getChildren().addAll(welcomeLabel, sloganLabel);

        // ============ 4. 右侧放置“箭头按钮”，点击后跳转登录界面 ============
        Button arrowButton = new Button("➤");
        arrowButton.setFont(new Font(30));
        arrowButton.setTextFill(Color.web("#1A57C3"));
        // 为了让它看起来更像圆形按钮，可加边框圆角
        arrowButton.setStyle("-fx-background-radius: 40; -fx-min-width: 60; -fx-min-height: 70;"
                + " -fx-background-color: #FFFFFF44;");

        // 设置按钮大致在右侧中部
        arrowButton.setLayoutX(570);
        arrowButton.setLayoutY(170);

        // 绑定点击事件：跳转到登录界面
        arrowButton.setOnAction(e -> {
            Scene loginScene = LoginScene.createScene(stage, WIDTH, HEIGHT);
            stage.setScene(loginScene);
            stage.setTitle("Finanger - Login");
        });

        group.getChildren().add(arrowButton);

        return group;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
