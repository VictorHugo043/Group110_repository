package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.GoalService;
import com.myfinanceapp.service.GoalFormService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SceneManager;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.IOException;
import java.time.LocalDate;

public class EditGoalScene {
    private static final Logger logger = LoggerFactory.getLogger(EditGoalScene.class);
    private static final Font LABEL_FONT = Font.font("Arial", 14);
    private static final Color LABEL_COLOR = Color.DARKBLUE;
    private static final String[] CURRENCIES = {"CNY", "USD", "EUR", "JPY", "GBP"};
    
    // UI Constants
    private static final String BACKGROUND_STYLE = "-fx-background-color: white;";
    private static final String SAVE_BUTTON_STYLE = "-fx-background-color: #3282FA; -fx-text-fill: white;";
    private static final double FORM_MAX_WIDTH = 600;
    private static final double BUTTON_WIDTH = 120;
    private static final double FIELD_WIDTH = 250;
    private static final double MAIN_PADDING = 40;
    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 450;

    public static Scene createScene(Stage stage, double width, double height, User loggedUser, Goal goalToEdit) {
        // 确保窗口大小不小于最小值
        final double finalWidth = Math.max(width, MIN_WINDOW_WIDTH);
        final double finalHeight = Math.max(height, MIN_WINDOW_HEIGHT);
        
        BorderPane root = new BorderPane();
        root.setStyle(BACKGROUND_STYLE);

        // Left sidebar
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Goals", loggedUser);
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
        Label titleLabel = new Label("Edit Goal");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.DARKBLUE);

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
        typeLabel.setTextFill(LABEL_COLOR);
        
        Tooltip tooltip = new Tooltip("Goal type cannot be modified");
        tooltip.setStyle("-fx-font-size: 12px;");
        tooltip.setShowDelay(javafx.util.Duration.millis(100));
        tooltip.setHideDelay(javafx.util.Duration.millis(200));
        typeLabel.setTooltip(tooltip);
        
        Label typeTitleLabel = new Label("Type of your goal:");
        typeTitleLabel.setFont(LABEL_FONT);
        typeTitleLabel.setTextFill(LABEL_COLOR);
        
        grid.add(typeTitleLabel, 0, 0);
        grid.add(typeLabel, 1, 0);

        // Goal title field
        TextField titleField = createTextField("Goal Title", 1, grid, "Goal title:", LABEL_FONT, LABEL_COLOR);
        titleField.setText(goalToEdit.getTitle());
        titleField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Target amount field
        TextField amountField = createTextField("Target Amount", 2, grid, "Target amount:", LABEL_FONT, LABEL_COLOR);
        amountField.setText(String.valueOf(goalToEdit.getTargetAmount()));
        amountField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));
        
        // Currency selection
        ComboBox<String> currencyCombo = createComboBox(CURRENCIES, 3, grid, "Currency:", LABEL_FONT, LABEL_COLOR);
        currencyCombo.setValue(goalToEdit.getCurrency());
        currencyCombo.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Deadline date picker
        DatePicker deadlinePicker = createDatePicker(4, grid, "Deadline:", LABEL_FONT, LABEL_COLOR);
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
        TextField categoryField = createTextField("Category (for Budget Control)", 5, grid, "Category:", LABEL_FONT, LABEL_COLOR);
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

        Button saveButton = createButton("Save Changes", SAVE_BUTTON_STYLE, event -> {
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

                    // Navigate back to goals list
                    Scene goalsScene = Goals.createScene(stage, stage.getScene().getWidth(), stage.getScene().getHeight(), loggedUser);
                    SceneManager.switchScene(stage, goalsScene);
                } catch (IOException e) {
                    logger.error("Failed to update goal", e);
                }
            }
        });

        Button cancelButton = createButton("Cancel", null, event -> {
            // Navigate back to goals list
            Scene goalsScene = Goals.createScene(stage, stage.getScene().getWidth(), stage.getScene().getHeight(), loggedUser);
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

    private static String getGoalTypeDisplay(String type) {
        switch (type) {
            case "SAVING":
                return "Saving Goal";
            case "DEBT_REPAYMENT":
                return "Debt Repayment Goal";
            case "BUDGET_CONTROL":
                return "Budget Control Goal";
            default:
                return type;
        }
    }

    private static ComboBox<String> createComboBox(String[] items, int row, GridPane grid, String labelText, Font font, Color color) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(items);
        comboBox.setPrefWidth(FIELD_WIDTH);
        addStyledRow(grid, row, labelText, comboBox, font, color);
        return comboBox;
    }

    private static TextField createTextField(String promptText, int row, GridPane grid, String labelText, Font font, Color color) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefWidth(FIELD_WIDTH);
        addStyledRow(grid, row, labelText, textField, font, color);
        return textField;
    }

    private static DatePicker createDatePicker(int row, GridPane grid, String labelText, Font font, Color color) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(FIELD_WIDTH);
        addStyledRow(grid, row, labelText, datePicker, font, color);
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

    private static void addStyledRow(GridPane grid, int row, String labelText, Control control, Font font, Color color) {
        Label label = new Label(labelText);
        label.setFont(font);
        label.setTextFill(color);
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }
} 