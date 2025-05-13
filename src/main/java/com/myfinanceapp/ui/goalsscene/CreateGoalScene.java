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

/**
 * Scene class for creating new financial goals.
 * This class provides functionality for:
 * - Creating new financial goals with different types (saving, debt repayment, budget control)
 * - Managing goal creation form with validation
 * - Supporting multiple currencies and languages
 * - Handling theme-specific styling
 * - Enforcing minimum window dimensions
 */
public class CreateGoalScene {
    private static final Logger logger = LoggerFactory.getLogger(CreateGoalScene.class);
    private static final Font LABEL_FONT = Font.font("Arial", 14);
    private static final LanguageService languageService = LanguageService.getInstance();
    private static final String[] GOAL_TYPES = {
        languageService.getTranslation("saving_goal"),
        languageService.getTranslation("debt_repayment_goal"),
        languageService.getTranslation("budget_control_goal")
    };
    private static final String[] CURRENCIES = {"CNY", "USD", "EUR", "JPY", "GBP"};

    // UI Constants
    private static final double FORM_MAX_WIDTH = 600;
    private static final double BUTTON_WIDTH = 120;
    private static final double FIELD_WIDTH = 250;
    private static final double MAIN_PADDING = 40;
    private static final double MIN_WINDOW_WIDTH = 800;
    private static final double MIN_WINDOW_HEIGHT = 450;

    private static ThemeService themeService;

    /**
     * Creates a scene for creating new goals with default theme and currency services.
     * This is an overloaded method that provides default service instances.
     *
     * @param stage The main application stage
     * @param width The desired scene width
     * @param height The desired scene height
     * @param loggedUser The currently logged-in user
     * @return A Scene object for creating new goals
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService(), new CurrencyService("CNY"));
    }

    /**
     * Creates a scene for creating new goals with a custom theme service.
     * This is an overloaded method that provides a default currency service.
     *
     * @param stage The main application stage
     * @param width The desired scene width
     * @param height The desired scene height
     * @param loggedUser The currently logged-in user
     * @param themeService The service for managing application theme
     * @return A Scene object for creating new goals
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService) {
        return createScene(stage, width, height, loggedUser, themeService, new CurrencyService("CNY"));
    }

    /**
     * Creates a scene for creating new goals with custom theme and currency services.
     * This method sets up the complete UI layout including form fields, validation,
     * and navigation controls.
     *
     * @param stage The main application stage
     * @param width The desired scene width
     * @param height The desired scene height
     * @param loggedUser The currently logged-in user
     * @param themeService The service for managing application theme
     * @param currencyService The service for handling currency conversions
     * @return A Scene object for creating new goals
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        CreateGoalScene.themeService = themeService;
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
        Label titleLabel = new Label(languageService.getTranslation("create_new_goal"));
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
        ComboBox<String> typeCombo = createComboBox(GOAL_TYPES, 0, grid, languageService.getTranslation("goal_type"), LABEL_FONT, themeService);
        typeCombo.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Goal title field
        TextField titleField = createTextField(languageService.getTranslation("goal_title_prompt"), 1, grid, languageService.getTranslation("goal_title"), LABEL_FONT, themeService);
        titleField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Target amount field
        TextField amountField = createTextField(languageService.getTranslation("target_amount_prompt"), 2, grid, languageService.getTranslation("target_amount"), LABEL_FONT, themeService);
        amountField.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Currency selection
        ComboBox<String> currencyCombo = createComboBox(CURRENCIES, 3, grid, languageService.getTranslation("currency"), LABEL_FONT, themeService);
        currencyCombo.getSelectionModel().selectFirst();
        currencyCombo.prefWidthProperty().bind(grid.widthProperty().multiply(0.6));

        // Deadline date picker
        DatePicker deadlinePicker = createDatePicker(4, grid, languageService.getTranslation("deadline"), LABEL_FONT, themeService);
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

        Button saveButton = createButton(languageService.getTranslation("save_goal"), themeService.getButtonStyle(), event -> {
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

                    // Navigate back to goals list with ThemeService and CurrencyService
                    Scene goalsScene = Goals.createScene(stage, stage.getScene().getWidth(), stage.getScene().getHeight(), loggedUser, themeService, currencyService);
                    SceneManager.switchScene(stage, goalsScene);
                } catch (IOException e) {
                    logger.error("Failed to save goal", e);
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

        // Add dynamic theme stylesheet for ComboBox, DatePicker, and TextField
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
        stage.setTitle("Finanger - " + languageService.getTranslation("create_new_goal"));

        return scene;
    }

    /**
     * Creates a styled ComboBox with the specified items and adds it to the form grid.
     *
     * @param items Array of items to populate the ComboBox
     * @param row Grid row index for placement
     * @param grid The GridPane to add the ComboBox to
     * @param labelText Text for the ComboBox label
     * @param font Font to use for the label
     * @param themeService Service for managing application theme
     * @return The created ComboBox
     */
    private static ComboBox<String> createComboBox(String[] items, int row, GridPane grid, String labelText, Font font, ThemeService themeService) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(items);
        comboBox.getSelectionModel().selectFirst();
        comboBox.setPrefWidth(FIELD_WIDTH);
        // Apply theme style class
        comboBox.getStyleClass().add(themeService.isDayMode() ? "day-theme-combo-box" : "night-theme-combo-box");
        addStyledRow(grid, row, labelText, comboBox, font, themeService);
        return comboBox;
    }

    /**
     * Creates a styled TextField and adds it to the form grid.
     *
     * @param promptText Placeholder text for the TextField
     * @param row Grid row index for placement
     * @param grid The GridPane to add the TextField to
     * @param labelText Text for the TextField label
     * @param font Font to use for the label
     * @param themeService Service for managing application theme
     * @return The created TextField
     */
    private static TextField createTextField(String promptText, int row, GridPane grid, String labelText, Font font, ThemeService themeService) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setPrefWidth(FIELD_WIDTH);
        // Apply custom style for TextField with border
        String textFieldStyle = themeService.isDayMode() ?
                "-fx-background-color: white; -fx-text-fill: black; -fx-prompt-text-fill: #555555; -fx-border-color: #D3D3D3; -fx-border-radius: 3;" :
                "-fx-background-color: #3C3C3C; -fx-text-fill: white; -fx-prompt-text-fill: #CCCCCC; -fx-border-color: #555555; -fx-border-radius: 3;";
        textField.setStyle(textFieldStyle);
        addStyledRow(grid, row, labelText, textField, font, themeService);
        return textField;
    }

    /**
     * Creates a styled DatePicker and adds it to the form grid.
     *
     * @param row Grid row index for placement
     * @param grid The GridPane to add the DatePicker to
     * @param labelText Text for the DatePicker label
     * @param font Font to use for the label
     * @param themeService Service for managing application theme
     * @return The created DatePicker
     */
    private static DatePicker createDatePicker(int row, GridPane grid, String labelText, Font font, ThemeService themeService) {
        DatePicker datePicker = new DatePicker(LocalDate.now().plusMonths(1));
        datePicker.setPrefWidth(FIELD_WIDTH);
        // Apply theme style class
        datePicker.getStyleClass().add(themeService.isDayMode() ? "day-theme-date-picker" : "night-theme-date-picker");
        addStyledRow(grid, row, labelText, datePicker, font, themeService);
        return datePicker;
    }

    /**
     * Creates a styled Button with the specified text and action handler.
     *
     * @param text Text to display on the button
     * @param style CSS style string for the button
     * @param action Event handler for button clicks
     * @return The created Button
     */
    private static Button createButton(String text, String style, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        if (style != null) {
            button.setStyle(style);
        }
        button.setPrefWidth(BUTTON_WIDTH);
        button.setOnAction(action);
        return button;
    }

    /**
     * Adds a styled row to the form grid with a label and control.
     *
     * @param grid The GridPane to add the row to
     * @param row Grid row index for placement
     * @param labelText Text for the label
     * @param control The control to add (TextField, ComboBox, etc.)
     * @param font Font to use for the label
     * @param themeService Service for managing application theme
     */
    private static void addStyledRow(GridPane grid, int row, String labelText, Control control, Font font, ThemeService themeService) {
        Label label = new Label(labelText);
        label.setFont(font);
        label.setStyle(themeService.getTextColorStyle());
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }
}