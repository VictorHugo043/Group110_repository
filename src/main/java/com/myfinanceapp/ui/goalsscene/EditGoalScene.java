package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.GoalService;
import com.myfinanceapp.service.GoalFormService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SceneManager;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.IOException;
import java.time.LocalDate;

public class EditGoalScene {
    private static final Logger logger = LoggerFactory.getLogger(EditGoalScene.class);
    private static final Font LABEL_FONT = Font.font("Arial", 14);
    private static final LanguageService languageService = LanguageService.getInstance();
    private static final String[] CURRENCIES = {"CNY", "USD", "EUR", "JPY", "GBP"};

    // UI Constants
    private static final double FORM_MAX_WIDTH = 600;
    private static final double BUTTON_WIDTH = 120;
    private static final double FIELD_WIDTH = 250;
    private static final double MAIN_PADDING = 40;
    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 450;

    // 存储ThemeService实例
    private static ThemeService themeService;

    // 重载方法，兼容旧的调用方式
    public static Scene createScene(Stage stage, double width, double height, User loggedUser, Goal goalToEdit) {
        return createScene(stage, width, height, loggedUser, goalToEdit, new ThemeService(), new CurrencyService("CNY"));
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser, Goal goalToEdit, ThemeService themeService) {
        return createScene(stage, width, height, loggedUser, goalToEdit, themeService, new CurrencyService("CNY"));
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser, Goal goalToEdit, ThemeService themeService, CurrencyService currencyService) {
        EditGoalScene.themeService = themeService; // 存储ThemeService实例

        // 确保窗口大小不小于最小值
        final double finalWidth = Math.max(width, MIN_WINDOW_WIDTH);
        final double finalHeight = Math.max(height, MIN_WINDOW_HEIGHT);

        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // Left sidebar
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Goals", loggedUser, themeService, currencyService);
        root.setLeft(sideBar);

        // Main container
        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(MAIN_PADDING));

        // 绑定主容器最大宽度到窗口宽度
        mainBox.maxWidthProperty().bind(
                root.widthProperty()
                        .subtract(sideBar.widthProperty())
                        .subtract(MAIN_PADDING * 2)
        );

        // Title
        Label titleLabel = new Label(languageService.getTranslation("edit_goal"));
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setStyle(themeService.getTextColorStyle());

        // Form container
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // 绑定网格宽度到主容器宽度
        grid.prefWidthProperty().bind(mainBox.maxWidthProperty());

        // Goal type label (disabled)
        Label typeLabel = new Label(getGoalTypeDisplay(goalToEdit.getType()));
        typeLabel.setFont(LABEL_FONT);
        typeLabel.setStyle(themeService.getTextColorStyle());

        Tooltip tooltip = new Tooltip(languageService.getTranslation("goal_type_cannot_modify"));
        tooltip.setStyle("-fx-font-size: 12px;" + themeService.getTextColorStyle());
        tooltip.setShowDelay(javafx.util.Duration.millis(100));
        tooltip.setHideDelay(javafx.util.Duration.millis(200));
        typeLabel.setTooltip(tooltip);

        Label typeTitleLabel = new Label(languageService.getTranslation("goal_type"));
        typeTitleLabel.setFont(LABEL_FONT);
        typeTitleLabel.setStyle(themeService.getTextColorStyle());

        grid.add(typeTitleLabel, 0, 0);
        grid.add(typeLabel, 1, 0);

        // Goal title field
        TextField titleField = createTextField(languageService.getTranslation("goal_title_prompt"), 1, grid, languageService.getTranslation("goal_title"), LABEL_FONT, themeService);
        titleField.setText(goalToEdit.getTitle());
        titleField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Target amount field
        TextField amountField = createTextField(languageService.getTranslation("target_amount_prompt"), 2, grid, languageService.getTranslation("target_amount"), LABEL_FONT, themeService);
        amountField.setText(String.valueOf(goalToEdit.getTargetAmount()));
        amountField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Currency selection
        ComboBox<String> currencyCombo = createComboBox(CURRENCIES, 3, grid, languageService.getTranslation("currency"), LABEL_FONT, themeService);
        currencyCombo.setValue(goalToEdit.getCurrency());
        currencyCombo.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Deadline date picker
        DatePicker deadlinePicker = createDatePicker(4, grid, languageService.getTranslation("deadline"), LABEL_FONT, themeService);
        deadlinePicker.setValue(goalToEdit.getDeadline());
        deadlinePicker.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Ensure deadline is in the future
        deadlinePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // Category field (only visible for budget control goals)
        TextField categoryField = createTextField(languageService.getTranslation("category_prompt"), 5, grid, languageService.getTranslation("category"), LABEL_FONT, themeService);
        categoryField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));
        Label categoryLabel = (Label) grid.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == 5 && GridPane.getColumnIndex(node) == 0)
                .findFirst().orElse(null);

        // Show category field only for budget control goals
        boolean isBudgetControl = goalToEdit.getType().equals("BUDGET_CONTROL");
        if (categoryLabel != null) {
            categoryLabel.setVisible(isBudgetControl);
        }
        categoryField.setVisible(isBudgetControl);
        if (isBudgetControl) {
            categoryField.setText(goalToEdit.getCategory());
        }

        // Buttons area
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.prefWidthProperty().bind(grid.widthProperty());

        Button saveButton = createButton(languageService.getTranslation("save_changes"), themeService.getButtonStyle(), event -> {
            if (GoalFormService.validateForm(loggedUser, titleField, amountField, null, categoryField, deadlinePicker)) {
                try {
                    // Update the existing goal
                    goalToEdit.setTitle(titleField.getText());
                    goalToEdit.setTargetAmount(Double.parseDouble(amountField.getText()));
                    goalToEdit.setDeadline(deadlinePicker.getValue());
                    goalToEdit.setCurrency(currencyCombo.getValue());
                    if (isBudgetControl) {
                        goalToEdit.setCategory(categoryField.getText());
                    }

                    // Save the updated goal
                    GoalService.updateGoal(goalToEdit, loggedUser);

                    // Navigate back to goals list with ThemeService and CurrencyService
                    Scene goalsScene = Goals.createScene(stage, stage.getScene().getWidth(), stage.getScene().getHeight(), loggedUser, themeService, currencyService);
                    SceneManager.switchScene(stage, goalsScene);
                } catch (IOException e) {
                    logger.error(languageService.getTranslation("failed_to_update_goal"), e);
                }
            }
        });

        Button cancelButton = createButton(languageService.getTranslation("cancel"), themeService.getButtonStyle(), event -> {
            // Navigate back to goals list with ThemeService and CurrencyService
            Scene goalsScene = Goals.createScene(stage, stage.getScene().getWidth(), stage.getScene().getHeight(), loggedUser, themeService, currencyService);
            SceneManager.switchScene(stage, goalsScene);
        });

        // 绑定按钮宽度
        saveButton.prefWidthProperty().bind(buttonBox.widthProperty().multiply(0.2));
        cancelButton.prefWidthProperty().bind(buttonBox.widthProperty().multiply(0.2));

        buttonBox.getChildren().addAll(saveButton, cancelButton);

        // Assemble the entire UI
        mainBox.getChildren().addAll(titleLabel, grid, buttonBox);

        // Center layout
        VBox centerContainer = new VBox(mainBox);
        centerContainer.setAlignment(Pos.CENTER);
        root.setCenter(centerContainer);

        // 创建场景
        Scene scene = new Scene(root, finalWidth, finalHeight);

        // 添加全局样式表
        scene.getStylesheets().add("data:text/css," + themeService.getThemeStylesheet());

        // 添加窗口大小变化监听器
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < MIN_WINDOW_WIDTH) {
                stage.setWidth(MIN_WINDOW_WIDTH);
            }
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < MIN_WINDOW_HEIGHT) {
                stage.setHeight(MIN_WINDOW_HEIGHT);
            }
        });

        // 设置窗口标题
        stage.setTitle("Finanger - " + languageService.getTranslation("edit_goal"));

        return scene;
    }

    private static String getGoalTypeDisplay(String type) {
        switch (type) {
            case "SAVING":
                return languageService.getTranslation("saving_goal");
            case "DEBT_REPAYMENT":
                return languageService.getTranslation("debt_repayment_goal");
            case "BUDGET_CONTROL":
                return languageService.getTranslation("budget_control_goal");
            default:
                return type;
        }
    }

    private static ComboBox<String> createComboBox(String[] items, int row, GridPane grid, String labelText, Font font, ThemeService themeService) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(items);
        comboBox.setPrefWidth(FIELD_WIDTH);
        // 应用主题样式类
        comboBox.getStyleClass().add(themeService.isDayMode() ? "day-theme-combo-box" : "night-theme-combo-box");
        addStyledRow(grid, row, labelText, comboBox, font, themeService);
        return comboBox;
    }

    private static TextField createTextField(String promptText, int row, GridPane grid, String labelText, Font font, ThemeService themeService) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefWidth(FIELD_WIDTH);
        // 应用自定义样式
        String textFieldStyle = themeService.isDayMode() ?
                "-fx-background-color: white; -fx-text-fill: black; -fx-prompt-text-fill: #555555; -fx-border-color: #D3D3D3; -fx-border-radius: 3;" :
                "-fx-background-color: #3C3C3C; -fx-text-fill: white; -fx-prompt-text-fill: #CCCCCC; -fx-border-color: #555555; -fx-border-radius: 3;";
        textField.setStyle(textFieldStyle);
        addStyledRow(grid, row, labelText, textField, font, themeService);
        return textField;
    }

    private static DatePicker createDatePicker(int row, GridPane grid, String labelText, Font font, ThemeService themeService) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(FIELD_WIDTH);
        // 应用主题样式类
        datePicker.getStyleClass().add(themeService.isDayMode() ? "day-theme-date-picker" : "night-theme-date-picker");
        addStyledRow(grid, row, labelText, datePicker, font, themeService);
        return datePicker;
    }

    private static Button createButton(String text, String style, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        if (style != null) {
            button.setStyle(style);
        }
        button.setPrefWidth(BUTTON_WIDTH);
        button.setOnAction(action);
        return button;
    }

    private static void addStyledRow(GridPane grid, int row, String labelText, Control control, Font font, ThemeService themeService) {
        Label label = new Label(labelText);
        label.setFont(font);
        label.setStyle(themeService.getTextColorStyle());
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }
}