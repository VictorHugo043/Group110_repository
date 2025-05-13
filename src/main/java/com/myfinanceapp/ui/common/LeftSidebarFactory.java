package com.myfinanceapp.ui.common;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.loginscene.LoginScene;
import com.myfinanceapp.ui.settingscene.SystemSettings;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.ui.goalsscene.Goals;
import com.myfinanceapp.ui.transactionscene.TransactionScene;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;
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
    private static final LanguageService languageService = LanguageService.getInstance();

    /**
     * 创建左侧边栏，支持传入一个 selectedButton 表示哪个按钮是"选中"。
     * 可取值如 "Status", "Goals", "New", "Settings", "Logout" 等
     * 重载方法，兼容旧的调用方式
     */
    public static VBox createLeftSidebar(Stage stage, String selectedButton, User loggedUser) {
        return createLeftSidebar(stage, selectedButton, loggedUser, new ThemeService(), new CurrencyService("CNY"));
    }

    /**
     * 创建左侧边栏，支持传入一个 selectedButton 表示哪个按钮是"选中"。
     * 可取值如 "Status", "Goals", "New", "Settings", "Logout" 等
     * 增加 CurrencyService 参数以确保货币设置的一致性
     */
    public static VBox createLeftSidebar(Stage stage, String selectedButton, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        VBox sideBar = new VBox(15);
        sideBar.setPadding(new Insets(20, 0, 20, 15));
        sideBar.setAlignment(Pos.TOP_LEFT);
        sideBar.setPrefWidth(170);

        // 在 sideBar 的右侧画一条 2px 蓝色竖线
        sideBar.setStyle(
                themeService.getCurrentThemeStyle() +
                        "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 0 2 0 0;"
        );

        // 根据 selectedButton 设置顶部文字
        String labelText = getWelcomeMessage(selectedButton);
        Label welcomeLabel = new Label(labelText);
        welcomeLabel.setFont(new Font(18));
        welcomeLabel.setStyle(themeService.getTextColorStyle());

        // 创建五个按钮，判断哪个是选中
        HBox statusBox   = createSidebarButtonBox(stage, "status",   "status_icon_default.png",   "status_icon_selected.png",   selectedButton.equals("Status"), loggedUser, themeService, currencyService);
        HBox goalsBox    = createSidebarButtonBox(stage, "goals",    "goals_icon_default.png",    "goals_icon_selected.png",    selectedButton.equals("Goals"), loggedUser, themeService, currencyService);
        HBox settingsBox = createSidebarButtonBox(stage, "settings", "settings_icon_default.png", "settings_icon_selected.png", selectedButton.equals("Settings"), loggedUser, themeService, currencyService);
        HBox newBox      = createSidebarButtonBox(stage, "new",      "new_icon_default.png",      "new_icon_selected.png",      selectedButton.equals("New"), loggedUser, themeService, currencyService);
        HBox logoutBox   = createSidebarButtonBox(stage, "logout",   "logout_icon_default.png",   "logout_icon_selected.png",   selectedButton.equals("Log out"), loggedUser, themeService, currencyService);

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
     * 根据选中的按钮获取欢迎消息
     */
    private static String getWelcomeMessage(String selectedButton) {
        String messageKey;
        switch (selectedButton) {
            case "Status":
                messageKey = "welcome_message_status";
                break;
            case "Goals":
                messageKey = "welcome_message_goals";
                break;
            case "New":
                messageKey = "welcome_message_new";
                break;
            case "Settings":
                messageKey = "welcome_message_settings";
                break;
            default:
                messageKey = "welcome_message_default";
                break;
        }
        return languageService.getTranslation(messageKey);
    }

    /**
     * 生成单个按钮Box，可根据 isActive 决定是否覆盖竖线
     * 增加 CurrencyService 参数以传递给目标场景
     */
    private static HBox createSidebarButtonBox(Stage stage, String translationKey, String defaultIcon, String selectedIcon, boolean isActive, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        Label label = new Label(languageService.getTranslation(translationKey));
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
                    themeService.getCurrentThemeStyle() +
                            "-fx-text-fill: #3282FA; " +
                            "-fx-border-color: #3282FA transparent #3282FA #3282FA; " +
                            "-fx-border-width: 2 0 2 2; " +
                            "-fx-border-radius: 8 0 0 8;" +
                            "-fx-background-radius: 8 0 0 8;"
            );
        } else {
            // 未选中样式
            label.setStyle(
                    "-fx-background-color: " + (themeService.isDayMode() ? "#E0F0FF" : "#4A6FA5") + "; " +
                            themeService.getTextColorStyle() +
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

            switch (translationKey) {
                case "status":
                    // 跳转 Status
                    StatusScene statusScene = new StatusScene(stage, currentWidth, currentHeight, loggedUser);
                    Scene newStatusScene = statusScene.createScene(themeService, currencyService);
                    SceneManager.switchScene(stage, newStatusScene);
                    StatusService statusService = new StatusService(statusScene, loggedUser, currencyService, languageService);
                    break;
                case "goals":
                    Scene goalsScene = Goals.createScene(stage, currentWidth, currentHeight, loggedUser, themeService, currencyService);
                    SceneManager.switchScene(stage, goalsScene);
                    break;
                case "new":
                    Scene transactionScene = TransactionScene.createScene(stage, currentWidth, currentHeight, loggedUser, themeService, currencyService);
                    SceneManager.switchScene(stage, transactionScene);
                    break;
                case "settings":
                    Scene settingsScene = SystemSettings.createScene(stage, currentWidth, currentHeight, loggedUser, themeService, currencyService);
                    SceneManager.switchScene(stage, settingsScene);
                    break;
                case "logout":
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, languageService.getTranslation("logout_confirmation"));
                    confirm.setHeaderText(null);
                    confirm.setTitle(languageService.getTranslation("logout_title"));
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            Scene loginScene = LoginScene.createScene(stage, currentWidth, currentHeight);
                            SceneManager.switchScene(stage, loginScene);
                            stage.setTitle("Finanger - " + languageService.getTranslation("login"));
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

    public static void updateLanguage(VBox sideBar, LanguageService languageService) {
        // 更新欢迎消息
        Label welcomeLabel = (Label) sideBar.getChildren().get(0);
        String currentText = welcomeLabel.getText();
        String messageKey = getMessageKeyFromText(currentText);
        welcomeLabel.setText(languageService.getTranslation(messageKey));

        // 更新导航按钮
        for (int i = 1; i < sideBar.getChildren().size(); i++) {
            HBox buttonBox = (HBox) sideBar.getChildren().get(i);
            Label buttonLabel = (Label) buttonBox.getChildren().get(0);
            
            // 根据按钮的文本内容确定对应的翻译键
            String currentButtonText = buttonLabel.getText();
            String translationKey = getTranslationKeyFromText(currentButtonText);
            
            if (!translationKey.isEmpty()) {
                String newText = languageService.getTranslation(translationKey);
                buttonLabel.setText(newText);
            }
        }
    }

    /**
     * 根据当前文本获取对应的消息键
     */
    private static String getMessageKeyFromText(String text) {
        if (text.contains("Welcome back") || text.contains("欢迎回来")) {
            return "welcome_message_status";
        } else if (text.contains("It's My Goal") || text.contains("这是我的目标")) {
            return "welcome_message_goals";
        } else if (text.contains("Every day") || text.contains("每一天都是")) {
            return "welcome_message_new";
        } else if (text.contains("Only you can do") || text.contains("只有你能做到")) {
            return "welcome_message_settings";
        }
        return "welcome_message_default";
    }

    /**
     * 根据当前文本获取对应的翻译键
     */
    private static String getTranslationKeyFromText(String text) {
        if (text.contains("Status") || text.contains("状态")) {
            return "status";
        } else if (text.contains("Goals") || text.contains("目标")) {
            return "goals";
        } else if (text.contains("New") || text.contains("新建")) {
            return "new";
        } else if (text.contains("Settings") || text.contains("设置")) {
            return "settings";
        } else if (text.contains("Log out") || text.contains("退出登录")) {
            return "logout";
        }
        return "";
    }
}