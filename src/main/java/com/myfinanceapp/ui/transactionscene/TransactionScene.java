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
import javafx.beans.value.ChangeListener;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

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



        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "New", loggedUser, themeService, currencyService);
        root.setLeft(sideBar);

        // Center manual input section
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

        // Date picker section
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
        descriptionField.getStyleClass()
                .add(themeService.isDayMode() ? "day-theme-text-field" : "night-theme-text-field");
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

        // Right side CSV file import section
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

        // Improve button styling only
        String buttonBaseStyle = themeService.getButtonStyle() +
                "-fx-font-weight: bold; " +
                "-fx-border-radius: 15; " +
                "-fx-min-height: 35;";

        Button importCSVButton = new Button("Select a file");
        importCSVButton.setPrefWidth(160);
        importCSVButton.setStyle(buttonBaseStyle);

        importCSVButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                TransactionService service = new TransactionService();
                service.importTransactionsFromCSV(loggedUser, file);
            }
        });

        // Add Reference Template button
        Button downloadTemplateButton = new Button("Reference Template");
        downloadTemplateButton.setPrefWidth(160);
        downloadTemplateButton.setStyle(buttonBaseStyle);

        downloadTemplateButton.setOnAction(event -> {
            // Create a FileChooser object to open a file selection dialog. Users can choose
            // the location and filename through this dialog.
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Template Excel");
            fileChooser.setInitialFileName("template.xlsx");
            // Set file chooser extension filter to allow users to select .xlsx format files.
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            // Display the file save dialog and return the target file (destFile) selected by the user, which contains the file path and name.
            File destFile = fileChooser.showSaveDialog(stage);
            if (destFile != null) {
                try {
                    // Read template.xlsx from resources
                    java.io.InputStream in = TransactionScene.class.getResourceAsStream("/template.xlsx");
                    if (in == null) {
                        throw new Exception("Template file not found in resources.");
                    }
                    // If the file stream 'in' is not null, copy the file content to the user-specified target file destFile
                    java.nio.file.Files.copy(in, destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    in.close();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Download Complete");
                    alert.setHeaderText(null);
                    alert.setContentText("Excel template downloaded successfully!");
                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Download Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to download template: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        HBox fileButtonBox = new HBox(importCSVButton, downloadTemplateButton);
        fileButtonBox.setSpacing(10);
        fileButtonBox.setAlignment(Pos.CENTER);

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
                fileButtonBox,
                formatLabel);

        rightBar.setSpacing(10);
        rightBar.setAlignment(Pos.CENTER);

        // GridPane layout settings
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

        // Ensure borders can stretch vertically
        GridPane.setVgrow(centerBox, Priority.ALWAYS);
        GridPane.setVgrow(rightBar, Priority.ALWAYS);
        GridPane.setHgrow(centerBox, Priority.ALWAYS);
        GridPane.setHgrow(rightBar, Priority.ALWAYS);

        VBox contentWrapper = new VBox(centerAndRight);
        contentWrapper.setFillWidth(true);
        // VBox.setVgrow(centerAndRight, Priority.ALWAYS);

        // Add ScrollPane for adaptive scrolling
        ScrollPane scrollPane = new ScrollPane(contentWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        scrollPane.getStyleClass().add("custom-scroll-pane");

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

        // Create a method to adjust all component font sizes
        Runnable adjustFontSizes = () -> {
            try {
                double currentWidth = scene.getWidth();
                double currentHeight = scene.getHeight();
                
                double scaleW = currentWidth / 800; 
                double scaleH = currentHeight / 600; 
                double scale = Math.min(scaleW, scaleH);

                // Base font size settings - can adjust these base values to suit different
                // window sizes
                double titleFontSize = 25 * scale;
                double labelFontSize = 17 * scale;
                double inputFontSize = 13 * scale;
                double formatLabelFontSize = 15 * scale;

                // Ensure font sizes have minimum and maximum limits to prevent too small or too
                // large
                titleFontSize = Math.max(14, Math.min(titleFontSize, 28));
                labelFontSize = Math.max(10, Math.min(labelFontSize, 22));
                inputFontSize = Math.max(6, Math.min(inputFontSize, 18));
                formatLabelFontSize = Math.max(5, Math.min(formatLabelFontSize, 20));

                // Update title font sizes
                topicLabel.setStyle("-fx-font-size: " + titleFontSize + "px; -fx-font-weight: bold;"
                        + themeService.getTextColorStyle());
                promptLabel.setStyle("-fx-font-size: " + titleFontSize + "px; -fx-font-weight: bold;"
                        + themeService.getTextColorStyle());

                // Update label font sizes
                dateLabel.setFont(Font.font(dateLabel.getFont().getFamily(), labelFontSize));
                typeLabel.setFont(Font.font(typeLabel.getFont().getFamily(), labelFontSize));
                currencyLabel.setFont(Font.font(currencyLabel.getFont().getFamily(), labelFontSize));
                amountLabel.setFont(Font.font(amountLabel.getFont().getFamily(), labelFontSize));
                descriptionLabel.setFont(Font.font(descriptionLabel.getFont().getFamily(), labelFontSize));
                categoryLabel.setFont(Font.font(categoryLabel.getFont().getFamily(), labelFontSize));
                methodLabel.setFont(Font.font(methodLabel.getFont().getFamily(), labelFontSize));
                formatLabel.setFont(Font.font(formatLabel.getFont().getFamily(), formatLabelFontSize));

                // Update button font sizes
                submitManualBtn.setStyle(submitManualBtn.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                importCSVButton.setStyle(importCSVButton.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                autoSortButton.setStyle(autoSortButton.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                downloadTemplateButton.setStyle(downloadTemplateButton.getStyle() + "-fx-font-size: " + inputFontSize + "px;");

                // Adjust button size proportionate to font
                double buttonHeight = 25 * scale;
                buttonHeight = Math.max(20, Math.min(buttonHeight, 30)); // Limit button height range
                submitManualBtn.setPrefHeight(buttonHeight);
                importCSVButton.setPrefHeight(buttonHeight);
                autoSortButton.setPrefHeight(buttonHeight);
                downloadTemplateButton.setPrefHeight(buttonHeight);
                // Ensure button min width for text visibility
                double buttonMinWidth = 120 * scale;
                buttonMinWidth = Math.max(80, Math.min(buttonMinWidth, 180));
                downloadTemplateButton.setMinWidth(buttonMinWidth);
                importCSVButton.setMinWidth(buttonMinWidth);

                // Update text input control font sizes
                amountField.setStyle(amountField.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                categoryField.setStyle(categoryField.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                methodField.setStyle(methodField.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                descriptionField.setStyle(descriptionField.getStyle() + "-fx-font-size: " + inputFontSize + "px;");

                // Ensure prompt text is always visible by setting min width
                amountField.setMinWidth(120 * scale);
                categoryField.setMinWidth(120 * scale);
                methodField.setMinWidth(120 * scale);
                descriptionField.setMinWidth(120 * scale);

                // Adjust text input field heights
                double fieldHeight = 28 * scale;
                fieldHeight = Math.max(22, Math.min(fieldHeight, 35)); // Limit input field height range
                amountField.setPrefHeight(fieldHeight);
                categoryField.setPrefHeight(fieldHeight);
                methodField.setPrefHeight(fieldHeight);

                // Description field height set separately, can be larger
                double descFieldHeight = 70 * scale;
                descFieldHeight = Math.max(45, Math.min(descFieldHeight, 90)); // Limit description field height range
                descriptionField.setPrefHeight(descFieldHeight);

                // Update dropdown menu font sizes
                typeCombo.setStyle(typeCombo.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                currencyCombo.setStyle(currencyCombo.getStyle() + "-fx-font-size: " + inputFontSize + "px;");

                // Adjust dropdown menu heights
                typeCombo.setPrefHeight(fieldHeight);
                currencyCombo.setPrefHeight(fieldHeight);

                // Update date picker font size
                datePicker.setStyle(datePicker.getStyle() + "-fx-font-size: " + inputFontSize + "px;");
                datePicker.setPrefHeight(fieldHeight);

                // Adjust spacing also with window size
                double spacing = 10 * scale;
                spacing = Math.max(5, Math.min(spacing, 15)); // Limit spacing range
                centerBox.setSpacing(spacing);
                rightBar.setSpacing(spacing);

                // Adjust padding to ensure content doesn't hug edges when shrunk
                double padding = 20 * scale;
                padding = Math.max(10, Math.min(padding, 25)); // Limit padding range
                centerBox.setPadding(new Insets(padding));
                rightBar.setPadding(new Insets(padding));

                // Recalculate optimal width and height
                centerBox.autosize();
                rightBar.autosize();
                centerAndRight.autosize();

                // For Reference Template button, allow even smaller minimum font size and min width
                double refBtnFontSize = Math.max(6, inputFontSize); // allow min 6px
                downloadTemplateButton.setStyle(downloadTemplateButton.getStyle() + "-fx-font-size: " + refBtnFontSize + "px;");

                // Set a smaller min width for the button
                double refBtnMinWidth = 60 * scale;
                refBtnMinWidth = Math.max(40, Math.min(refBtnMinWidth, 120));
                downloadTemplateButton.setMinWidth(refBtnMinWidth);
            } catch (Exception e) {
                System.err.println("Font adjustment error: " + e.getMessage());
                e.printStackTrace();
            }
        };

        // Set a fixed margin constant to ensure borders and window edges always
        // maintain this distance
        final int BORDER_MARGIN = 20; // Can adjust this value as needed

        PauseTransition layoutPause = new PauseTransition(Duration.millis(50));
        layoutPause.setOnFinished(e -> {
            adjustFontSizes.run();
            centerAndRight.layout();
        });
        layoutPause.play();

        ChangeListener<Number> stageSizeListener = (obs, oldVal, newVal) -> {
            layoutPause.play(); // Use the pause transition to ensure the layout is completed
        };
        
        // Add scene size monitoring
        scene.widthProperty().addListener(stageSizeListener);
        scene.heightProperty().addListener(stageSizeListener);
        
        // Modify the visibility listener
        scene.getRoot().visibleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                layoutPause.play();
                Platform.runLater(() -> {
                    centerAndRight.setPrefWidth(stage.getWidth() - sideBar.getWidth() - BORDER_MARGIN);
                    centerAndRight.setPrefHeight(stage.getHeight() - BORDER_MARGIN);
                    centerAndRight.layout();
                });
            }
        });

        return scene;
    }
}
