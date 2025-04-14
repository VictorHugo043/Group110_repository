package com.myfinanceapp.ui.mainwindow;

import com.myfinanceapp.ui.loginscene.LoginScene;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * 主窗口，斜线分割，但使用动态重排：根据窗口大小实时计算坐标
 */
public class MainWindow extends Application {

    // 原始设计尺寸
    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 450;

    // 记录UI控件 & 多边形
    private Group root;
    private Polygon whitePolygon;
    private Polygon bluePolygon;
    private Label welcomeLabel;
    private Label sloganLabel;
    private Button arrowButton;

    // =========== 各节点的原始"比率"数据 ===========

    // 白色多边形 (4个顶点)，原先坐标: (0,0)->(500,0)->(300,450)->(0,450)
    // 转化为 (xFrac,yFrac)
    private final double[] whitePolyFractions = {
            0.0, 0.0,
            500.0/INITIAL_WIDTH, 0.0,
            300.0/INITIAL_WIDTH, 450.0/INITIAL_HEIGHT,
            0.0, 450.0/INITIAL_HEIGHT
    };

    // 蓝色多边形 (4个顶点)，原先: (500,0)->(800,0)->(800,450)->(300,450)
    private final double[] bluePolyFractions = {
            500.0/INITIAL_WIDTH, 0.0,
            1.0, 0.0,
            1.0, 1.0,
            300.0/INITIAL_WIDTH, 1.0
    };

    // welcomeLabel 原先 (x=50, y=80)
    private final double welcomeLabelXFrac = 50.0/INITIAL_WIDTH;
    private final double welcomeLabelYFrac = 80.0/INITIAL_HEIGHT;

    // sloganLabel 原先 (x=50, y=150)
    private final double sloganLabelXFrac = 50.0/INITIAL_WIDTH;
    private final double sloganLabelYFrac = 150.0/INITIAL_HEIGHT;

    // arrowButton 原先 (x=570, y=170)
    private final double arrowBtnXFrac = 570.0/INITIAL_WIDTH;
    private final double arrowBtnYFrac = 170.0/INITIAL_HEIGHT;

    @Override
    public void start(Stage stage) {
        root = new Group();
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);

        stage.setTitle("Finanger - Welcome");
        stage.setScene(scene);
        stage.setResizable(true); // 允许拉伸
        stage.setMinWidth(800);
        stage.setMinHeight(450);

        // 初始化UI
        initUI(stage);

        // 监听Scene大小变化，实时重排
        scene.widthProperty().addListener((obs, oldVal, newVal) -> relayout());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> relayout());

        // 首次布局
        relayout();

        stage.show();
    }

    /**
     * 初始化UI控件和多边形
     */
    private void initUI(Stage stage) {
        // ============ 创建多边形 ============
        whitePolygon = new Polygon();
        whitePolygon.setFill(Color.WHITE);

        bluePolygon = new Polygon();
        bluePolygon.setFill(Color.web("#A3D1FF"));

        root.getChildren().addAll(whitePolygon, bluePolygon);

        // ============ Label: "Welcome," ============
        welcomeLabel = new Label("Welcome,");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        welcomeLabel.setTextFill(Color.web("#1A57C3"));
        root.getChildren().add(welcomeLabel);

        // ============ Label: slogan ============
        sloganLabel = new Label("Finanger is your best\npersonal financial manager");
        sloganLabel.setFont(new Font("Lobster", 26));
        sloganLabel.setTextFill(Color.web("#1A57C3"));
        root.getChildren().add(sloganLabel);

        // ============ Button: "➤" ============
        arrowButton = new Button("➤");
        arrowButton.setFont(new Font(30));
        arrowButton.setTextFill(Color.web("#1A57C3"));
        arrowButton.setStyle("-fx-background-radius: 40; -fx-min-width: 60; -fx-min-height: 70;"
                + " -fx-background-color: #FFFFFF44;");

        arrowButton.setOnAction(e -> {
            Scene loginScene = LoginScene.createScene(stage, root.getScene().getWidth(), root.getScene().getHeight());
            stage.setScene(loginScene);
            stage.setTitle("Finanger - Login");
        });

        root.getChildren().add(arrowButton);
    }

    /**
     * 每当窗口大小改变，就调用此方法重新计算多边形顶点和控件坐标
     */
    private void relayout() {
        double curWidth = root.getScene().getWidth();
        double curHeight = root.getScene().getHeight();

        // ============ 重算白色多边形的坐标 ============
        whitePolygon.getPoints().setAll(
                whitePolyFractions[0] * curWidth, whitePolyFractions[1] * curHeight,
                whitePolyFractions[2] * curWidth, whitePolyFractions[3] * curHeight,
                whitePolyFractions[4] * curWidth, whitePolyFractions[5] * curHeight,
                whitePolyFractions[6] * curWidth, whitePolyFractions[7] * curHeight
        );

        // ============ 重算蓝色多边形的坐标 ============
        bluePolygon.getPoints().setAll(
                bluePolyFractions[0] * curWidth, bluePolyFractions[1] * curHeight,
                bluePolyFractions[2] * curWidth, bluePolyFractions[3] * curHeight,
                bluePolyFractions[4] * curWidth, bluePolyFractions[5] * curHeight,
                bluePolyFractions[6] * curWidth, bluePolyFractions[7] * curHeight
        );

        // ============ 重算 Label/按钮坐标 ============

        welcomeLabel.setLayoutX(welcomeLabelXFrac * curWidth);
        welcomeLabel.setLayoutY(welcomeLabelYFrac * curHeight);

        sloganLabel.setLayoutX(sloganLabelXFrac * curWidth);
        sloganLabel.setLayoutY(sloganLabelYFrac * curHeight);

        arrowButton.setLayoutX(arrowBtnXFrac * curWidth);
        arrowButton.setLayoutY(arrowBtnYFrac * curHeight);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
