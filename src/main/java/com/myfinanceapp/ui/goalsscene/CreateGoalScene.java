package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.GoalService;
import com.myfinanceapp.service.GoalFormService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SceneManager;
import com.myfinanceapp.service.ThemeService;
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

public class CreateGoalScene {
    private static final Logger logger = LoggerFactory.getLogger(CreateGoalScene.class);
    private static final Font LABEL_FONT = Font.font("Arial", 14);
    private static final String[] GOAL_TYPES = {"Saving Goal", "Debt Repayment Goal", "Budget Control Goal"};
    private static final String[] CURRENCIES = {"CNY", "USD", "EUR", "JPY", "GBP"};

    // UI Constants
    private static final double FORM_MAX_WIDTH = 600;
    private static final double BUTTON_WIDTH = 120;
    private static final double FIELD_WIDTH = 250;
    private static final double MAIN_PADDING = 40;
    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 450;

    private static ThemeService themeService; // Store ThemeService instance

    // 重载方法，兼容旧的调用方式
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService());
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService) {
        CreateGoalScene.themeService = themeService; // Store the ThemeService instance
        // 确保窗口大小不小于最小值
        final double finalWidth = Math.max(width, MIN_WINDOW_WIDTH);
        final double finalHeight = Math.max(height, MIN_WINDOW_HEIGHT);

        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // Left sidebar
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Goals", loggedUser, themeService);
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
        Label titleLabel = new Label("Create New Goal");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setStyle(themeService.getTextColorStyle());

        // Form container
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // 绑定网格宽度到主容器宽度
        grid.prefWidthProperty().bind(mainBox.maxWidthProperty());

        // Goal type selection
        ComboBox<String> typeCombo = createComboBox(GOAL_TYPES, 0, grid, "Type of your goal:", LABEL_FONT, themeService);
        typeCombo.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Goal title field
        TextField titleField = createTextField("Goal Title", 1, grid, "Goal title:", LABEL_FONT, themeService);
        titleField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Target amount field
        TextField amountField = createTextField("Target Amount", 2, grid, "Target amount:", LABEL_FONT, themeService);
        amountField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Currency selection
        ComboBox<String> currencyCombo = createComboBox(CURRENCIES, 3, grid, "Currency:", LABEL_FONT, themeService);
        currencyCombo.getSelectionModel().selectFirst();
        currencyCombo.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Deadline date picker
        DatePicker deadlinePicker = createDatePicker(4, grid, "Deadline:", LABEL_FONT, themeService);
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
        TextField categoryField = createTextField("Category (for Budget Control)", 5, grid, "Category:", LABEL_FONT, themeService);
        categoryField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));
        Label categoryLabel = (Label) grid.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == 5 && GridPane.getColumnIndex(node) == 0)
                .findFirst().orElse(null);

        if (categoryLabel != null) {
            categoryLabel.setVisible(false);
        }
        categoryField.setVisible(false);

        // Show/hide category field based on selected goal type
        typeCombo.setOnAction(e -> {
            boolean isBudgetControl = typeCombo.getValue().equals(GOAL_TYPES[2]); // "Budget Control Goal"
            categoryField.setVisible(isBudgetControl);
            if (categoryLabel != null) {
                categoryLabel.setVisible(isBudgetControl);
            }
        });

        // Buttons area
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.prefWidthProperty().bind(grid.widthProperty());

        Button saveButton = createButton("Save Goal", themeService.getButtonStyle(), event -> {
            if (GoalFormService.validateForm(loggedUser, titleField, amountField, typeCombo, categoryField, deadlinePicker)) {
                try {
                    Goal newGoal = GoalFormService.createNewGoal(
                            loggedUser,
                            titleField.getText(),
                            amountField.getText(),
                            typeCombo.getValue(),
                            categoryField.getText(),
                            deadlinePicker.getValue(),
                            currencyCombo.getValue()
                    );

                    // Save the new goal to storage with user information
                    GoalService.addGoal(newGoal, loggedUser);

                    // Navigate back to goals list with ThemeService
                    Scene goalsScene = Goals.createScene(stage, stage.getScene().getWidth(), stage.getScene().getHeight(), loggedUser, themeService);
                    SceneManager.switchScene(stage, goalsScene);
                } catch (IOException e) {
                    logger.error("Failed to save goal", e);
                }
            }
        });

        Button cancelButton = createButton("Cancel", themeService.getButtonStyle(), event -> {
            // Navigate back to goals list with ThemeService
            Scene goalsScene = Goals.createScene(stage, stage.getScene().getWidth(), stage.getScene().getHeight(), loggedUser, themeService);
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

        return scene;
    }

    private static ComboBox<String> createComboBox(String[] items, int row, GridPane grid, String labelText, Font font, ThemeService themeService) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(items);
        comboBox.getSelectionModel().selectFirst();
        comboBox.setPrefWidth(FIELD_WIDTH);
        addStyledRow(grid, row, labelText, comboBox, font, themeService);
        return comboBox;
    }

    private static TextField createTextField(String promptText, int row, GridPane grid, String labelText, Font font, ThemeService themeService) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefWidth(FIELD_WIDTH);
        addStyledRow(grid, row, labelText, textField, font, themeService);
        return textField;
    }

    private static DatePicker createDatePicker(int row, GridPane grid, String labelText, Font font, ThemeService themeService) {
        DatePicker datePicker = new DatePicker(LocalDate.now().plusMonths(1));
        datePicker.setPrefWidth(FIELD_WIDTH);
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