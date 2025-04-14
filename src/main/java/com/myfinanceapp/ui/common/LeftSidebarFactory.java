package com.myfinanceapp.ui.common;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.loginscene.LoginScene;
import com.myfinanceapp.ui.settingscene.SystemSettings;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.ui.goalsscene.Goals;
import com.myfinanceapp.ui.transactionscene.TransactionScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.myfinanceapp.ui.common.SceneManager;
import javafx.scene.Scene;

import java.util.Objects;

public class LeftSidebarFactory {

    /**
     * 创建左侧边栏，支持传入一个 selectedButton 表示哪个按钮是"选中"。
     * 可取值如 "Status", "Goals", "New", "Settings", "Logout" 等
     */
    public static VBox createLeftSidebar(Stage stage, String selectedButton, User loggedUser) {
        VBox sideBar = new VBox(15);
        sideBar.setPadding(new Insets(20, 0, 20, 15));
        sideBar.setAlignment(Pos.TOP_LEFT);
        sideBar.setPrefWidth(170);

        // 在 sideBar 的右侧画一条 2px 蓝色竖线
        sideBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 0 2 0 0;"
        );

        /*// 顶部文字
        Label welcomeLabel = new Label("Only \nyou can do!");
        welcomeLabel.setFont(new Font(18));
        welcomeLabel.setTextFill(javafx.scene.paint.Color.DARKBLUE);*/
        // 根据 selectedButton 设置顶部文字
        String labelText;
        switch (selectedButton) {
            case "Status":
                labelText = "Welcome back!";
                break;
            case "Goals":
                labelText = "It's My Goal!!!!!";
                break;
            case "New":
                labelText = "Every day is a\nnew beginning";
                break;
            case "Settings":
                labelText = "Only you can do!";
                break;
            default:
                labelText = "Only \nyou can do!"; // 或你想要的默认值
                break;
        }

        Label welcomeLabel = new Label(labelText);
        welcomeLabel.setFont(new Font(18));
        welcomeLabel.setTextFill(javafx.scene.paint.Color.DARKBLUE);


        // 创建五个按钮，判断哪个是选中
        // 例：String "Settings" 表示 Settings 选中
        HBox statusBox   = createSidebarButtonBox(stage, "Status",   "status_icon_default.png",   "status_icon_selected.png",   selectedButton.equals("Status"),loggedUser);
        HBox goalsBox    = createSidebarButtonBox(stage, "Goals",    "goals_icon_default.png",    "goals_icon_selected.png",    selectedButton.equals("Goals"),loggedUser);
        HBox newBox      = createSidebarButtonBox(stage, "New",      "new_icon_default.png",      "new_icon_selected.png",      selectedButton.equals("New"),loggedUser);
        HBox settingsBox = createSidebarButtonBox(stage, "Settings", "settings_icon_default.png", "settings_icon_selected.png", selectedButton.equals("Settings"),loggedUser);
        HBox logoutBox   = createSidebarButtonBox(stage, "Log out",  "logout_icon_default.png",   "logout_icon_selected.png",   selectedButton.equals("Log out"),loggedUser);

        sideBar.getChildren().addAll(
                welcomeLabel,
                statusBox,
                goalsBox,
                newBox,
                settingsBox,
                logoutBox
        );
        return sideBar;
    }

    /**
     * 生成单个按钮Box，可根据 isActive 决定是否覆盖竖线
     */
    private static HBox createSidebarButtonBox(Stage stage, String text, String defaultIcon, String selectedIcon, boolean isActive,User loggedUser) {
        Label label = new Label(text);
        label.setFont(new Font(14));
        label.setPrefSize(isActive ? 172 : 170, 40); // 选中时多2px
        label.setAlignment(Pos.CENTER_LEFT);
        label.setPadding(new Insets(0, 10, 0, 20));

        String iconFile = isActive ? selectedIcon : defaultIcon;
        var url = Objects.requireNonNull(
                LeftSidebarFactory.class.getResource("/pictures/" + iconFile),
                "Resource /pictures/" + iconFile + " not found!"
        );
        ImageView iconView = new ImageView(new Image(url.toExternalForm()));
        iconView.setFitWidth(18);
        iconView.setFitHeight(18);
        label.setGraphic(iconView);
        label.setGraphicTextGap(10);

        if (isActive) {
            // 选中样式
            label.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-text-fill: #3282FA; " +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                            "-fx-border-width: 2 0 2 2; " +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
        } else {
            // 未选中样式
            label.setStyle(
                    "-fx-background-color: #E0F0FF; " +
                            "-fx-text-fill: black; " +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                            "-fx-border-width: 2 0 2 2; " +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
        }

        // 点击事件，根据按钮 text 做不同跳转
        label.setOnMouseClicked(e -> {
            // 获取当前窗口的实际大小
            double currentWidth = stage.getScene().getWidth();
            double currentHeight = stage.getScene().getHeight();
            
            switch (text) {
                case "Status":
                    // 跳转 Status
                    StatusScene statusScene = new StatusScene(stage, currentWidth, currentHeight, loggedUser);
                    Scene newStatusScene = statusScene.createScene();
                    SceneManager.switchScene(stage, newStatusScene);
                    StatusService statusService = new StatusService(statusScene, loggedUser); // 初始化服务
                    break;
                case "Goals":
                    Scene goalsScene = Goals.createScene(stage, currentWidth, currentHeight, loggedUser);
                    SceneManager.switchScene(stage, goalsScene);
                    break;
                case "New":
                    Scene transactionScene = TransactionScene.createScene(stage, currentWidth, currentHeight, loggedUser);
                    SceneManager.switchScene(stage, transactionScene);
                    break;
                case "Settings":
                    Scene settingsScene = SystemSettings.createScene(stage, currentWidth, currentHeight, loggedUser);
                    SceneManager.switchScene(stage, settingsScene);
                    break;
                case "Log out":
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?");
                    confirm.setHeaderText(null);
                    confirm.setTitle("Confirm Logout");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            Scene loginScene = LoginScene.createScene(stage, currentWidth, currentHeight);
                            SceneManager.switchScene(stage, loginScene);
                            stage.setTitle("Finanger - Login");
                        }
                    });
                    break;
            }
        });

        HBox box = new HBox(label);
        box.setAlignment(Pos.CENTER_LEFT);
        if (isActive) {
            box.setTranslateX(2);
        }
        return box;
    }
}
