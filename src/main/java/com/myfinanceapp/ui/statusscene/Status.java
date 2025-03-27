package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.AiChatService;
import com.myfinanceapp.service.TransactionService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Status {
    private static User currentUser;
    private static final List<Map<String, String>> chatMessages = new ArrayList<>();
    private static TextArea suggestionsArea;
    private static TransactionService txService = new TransactionService();
    private static LineChart<String, Number> lineChart;
    private static BarChart<String, Number> barChart;
    private static Label exLabel;
    private static Label inLabel;
    private static PieChart pieChart;
    private static String currentPeriod = "This Month";

    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        Status.currentUser = loggedUser;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Status", loggedUser);
        root.setLeft(sideBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        root.setCenter(scrollPane);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setFillWidth(true);
        scrollPane.setContent(mainContent);

        Pane topPane = createTopPane();
        mainContent.getChildren().add(topPane);

        HBox bottomArea = new HBox(20);
        bottomArea.setAlignment(Pos.TOP_CENTER);
        bottomArea.setFillHeight(true);

        VBox leftColumn = new VBox(20);
        leftColumn.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        Pane categoryPane = createCategoryPane();
        Pane transactionsPane = createTransactionsPane();
        leftColumn.getChildren().addAll(categoryPane, transactionsPane);

        VBox rightColumn = new VBox(20);
        rightColumn.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        Pane aiPane = createAIPane();
        Pane suggestionPane = createSuggestionPane();
        rightColumn.getChildren().addAll(aiPane, suggestionPane);

        bottomArea.getChildren().addAll(leftColumn, rightColumn);
        mainContent.getChildren().add(bottomArea); // Note: Should be bottomArea

        return new Scene(root, width, height);
    }

    private static Pane createTopPane() {
        BorderPane topBorder = new BorderPane();
        topBorder.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );
        topBorder.setPadding(new Insets(15));

        Label title = new Label("Income and Expenses");
        title.setFont(Font.font(20));
        title.setTextFill(Color.web("#3282FA"));
        title.setWrapText(true);

        VBox exInBox = new VBox(15);
        exInBox.setAlignment(Pos.TOP_LEFT);
        exLabel = new Label();
        inLabel = new Label();
        updateSummaryLabels("This Month");

        exInBox.getChildren().addAll(exLabel, inLabel);

        HBox dateBox = new HBox(5);
        Label dateLabel = new Label("Date Selection");
        dateLabel.setWrapText(true);
        ComboBox<String> dateCombo = new ComboBox<>();
        dateCombo.getItems().addAll("This Month", "Last Month", "All Transactions");
        dateCombo.setValue("This Month");
        dateBox.getChildren().addAll(dateLabel, dateCombo);

        HBox chartTypeBox = new HBox(5);
        Label chartTypeLabel = new Label("Chart Type");
        chartTypeLabel.setWrapText(true);
        ComboBox<String> chartTypeCombo = new ComboBox<>();
        chartTypeCombo.getItems().addAll("Line graph", "Bar graph");
        chartTypeCombo.setValue("Line graph");
        chartTypeBox.getChildren().addAll(chartTypeLabel, chartTypeCombo);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Amount");

        CategoryAxis barXAxis = new CategoryAxis();
        NumberAxis barYAxis = new NumberAxis();
        barXAxis.setLabel("Date");
        barYAxis.setLabel("Amount");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Ex/In Trend");
        barChart = new BarChart<>(barXAxis, barYAxis);
        barChart.setTitle("Ex/In Trend");

        StackPane chartPane = new StackPane(lineChart);
        HBox.setHgrow(chartPane, Priority.ALWAYS);
        VBox.setVgrow(chartPane, Priority.ALWAYS);

        dateCombo.setOnAction(e -> {
            currentPeriod = dateCombo.getValue();
            updateCharts(dateCombo.getValue());
            updateSummaryLabels(dateCombo.getValue());
        });

        chartTypeCombo.setOnAction(e -> {
            chartPane.getChildren().clear();
            if ("Line graph".equals(chartTypeCombo.getValue())) {
                chartPane.getChildren().add(lineChart);
            } else {
                chartPane.getChildren().add(barChart);
            }
            updateCharts(dateCombo.getValue());
        });

        updateCharts("This Month");

        VBox leftSide = new VBox(10, dateBox, chartTypeBox, exInBox);
        leftSide.setAlignment(Pos.TOP_LEFT);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(title, 0, 0, 2, 1);
        GridPane.setHalignment(title, HPos.CENTER);
        gridPane.add(leftSide, 0, 1);
        gridPane.add(chartPane, 1, 1);
        GridPane.setHgrow(chartPane, Priority.ALWAYS);
        GridPane.setVgrow(chartPane, Priority.ALWAYS);

        topBorder.setCenter(gridPane);
        return topBorder;
    }

    private static void updateSummaryLabels(String period) {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        switch (period) {
            case "Last Month":
                startDate = now.minusMonths(1).withDayOfMonth(1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                break;
            case "All Transactions":
                if (transactions.isEmpty()) {
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                } else {
                    startDate = transactions.stream()
                            .map(t -> LocalDate.parse(t.getTransactionDate()))
                            .min(LocalDate::compareTo)
                            .orElse(now.withDayOfMonth(1));
                    endDate = transactions.stream()
                            .map(t -> LocalDate.parse(t.getTransactionDate()))
                            .max(LocalDate::compareTo)
                            .orElse(now.withDayOfMonth(now.lengthOfMonth()));
                }
                break;
            default: // "This Month"
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
        }

        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;
        transactions = transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
                    return !txDate.isBefore(finalStartDate) && !txDate.isAfter(finalEndDate);
                })
                .collect(Collectors.toList());

        double totalIncome = transactions.stream()
                .filter(t -> "Income".equals(t.getTransactionType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
        double totalExpense = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        exLabel.setText(String.format("Ex.  %.2f CNY", totalExpense));
        inLabel.setText(String.format("In.  %.2f CNY", totalIncome));
        exLabel.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-border-radius: 30; -fx-background-radius: 30; -fx-padding: 10 20 10 20;");
        inLabel.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-border-radius: 30; -fx-background-radius: 30; -fx-padding: 10 20 10 20;");
    }

    private static void updateCharts(String period) {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        switch (period) {
            case "Last Month":
                startDate = now.minusMonths(1).withDayOfMonth(1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                break;
            case "All Transactions":
                if (transactions.isEmpty()) {
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                } else {
                    startDate = transactions.stream()
                            .map(t -> LocalDate.parse(t.getTransactionDate()))
                            .min(LocalDate::compareTo)
                            .orElse(now.withDayOfMonth(1));
                    endDate = transactions.stream()
                            .map(t -> LocalDate.parse(t.getTransactionDate()))
                            .max(LocalDate::compareTo)
                            .orElse(now.withDayOfMonth(now.lengthOfMonth()));
                }
                break;
            default: // "This Month"
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
        }

        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;
        transactions = transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
                    return !txDate.isBefore(finalStartDate) && !txDate.isAfter(finalEndDate);
                })
                .collect(Collectors.toList());

        // Calculate the total number of days in the period
        long totalDays = finalEndDate.toEpochDay() - finalStartDate.toEpochDay() + 1;

        // Create a list of all dates in the period
        List<String> allDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        DateTimeFormatter formatter = period.equals("All Transactions") ?
                DateTimeFormatter.ofPattern("yyyy-MM-dd") :
                DateTimeFormatter.ofPattern("MM-dd");
        while (!currentDate.isAfter(endDate)) {
            allDates.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }

        // Aggregate transactions by date
        Map<String, Double> incomeByDate = transactions.stream()
                .filter(t -> "Income".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(formatter),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Double> expenseByDate = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(formatter),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // Update Line Chart
        lineChart.getData().clear();
        CategoryAxis lineXAxis = (CategoryAxis) lineChart.getXAxis();
        lineXAxis.getCategories().clear(); // Clear existing categories
        lineXAxis.setCategories(FXCollections.observableArrayList(allDates));
        lineXAxis.setLabel("Date");

        XYChart.Series<String, Number> lineIncomeSeries = new XYChart.Series<>();
        lineIncomeSeries.setName("Income");
        XYChart.Series<String, Number> lineExpenseSeries = new XYChart.Series<>();
        lineExpenseSeries.setName("Expense");

        for (String date : allDates) {
            double income = incomeByDate.getOrDefault(date, 0.0);
            double expense = expenseByDate.getOrDefault(date, 0.0);
            lineIncomeSeries.getData().add(new XYChart.Data<>(date, income));
            lineExpenseSeries.getData().add(new XYChart.Data<>(date, expense));
        }

        lineChart.getData().addAll(lineIncomeSeries, lineExpenseSeries);

        // Adjust line chart x-axis labels for better readability if the range is large
        if (totalDays > 30) {
            lineXAxis.setTickLabelRotation(45);
            lineXAxis.setTickLabelsVisible(true);
        } else {
            lineXAxis.setTickLabelRotation(0);
            lineXAxis.setTickLabelsVisible(true);
        }

        // Update Bar Chart
        barChart.getData().clear();
        CategoryAxis barXAxis = (CategoryAxis) barChart.getXAxis();
        barXAxis.getCategories().clear(); // Clear existing categories
        barXAxis.setCategories(FXCollections.observableArrayList(allDates));
        barXAxis.setLabel("Date");

        XYChart.Series<String, Number> barIncomeSeries = new XYChart.Series<>();
        barIncomeSeries.setName("Income");
        XYChart.Series<String, Number> barExpenseSeries = new XYChart.Series<>();
        barExpenseSeries.setName("Expense");

        for (String date : allDates) {
            double income = incomeByDate.getOrDefault(date, 0.0);
            double expense = expenseByDate.getOrDefault(date, 0.0);
            barIncomeSeries.getData().add(new XYChart.Data<>(date, income));
            barExpenseSeries.getData().add(new XYChart.Data<>(date, expense));
        }

        barChart.getData().addAll(barIncomeSeries, barExpenseSeries);

        // Adjust bar chart x-axis labels for better readability if the range is large
        if (totalDays > 30) {
            barXAxis.setTickLabelRotation(45);
            barXAxis.setTickLabelsVisible(true);
        } else {
            barXAxis.setTickLabelRotation(0);
            barXAxis.setTickLabelsVisible(true);
        }
    }

    private static Pane createCategoryPane() {
        BorderPane categoryPane = new BorderPane();
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
        title.setWrapText(true);

        pieChart = new PieChart();
        VBox.setVgrow(pieChart, Priority.ALWAYS);
        HBox.setHgrow(pieChart, Priority.ALWAYS);
        updatePieChart();

        VBox content = new VBox(10, title, pieChart);
        content.setAlignment(Pos.CENTER);
        categoryPane.setCenter(content);
        return categoryPane;
    }

    private static void updatePieChart() {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        switch (currentPeriod) {
            case "Last Month":
                startDate = now.minusMonths(1).withDayOfMonth(1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                break;
            case "All Transactions":
                if (transactions.isEmpty()) {
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                } else {
                    startDate = transactions.stream()
                            .map(t -> LocalDate.parse(t.getTransactionDate()))
                            .min(LocalDate::compareTo)
                            .orElse(now.withDayOfMonth(1));
                    endDate = transactions.stream()
                            .map(t -> LocalDate.parse(t.getTransactionDate()))
                            .max(LocalDate::compareTo)
                            .orElse(now.withDayOfMonth(now.lengthOfMonth()));
                }
                break;
            default: // "This Month"
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
        }

        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;
        transactions = transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
                    return !txDate.isBefore(finalStartDate) && !txDate.isAfter(finalEndDate);
                })
                .collect(Collectors.toList());

        Map<String, Double> categoryTotals = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryTotals.forEach((category, amount) ->
                pieChartData.add(new PieChart.Data(category + " " + String.format("%.2f CNY", amount), amount))
        );
        pieChart.setData(pieChartData);
    }

    private static Pane createTransactionsPane() {
        BorderPane txPane = new BorderPane();
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
        title.setWrapText(true);

        VBox transactionsBox = new VBox(5);
        transactionsBox.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(transactionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(150);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        List<Transaction> transactions = txService.loadTransactions(currentUser);
        transactions.stream()
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .forEach(t -> {
                    Label txLabel = new Label(String.format("%s   %s    %.2f CNY",
                            t.getTransactionDate(),
                            t.getCategory(),
                            t.getAmount()));
                    txLabel.setWrapText(true);
                    transactionsBox.getChildren().add(txLabel);
                });

        VBox vbox = new VBox(10, title, scrollPane);
        vbox.setAlignment(Pos.TOP_LEFT);
        txPane.setCenter(vbox);
        return txPane;
    }

    private static Pane createAIPane() {
        BorderPane aiPane = new BorderPane();
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
        title.setWrapText(true);

        TextArea questionArea = new TextArea();
        questionArea.setPromptText("Type your question...");
        VBox.setVgrow(questionArea, Priority.ALWAYS);
        HBox.setHgrow(questionArea, Priority.ALWAYS);

        Button sendBtn = new Button("➤");
        sendBtn.setStyle("-fx-background-color: #A3D1FF; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-weight: bold;");

        sendBtn.setOnAction(e -> {
            String userInput = questionArea.getText().trim();
            if (!userInput.isEmpty()) {
                questionArea.setDisable(true);
                sendBtn.setDisable(true);

                List<Transaction> txList = txService.loadTransactions(currentUser);
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
                String systemPrompt =
                        "现在你是我的专属财务管理助手，我希望你解答我有关个人财务的问题。\n" +
                                "这是我的财务数据结构: Transaction Date(YYYY-MM-DD), Type(Income/Expense), Currency, Amount, Category, PaymentMethod.\n" +
                                "下面是我目前的数据：\n" + dataSummary +
                                "\n用户的问题是： " + userInput;

                String answer = AiChatService.chatCompletion(chatMessages, systemPrompt);
                if (answer != null) {
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

    private static Pane createSuggestionPane() {
        BorderPane sugPane = new BorderPane();
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
        title.setWrapText(true);

        suggestionsArea = new TextArea();
        suggestionsArea.setEditable(false);
        suggestionsArea.setWrapText(true);
        VBox.setVgrow(suggestionsArea, Priority.ALWAYS);
        HBox.setHgrow(suggestionsArea, Priority.ALWAYS);

        Button moreBtn = new Button("More>");
        moreBtn.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-background-radius: 10;");

        VBox vbox = new VBox(10, title, suggestionsArea, moreBtn);
        vbox.setAlignment(Pos.TOP_LEFT);
        sugPane.setCenter(vbox);
        return sugPane;
    }
}