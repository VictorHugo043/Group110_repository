package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
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

public class StatusScene {
    private final User currentUser;
    private final Stage stage;
    private final double width;
    private final double height;

    // UI 组件
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public ComboBox<String> chartTypeCombo;
    public Label exLabel;
    public Label inLabel;
    public LineChart<String, Number> lineChart;
    public BarChart<String, Number> barChart;
    public PieChart pieChart;
    public TextArea suggestionsArea;
    public TextArea questionArea;
    public Button sendBtn;
    public VBox transactionsBox;
    public StackPane chartPane;

    public StatusScene(Stage stage, double width, double height, User loggedUser) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.currentUser = loggedUser;
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Status", currentUser);
        root.setLeft(sideBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        // 设置主 ScrollPane 背景为白色，解决颜色不一致问题
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");
        scrollPane.getStyleClass().add("white-bg-scroll-pane");
        root.setCenter(scrollPane);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setFillWidth(true);
        mainContent.setStyle("-fx-background-color: white;");
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
        mainContent.getChildren().add(bottomArea);

        Scene scene = new Scene(root, width, height);

        // 添加全局CSS样式，确保所有 ScrollPane 的视口背景是白色
        scene.getStylesheets().add("data:text/css," + 
                                 ".white-bg-scroll-pane .viewport {-fx-background-color: white;}" +
                                 ".white-bg-scroll-pane {-fx-background: white; -fx-background-color: white;}");

        return scene;
    }

    private Pane createTopPane() {
        BorderPane topBorder = new BorderPane();
        topBorder.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2; -fx-background-color: white;");
        topBorder.setPadding(new Insets(15));

        Label title = new Label("Income and Expenses");
        title.setFont(Font.font(20));
        title.setTextFill(Color.web("#3282FA"));
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
        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Select start date");
        startDatePicker.setPrefWidth(150);
        controlGrid.add(startDateLabel, 0, 0);
        controlGrid.add(startDatePicker, 1, 0);

        // End Date
        Label endDateLabel = new Label("End Date");
        endDateLabel.setWrapText(true);
        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Select end date");
        endDatePicker.setPrefWidth(150);
        controlGrid.add(endDateLabel, 0, 1);
        controlGrid.add(endDatePicker, 1, 1);

        // Chart Type
        Label chartTypeLabel = new Label("Chart Type");
        chartTypeLabel.setWrapText(true);
        chartTypeCombo = new ComboBox<>();
        chartTypeCombo.getItems().addAll("Line graph", "Bar graph");
        chartTypeCombo.setValue("Line graph");
        chartTypeCombo.setPrefWidth(150);
        controlGrid.add(chartTypeLabel, 0, 2);
        controlGrid.add(chartTypeCombo, 1, 2);

        // Ex. 和 In. 标签
        exLabel = new Label();
        inLabel = new Label();

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
        categoryPane.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2; -fx-background-color: white;");
        categoryPane.setPadding(new Insets(15));

        Label title = new Label("Category Proportion Analysis");
        title.setFont(Font.font(16));
        title.setTextFill(Color.web("#3282FA"));
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
        txPane.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2; -fx-background-color: white;");
        txPane.setPadding(new Insets(15));

        Label title = new Label("Recent Transactions");
        title.setFont(Font.font(16));
        title.setTextFill(Color.web("#3282FA"));
        title.setWrapText(true);

        transactionsBox = new VBox(5);
        transactionsBox.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(transactionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(150);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox vbox = new VBox(10, title, scrollPane);
        vbox.setAlignment(Pos.TOP_LEFT);
        txPane.setCenter(vbox);
        return txPane;
    }

    private Pane createAIPane() {
        BorderPane aiPane = new BorderPane();
        aiPane.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2; -fx-background-color: white;");
        aiPane.setPadding(new Insets(15));

        Label title = new Label("Ask Your AI Assistant:");
        title.setFont(Font.font(16));
        title.setTextFill(Color.web("#3282FA"));
        title.setWrapText(true);

        questionArea = new TextArea();
        questionArea.setPromptText("Type your question...");
        VBox.setVgrow(questionArea, Priority.ALWAYS);
        HBox.setHgrow(questionArea, Priority.ALWAYS);

        sendBtn = new Button("➤");
        sendBtn.setStyle("-fx-background-color: #A3D1FF; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-weight: bold;");

        VBox content = new VBox(10, title, questionArea, sendBtn);
        content.setAlignment(Pos.TOP_LEFT);
        aiPane.setCenter(content);
        return aiPane;
    }

    private Pane createSuggestionPane() {
        BorderPane sugPane = new BorderPane();
        sugPane.setStyle("-fx-border-color: #3282FA; -fx-border-radius: 20; -fx-background-radius: 20; -fx-border-width: 2; -fx-background-color: white;");
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