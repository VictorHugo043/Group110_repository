package com.myfinanceapp.ui.goalsscene;
import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.statusscene.Status;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Goals {

    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // 左侧导航栏
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Goals", loggedUser);
        root.setLeft(sideBar);

        // 将HBox替换为GridPane
        GridPane centerGrid = new GridPane();
        centerGrid.setAlignment(Pos.CENTER);
        centerGrid.setHgap(20); // 水平间距
        centerGrid.setVgap(20); // 垂直间距

        // 创建目标卡片
        VBox savingGoalCard = createSavingGoalCard();
        VBox debtRepaymentCard = createDebtRepaymentCard();
        VBox budgetControlCard = createBudgetControlCard();
        VBox createNewGoalCard = createCreateNewGoalCard();
        
        // 将卡片添加到网格中，指定行和列位置
        centerGrid.add(savingGoalCard, 0, 0);      // 第一行第一列
        centerGrid.add(debtRepaymentCard, 1, 0);   // 第一行第二列
        centerGrid.add(budgetControlCard, 0, 1);   // 第二行第一列
        centerGrid.add(createNewGoalCard, 1, 1);   // 第二行第二列
        
        root.setCenter(centerGrid);

        return new Scene(root, width, height);
    }

    private static VBox createSavingGoalCard() {
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

        Label title = new Label("Saving Goal");
        title.setFont(Font.font("Arial", 16));
        title.setTextFill(Color.DARKBLUE);
        
        // 创建文字信息部分
        VBox textInfo = new VBox(5);
        textInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label targetAmount = new Label("Target Amount: 1000 CNY");
        Label deadline = new Label("Deadline: 2025.3.30");
        Label currentSavings = new Label("Current Savings: 600 CNY");
        
        textInfo.getChildren().addAll(targetAmount, deadline, currentSavings);
        
        // 创建进度圆形和标签的容器
        StackPane progressContainer = new StackPane();
        Circle progressCircle = new Circle(40);
        progressCircle.setStroke(Color.BLUE);
        progressCircle.setFill(Color.TRANSPARENT);
        progressCircle.setStrokeWidth(8);
        
        Label progressLabel = new Label("60%");
        progressLabel.setFont(Font.font("Arial", 18));
        progressLabel.setTextFill(Color.BLUE);
        
        progressContainer.getChildren().addAll(progressCircle, progressLabel);
        
        // 创建左右布局的HBox
        HBox contentLayout = new HBox(15);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.getChildren().addAll(textInfo, progressContainer);
        
        card.getChildren().addAll(title, contentLayout);
        return card;
    }

    private static VBox createDebtRepaymentCard() {
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

        Label title = new Label("Debt Repayment Goal");
        title.setFont(Font.font("Arial", 16));
        title.setTextFill(Color.DARKBLUE);
        
        // 创建文字信息部分
        VBox textInfo = new VBox(5);
        textInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label totalDebtAmount = new Label("Total Debt Amount: 500 CNY");
        Label repaymentDeadline = new Label("Repayment Deadline: 2025.3.30");
        Label amountPaid = new Label("Amount Paid: 500 CNY");
        
        textInfo.getChildren().addAll(totalDebtAmount, repaymentDeadline, amountPaid);
        
        // 创建对号圆形和标签的容器
        StackPane checkContainer = new StackPane();
        Circle checkCircle = new Circle(40);
        checkCircle.setStroke(Color.GREEN);
        checkCircle.setFill(Color.TRANSPARENT);
        checkCircle.setStrokeWidth(8);
        
        Label checkLabel = new Label("\u2713");
        checkLabel.setFont(Font.font("Arial", 24));
        checkLabel.setTextFill(Color.GREEN);
        
        checkContainer.getChildren().addAll(checkCircle, checkLabel);
        
        // 创建左右布局的HBox
        HBox contentLayout = new HBox(15);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.getChildren().addAll(textInfo, checkContainer);
        
        card.getChildren().addAll(title, contentLayout);
        return card;
    }

    private static VBox createBudgetControlCard() {
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

        Label title = new Label("Budget Control Goal");
        title.setFont(Font.font("Arial", 16));
        title.setTextFill(Color.DARKBLUE);
        
        // 创建文字信息部分
        VBox textInfo = new VBox(5);
        textInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label budgetCategory = new Label("Budget Category: Food expenses");
        Label budgetAmount = new Label("Budget Amount: 2000 CNY");
        Label currentExpenses = new Label("Current Expenses: 2200 CNY");
        
        textInfo.getChildren().addAll(budgetCategory, budgetAmount, currentExpenses);
        
        // 创建错误圆形和标签的容器
        StackPane errorContainer = new StackPane();
        Circle errorCircle = new Circle(40);
        errorCircle.setStroke(Color.RED);
        errorCircle.setFill(Color.TRANSPARENT);
        errorCircle.setStrokeWidth(8);
        
        Label errorLabel = new Label("\u2717");
        errorLabel.setFont(Font.font("Arial", 24));
        errorLabel.setTextFill(Color.RED);
        
        errorContainer.getChildren().addAll(errorCircle, errorLabel);
        
        // 创建左右布局的HBox
        HBox contentLayout = new HBox(15);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.getChildren().addAll(textInfo, errorContainer);
        
        card.getChildren().addAll(title, contentLayout);
        return card;
    }

    private static VBox createCreateNewGoalCard() {
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
        return card;
    }
}