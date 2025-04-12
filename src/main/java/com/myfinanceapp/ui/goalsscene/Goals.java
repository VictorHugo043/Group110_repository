package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.service.GoalService;
import com.myfinanceapp.service.TransactionDataService;

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

public class Goals {
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // 左侧导航栏
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Goals", loggedUser);
        root.setLeft(sideBar);

        // 创建网格布局
        GridPane centerGrid = new GridPane();
        centerGrid.setAlignment(Pos.CENTER);
        centerGrid.setHgap(20);
        centerGrid.setVgap(20);
        centerGrid.setPadding(new Insets(20, 20, 20, 20));

        // 获取用户的目标列表
        List<Goal> userGoals = GoalService.getUserGoals(loggedUser);

        // Add debugging label to show how many goals were loaded
        String debugText = "Found " + userGoals.size() + " goals for user";
        if (loggedUser != null) {
            debugText += " (ID: " + loggedUser.getUid() + ")";
        }
        Label debugLabel = new Label(debugText);
        debugLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        // VBox to hold the debug info and grid
        VBox centerContent = new VBox(10);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.getChildren().add(debugLabel);

        // 初始化列数
        int initialMaxCols = calculateMaxColumns(width);
        
        // 创建一个列表来存储所有卡片，以便后续重新布局
        List<VBox> allCards = new ArrayList<>();
        
        // If no goals, show a message
        if (userGoals.isEmpty()) {
            Label noGoalsLabel = new Label("No goals found. Create your first goal!");
            noGoalsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
            centerContent.getChildren().add(noGoalsLabel);
        } else {
            // 创建所有目标卡片
            for (Goal goal : userGoals) {
                try {
                    if (loggedUser == null || goal.getUserId() == null || 
                        loggedUser.getUid().equals(goal.getUserId())) {
                        VBox goalCard = createGoalCard(goal, stage, loggedUser);
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
        VBox createNewGoalCard = createCreateNewGoalCard(stage, loggedUser);
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
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        // 绑定滚动面板大小到窗口大小
        scrollPane.prefWidthProperty().bind(root.widthProperty().subtract(sideBar.widthProperty()));
        scrollPane.prefHeightProperty().bind(root.heightProperty());
        
        // Set the scrollPane as the center of the BorderPane
        root.setCenter(scrollPane);
        
        // 创建场景
        Scene scene = new Scene(root, width, height);

        // 添加窗口大小变化监听器
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            int newMaxCols = calculateMaxColumns(newVal.doubleValue());
            // 清除现有布局
            centerGrid.getChildren().clear();
            centerGrid.getColumnConstraints().clear();
            
            // 重新布局所有卡片
            layoutCards(centerGrid, allCards, newMaxCols);
        });
        
        return scene;
    }
    
    private static int calculateMaxColumns(double windowWidth) {
        // 根据窗口宽度计算每行显示的目标卡片数量
        // 假设每个卡片最小宽度为300px，间距为20px
        int minCardWidth = 300;
        int gap = 20;
        int maxCols = (int) ((windowWidth - 100) / (minCardWidth + gap));
        return Math.max(1, maxCols);
    }

    private static VBox createLabelPair(String title, String value) {
        VBox container = new VBox(2);  // 2 pixels spacing between labels
        container.setAlignment(Pos.CENTER_LEFT);
        
        // Create title label with bold font
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.GRAY);
        
        // Create value label with regular font
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        valueLabel.setTextFill(Color.BLACK);
        valueLabel.setWrapText(true);  // Enable text wrapping for long values
        
        container.getChildren().addAll(titleLabel, valueLabel);
        return container;
    }

    private static VBox createGoalCard(Goal goal, Stage stage, User loggedUser) {
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
                        "-fx-background-color: white;"
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
            confirmation.setTitle("Delete Goal");
            confirmation.setHeaderText("Delete \"" + goal.getTitle() + "\"");
            confirmation.setContentText("Are you sure you want to delete this goal? This action cannot be undone.");
            
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
                        Scene newScene = createScene(stage, originalWidth, originalHeight, loggedUser);
                        stage.setScene(newScene);
                    } catch (IOException e) {
                        // Show error message
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText("Failed to delete goal");
                        error.setContentText("An error occurred: " + e.getMessage());
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
        title.setTextFill(Color.DARKBLUE);

        // 创建文字信息部分
        VBox textInfo = new VBox(8);
        textInfo.setAlignment(Pos.CENTER_LEFT);
        textInfo.setPrefWidth(200);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        // 根据目标类型创建不同的内容
        StackPane indicatorContainer = new StackPane();
        try {
            TransactionDataService transactionService = new TransactionDataService(loggedUser.getUid());
            double progress = 0;
            boolean isCompleted = false;
            String currency = goal.getCurrency();

            switch (goal.getType()) {
                case "SAVING":
                    double currentNetBalance = transactionService.calculateNetBalance();
                    VBox targetAmountBox = createLabelPair("Target Amount", 
                        GoalService.formatNumber(goal.getTargetAmount()) + " " + currency);
                    VBox deadlineBox = createLabelPair("Deadline", 
                        goal.getDeadline().format(formatter));
                    VBox currentSavingsBox = createLabelPair("Current Savings", 
                        GoalService.formatNumber(currentNetBalance) + " " + currency);
                    textInfo.getChildren().addAll(targetAmountBox, deadlineBox, currentSavingsBox);
                    progress = GoalService.calculateSavingProgress(currentNetBalance, goal.getTargetAmount());
                    break;

                case "DEBT_REPAYMENT":
                    double totalDebtRepayment = transactionService.calculateTotalAmountByCategory("Loan Repayment");
                    VBox totalDebtBox = createLabelPair("Total Debt Amount", 
                        GoalService.formatNumber(goal.getTargetAmount()) + " " + currency);
                    VBox repaymentDeadlineBox = createLabelPair("Repayment Deadline", 
                        goal.getDeadline().format(formatter));
                    VBox amountPaidBox = createLabelPair("Amount Paid", 
                        GoalService.formatNumber(totalDebtRepayment) + " " + currency);
                    textInfo.getChildren().addAll(totalDebtBox, repaymentDeadlineBox, amountPaidBox);
                    progress = GoalService.calculateDebtProgress(totalDebtRepayment, goal.getTargetAmount());
                    isCompleted = progress >= 100;
                    break;

                case "BUDGET_CONTROL":
                    double currentExpense = transactionService.calculateTotalExpense();
                    VBox budgetCategoryBox = createLabelPair("Budget Category", 
                        goal.getCategory() != null ? goal.getCategory() : "General");
                    VBox budgetAmountBox = createLabelPair("Budget Amount", 
                        GoalService.formatNumber(goal.getTargetAmount()) + " " + currency);
                    VBox currentExpensesBox = createLabelPair("Current Expenses", 
                        GoalService.formatNumber(currentExpense) + " " + currency);
                    textInfo.getChildren().addAll(budgetCategoryBox, budgetAmountBox, currentExpensesBox);
                    progress = GoalService.calculateBudgetUsage(currentExpense, goal.getTargetAmount());
                    break;
            }

            // 创建进度指示器
            Color progressColor = GoalService.getProgressColor(goal.getType(), progress, isCompleted);
            String progressText = GoalService.getProgressText(goal.getType(), progress, isCompleted);
            int fontSize = GoalService.getProgressFontSize(goal.getType(), isCompleted);

            // 1. 创建背景圆弧（灰色部分，表示未完成）
            Arc backgroundArc = new Arc(0, 0, 40, 40, 90, 360);
            backgroundArc.setType(ArcType.OPEN);
            backgroundArc.setStroke(Color.LIGHTGRAY);
            backgroundArc.setFill(Color.TRANSPARENT);
            backgroundArc.setStrokeWidth(8);

            // 2. 创建进度圆弧
            Arc progressArc = new Arc(0, 0, 40, 40, 90, -360 * Math.min(100, progress) / 100);
            progressArc.setType(ArcType.OPEN);
            progressArc.setStroke(progressColor);
            progressArc.setFill(Color.TRANSPARENT);
            progressArc.setStrokeWidth(8);

            // 3. 将两个圆弧组合起来
            Group arcGroup = new Group(backgroundArc, progressArc);

            // 4. 添加进度文字标签
            Label progressLabel = new Label(progressText);
            progressLabel.setFont(Font.font("Arial", fontSize));
            progressLabel.setTextFill(progressColor);

            // 5. 添加到容器
            indicatorContainer.getChildren().addAll(arcGroup, progressLabel);

        } catch (IOException e) {
            Label errorLabel = new Label("Error loading transaction data");
            textInfo.getChildren().add(errorLabel);
            e.printStackTrace();
        }

        // Create a container for the title and delete button
        StackPane titleContainer = new StackPane();
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        titleContainer.getChildren().addAll(titleBox, deleteButton);
        StackPane.setAlignment(titleBox, Pos.CENTER);
        StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);
        StackPane.setMargin(deleteButton, new Insets(0, 0, 0, 0));
        
        // 创建左右布局的HBox，添加内边距
        HBox contentLayout = new HBox(20);
        contentLayout.setAlignment(Pos.CENTER_LEFT);
        contentLayout.setPadding(new Insets(10, 0, 0, 0));
        contentLayout.getChildren().addAll(textInfo, indicatorContainer);
        
        // Add the title container instead of just the title
        card.getChildren().addAll(titleContainer, contentLayout);
        return card;
    }

    private static VBox createCreateNewGoalCard(Stage stage, User loggedUser) {
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
                        "-fx-background-color: white;"
        );

        Label title = new Label("Create a new goal");
        title.setFont(Font.font("Arial", 18));
        title.setTextFill(Color.GRAY);

        // 创建文字信息部分，添加内边距
        VBox textInfo = new VBox(8);
        textInfo.setAlignment(Pos.CENTER_LEFT);
        textInfo.setPadding(new Insets(10, 0, 0, 0));
        Label instructionLabel = new Label("Click to create");
        Label instructionLabel2 = new Label("a new financial goal");
        textInfo.getChildren().addAll(instructionLabel, instructionLabel2);

        // 创建加号圆形
        StackPane plusContainer = new StackPane();
        plusContainer.setPadding(new Insets(0, 0, 0, 20));
        Circle plusCircle = new Circle(40);
        plusCircle.setStroke(Color.GRAY);
        plusCircle.setFill(Color.TRANSPARENT);
        plusCircle.setStrokeWidth(8);
        Label plusLabel = new Label("+");
        plusLabel.setFont(Font.font("Arial", 24));
        plusLabel.setTextFill(Color.GRAY);
        plusContainer.getChildren().addAll(plusCircle, plusLabel);

        // 创建左右布局的HBox
        HBox contentLayout = new HBox(15);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.getChildren().addAll(textInfo, plusContainer);
        card.getChildren().addAll(title, contentLayout);

        // 添加点击事件处理
        card.setOnMouseClicked(event -> {
            // 获取当前窗口的实际大小
            double currentWidth = stage.getScene().getWidth();
            double currentHeight = stage.getScene().getHeight();
            
            // Navigate to create goal page with current window dimensions
            stage.setScene(CreateGoalScene.createScene(stage, currentWidth, currentHeight, loggedUser));
        });

        return card;
    }

    // 新增辅助方法：布局卡片
    private static void layoutCards(GridPane grid, List<VBox> cards, int maxCols) {
        // 添加列约束
        for (int i = 0; i < maxCols; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setMinWidth(300);
            column.setMaxWidth(400);
            column.setHgrow(Priority.SOMETIMES);
            grid.getColumnConstraints().add(column);
        }

        // 布局所有卡片
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