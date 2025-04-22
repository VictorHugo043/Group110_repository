package com.myfinanceapp.ui.statusscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.transactionscene.TransactionManagementScene;
import com.myfinanceapp.service.ThemeService;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.web.WebView;
import javafx.scene.text.Text;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

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
    public ThemeService themeService;
    private ScrollPane pieChartScrollPane;

    public StatusScene(Stage stage, double width, double height, User loggedUser) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.currentUser = loggedUser;
    }

    public Scene createScene() {
        return createScene(new ThemeService());
    }

    public Scene createScene(ThemeService themeService) {
        this.themeService = themeService;
        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Status", currentUser, themeService);
        root.setLeft(sideBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        String backgroundColor = themeService.isDayMode() ? "white" : "#2A2A2A";
        scrollPane.setStyle("-fx-background: " + backgroundColor + "; -fx-background-color: " + backgroundColor + "; -fx-border-width: 0;");

        scrollPane.setPadding(new Insets(2));
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
        VBox.setVgrow(aiPane, Priority.NEVER);
        VBox.setVgrow(suggestionPane, Priority.ALWAYS);
        rightColumn.getChildren().addAll(aiPane, suggestionPane);

        bottomArea.getChildren().addAll(leftColumn, rightColumn);
        mainContent.getChildren().add(bottomArea);

        Scene scene = new Scene(root, width, height);

        // Add existing chart stylesheet
        scene.getStylesheets().add(getClass().getResource("/css/chart-styles.css").toExternalForm());
        // Add dynamic theme stylesheet for ComboBox and DatePicker
        scene.getStylesheets().add("data:text/css," + themeService.getThemeStylesheet());
        // Add scroll pane styles
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

        GridPane controlGrid = new GridPane();
        controlGrid.setVgap(5);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(80);
        labelColumn.setHalignment(HPos.LEFT);
        ColumnConstraints controlColumn = new ColumnConstraints();
        controlColumn.setMinWidth(150);
        controlColumn.setHalignment(HPos.LEFT);
        controlGrid.getColumnConstraints().addAll(labelColumn, controlColumn);

        Label startDateLabel = new Label("Start Date");
        startDateLabel.setWrapText(true);
        startDateLabel.setStyle(themeService.getTextColorStyle());
        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Select start date");
        startDatePicker.setPrefWidth(150);
        startDatePicker.getStyleClass().add(themeService.isDayMode() ? "day-theme-date-picker" : "night-theme-date-picker");
        controlGrid.add(startDateLabel, 0, 0);
        controlGrid.add(startDatePicker, 1, 0);

        Label endDateLabel = new Label("End Date");
        endDateLabel.setWrapText(true);
        endDateLabel.setStyle(themeService.getTextColorStyle());
        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Select end date");
        endDatePicker.setPrefWidth(150);
        endDatePicker.getStyleClass().add(themeService.isDayMode() ? "day-theme-date-picker" : "night-theme-date-picker");
        controlGrid.add(endDateLabel, 0, 1);
        controlGrid.add(endDatePicker, 1, 1);

        Label chartTypeLabel = new Label("Chart Type");
        chartTypeLabel.setWrapText(true);
        chartTypeLabel.setStyle(themeService.getTextColorStyle());
        chartTypeCombo = new ComboBox<>();
        chartTypeCombo.getItems().addAll("Line graph", "Bar graph");
        chartTypeCombo.setValue("Line graph");
        chartTypeCombo.setPrefWidth(150);
        chartTypeCombo.getStyleClass().add(themeService.isDayMode() ? "day-theme-combo-box" : "night-theme-combo-box");
        controlGrid.add(chartTypeLabel, 0, 2);
        controlGrid.add(chartTypeCombo, 1, 2);

        exLabel = new Label();
        exLabel.setStyle(themeService.getTextColorStyle());
        inLabel = new Label();
        inLabel.setStyle(themeService.getTextColorStyle());

        VBox leftSide = new VBox();
        leftSide.setAlignment(Pos.TOP_LEFT);
        leftSide.setFillWidth(true);

        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);
        Region spacer3 = new Region();
        VBox.setVgrow(spacer3, Priority.ALWAYS);
        Region spacer4 = new Region();
        VBox.setVgrow(spacer4, Priority.ALWAYS);

        leftSide.getChildren().addAll(controlGrid, spacer1, exLabel, spacer2, inLabel, spacer3, new Region(), spacer4);

        // Create LineChart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Amount");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Ex/In Trend");

        // Create BarChart
        CategoryAxis barXAxis = new CategoryAxis();
        NumberAxis barYAxis = new NumberAxis();
        barXAxis.setLabel("Date");
        barYAxis.setLabel("Amount");
        barChart = new BarChart<>(barXAxis, barYAxis);
        barChart.setTitle("Ex/In Trend");

        // Reduce spacing between chart and "Date" label
        barXAxis.setTickLabelGap(5); // Reduce gap between tick labels
        barXAxis.setStyle("-fx-padding: 0 0 5 0;"); // Minimize padding below the axis label
        barChart.setStyle("-fx-padding: 0;"); // Remove padding around the chart itself

        // Rotate x-axis labels for bar chart to prevent overlap
        barXAxis.setTickLabelRotation(45);
        barChart.setBarGap(2);
        barChart.setCategoryGap(10);

        // Wrap the bar chart in a ScrollPane to enable horizontal scrolling
        ScrollPane barChartScrollPane = new ScrollPane(barChart);
        barChartScrollPane.setFitToHeight(true);
        barChartScrollPane.setFitToWidth(false); // Allow horizontal scrolling
        barChartScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        barChartScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Dynamically adjust the number of visible labels based on chart width
        barChartScrollPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();
            int labelCount = barXAxis.getCategories().size();
            if (labelCount > 0) {
                int maxLabels = (int) (width / 60); // Assuming 60px per label for readability
                if (maxLabels < 1) maxLabels = 1;
                int skip = Math.max(1, labelCount / maxLabels);
                barXAxis.setTickLabelRotation(45);
                barXAxis.setTickLabelGap(5);
                barXAxis.getCategories().forEach(category -> {
                    int index = barXAxis.getCategories().indexOf(category);
                    barXAxis.lookupAll(".axis-tick-mark").forEach(tick -> {
                        if (tick.getUserData() != null && tick.getUserData().equals(category)) {
                            tick.setVisible(index % skip == 0);
                        }
                    });
                    barXAxis.lookupAll(".axis-tick-label").forEach(node -> {
                        if (node instanceof Text) {
                            Text label = (Text) node;
                            if (label.getText().equals(category)) {
                                label.setVisible(index % skip == 0);
                            }
                        }
                    });
                });
            }
        });

        // Set up chartPane with dynamic resizing
        chartPane = new StackPane(lineChart);
        chartPane.setMinHeight(300); // Set a minimum height to prevent compression
        chartPane.setPrefHeight(400); // Set a preferred height
        HBox.setHgrow(chartPane, Priority.ALWAYS);
        VBox.setVgrow(chartPane, Priority.ALWAYS);

        // Add a listener to dynamically adjust chart height based on parent container
        chartPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            double chartHeight = newHeight.doubleValue();
            barChart.setPrefHeight(chartHeight);
            lineChart.setPrefHeight(chartHeight);
        });

        // Switch between line and bar chart based on selection
        chartTypeCombo.setOnAction(e -> {
            chartPane.getChildren().clear();
            if ("Bar graph".equals(chartTypeCombo.getValue())) {
                chartPane.getChildren().add(barChartScrollPane);
            } else {
                chartPane.getChildren().add(lineChart); // Fixed syntax error
            }
        });

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
        pieChart.setLabelsVisible(false); // Disable indicator labels next to pie slices
        pieChart.setLegendVisible(true); // Ensure legend is visible by default

        // Create a container to hold the pie chart and its legends
        VBox pieChartContainer = new VBox(10); // Use VBox to stack pie chart and legends vertically
        pieChartContainer.setAlignment(Pos.CENTER);
        pieChartContainer.getChildren().add(pieChart);

        pieChartScrollPane = new ScrollPane(pieChartContainer);
        pieChartScrollPane.setFitToWidth(true); // Ensure the container fits the width of the ScrollPane
        pieChartScrollPane.setFitToHeight(false);
        pieChartScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pieChartScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable horizontal scrolling
        pieChartScrollPane.getStyleClass().add("pie-chart-container");

        // Adjust legend position based on container width
        pieChartScrollPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double width = newValue.doubleValue();
                pieChart.getStyleClass().removeAll("pie-chart-legend-side", "pie-chart-legend-bottom");
                pieChartContainer.getChildren().clear();

                if (width < 500) { // Threshold for small window (legends at bottom)
                    pieChart.setLegendSide(Side.BOTTOM);
                    pieChart.setLegendVisible(true);
                    pieChart.getStyleClass().add("pie-chart-legend-bottom");
                    pieChartContainer.getChildren().setAll(pieChart);
                } else { // Larger window (legends on right)
                    pieChart.setLegendSide(Side.RIGHT);
                    pieChart.setLegendVisible(true);
                    pieChart.getStyleClass().add("pie-chart-legend-side");
                    pieChartContainer.getChildren().setAll(pieChart);
                }
            }
        });

        // Ensure initial legend positioning
        if (pieChartScrollPane.getWidth() < 500 || pieChartScrollPane.getWidth() == 0) {
            pieChart.setLegendSide(Side.BOTTOM);
            pieChart.getStyleClass().add("pie-chart-legend-bottom");
            pieChartContainer.getChildren().setAll(pieChart);
        } else {
            pieChart.setLegendSide(Side.RIGHT);
            pieChart.getStyleClass().add("pie-chart-legend-side");
            pieChartContainer.getChildren().setAll(pieChart);
        }

        VBox content = new VBox(10, title, pieChartScrollPane);
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
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        TransactionManagementScene txManagementScene = new TransactionManagementScene(stage, currentWidth, currentHeight, currentUser);
        Scene scene = txManagementScene.createScene(themeService);
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
        questionArea.setPrefHeight(80);
        questionArea.setWrapText(true);
        String backgroundColor = themeService.isDayMode() ? "white" : "#3C3C3C";
        String textColor = themeService.isDayMode() ? "black" : "white";
        String promptTextColor = themeService.isDayMode() ? "#555555" : "#CCCCCC";
        String textAreaStyle = String.format(
                "-fx-background-color: %s; " +
                        "-fx-control-inner-background: %s; " +
                        "-fx-text-fill: %s; " +
                        "-fx-prompt-text-fill: %s;",
                backgroundColor, backgroundColor, textColor, promptTextColor
        );
        questionArea.setStyle(textAreaStyle);
        VBox.setVgrow(questionArea, Priority.NEVER);
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
        String webViewBgColor = themeService.isDayMode() ? "white" : "#3C3C3C";
        suggestionsWebView.setStyle("-fx-background-color: " + webViewBgColor + ";");
        VBox.setVgrow(suggestionsWebView, Priority.ALWAYS);

        String cssFile = themeService.isDayMode() ? "/css/markdown-day.css" : "/css/markdown-night.css";
        if (getClass().getResource(cssFile) != null) {
            suggestionsWebView.getEngine().setUserStyleSheetLocation(getClass().getResource(cssFile).toExternalForm());
        } else {
            suggestionsWebView.getEngine().setUserStyleSheetLocation("data:text/css," + getDefaultMarkdownCss(themeService.isDayMode()));
        }

        moreBtn = new Button("More>");
        moreBtn.setStyle(themeService.getButtonStyle());
        moreBtn.setOnAction(e -> showChatHistory());

        VBox vbox = new VBox(10, title, suggestionsWebView, moreBtn);
        vbox.setAlignment(Pos.TOP_LEFT);
        sugPane.setCenter(vbox);
        return sugPane;
    }

    private String getDefaultMarkdownCss(boolean isDayMode) {
        if (isDayMode) {
            return "body { background-color: white; color: black; margin: 0; padding: 0; } " +
                    ".chat-history { padding: 10px; font-family: Arial, sans-serif; } " +
                    ".user-message { background-color: #E1F5FE; border-radius: 10px; padding: 8px 12px; margin: 8px 0; max-width: 90%; color: black; } " +
                    ".assistant-message { background-color: #F1F8E9; border-radius: 10px; padding: 8px 12px; margin: 8px 0; max-width: 90%; color: black; } " +
                    ".user-message, .assistant-message { word-wrap: break-word; overflow-wrap: break-word; }";
        } else {
            return "body { background-color: #3C3C3C; color: white; margin: 0; padding: 0; } " +
                    ".chat-history { padding: 10px; font-family: Arial, sans-serif; } " +
                    ".user-message { background-color: #4A6FA5; border-radius: 10px; padding: 8px 12px; margin: 8px 0; max-width: 90%; color: white; } " +
                    ".assistant-message { background-color: #3C3C3C; border-radius: 10px; padding: 8px 12px; margin: 8px 0; max-width: 90%; color: white; } " +
                    ".user-message, .assistant-message { word-wrap: break-word; overflow-wrap: break-word; }";
        }
    }

    private void showChatHistory() {
        Stage historyStage = new Stage();
        historyStage.setTitle("Chat History");

        WebView historyView = new WebView();
        historyView.setPrefSize(600, 400);
        String cssFile = themeService.isDayMode() ? "/css/markdown-day.css" : "/css/markdown-night.css";
        if (getClass().getResource(cssFile) != null) {
            historyView.getEngine().setUserStyleSheetLocation(getClass().getResource(cssFile).toExternalForm());
        } else {
            historyView.getEngine().setUserStyleSheetLocation("data:text/css," + getDefaultMarkdownCss(themeService.isDayMode()));
        }

        StringBuilder htmlContent = new StringBuilder("<!DOCTYPE html><html><head>");
        htmlContent.append("<meta charset='UTF-8'>");
        htmlContent.append("</head><body style='background-color: ")
                .append(themeService.isDayMode() ? "white" : "#3C3C3C")
                .append("; color: ")
                .append(themeService.isDayMode() ? "black" : "white")
                .append("; margin: 0; padding: 0;'>");
        htmlContent.append("<div class='chat-history'>");
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
        htmlContent.append("</div></body></html>");

        historyView.getEngine().loadContent(htmlContent.toString());

        Scene scene = new Scene(new StackPane(historyView), 600, 400);
        historyStage.setScene(scene);
        historyStage.show();
    }
}