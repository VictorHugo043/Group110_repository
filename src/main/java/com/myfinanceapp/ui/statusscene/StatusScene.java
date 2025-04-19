package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.transactionscene.TransactionManagementScene;
import com.myfinanceapp.service.ThemeService;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatusScene {
    private final User currentUser;
    private final Stage stage;
    private final double width;
    private final double height;
    public List<Map<String, String>> chatHistory = new ArrayList<>();
    private Button moreBtn;

    // UI 组件
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public ComboBox<String> chartTypeCombo;
    public Label exLabel;
    public Label inLabel;
    public LineChart<String, Number> lineChart;
    public BarChart<String, Number> barChart;
    public PieChart pieChart;
    public WebView suggestionsWebView;
    public TextArea questionArea;
    public Button sendBtn;
    public VBox transactionsBox;
    public StackPane chartPane;
    private ThemeService themeService; // Store ThemeService instance

    public StatusScene(Stage stage, double width, double height, User loggedUser) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.currentUser = loggedUser;
    }

    // Overloaded method for backward compatibility
    public Scene createScene() {
        return createScene(new ThemeService());
    }

    public Scene createScene(ThemeService themeService) {
        this.themeService = themeService; // Store the ThemeService instance
        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Status", currentUser, themeService);
        root.setLeft(sideBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        // Set proper background colors and styling
        String backgroundColor = themeService.isDayMode() ? "white" : "#2A2A2A";
        scrollPane.setStyle("-fx-background: " + backgroundColor + "; -fx-background-color: " + backgroundColor + "; -fx-border-width: 0;");

        // Remove any padding that might affect the layout
        scrollPane.setPadding(new Insets(0));
        root.setCenter(scrollPane);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setFillWidth(true);
        mainContent.setStyle("-fx-background-color: " + backgroundColor + ";");
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
        VBox.setVgrow(aiPane, Priority.NEVER); // AI问题框不自动增长
        VBox.setVgrow(suggestionPane, Priority.ALWAYS); // 建议框可以占据所有剩余空间
        rightColumn.getChildren().addAll(aiPane, suggestionPane);

        bottomArea.getChildren().addAll(leftColumn, rightColumn);
        mainContent.getChildren().add(bottomArea);

        Scene scene = new Scene(root, width, height);

        // Add global CSS styles for consistent appearance
        scene.getStylesheets().add("data:text/css," +
                ".scroll-pane { -fx-background-insets: 0; -fx-padding: 0; }" +
                ".scroll-pane > .viewport { -fx-background-color: " + backgroundColor + "; }" +
                ".scroll-pane > .corner { -fx-background-color: " + backgroundColor + "; }");

        return scene;
    }

    private Pane createTopPane() {
        BorderPane topBorder = new BorderPane();
        topBorder.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2;" + themeService.getCurrentFormBackgroundStyle());
        topBorder.setPadding(new Insets(15));

        Label title = new Label("Income and Expenses");
        title.setFont(Font.font(20));
        title.setStyle(themeService.getTextColorStyle());
        title.setWrapText(true);

        // 使用 GridPane 确保 Start Date, End Date, Chart Type 的控件左端对齐
        GridPane controlGrid = new GridPane();
        controlGrid.setVgap(5);

        // 设置列约束，确保控件左端对齐
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(80); // 标签列宽度
        labelColumn.setHalignment(HPos.LEFT);
        ColumnConstraints controlColumn = new ColumnConstraints();
        controlColumn.setMinWidth(150); // 控件列宽度，确保 DatePicker 和 ComboBox 宽度一致
        controlColumn.setHalignment(HPos.LEFT);
        controlGrid.getColumnConstraints().addAll(labelColumn, controlColumn);

        // Start Date
        Label startDateLabel = new Label("Start Date");
        startDateLabel.setWrapText(true);
        startDateLabel.setStyle(themeService.getTextColorStyle());
        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Select start date");
        startDatePicker.setPrefWidth(150);
        controlGrid.add(startDateLabel, 0, 0);
        controlGrid.add(startDatePicker, 1, 0);

        // End Date
        Label endDateLabel = new Label("End Date");
        endDateLabel.setWrapText(true);
        endDateLabel.setStyle(themeService.getTextColorStyle());
        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Select end date");
        endDatePicker.setPrefWidth(150);
        controlGrid.add(endDateLabel, 0, 1);
        controlGrid.add(endDatePicker, 1, 1);

        // Chart Type
        Label chartTypeLabel = new Label("Chart Type");
        chartTypeLabel.setWrapText(true);
        chartTypeLabel.setStyle(themeService.getTextColorStyle());
        chartTypeCombo = new ComboBox<>();
        chartTypeCombo.getItems().addAll("Line graph", "Bar graph");
        chartTypeCombo.setValue("Line graph");
        chartTypeCombo.setPrefWidth(150);
        controlGrid.add(chartTypeLabel, 0, 2);
        controlGrid.add(chartTypeCombo, 1, 2);

        // Ex. 和 In. 标签
        exLabel = new Label();
        exLabel.setStyle(themeService.getTextColorStyle());
        inLabel = new Label();
        inLabel.setStyle(themeService.getTextColorStyle());

        // 使用 VBox 实现均匀分布
        VBox leftSide = new VBox();
        leftSide.setAlignment(Pos.TOP_LEFT);
        leftSide.setFillWidth(true);

        // 添加占位符 Region，实现均匀分布
        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);
        Region spacer3 = new Region();
        VBox.setVgrow(spacer3, Priority.ALWAYS);
        Region spacer4 = new Region();
        VBox.setVgrow(spacer4, Priority.ALWAYS);

        leftSide.getChildren().addAll(controlGrid, spacer1, exLabel, spacer2, inLabel, spacer3, new Region(), spacer4);

        // 图表区域
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Amount");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Ex/In Trend");

        CategoryAxis barXAxis = new CategoryAxis();
        NumberAxis barYAxis = new NumberAxis();
        barXAxis.setLabel("Date");
        barYAxis.setLabel("Amount");
        barChart = new BarChart<>(barXAxis, barYAxis);
        barChart.setTitle("Ex/In Trend");

        chartPane = new StackPane(lineChart);
        HBox.setHgrow(chartPane, Priority.ALWAYS);
        VBox.setVgrow(chartPane, Priority.ALWAYS);

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

    private Pane createCategoryPane() {
        BorderPane categoryPane = new BorderPane();
        categoryPane.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2;" + themeService.getCurrentFormBackgroundStyle());
        categoryPane.setPadding(new Insets(15));

        Label title = new Label("Category Proportion Analysis");
        title.setFont(Font.font(16));
        title.setStyle(themeService.getTextColorStyle());
        title.setWrapText(true);

        pieChart = new PieChart();
        VBox.setVgrow(pieChart, Priority.ALWAYS);
        HBox.setHgrow(pieChart, Priority.ALWAYS);

        VBox content = new VBox(10, title, pieChart);
        content.setAlignment(Pos.CENTER);
        categoryPane.setCenter(content);
        return categoryPane;
    }

    private Pane createTransactionsPane() {
        BorderPane txPane = new BorderPane();
        txPane.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2;" + themeService.getCurrentFormBackgroundStyle());
        txPane.setPadding(new Insets(15));

        Label title = new Label("Recent Transactions");
        title.setFont(Font.font(16));
        title.setStyle(themeService.getTextColorStyle());
        title.setWrapText(true);

        // 添加管理按钮
        Button manageBtn = new Button("Manage All Transactions");
        manageBtn.setStyle(themeService.getButtonStyle());
        manageBtn.setOnAction(e -> openTransactionManagement());

        HBox titleBar = new HBox(10, title, manageBtn);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);

        transactionsBox = new VBox(5);
        transactionsBox.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(transactionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(150);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox vbox = new VBox(10, titleBar, scrollPane);
        vbox.setAlignment(Pos.TOP_LEFT);
        txPane.setCenter(vbox);
        return txPane;
    }

    private void openTransactionManagement() {
        // 获取当前窗口的实际大小
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        TransactionManagementScene txManagementScene = new TransactionManagementScene(stage, currentWidth, currentHeight, currentUser);
        Scene scene = txManagementScene.createScene(themeService); // Pass themeService
        stage.setScene(scene);
    }

    private Pane createAIPane() {
        BorderPane aiPane = new BorderPane();
        aiPane.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2;" + themeService.getCurrentFormBackgroundStyle());
        aiPane.setPadding(new Insets(15));

        Label title = new Label("Ask Your AI Assistant:");
        title.setFont(Font.font(16));
        title.setStyle(themeService.getTextColorStyle());
        title.setWrapText(true);

        questionArea = new TextArea();
        questionArea.setPromptText("Type your question...");
        questionArea.setPrefHeight(80); // 设置较小的高度
        questionArea.setWrapText(true); // 设置文本自动换行
        VBox.setVgrow(questionArea, Priority.NEVER); // 防止垂直伸展
        HBox.setHgrow(questionArea, Priority.ALWAYS);

        sendBtn = new Button("➤");
        sendBtn.setStyle(themeService.getButtonStyle() + "-fx-font-weight: bold;");

        VBox content = new VBox(10, title, questionArea, sendBtn);
        content.setAlignment(Pos.TOP_LEFT);
        aiPane.setCenter(content);
        return aiPane;
    }

    private Pane createSuggestionPane() {
        BorderPane sugPane = new BorderPane();
        sugPane.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2;" + themeService.getCurrentFormBackgroundStyle());
        sugPane.setPadding(new Insets(15));

        Label title = new Label("Suggestion");
        title.setFont(Font.font(16));
        title.setStyle(themeService.getTextColorStyle());
        title.setWrapText(true);

        suggestionsWebView = new WebView();
        suggestionsWebView.setPrefHeight(250);
        VBox.setVgrow(suggestionsWebView, Priority.ALWAYS); // 允许垂直伸展
        suggestionsWebView.getEngine().setUserStyleSheetLocation(
                getClass().getResource("/css/markdown.css").toExternalForm()
        );

        moreBtn = new Button("More>");
        moreBtn.setStyle(themeService.getButtonStyle());
        moreBtn.setOnAction(e -> showChatHistory());

        VBox vbox = new VBox(10, title, suggestionsWebView, moreBtn);
        vbox.setAlignment(Pos.TOP_LEFT);
        sugPane.setCenter(vbox);
        return sugPane;
    }

    private void showChatHistory() {
        Stage historyStage = new Stage();
        historyStage.setTitle("Chat History");

        WebView historyView = new WebView();
        historyView.setPrefSize(600, 400);
        historyView.getEngine().setUserStyleSheetLocation(
                getClass().getResource("/css/markdown.css").toExternalForm()
        );

        // 构建HTML聊天记录
        StringBuilder htmlContent = new StringBuilder("<div class='chat-history'>");
        for (Map<String, String> message : chatHistory) {
            String role = message.get("role");
            String content = message.get("content");

            if ("user".equals(role)) {
                htmlContent.append("<div class='user-message'>")
                        .append("<strong>You:</strong> ")
                        .append(content)
                        .append("</div>");
            } else if ("assistant".equals(role)) {
                htmlContent.append("<div class='assistant-message'>")
                        .append("<strong>Assistant:</strong> ")
                        .append(content)
                        .append("</div>");
            }
        }
        htmlContent.append("</div>");

        historyView.getEngine().loadContent(htmlContent.toString());

        Scene scene = new Scene(new StackPane(historyView), 600, 400);
        historyStage.setScene(scene);
        historyStage.show();
    }
}