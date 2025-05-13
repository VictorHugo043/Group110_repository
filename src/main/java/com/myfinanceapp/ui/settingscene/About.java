package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SettingsTopBarFactory;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * An about page interface for the Finanger application.
 * This scene displays information about the application, including its description
 * and version details, with support for theme customization and internationalization.
 */
public class About {
    private static final LanguageService languageService = LanguageService.getInstance();

    /**
     * Creates and returns an about scene with default theme and currency settings.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @param loggedUser The currently logged-in user
     * @return A configured Scene object for the about interface
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService(), new CurrencyService("CNY"));
    }

    /**
     * Creates and returns an about scene with specified theme settings.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @param loggedUser The currently logged-in user
     * @param themeService The theme service to use for styling
     * @return A configured Scene object for the about interface
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService) {
        return createScene(stage, width, height, loggedUser, themeService, new CurrencyService("CNY"));
    }

    /**
     * Creates and returns an about scene with specified theme and currency settings.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @param loggedUser The currently logged-in user
     * @param themeService The theme service to use for styling
     * @param currencyService The currency service to use for the application
     * @return A configured Scene object for the about interface
     */
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
     * Creates the main content for the about page.
     * Includes title, description, and navigation controls.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @param loggedUser The currently logged-in user
     * @param themeService The theme service to use for styling
     * @param currencyService The currency service to use for the application
     * @return A Pane containing the about page content
     */
    private static Pane createAboutContent(Stage stage, double width, double height, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(30));

        Label titleLabel = new Label(languageService.getTranslation("about_finanger"));
        titleLabel.setFont(new Font(20));
        titleLabel.setStyle(themeService.getTextColorStyle());

        Label descLabel = new Label(languageService.getTranslation("about_description"));
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

        Button backBtn = new Button(languageService.getTranslation("back_to_status"));
        backBtn.setStyle(themeService.getButtonStyle());
        backBtn.setOnAction(e -> {
            StatusScene statusScene = new StatusScene(stage, width, height, loggedUser);
            stage.setScene(statusScene.createScene(themeService, currencyService));
            StatusService statusService = new StatusService(statusScene, loggedUser, currencyService, languageService);
            stage.setTitle("Finanger - " + languageService.getTranslation("status"));
        });

        buttonBox.getChildren().add(backBtn);

        container.getChildren().addAll(titleLabel, scrollPane, buttonBox);
        return container;
    }
}