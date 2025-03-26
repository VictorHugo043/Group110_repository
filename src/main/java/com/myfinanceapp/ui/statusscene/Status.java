package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.AiChatService;
import com.myfinanceapp.service.TransactionService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Status 界面示例：包含：
 * - 顶部：折线图 + 收入/支出
 * - 中下方：左右列
 *   => 左列: CategoryProportion + RecentTransactions
 *   => 右列: AI Pane(只输入) + Suggestion(显示AI对话/历史)
 */
public class Status {

    // 当前登录用户
    private static User currentUser;

    // AI 对话上下文：用 role=user/assistant, content=...
    private static final List<Map<String, String>> chatMessages = new ArrayList<>();

    // Suggestion 区域：用来显示 AI 问答历史
    private static TextArea suggestionsArea;

    /**
     * 生成 Status 场景
     * @param stage 主 Stage
     * @param width 场景宽
     * @param height 场景高
     * @param loggedUser 当前登录用户
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        Status.currentUser = loggedUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // 左侧导航栏
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Status", loggedUser);
        root.setLeft(sideBar);

        // 右侧主容器: ScrollPane + VBox
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        scrollPane.setContent(mainContent);

        // (1) 顶部圆角边框：折线图
        Pane topPane = createTopPane();
        mainContent.getChildren().add(topPane);

        // (2) 中下方：左右两列
        HBox bottomArea = new HBox(20);
        bottomArea.setAlignment(Pos.TOP_CENTER);
        bottomArea.setPrefHeight(600);

        // 左列: Category + Recent Tx
        VBox leftColumn = new VBox(20);
        leftColumn.setAlignment(Pos.TOP_CENTER);

        Pane categoryPane = createCategoryPane();
        Pane transactionsPane = createTransactionsPane();
        leftColumn.getChildren().addAll(categoryPane, transactionsPane);

        // 右列: AI(只输入) + Suggestion(显示对话)
        VBox rightColumn = new VBox(20);
        rightColumn.setAlignment(Pos.TOP_CENTER);

        Pane aiPane = createAIPane();                // 上半: 输入
        Pane suggestionPane = createSuggestionPane(); // 下半: AI历史
        rightColumn.getChildren().addAll(aiPane, suggestionPane);

        bottomArea.getChildren().addAll(leftColumn, rightColumn);
        mainContent.getChildren().add(bottomArea);

        return new Scene(root, width, height);
    }

    /**
     * 顶部区域: 折线图 + 收入/支出 + Date/Chart Combo
     */
    private static Pane createTopPane(){
        BorderPane topBorder = new BorderPane();
        topBorder.setPrefHeight(300);
        topBorder.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );
        topBorder.setPadding(new Insets(15));

        Label title = new Label("Income and Expenses for This Month");
        title.setFont(Font.font(20));
        title.setTextFill(Color.web("#3282FA"));

        VBox exInBox = new VBox(15);
        exInBox.setAlignment(Pos.TOP_LEFT);

        Label exLabel = new Label("Ex.  2000.0 CNY");
        exLabel.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-border-radius: 30; -fx-background-radius: 30;"
                + "-fx-padding: 10 20 10 20;");
        Label inLabel = new Label("In.  3500.0 CNY");
        inLabel.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-border-radius: 30; -fx-background-radius: 30;"
                + "-fx-padding: 10 20 10 20;");
        exInBox.getChildren().addAll(exLabel, inLabel);

        VBox comboBoxArea = new VBox(10);
        comboBoxArea.setAlignment(Pos.TOP_LEFT);

        HBox dateBox = new HBox(5);
        Label dateLabel = new Label("Date Selection");
        ComboBox<String> dateCombo = new ComboBox<>();
        dateCombo.getItems().addAll("This Month", "Last Month", "Custom Range");
        dateCombo.setValue("This Month");
        dateBox.getChildren().addAll(dateLabel, dateCombo);

        HBox chartTypeBox = new HBox(5);
        Label chartTypeLabel = new Label("Chart Type");
        ComboBox<String> chartTypeCombo = new ComboBox<>();
        chartTypeCombo.getItems().addAll("Line graph", "Bar graph");
        chartTypeCombo.setValue("Line graph");
        chartTypeBox.getChildren().addAll(chartTypeLabel, chartTypeCombo);

        comboBoxArea.getChildren().addAll(dateBox, chartTypeBox);

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Amount");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Ex/In Trend");
        lineChart.setPrefSize(350, 200);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Example Data");
        series.getData().add(new XYChart.Data<>(1, 50));
        series.getData().add(new XYChart.Data<>(2, 120));
        series.getData().add(new XYChart.Data<>(3, 75));
        series.getData().add(new XYChart.Data<>(4, 200));
        series.getData().add(new XYChart.Data<>(5, 140));
        lineChart.getData().add(series);

        VBox centerBox = new VBox(10, title);
        centerBox.setAlignment(Pos.TOP_CENTER);

        HBox middleRow = new HBox(30, exInBox, comboBoxArea, lineChart);
        middleRow.setAlignment(Pos.CENTER_LEFT);

        centerBox.getChildren().add(middleRow);
        topBorder.setCenter(centerBox);
        return topBorder;
    }

    /**
     * 左列上方：Category Proportion
     */
    private static Pane createCategoryPane(){
        BorderPane categoryPane = new BorderPane();
        categoryPane.setPrefSize(400, 250);
        categoryPane.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );
        categoryPane.setPadding(new Insets(15));

        Label title = new Label("Category Proportion Analysis");
        title.setFont(Font.font(16));
        title.setTextFill(Color.web("#3282FA"));

        PieChart pieChart = new PieChart();
        pieChart.setPrefSize(300, 180);
        PieChart.Data slice1 = new PieChart.Data("Food 100CNY", 100);
        PieChart.Data slice2 = new PieChart.Data("Shopping 150CNY", 150);
        PieChart.Data slice3 = new PieChart.Data("Subscription 146CNY", 146);
        pieChart.getData().addAll(slice1, slice2, slice3);

        VBox content = new VBox(10, title, pieChart);
        content.setAlignment(Pos.TOP_CENTER);

        categoryPane.setCenter(content);
        return categoryPane;
    }

    /**
     * 左列下方：Recent Transactions
     */
    private static Pane createTransactionsPane(){
        BorderPane txPane = new BorderPane();
        txPane.setPrefSize(400, 200);
        txPane.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );
        txPane.setPadding(new Insets(15));

        Label title = new Label("Recent Transactions");
        title.setFont(Font.font(16));
        title.setTextFill(Color.web("#3282FA"));

        Label line1 = new Label("October 15   Electricity bill    125.75 CNY");
        Label line2 = new Label("October 15   Mobile phone bill   49.99 CNY");
        Label line3 = new Label("October 13   Gasoline            55.80 CNY");

        VBox vbox = new VBox(10, title, line1, line2, line3);
        vbox.setAlignment(Pos.TOP_LEFT);

        txPane.setCenter(vbox);
        return txPane;
    }

    /**
     * 右列上方：AI输入(不显示历史)，在发送前将 userInput + 交易数据 + 专属提示 拼接
     */
    private static Pane createAIPane(){
        BorderPane aiPane = new BorderPane();
        aiPane.setPrefSize(400, 200);
        aiPane.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );
        aiPane.setPadding(new Insets(15));

        Label title = new Label("Ask Your AI Assistant:");
        title.setFont(Font.font(16));
        title.setTextFill(Color.web("#3282FA"));

        TextArea questionArea = new TextArea();
        questionArea.setPromptText("Type your question...");
        questionArea.setPrefSize(250, 80);

        Button sendBtn = new Button("➤");
        sendBtn.setStyle("-fx-background-color: #A3D1FF; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-weight: bold;");

        // 发送事件
        sendBtn.setOnAction(e -> {
            String userInput = questionArea.getText().trim();
            if(!userInput.isEmpty()){
                questionArea.setDisable(true);
                sendBtn.setDisable(true);

                // 1) 从 transactions/<uid>.json 加载用户财务数据
                TransactionService txService = new TransactionService();
                List<Transaction> txList = txService.loadTransactions(currentUser);
                // ^ 使用 currentUser, 不再是 loggedUser 变量

                // 2) 拼交易数据
                StringBuilder dataSummary = new StringBuilder();
                dataSummary.append("以下是我的财务交易数据，每条格式：Date, Type, Currency, Amount, Category, PaymentMethod:\n");
                for (Transaction tx : txList) {
                    dataSummary.append(String.format(
                            "- %s, %s, %s, %.2f, %s, %s\n",
                            tx.getTransactionDate(),
                            tx.getTransactionType(),
                            tx.getCurrency(),
                            tx.getAmount(),
                            tx.getCategory(),
                            tx.getPaymentMethod()
                    ));
                }
                // 3) 专属提示 + 用户数据 + 用户本次问题
                String systemPrompt =
                        "现在你是我的专属财务管理助手，我希望你解答我有关个人财务的问题。\n" +
                                "这是我的财务数据结构: Transaction Date(YYYY-MM-DD), Type(Income/Expense), Currency, Amount, Category, PaymentMethod.\n" +
                                "下面是我目前的数据：\n" + dataSummary +
                                "\n用户的问题是： " + userInput;

                // 调用AiChatService
                String answer = AiChatService.chatCompletion(chatMessages, systemPrompt);
                if(answer != null){
                    suggestionsArea.appendText("You: " + userInput + "\nAI: " + answer + "\n\n");
                } else {
                    suggestionsArea.appendText("AI 请求失败，未能获取答复\n\n");
                }

                questionArea.clear();
                questionArea.setDisable(false);
                sendBtn.setDisable(false);
            }
        });

        VBox content = new VBox(10, title, questionArea, sendBtn);
        content.setAlignment(Pos.TOP_LEFT);

        aiPane.setCenter(content);
        return aiPane;
    }

    /**
     * 右列下方：Suggestion Pane（显示AI对话历史）
     */
    private static Pane createSuggestionPane(){
        BorderPane sugPane = new BorderPane();
        sugPane.setPrefSize(400, 200);
        sugPane.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );
        sugPane.setPadding(new Insets(15));

        Label title = new Label("Suggestion");
        title.setFont(Font.font(16));
        title.setTextFill(Color.web("#3282FA"));

        suggestionsArea = new TextArea();
        suggestionsArea.setEditable(false);
        suggestionsArea.setWrapText(true);
        suggestionsArea.setPrefHeight(120);

        Button moreBtn = new Button("More>");
        moreBtn.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-background-radius: 10;");

        VBox vbox = new VBox(10, title, suggestionsArea, moreBtn);
        vbox.setAlignment(Pos.TOP_LEFT);

        sugPane.setCenter(vbox);
        return sugPane;
    }
}
