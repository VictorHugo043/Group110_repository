package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SettingsTopBarFactory;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class About {

    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        // 整体 BorderPane
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // 左侧边栏: Settings选中 (与 SystemSettings 相同)
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Settings", loggedUser);
        root.setLeft(sideBar);

        // 中心容器: 垂直组合 (topBar, outerBox)，放入 centerBox
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);

        // Tab栏: About 选中
        HBox topBar = SettingsTopBarFactory.createTopBar(stage, "About", loggedUser);

        // outerBox: 下方圆角容器
        VBox outerBox = new VBox(0);
        outerBox.setAlignment(Pos.TOP_CENTER);
        outerBox.setMaxWidth(510);
        outerBox.setMaxHeight(400);
        outerBox.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 0 0 12 12;" +
                        "-fx-background-radius: 0 0 12 12;" +
                        "-fx-background-color: white;"
        );

        // 中心内容：About 文本
        Pane aboutContent = createAboutContent(stage, loggedUser);

        outerBox.getChildren().addAll(aboutContent);
        container.getChildren().addAll(topBar, outerBox);

        centerBox.getChildren().add(container);
        root.setCenter(centerBox);

        return new Scene(root, width, height);
    }

    /**
     * 生成 About 界面的正文内容
     */
    private static Pane createAboutContent(Stage stage, User loggedUser) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(30));

        Label titleLabel = new Label("About Finanger");
        titleLabel.setFont(new Font(20));

        Label descLabel = new Label(
                "Finanger is an AI-powered personal finance manager designed to help you take control " +
                        "of your money with ease. Whether you input transactions manually or import files, " +
                        "Finanger keeps everything organized in one place.\n\n" +
                        "Using smart AI, Finanger automatically categorizes your expenses, detects spending " +
                        "patterns, and suggests personalized budgets and saving tips. Of course, you stay in " +
                        "control — review and adjust any misclassifications at any time.\n\n" +
                        "Smarter finance starts here. With Finanger, you’re not just tracking money — " +
                        "you’re mastering it."
        );
        descLabel.setFont(new Font(14));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(500);

        // 按钮区
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        Button resetBtn = new Button("Reset to Default");
        resetBtn.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;");

        Button backBtn = new Button("Back to Mainpage");
        backBtn.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> {
            // 回到 Status 界面
            StatusScene statusScene = new StatusScene(stage, 800, 450, loggedUser);
            stage.setScene(statusScene.createScene());
            StatusService statusService = new StatusService(statusScene, loggedUser); // 初始化服务
            stage.setTitle("Finanger - Status"); // 可选：设置标题
        });

        buttonBox.getChildren().addAll(resetBtn, backBtn);

        container.getChildren().addAll(titleLabel, descLabel, buttonBox);
        return container;
    }
}