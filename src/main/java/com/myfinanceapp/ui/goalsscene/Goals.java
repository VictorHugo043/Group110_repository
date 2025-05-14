package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.service.GoalService;
import com.myfinanceapp.service.TransactionDataService;
import com.myfinanceapp.ui.common.SceneManager;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.Group;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Main scene class for displaying and managing financial goals.
 * This class provides functionality for:
 * - Displaying a grid of goal cards with progress indicators
 * - Creating new goals
 * - Editing existing goals
 * - Deleting goals
 * - Supporting multiple currencies and languages
 * - Handling theme-specific styling
 * - Responsive layout that adapts to window size
 *
 * @author SE_Group110
 * @version 4.0
 */
public class Goals {
    private static final LanguageService languageService = LanguageService.getInstance();

    /**
     * Creates a scene for displaying goals with default theme and currency services.
     * This is an overloaded method that provides default service instances.
     *
     * @param stage The main application stage
     * @param width The desired scene width
     * @param height The desired scene height
     * @param loggedUser The currently logged-in user
     * @return A Scene object for displaying goals
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService(), new CurrencyService("CNY"));
    }

    /**
     * Creates a scene for displaying goals with custom theme and currency services.
     * This method sets up the complete UI layout including goal cards, progress indicators,
     * and navigation controls.
     *
     * @param stage The main application stage
     * @param width The desired scene width
     * @param height The desired scene height
     * @param loggedUser The currently logged-in user
     * @param themeService The service for managing application theme
     * @param currencyService The service for handling currency conversions
     * @return A Scene object for displaying goals
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // Left navigation bar
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Goals", loggedUser, themeService, currencyService);
        root.setLeft(sideBar);

        // Create grid layout
        GridPane centerGrid = new GridPane();
        centerGrid.setAlignment(Pos.CENTER);
        centerGrid.setHgap(20);
        centerGrid.setVgap(20);
        centerGrid.setPadding(new Insets(20, 20, 20, 20));

        // Get user's goal list
        List<Goal> userGoals = GoalService.getUserGoals(loggedUser);

        // VBox to hold the grid
        VBox centerContent = new VBox(10);
        centerContent.setAlignment(Pos.TOP_CENTER);

        // Initialize column count
        int initialMaxCols = calculateMaxColumns(width);

        // Create a list to store all cards for later relayout
        List<VBox> allCards = new ArrayList<>();

        // If no goals, show a message
        if (userGoals.isEmpty()) {
            Label noGoalsLabel = new Label(languageService.getTranslation("no_goals_found"));
            noGoalsLabel.setStyle("-fx-font-size: 16px;" + themeService.getTextColorStyle());
            centerContent.getChildren().add(noGoalsLabel);
        } else {
            // 创建所有目标卡片
            for (Goal goal : userGoals) {
                try {
                    if (loggedUser == null || goal.getUserId() == null ||
                            loggedUser.getUid().equals(goal.getUserId())) {
                        VBox goalCard = createGoalCard(goal, stage, loggedUser, themeService, currencyService);
                        goalCard.setMinWidth(300);
                        goalCard.setMaxWidth(400);
                        allCards.add(goalCard);
                    }
                } catch (Exception e) {
                    System.err.println("Error displaying goal: " + goal.getTitle());
                    e.printStackTrace();
                }
            }
        }

        // 添加"创建新目标"卡片
        VBox createNewGoalCard = createCreateNewGoalCard(stage, loggedUser, themeService, currencyService);
        createNewGoalCard.setMinWidth(300);
        createNewGoalCard.setMaxWidth(400);
        allCards.add(createNewGoalCard);

        // 初始布局所有卡片
        layoutCards(centerGrid, allCards, initialMaxCols);

        // Add the grid to the center content
        centerContent.getChildren().add(centerGrid);

        // Create a ScrollPane and add the centerContent to it
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Set proper background colors and styling
        String backgroundColor = themeService.isDayMode() ? "white" : "#2A2A2A";
        scrollPane.setStyle("-fx-background: " + backgroundColor + "; -fx-background-color: " + backgroundColor + "; -fx-border-width: 0;");
        centerContent.setStyle("-fx-background-color: " + backgroundColor + ";");

        // Remove any padding that might affect the layout
        scrollPane.setPadding(new Insets(0));

        // Bind scroll pane size to window size
        scrollPane.prefWidthProperty().bind(root.widthProperty().subtract(sideBar.widthProperty()));
        scrollPane.prefHeightProperty().bind(root.heightProperty());

        // Set the scrollPane as the center of the BorderPane with proper alignment
        BorderPane.setAlignment(scrollPane, Pos.CENTER);
        root.setCenter(scrollPane);

        // Create scene
        Scene scene = new Scene(root, width, height);

        // Add global CSS styles for consistent appearance
        scene.getStylesheets().add("data:text/css," +
                ".scroll-pane { -fx-background-insets: 0; -fx-padding: 0; }" +
                ".scroll-pane > .viewport { -fx-background-color: " + backgroundColor + "; }" +
                ".scroll-pane > .corner { -fx-background-color: " + backgroundColor + "; }");

        // Add window size change listener
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            int newMaxCols = calculateMaxColumns(newVal.doubleValue());
            // Clear existing layout
            centerGrid.getChildren().clear();
            centerGrid.getColumnConstraints().clear();

            // Relayout all cards
            layoutCards(centerGrid, allCards, newMaxCols);
        });

        // Set window title
        stage.setTitle("Finanger - " + languageService.getTranslation("goals"));

        return scene;
    }

    /**
     * Calculates the maximum number of columns for the goal grid based on window width.
     *
     * @param windowWidth The current window width
     * @return The maximum number of columns that can fit in the window
     */
    private static int calculateMaxColumns(double windowWidth) {
        // Calculate number of goal cards per row based on window width
        // Assuming minimum card width of 300px and gap of 20px
        int minCardWidth = 300;
        int gap = 20;
        int maxCols = (int) ((windowWidth - 100) / (minCardWidth + gap));
        return Math.max(1, maxCols);
    }

    /**
     * Creates a container with a title and value label pair.
     *
     * @param title The title text
     * @param value The value text
     * @param themeService Service for managing application theme
     * @return A VBox containing the styled label pair
     */
    private static VBox createLabelPair(String title, String value, ThemeService themeService) {
        VBox container = new VBox(2);  // 2 pixels spacing between labels
        container.setAlignment(Pos.CENTER_LEFT);

        // Create title label with bold font
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.GRAY);

        // Create value label with regular font
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        valueLabel.setStyle(themeService.getTextColorStyle());
        valueLabel.setWrapText(true);  // Enable text wrapping for long values

        container.getChildren().addAll(titleLabel, valueLabel);
        return container;
    }

    /**
     * Creates a card displaying a single goal with its progress and controls.
     *
     * @param goal The goal to display
     * @param stage The main application stage
     * @param loggedUser The currently logged-in user
     * @param themeService Service for managing application theme
     * @param currencyService Service for handling currency conversions
     * @return A VBox containing the styled goal card
     */
    private static VBox createGoalCard(Goal goal, Stage stage, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(300);
        card.setMinHeight(200);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        themeService.getCurrentFormBackgroundStyle()
        );

        // Create delete button
        Button deleteButton = new Button("×");
        deleteButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #FF5252;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        // Initially hide the delete button
        deleteButton.setVisible(false);

        // Show delete button on mouse enter
        card.setOnMouseEntered(event -> {
            deleteButton.setVisible(true);
        });

        // Hide delete button on mouse exit
        card.setOnMouseExited(event -> {
            deleteButton.setVisible(false);
        });

        // Position the delete button to the top-right
        StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);
        StackPane.setMargin(deleteButton, new Insets(0, 0, 0, 0));

        // Handle delete button click
        deleteButton.setOnAction(event -> {
            // Confirm deletion with alert
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle(languageService.getTranslation("delete_goal"));
            confirmation.setHeaderText(languageService.getTranslation("delete_goal_header") + " \"" + goal.getTitle() + "\"");
            confirmation.setContentText(languageService.getTranslation("delete_goal_confirmation"));

            // Show dialog and wait for user response
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Store original window dimensions
                        double originalWidth = stage.getScene().getWidth();
                        double originalHeight = stage.getScene().getHeight();

                        // Delete the goal
                        GoalService.deleteGoal(goal.getId(), loggedUser);

                        // Refresh the goals scene with the exact original dimensions
                        Scene newScene = createScene(stage, originalWidth, originalHeight, loggedUser, themeService, currencyService);
                        stage.setScene(newScene);
                    } catch (IOException e) {
                        // Show error message
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle(languageService.getTranslation("error"));
                        error.setHeaderText(languageService.getTranslation("failed_to_delete_goal"));
                        error.setContentText(languageService.getTranslation("error_occurred") + ": " + e.getMessage());
                        error.show();
                        e.printStackTrace();
                    }
                }
            });

            // Prevent event from bubbling up to the card click handler
            event.consume();
        });

        Label title = new Label(goal.getTitle());
        title.setFont(Font.font("Arial", 16));
        title.setStyle(themeService.getTextColorStyle());

        // Create text information section
        VBox textInfo = new VBox(8);
        textInfo.setAlignment(Pos.CENTER_LEFT);
        textInfo.setPrefWidth(200);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        // Create different content based on goal type
        StackPane indicatorContainer = new StackPane();
        try {
            TransactionDataService transactionService = new TransactionDataService(loggedUser.getUid());
            double progress = 0;
            boolean isCompleted = false;
            String currency = goal.getCurrency();

            switch (goal.getType()) {
                case "SAVING":
                    double currentNetBalance = transactionService.calculateNetBalance();
                    VBox targetAmountBox = createLabelPair(languageService.getTranslation("target_amount"),
                            GoalService.formatNumber(goal.getTargetAmount()) + " " + currency, themeService);
                    VBox deadlineBox = createLabelPair(languageService.getTranslation("deadline"),
                            goal.getDeadline().format(formatter), themeService);
                    VBox currentSavingsBox = createLabelPair(languageService.getTranslation("current_savings"),
                            GoalService.formatNumber(currentNetBalance) + " " + currency, themeService);
                    textInfo.getChildren().addAll(targetAmountBox, deadlineBox, currentSavingsBox);
                    progress = GoalService.calculateSavingProgress(currentNetBalance, goal.getTargetAmount());
                    break;

                case "DEBT_REPAYMENT":
                    double totalDebtRepayment = transactionService.calculateTotalAmountByCategory("Debt");
                    VBox totalDebtBox = createLabelPair(languageService.getTranslation("total_debt_amount"),
                            GoalService.formatNumber(goal.getTargetAmount()) + " " + currency, themeService);
                    VBox repaymentDeadlineBox = createLabelPair(languageService.getTranslation("repayment_deadline"),
                            goal.getDeadline().format(formatter), themeService);
                    VBox amountPaidBox = createLabelPair(languageService.getTranslation("amount_paid"),
                            GoalService.formatNumber(totalDebtRepayment) + " " + currency, themeService);
                    textInfo.getChildren().addAll(totalDebtBox, repaymentDeadlineBox, amountPaidBox);
                    progress = GoalService.calculateDebtProgress(totalDebtRepayment, goal.getTargetAmount());
                    isCompleted = progress >= 100;
                    break;

                case "BUDGET_CONTROL":
                    double currentExpense = transactionService.calculateTotalExpense();
                    VBox budgetCategoryBox = createLabelPair(languageService.getTranslation("budget_category"),
                            goal.getCategory() != null ? goal.getCategory() : languageService.getTranslation("general"), themeService);
                    VBox budgetAmountBox = createLabelPair(languageService.getTranslation("budget_amount"),
                            GoalService.formatNumber(goal.getTargetAmount()) + " " + currency, themeService);
                    VBox currentExpensesBox = createLabelPair(languageService.getTranslation("current_expenses"),
                            GoalService.formatNumber(currentExpense) + " " + currency, themeService);
                    textInfo.getChildren().addAll(budgetCategoryBox, budgetAmountBox, currentExpensesBox);
                    progress = GoalService.calculateBudgetUsage(currentExpense, goal.getTargetAmount());
                    break;
            }

            // Create progress indicator
            Color progressColor = GoalService.getProgressColor(goal.getType(), progress, isCompleted);
            String progressText = GoalService.getProgressText(goal.getType(), progress, isCompleted);
            int fontSize = GoalService.getProgressFontSize(goal.getType(), isCompleted);

            // 1. Create background arc (gray part, representing incomplete)
            Arc backgroundArc = new Arc(0, 0, 40, 40, 90, 360);
            backgroundArc.setType(ArcType.OPEN);
            backgroundArc.setStroke(Color.LIGHTGRAY);
            backgroundArc.setFill(Color.TRANSPARENT);
            backgroundArc.setStrokeWidth(8);

            // 2. Create progress arc
            Arc progressArc = new Arc(0, 0, 40, 40, 90, -360 * Math.min(100, progress) / 100);
            progressArc.setType(ArcType.OPEN);
            progressArc.setStroke(progressColor);
            progressArc.setFill(Color.TRANSPARENT);
            progressArc.setStrokeWidth(8);

            // 3. Combine the two arcs
            Group arcGroup = new Group(backgroundArc, progressArc);

            // 4. Add progress text label
            Label progressLabel = new Label(progressText);
            progressLabel.setFont(Font.font("Arial", fontSize));
            progressLabel.setTextFill(progressColor);

            // 5. Add to container
            indicatorContainer.getChildren().addAll(arcGroup, progressLabel);

        } catch (IOException e) {
            Label errorLabel = new Label(languageService.getTranslation("error_loading_transaction"));
            errorLabel.setStyle(themeService.getTextColorStyle());
            textInfo.getChildren().add(errorLabel);
            e.printStackTrace();
        }

        // Create HBox for left-right layout with padding
        HBox contentLayout = new HBox(20);
        contentLayout.setAlignment(Pos.CENTER_LEFT);
        contentLayout.setPadding(new Insets(10, 0, 0, 0));
        contentLayout.getChildren().addAll(textInfo, indicatorContainer);

        // Create a container for the title and delete button
        StackPane titleContainer = new StackPane();
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        titleContainer.getChildren().addAll(titleBox, deleteButton);
        StackPane.setAlignment(titleBox, Pos.CENTER);
        StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);
        StackPane.setMargin(deleteButton, new Insets(0, 0, 0, 0));

        // Add the title container instead of just the title
        card.getChildren().addAll(titleContainer, contentLayout);

        // Add click handler for editing
        card.setOnMouseClicked(event -> {
            if (!event.isConsumed()) {  // Only handle if not already consumed by delete button
                double currentWidth = stage.getScene().getWidth();
                double currentHeight = stage.getScene().getHeight();
                Scene editScene = EditGoalScene.createScene(stage, currentWidth, currentHeight, loggedUser, goal, themeService, currencyService);
                SceneManager.switchScene(stage, editScene);
            }
        });

        return card;
    }

    /**
     * Creates a card for adding new goals.
     *
     * @param stage The main application stage
     * @param loggedUser The currently logged-in user
     * @param themeService Service for managing application theme
     * @param currencyService Service for handling currency conversions
     * @return A VBox containing the styled "create new goal" card
     */
    private static VBox createCreateNewGoalCard(Stage stage, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(300);
        card.setMinHeight(200);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        themeService.getCurrentFormBackgroundStyle()
        );

        Label title = new Label(languageService.getTranslation("create_new_goal"));
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.GRAY);

        // Create text information section with padding
        VBox textInfo = new VBox(8);
        textInfo.setAlignment(Pos.CENTER_LEFT);
        textInfo.setPadding(new Insets(10, 0, 0, 0));
        Label instructionLabel = new Label(languageService.getTranslation("click_to_create"));
        Label instructionLabel2 = new Label(languageService.getTranslation("new_financial_goal"));
        instructionLabel.setStyle(themeService.getTextColorStyle());
        instructionLabel2.setStyle(themeService.getTextColorStyle());
        textInfo.getChildren().addAll(instructionLabel, instructionLabel2);

        // Create plus circle
        StackPane plusContainer = new StackPane();
        plusContainer.setPadding(new Insets(0, 0, 0, 20));
        Circle plusCircle = new Circle(40);
        plusCircle.setStroke(themeService.isDayMode() ? Color.GRAY : Color.LIGHTGRAY);
        plusCircle.setFill(Color.TRANSPARENT);
        plusCircle.setStrokeWidth(8);
        Label plusLabel = new Label("+");
        plusLabel.setFont(Font.font("Arial", 24));
        plusLabel.setTextFill(themeService.isDayMode() ? Color.GRAY : Color.LIGHTGRAY);
        plusContainer.getChildren().addAll(plusCircle, plusLabel);

        // Create HBox for left-right layout
        HBox contentLayout = new HBox(15);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.getChildren().addAll(textInfo, plusContainer);
        card.getChildren().addAll(title, contentLayout);

        // Add click event handler
        card.setOnMouseClicked(event -> {
            // Get current window dimensions
            double currentWidth = stage.getScene().getWidth();
            double currentHeight = stage.getScene().getHeight();

            // Navigate to create goal page with current window dimensions
            Scene createScene = CreateGoalScene.createScene(stage, currentWidth, currentHeight, loggedUser, themeService, currencyService);
            SceneManager.switchScene(stage, createScene);
        });

        return card;
    }

    /**
     * Arranges goal cards in a grid layout.
     *
     * @param grid The GridPane to arrange cards in
     * @param cards List of card VBoxes to arrange
     * @param maxCols Maximum number of columns in the grid
     */
    private static void layoutCards(GridPane grid, List<VBox> cards, int maxCols) {
        // Add column constraints
        for (int i = 0; i < maxCols; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setMinWidth(300);
            column.setMaxWidth(400);
            column.setHgrow(Priority.SOMETIMES);
            grid.getColumnConstraints().add(column);
        }

        // Layout all cards
        int row = 0;
        int col = 0;
        for (VBox card : cards) {
            grid.add(card, col, row);
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }
}