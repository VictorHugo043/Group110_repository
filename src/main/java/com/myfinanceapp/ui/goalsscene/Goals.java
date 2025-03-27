package com.myfinanceapp.ui.goalsscene;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.service.GoalManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        centerGrid.setHgap(20); // 水平间距
        centerGrid.setVgap(20); // 垂直间距
        centerGrid.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));

        // 获取用户的目标列表，使用 GoalManager 的 getUserGoals 方法
        List<Goal> userGoals = GoalManager.getUserGoals(loggedUser);

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

        // Define maxCols outside the if-else block
        int maxCols = 2; // 每行最多显示2个卡片

        // If no goals, show a message
        if (userGoals.isEmpty()) {
            Label noGoalsLabel = new Label("No goals found. Create your first goal!");
            noGoalsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
            centerContent.getChildren().add(noGoalsLabel);
        } else {
            // 动态创建目标卡片并添加到网格
            int row = 0;
            int col = 0;
            for (Goal goal : userGoals) {
                try {
                    // 确保目标的用户ID与当前登录用户匹配
                    if (loggedUser == null || goal.getUserId() == null || 
                        loggedUser.getUid().equals(goal.getUserId())) {
                        VBox goalCard = createGoalCard(goal);
                        centerGrid.add(goalCard, col, row);
                        // Debug info for each goal
                        System.out.println("Added goal: " + goal.getTitle() + 
                                          " (User ID: " + goal.getUserId() + 
                                          ") at position [" + col + "," + row + "]");
                        // 更新行列位置
                        col++;
                        if (col >= maxCols) {
                            col = 0;
                            row++;
                        }
                    } else {
                        System.out.println("Skipping goal: " + goal.getTitle() + 
                                          " (belongs to user ID: " + goal.getUserId() + 
                                          ", not current user: " + loggedUser.getUid() + ")");
                    }
                } catch (Exception e) {
                    System.err.println("Error displaying goal: " + goal.getTitle());
                    e.printStackTrace();
                }
            }
        }

        // Calculate the correct position for the "Create new goal" card
        int newGoalCol = userGoals.size() % maxCols;
        int newGoalRow = userGoals.size() / maxCols;

        // 添加"创建新目标"卡片（始终放在最后一个位置）
        VBox createNewGoalCard = createCreateNewGoalCard(stage, loggedUser);
        centerGrid.add(createNewGoalCard, newGoalCol, newGoalRow);
        System.out.println("Added 'Create New Goal' card at position [" + newGoalCol + "," + newGoalRow + "]");

        // Add the grid to the center content
        centerContent.getChildren().add(centerGrid);
        root.setCenter(centerContent);
        return new Scene(root, width, height);
    }

    private static VBox createGoalCard(Goal goal) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(300);
        card.setMaxHeight(200);
        card.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-background-color: white;"
        );

        Label title = new Label(goal.getTitle());
        title.setFont(Font.font("Arial", 16));
        title.setTextFill(Color.DARKBLUE);

        // 创建文字信息部分
        VBox textInfo = new VBox(5);
        textInfo.setAlignment(Pos.CENTER_LEFT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        // 根据目标类型创建不同的内容
        StackPane indicatorContainer = new StackPane();
        switch (goal.getType()) {
            case "SAVING":
                Label targetAmount = new Label("Target Amount: " + goal.getTargetAmount() + " CNY");
                Label deadline = new Label("Deadline: " + goal.getDeadline().format(formatter));
                Label currentSavings = new Label("Current Savings: " + goal.getCurrentAmount() + " CNY");
                textInfo.getChildren().addAll(targetAmount, deadline, currentSavings);

                // 创建进度圆形
                Circle progressCircle = new Circle(40);
                progressCircle.setStroke(Color.BLUE);
                progressCircle.setFill(Color.TRANSPARENT);
                progressCircle.setStrokeWidth(8);
                Label progressLabel = new Label(goal.getProgressPercentage() + "%");
                progressLabel.setFont(Font.font("Arial", 18));
                progressLabel.setTextFill(Color.BLUE);
                indicatorContainer.getChildren().addAll(progressCircle, progressLabel);
                break;
            case "DEBT_REPAYMENT":
                Label totalDebtAmount = new Label("Total Debt Amount: " + goal.getTargetAmount() + " CNY");
                Label repaymentDeadline = new Label("Repayment Deadline: " + goal.getDeadline().format(formatter));
                Label amountPaid = new Label("Amount Paid: " + goal.getCurrentAmount() + " CNY");
                textInfo.getChildren().addAll(totalDebtAmount, repaymentDeadline, amountPaid);

                // 创建对号/进度圆形
                Circle checkCircle = new Circle(40);
                if (goal.isCompleted()) {
                    checkCircle.setStroke(Color.GREEN);
                    Label checkLabel = new Label("\u2713");
                    checkLabel.setFont(Font.font("Arial", 24));
                    checkLabel.setTextFill(Color.GREEN);
                    indicatorContainer.getChildren().addAll(checkCircle, checkLabel);
                } else {
                    checkCircle.setStroke(Color.BLUE);
                    Label debtProgressLabel = new Label(goal.getProgressPercentage() + "%");
                    debtProgressLabel.setFont(Font.font("Arial", 18));
                    debtProgressLabel.setTextFill(Color.BLUE);
                    indicatorContainer.getChildren().addAll(checkCircle, debtProgressLabel);
                }
                checkCircle.setFill(Color.TRANSPARENT);
                checkCircle.setStrokeWidth(8);
                break;
            case "BUDGET_CONTROL":
                Label budgetCategory = new Label("Budget Category: " + (goal.getCategory() != null ? goal.getCategory() : "General"));
                Label budgetAmount = new Label("Budget Amount: " + goal.getTargetAmount() + " CNY");
                Label currentExpenses = new Label("Current Expenses: " + goal.getCurrentAmount() + " CNY");
                textInfo.getChildren().addAll(budgetCategory, budgetAmount, currentExpenses);

                // 创建指示器
                Circle statusCircle = new Circle(40);
                statusCircle.setFill(Color.TRANSPARENT);
                statusCircle.setStrokeWidth(8);
                if (goal.isCompleted()) {
                    statusCircle.setStroke(Color.GREEN);
                    Label checkLabel = new Label("\u2713");
                    checkLabel.setFont(Font.font("Arial", 24));
                    checkLabel.setTextFill(Color.GREEN);
                    indicatorContainer.getChildren().addAll(statusCircle, checkLabel);
                } else {
                    statusCircle.setStroke(Color.RED);
                    Label errorLabel = new Label("\u2717");
                    errorLabel.setFont(Font.font("Arial", 24));
                    errorLabel.setTextFill(Color.RED);
                    indicatorContainer.getChildren().addAll(statusCircle, errorLabel);
                }
                break;
        }

        // 创建左右布局的HBox
        HBox contentLayout = new HBox(15);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.getChildren().addAll(textInfo, indicatorContainer);
        card.getChildren().addAll(title, contentLayout);
        return card;
    }

    private static VBox createCreateNewGoalCard(Stage stage, User loggedUser) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(300);
        card.setMaxHeight(200);
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

        // 创建文字信息部分
        VBox textInfo = new VBox(5);
        textInfo.setAlignment(Pos.CENTER_LEFT);
        Label instructionLabel = new Label("Click to create");
        Label instructionLabel2 = new Label("a new financial goal");
        textInfo.getChildren().addAll(instructionLabel, instructionLabel2);

        // 创建加号圆形
        StackPane plusContainer = new StackPane();
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
            // Navigate to create goal page with consistent dimensions
            stage.setScene(CreateGoalScene.createScene(stage, 800, 450, loggedUser));
        });

        return card;
    }
}