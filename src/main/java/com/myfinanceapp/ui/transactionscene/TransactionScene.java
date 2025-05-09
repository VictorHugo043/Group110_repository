package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.TransactionService;
import com.myfinanceapp.service.ThemeService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.application.Platform;

import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.service.AISortingService;
import com.myfinanceapp.service.CurrencyService;

import java.io.File;
import java.time.LocalDate;
import java.text.ParseException;

public class TransactionScene {
    // Overloaded method for backward compatibility
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService());
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser,
                                    ThemeService themeService) {
        return createScene(stage, width, height, loggedUser, themeService, new CurrencyService("CNY"));
    }



    public static Scene createScene(Stage stage, double width, double height, User loggedUser,
                                    ThemeService themeService, CurrencyService currencyService) {
        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // 保存初始宽高，用于计算缩放比例
        final double INITIAL_WIDTH = width;
        final double INITIAL_HEIGHT = height;

        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "New", loggedUser, themeService, currencyService);
        root.setLeft(sideBar);

        // 中间手动输入部分
        VBox centerBox = new VBox();
        centerBox.setStyle(
                "-fx-border-color: #3282fa;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        themeService.getCurrentFormBackgroundStyle() +
                        "-fx-padding: 20;");
        centerBox.setPadding(new Insets(20, 20, 40, 20));
        centerBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(centerBox, Priority.ALWAYS);

        Label topicLabel = new Label("Manual Import:");
        topicLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;" + themeService.getTextColorStyle());

        // 日期选择器部分
        Label dateLabel = new Label("Transaction Date");
        dateLabel.setStyle(themeService.getTextColorStyle());

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select date");
        datePicker.getStyleClass().add(themeService.isDayMode() ? "day-theme-date-picker" : "night-theme-date-picker");
        datePicker.setVisible(false);
        datePicker.setVisible(true);

        datePicker.setMaxWidth(200);
        datePicker.setPrefWidth(150);

        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean isFuture = date.isAfter(LocalDate.now());
                setDisable(isFuture);
                if (isFuture) {
                    setStyle("-fx-text-fill: #808080;");
                } else {
                    setStyle(themeService.isDayMode() ? "-fx-text-fill: black;" : "-fx-text-fill: white;");
                }
            }
        });

        VBox dateBox = new VBox(dateLabel, datePicker);
        dateBox.setAlignment(Pos.CENTER);

        Label typeLabel = new Label("Transaction Type");
        typeLabel.setStyle(themeService.getTextColorStyle());
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Income", "Expense");
        typeCombo.setMaxWidth(200);
        typeCombo.setPrefWidth(150);
        typeCombo.setValue("Expense");
        typeCombo.setFocusTraversable(false);
        typeCombo.getStyleClass().add(themeService.isDayMode() ? "day-theme-combo-box" : "night-theme-combo-box");
        typeCombo.setVisible(false);
        typeCombo.setVisible(true);
        VBox typeBox = new VBox(typeLabel, typeCombo);
        typeBox.setAlignment(Pos.CENTER);

        Label currencyLabel = new Label("Currency");
        currencyLabel.setStyle(themeService.getTextColorStyle());
        ComboBox<String> currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll("CNY", "USD", "EUR");
        currencyCombo.setMaxWidth(200);
        currencyCombo.setPrefWidth(150);
        currencyCombo.setValue("CNY");
        currencyCombo.setFocusTraversable(false);
        currencyCombo.getStyleClass().add(themeService.isDayMode() ? "day-theme-combo-box" : "night-theme-combo-box");
        currencyCombo.setVisible(false);
        currencyCombo.setVisible(true);
        VBox currencyBox = new VBox(currencyLabel, currencyCombo);
        currencyBox.setAlignment(Pos.CENTER);

        Label amountLabel = new Label("Amount");
        amountLabel.setStyle(themeService.getTextColorStyle());
        TextField amountField = new TextField();
        amountField.setPromptText("Please enter amount");
        amountField.setMaxWidth(200);
        amountField.setPrefWidth(150);
        amountField.setFocusTraversable(false);
        amountField.getStyleClass().add(themeService.isDayMode() ? "day-theme-text-field" : "night-theme-text-field");
        amountField.setVisible(false);
        amountField.setVisible(true);
        VBox amountBox = new VBox(amountLabel, amountField);
        amountBox.setAlignment(Pos.CENTER);

        Label descriptionLabel = new Label("Description");
        descriptionLabel.setStyle(themeService.getTextColorStyle());
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter transaction description");
        descriptionField.setMaxWidth(200);
        descriptionField.setPrefWidth(150);
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        descriptionField.setFocusTraversable(false);
        descriptionField.getStyleClass().add(themeService.isDayMode() ? "day-theme-text-field" : "night-theme-text-field");
        descriptionField.setVisible(false);
        descriptionField.setVisible(true);
        VBox descriptionBox = new VBox(descriptionLabel, descriptionField);
        descriptionBox.setAlignment(Pos.CENTER);

        Button autoSortButton = new Button("Auto-sorting");
        autoSortButton.setStyle(themeService.getButtonStyle() + "-fx-font-weight: bold; " + "-fx-border-radius: 15;");
        autoSortButton.setMaxWidth(100);
        autoSortButton.setPrefWidth(100);

        Label categoryLabel = new Label("Category");
        categoryLabel.setStyle(themeService.getTextColorStyle());
        TextField categoryField = new TextField();
        categoryField.setPromptText("e.g., Salary, Rent, Utilities");
        categoryField.setMaxWidth(200);
        categoryField.setPrefWidth(150);
        categoryField.setFocusTraversable(false);
        categoryField.getStyleClass().add(themeService.isDayMode() ? "day-theme-text-field" : "night-theme-text-field");
        categoryField.setVisible(false);
        categoryField.setVisible(true);

        HBox categoryAndButton = new HBox(categoryField, autoSortButton);
        categoryAndButton.setSpacing(5);
        categoryAndButton.setAlignment(Pos.CENTER);
        VBox categoryBox = new VBox(categoryLabel, categoryAndButton);
        categoryBox.setAlignment(Pos.CENTER);

        Label methodLabel = new Label("Payment Method");
        methodLabel.setStyle(themeService.getTextColorStyle());
        TextField methodField = new TextField();
        methodField.setPromptText("e.g., Cash, PayPal, Bank Transfer");
        methodField.setMaxWidth(200);
        methodField.setPrefWidth(150);
        methodField.setFocusTraversable(false);
        methodField.getStyleClass().add(themeService.isDayMode() ? "day-theme-text-field" : "night-theme-text-field");
        methodField.setVisible(false);
        methodField.setVisible(true);
        VBox methodBox = new VBox(methodLabel, methodField);
        methodBox.setAlignment(Pos.CENTER);

        autoSortButton.setOnAction(event -> {
            String description = descriptionField.getText();
            if (description.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a description first");
                alert.showAndWait();
                return;
            }

            try {
                String category = AISortingService.sort(description);
                categoryField.setText(category);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to auto-sort category: " + e.getMessage());
                alert.showAndWait();
            }
        });

        Button submitManualBtn = new Button("Submit");
        submitManualBtn.setMaxWidth(150);
        submitManualBtn.setPrefWidth(100);
        submitManualBtn.setStyle(themeService.getButtonStyle() + "-fx-font-weight: bold; " + "-fx-border-radius: 15;");
        VBox.setMargin(submitManualBtn, new Insets(10, 0, 0, 0));
        submitManualBtn.setAlignment(Pos.CENTER);

        submitManualBtn.setOnAction(event -> {
            String selectedDate = datePicker.getValue() != null ? datePicker.getValue().toString() : null;

            if (selectedDate == null || selectedDate.isEmpty() ||
                    amountField.getText().isEmpty() ||
                    categoryField.getText().isEmpty() ||
                    methodField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing Information");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all blanks before submitting");
                alert.showAndWait();
                return;
            }

            try {
                String[] dateParts = selectedDate.split("-");
                if (dateParts.length == 3) {
                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    int day = Integer.parseInt(dateParts[2]);

                    if (month < 1 || month > 12) {
                        throw new ParseException("Invalid month", 0);
                    }

                    if (day < 1 || day > 31) {
                        throw new ParseException("Invalid day", 0);
                    }

                    int maxDays;
                    switch (month) {
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            maxDays = 30;
                            break;
                        case 2:
                            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                            maxDays = isLeapYear ? 29 : 28;
                            break;
                        default:
                            maxDays = 31;
                    }

                    if (day > maxDays) {
                        throw new ParseException("Day exceeds maximum for month", 0);
                    }
                }
            } catch (ParseException | NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Date");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid date in format yyyy-MM-dd\n" +
                        "Month must be 1-12 and day must be 1-31");
                alert.showAndWait();
                return;
            }

            if (!categoryField.getText().matches("^[a-zA-Z\\s]+$")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Category");
                alert.setHeaderText(null);
                alert.setContentText("Category must contain only English letters");
                alert.showAndWait();
                return;
            }

            if (!methodField.getText().matches("^[a-zA-Z\\s]+$")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Payment Method");
                alert.setHeaderText(null);
                alert.setContentText("Payment method must contain only English letters");
                alert.showAndWait();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Amount");
                alert.setHeaderText(null);
                alert.setContentText("Please type in a valid number");
                alert.showAndWait();
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setTransactionDate(selectedDate);
            transaction.setTransactionType(typeCombo.getValue());
            transaction.setCurrency(currencyCombo.getValue());
            transaction.setAmount(amount);
            transaction.setCategory(categoryField.getText());
            transaction.setPaymentMethod(methodField.getText());
            transaction.setDescription(descriptionField.getText());

            TransactionService service = new TransactionService();
            service.addTransaction(loggedUser, transaction);

            datePicker.setValue(null);
            amountField.clear();
            categoryField.clear();
            methodField.clear();
            descriptionField.clear();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Transaction Added");
            alert.setHeaderText(null);
            alert.setContentText("Transaction has been successfully added.");
            alert.showAndWait();
        });

        centerBox.getChildren().addAll(
                topicLabel,
                dateBox,
                typeBox,
                currencyBox,
                amountBox,
                descriptionBox,
                categoryBox,
                methodBox,
                submitManualBtn);
        centerBox.setSpacing(10);
        centerBox.setAlignment(Pos.CENTER);

        // 右侧传输csv文件部分
        VBox rightBar = new VBox();
        rightBar.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        themeService.getCurrentFormBackgroundStyle() +
                        "-fx-padding: 20;");
        rightBar.setPadding(new Insets(20, 20, 20, 20));
        rightBar.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(rightBar, Priority.ALWAYS);

        Label promptLabel = new Label("File Import:");
        promptLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;" + themeService.getTextColorStyle());
        VBox.setMargin(promptLabel, new Insets(10, 0, 0, 0));

        Button importCSVButton = new Button("Select a file");
        importCSVButton.setPrefWidth(100);
        importCSVButton.setStyle(themeService.getButtonStyle() + "-fx-font-weight: bold; " + "-fx-border-radius: 15;");

        importCSVButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                TransactionService service = new TransactionService();
                service.importTransactionsFromCSV(loggedUser, file);
            }
        });

        Label formatLabel = new Label("Your .CSV file should\ncontain the following columns:\n\n" +
                "Transaction Date\n" +
                "(format: YYYY-MM-DD, e.g. 2025-03-15)\n\n" +
                "Transaction Type\n" +
                "(only: Income / Expense)\n\n" +
                "Currency\n" +
                "(currency type, e.g. CNY, USD)\n\n" +
                "Amount\n" +
                "(number format, e.g. 1234.56)\n\n" +
                "Description\n" +   
                "(transaction description)\n\n" +
                "Category\n" +
                "(income and expense category)\n\n" +
                "Payment Method\n" +
                "(payment method)");
        formatLabel.setFont(new Font(11));
        formatLabel.setStyle(themeService.getTextColorStyle());
        VBox.setMargin(formatLabel, new Insets(10, 0, 20, 0));

        rightBar.getChildren().addAll(
                promptLabel,
                importCSVButton,
                formatLabel);

        rightBar.setSpacing(10);
        rightBar.setAlignment(Pos.CENTER);

        // GridPane布局设置
        GridPane centerAndRight = new GridPane();
        centerAndRight.setPadding(new Insets(20, 20, 20, 20));
        centerAndRight.setHgap(20);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        column1.setFillWidth(true);
        column1.setHgrow(Priority.ALWAYS);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        column2.setFillWidth(true);
        column2.setHgrow(Priority.ALWAYS);

        centerAndRight.getColumnConstraints().addAll(column1, column2);

        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.setVgrow(Priority.ALWAYS);
        rowConstraint.setFillHeight(true);
        centerAndRight.getRowConstraints().add(rowConstraint);

        centerBox.setAlignment(Pos.TOP_CENTER);
        rightBar.setAlignment(Pos.TOP_CENTER);
        centerAndRight.add(centerBox, 0, 0);
        centerAndRight.add(rightBar, 1, 0);

        // 确保框线可以竖直拉伸
        GridPane.setVgrow(centerBox, Priority.ALWAYS);
        GridPane.setVgrow(rightBar, Priority.ALWAYS);
        GridPane.setHgrow(centerBox, Priority.ALWAYS);
        GridPane.setHgrow(rightBar, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(centerAndRight);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        String backgroundColor = themeService.isDayMode() ? "white" : "#2A2A2A";
        scrollPane.setStyle(
                "-fx-background: " + backgroundColor + ";" +
                        "-fx-border-color: transparent;" +
                        "-fx-control-inner-background: " + backgroundColor + ";" +
                        "-fx-text-fill: transparent;");
        scrollPane.setPadding(new Insets(0));

        VBox contentWrapper = new VBox(centerAndRight);
        contentWrapper.setFillWidth(true);
        VBox.setVgrow(centerAndRight, Priority.ALWAYS);
        contentWrapper.setMinHeight(400);

        scrollPane.setContent(contentWrapper);

        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setMaxWidth(Double.MAX_VALUE);
        BorderPane.setMargin(scrollPane, new Insets(0));

        scrollPane.getStyleClass().add("scroll-pane");
        
        root.setCenter(scrollPane);
        Scene scene = new Scene(root, width, height);

        String componentStylesheet = """
                /* DatePicker styles */
                .day-theme-date-picker {
                    -fx-background-color: white;
                    -fx-text-fill: black;
                    -fx-border-color: #999999;
                    -fx-border-width: 1;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                    -fx-prompt-text-fill: gray;
                }
                .night-theme-date-picker {
                    -fx-background-color: #3C3C3C;
                    -fx-text-fill: white;
                    -fx-border-color: #666666;
                    -fx-border-width: 1;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                    -fx-prompt-text-fill: lightgray;
                }
                .day-theme-date-picker .text-field {
                    -fx-background-color: white;
                    -fx-text-fill: black;
                    -fx-prompt-text-fill: gray;
                }
                .night-theme-date-picker .text-field {
                    -fx-background-color: #3C3C3C;
                    -fx-text-fill: white;
                    -fx-prompt-text-fill: lightgray;
                }
                .day-theme-date-picker .arrow-button {
                    -fx-background-color: white;
                    -fx-border-color: #999999;
                    -fx-border-width: 1;
                }
                .night-theme-date-picker .arrow-button {
                    -fx-background-color: #3C3C3C;
                    -fx-border-color: #666666;
                    -fx-border-width: 1;
                }
                .day-theme-date-picker .calendar-popup {
                    -fx-background-color: white;
                }
                .night-theme-date-picker .calendar-popup {
                    -fx-background-color: #3C3C3C;
                }
                .day-theme-date-picker .month-year-pane {
                    -fx-background-color: white;
                }
                .night-theme-date-picker .month-year-pane {
                    -fx-background-color: #3C3C3C;
                }
                .day-theme-date-picker .spinner .label {
                    -fx-text-fill: black;
                }
                .night-theme-date-picker .spinner .label {
                    -fx-text-fill: white;
                }
                .day-theme-date-picker .spinner .button {
                    -fx-background-color: white;
                    -fx-border-color: #999999;
                    -fx-border-width: 1;
                }
                .night-theme-date-picker .spinner .button {
                    -fx-background-color: #3C3C3C;
                    -fx-border-color: #666666;
                    -fx-border-width: 1;
                }

                /* ComboBox styles */
                .day-theme-combo-box {
                    -fx-background-color: white;
                    -fx-text-fill: black;
                    -fx-border-color: #999999;
                    -fx-border-width: 1;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                }
                .night-theme-combo-box {
                    -fx-background-color: #3C3C3C;
                    -fx-text-fill: white;
                    -fx-border-color: #666666;
                    -fx-border-width: 1;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                }
                .day-theme-combo-box .list-cell {
                    -fx-background-color: white;
                    -fx-text-fill: black;
                }
                .night-theme-combo-box .list-cell {
                    -fx-background-color: #3C3C3C;
                    -fx-text-fill: white;
                }
                .day-theme-combo-box .list-cell:hover {
                    -fx-background-color: #E0F0FF;
                }
                .night-theme-combo-box .list-cell:hover {
                    -fx-background-color: #4A6FA5;
                }
                .day-theme-combo-box .arrow-button {
                    -fx-background-color: white;
                    -fx-border-color: #999999;
                    -fx-border-width: 1;
                }
                .night-theme-combo-box .arrow-button {
                    -fx-background-color: #3C3C3C;
                    -fx-border-color: #666666;
                    -fx-border-width: 1;
                }

                /* TextField and TextArea styles */
                .day-theme-text-field {
                    -fx-background-color: white;
                    -fx-text-fill: black;
                    -fx-border-color: #999999;
                    -fx-border-width: 1;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                    -fx-prompt-text-fill: gray;
                }
                .night-theme-text-field {
                    -fx-background-color: #3C3C3C;
                    -fx-text-fill: white;
                    -fx-border-color: #666666;
                    -fx-border-width: 1;
                    -fx-border-radius: 4;
                    -fx-background-radius: 4;
                    -fx-prompt-text-fill: lightgray;
                }
                .day-theme-text-field .text {
                    -fx-fill: black;
                }
                .night-theme-text-field .text {
                    -fx-fill: white;
                }
                """;
        scene.getStylesheets().add("data:text/css," + componentStylesheet);

        scene.getStylesheets().add("data:text/css," + themeService.getThemeStylesheet());

        String labelColor = themeService.isDayMode() ? "darkblue" : "white";
        scene.getStylesheets().add("data:,Label { -fx-text-fill: " + labelColor + "; }");

        // 添加滚动条样式
        String scrollbarStyle = """
                .scroll-pane .scroll-bar:vertical {
                    -fx-background-color: #e0e0e0;
                    -fx-padding: 2;
                }
                .scroll-pane .scroll-bar:vertical .thumb {
                    -fx-background-color: #3282fa;
                    -fx-background-radius: 5px;
                }
                .scroll-pane .scroll-bar:vertical .track {
                    -fx-background-color: #f0f0f0;
                    -fx-background-radius: 0;
                }
                .scroll-pane .corner {
                    -fx-background-color: transparent;
                }
                """;
        scene.getStylesheets().add("data:text/css," + scrollbarStyle);

        // 创建一个方法来调整所有组件的字体大小
        Runnable adjustFontSizes = () -> {
            try {
                // 计算当前窗口相对于初始窗口的缩放比例
                double scaleW = scene.getWidth() / INITIAL_WIDTH;
                double scaleH = scene.getHeight() / INITIAL_HEIGHT;
                double scale = Math.min(scaleW, scaleH);
                
                // 基础字体大小设置 - 可以调整这些基础值以适应不同窗口大小
                double titleFontSize = 25 * scale;
                double labelFontSize = 17 * scale;
                double inputFontSize = 15 * scale;
                double formatLabelFontSize = 15 * scale;
                
                // 确保字体大小有最小值和最大值限制，防止太小或太大
                titleFontSize = Math.max(14, Math.min(titleFontSize, 28));
                labelFontSize = Math.max(10, Math.min(labelFontSize, 22));
                inputFontSize = Math.max(9, Math.min(inputFontSize, 22));
                formatLabelFontSize = Math.max(8, Math.min(formatLabelFontSize, 20));
                
                // 更新标题字体大小
                topicLabel.setStyle("-fx-font-size: " + titleFontSize + "px; -fx-font-weight: bold;" + themeService.getTextColorStyle());
                promptLabel.setStyle("-fx-font-size: " + titleFontSize + "px; -fx-font-weight: bold;" + themeService.getTextColorStyle());
                
                // 更新标签字体大小
                dateLabel.setFont(Font.font(dateLabel.getFont().getFamily(), labelFontSize));
                typeLabel.setFont(Font.font(typeLabel.getFont().getFamily(), labelFontSize));
                currencyLabel.setFont(Font.font(currencyLabel.getFont().getFamily(), labelFontSize));
                amountLabel.setFont(Font.font(amountLabel.getFont().getFamily(), labelFontSize));
                descriptionLabel.setFont(Font.font(descriptionLabel.getFont().getFamily(), labelFontSize));
                categoryLabel.setFont(Font.font(categoryLabel.getFont().getFamily(), labelFontSize));
                methodLabel.setFont(Font.font(methodLabel.getFont().getFamily(), labelFontSize));
                formatLabel.setFont(Font.font(formatLabel.getFont().getFamily(), formatLabelFontSize));
                
                // 更新按钮字体大小
                submitManualBtn.setStyle(submitManualBtn.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                importCSVButton.setStyle(importCSVButton.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                autoSortButton.setStyle(autoSortButton.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                
                // 调整按钮大小与字体成比例
                double buttonHeight = 25 * scale;
                buttonHeight = Math.max(20, Math.min(buttonHeight, 30)); // 限制按钮高度范围
                submitManualBtn.setPrefHeight(buttonHeight);
                importCSVButton.setPrefHeight(buttonHeight);
                autoSortButton.setPrefHeight(buttonHeight);
                
                // 更新文本输入控件字体大小
                amountField.setStyle(amountField.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                categoryField.setStyle(categoryField.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                methodField.setStyle(methodField.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                descriptionField.setStyle(descriptionField.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                
                // 调整文本输入框高度
                double fieldHeight = 28 * scale;
                fieldHeight = Math.max(22, Math.min(fieldHeight, 35)); // 限制输入框高度范围
                amountField.setPrefHeight(fieldHeight);
                categoryField.setPrefHeight(fieldHeight);
                methodField.setPrefHeight(fieldHeight);
                
                // 描述框高度单独设置，可以更大一些
                double descFieldHeight = 70 * scale;
                descFieldHeight = Math.max(45, Math.min(descFieldHeight, 90)); // 限制描述框高度范围
                descriptionField.setPrefHeight(descFieldHeight);
                
                // 更新下拉菜单字体大小
                typeCombo.setStyle(typeCombo.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                currencyCombo.setStyle(currencyCombo.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                
                // 调整下拉菜单高度
                typeCombo.setPrefHeight(fieldHeight);
                currencyCombo.setPrefHeight(fieldHeight);
                
                // 更新日期选择器字体大小
                datePicker.setStyle(datePicker.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                datePicker.setPrefHeight(fieldHeight);
                
                // 调整间距也随窗口大小变化
                double spacing = 10 * scale;
                spacing = Math.max(5, Math.min(spacing, 15)); // 限制间距范围
                centerBox.setSpacing(spacing);
                rightBar.setSpacing(spacing);
                
                // 调整内边距，确保内容在缩小时不会贴边
                double padding = 20 * scale;
                padding = Math.max(10, Math.min(padding, 25)); // 限制内边距范围
                centerBox.setPadding(new Insets(padding));
                rightBar.setPadding(new Insets(padding));
                
                // 重新计算最佳宽度和高度
                centerBox.autosize();
                rightBar.autosize();
                centerAndRight.autosize();
                
                // 请求滚动面板重新布局
                scrollPane.requestLayout();
            } catch (Exception e) {
                System.err.println("Font adjustment error: " + e.getMessage());
                e.printStackTrace();
            }
        };
        
        // 设置一个固定的边距常量，确保边框和窗口边缘始终保持这个距离
        final int BORDER_MARGIN = 20; // 可以根据需要调整这个值

        // 在监听器中使用这个固定边距
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            centerAndRight.setPrefWidth(newVal.doubleValue() - sideBar.getWidth() - BORDER_MARGIN);
            scrollPane.layout();
            Platform.runLater(adjustFontSizes);
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            centerAndRight.setPrefHeight(newVal.doubleValue() - BORDER_MARGIN);
            scrollPane.layout();
            Platform.runLater(adjustFontSizes);
        });

        // 确保第一次加载时就正确设置布局
        // 这段代码解决初始加载时边框位置不正确的问题
        stage.setOnShown(event -> {
            Platform.runLater(() -> {
                // 使用相同的边距确保一致性
                centerAndRight.setPrefWidth(scene.getWidth() - sideBar.getWidth() - BORDER_MARGIN);
                centerAndRight.setPrefHeight(scene.getHeight() - BORDER_MARGIN);
                centerAndRight.layout();
                scrollPane.layout();
                adjustFontSizes.run();
            });
        });
        // 确保第一次加载时也调整布局
        Platform.runLater(() -> {
            // 立即设置正确的宽度和高度，不等待窗口调整事件
            centerAndRight.setPrefWidth(scene.getWidth() - sideBar.getWidth() - 10);
            centerAndRight.setPrefHeight(scene.getHeight() - 10);
            
            // 请求立即重新布局
            scrollPane.layout();
            centerAndRight.layout();
            
            // 调整字体大小
            adjustFontSizes.run();
        });

        return scene;
    }
}
