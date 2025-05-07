package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SettingsTopBarFactory;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class About {
    // 重载方法，兼容旧的调用方式
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService(), new CurrencyService("USD"));
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService) {
        return createScene(stage, width, height, loggedUser, themeService, new CurrencyService("USD"));
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        // 整体 BorderPane
        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // 左侧边栏: Settings选中 (与 SystemSettings 相同)
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Settings", loggedUser, themeService, currencyService);
        root.setLeft(sideBar);

        // 中心容器: 垂直组合 (topBar, outerBox)，放入 centerBox
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);

        // Tab栏: About 选中
        HBox topBar = SettingsTopBarFactory.createTopBar(stage, "About", loggedUser, themeService, currencyService);

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
                        themeService.getCurrentFormBackgroundStyle()
        );

        // 中心内容：About 文本
        Pane aboutContent = createAboutContent(stage, width, height, loggedUser, themeService, currencyService);

        outerBox.getChildren().addAll(aboutContent);
        container.getChildren().addAll(topBar, outerBox);

        centerBox.getChildren().add(container);
        root.setCenter(centerBox);

        return new Scene(root, width, height);
    }

    /**
     * 生成 About 界面的正文内容
     */
    private static Pane createAboutContent(Stage stage, double width, double height, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(30));

        Label titleLabel = new Label("About Finanger");
        titleLabel.setFont(new Font(20));
        titleLabel.setStyle(themeService.getTextColorStyle());

        Label descLabel = new Label(
                "Finanger is an AI-powered personal finance manager designed to help you take control " +
                        "of your money with ease. Whether you input transactions manually or import files, " +
                        "Finanger keeps everything organized in one place.\n\n" +
                        "Using smart AI, Finanger automatically categorizes your expenses, detects spending " +
                        "patterns, and suggests personalized budgets and saving tips. Of course, you stay in " +
                        "control — review and adjust any misclassifications at any time.\n\n" +
                        "Smarter finance starts here. With Finanger, you're not just tracking money — " +
                        "you're mastering it."
        );
        descLabel.setFont(new Font(14));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(500);
        descLabel.setStyle(themeService.getTextColorStyle());

        // Wrap descLabel in a ScrollPane to enable scrolling
        ScrollPane scrollPane = new ScrollPane(descLabel);
        scrollPane.setFitToWidth(true); // Ensure the content fits the width of the ScrollPane
        scrollPane.setPrefViewportHeight(250); // Set a reasonable height for the viewport
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // 按钮区
        VBox buttonBox = new VBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button backBtn = new Button("Back to Status");
        backBtn.setStyle(themeService.getButtonStyle());
        backBtn.setOnAction(e -> {
            StatusScene statusScene = new StatusScene(stage, width, height, loggedUser);
            stage.setScene(statusScene.createScene(themeService, currencyService));
            StatusService statusService = new StatusService(statusScene, loggedUser, currencyService);
            stage.setTitle("Finanger - Status");
        });

        buttonBox.getChildren().add(backBtn);

        container.getChildren().addAll(titleLabel, scrollPane, buttonBox);
        return container;
    }
}