package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.TransactionService;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.LanguageService;
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

/**
 * A comprehensive transaction interface for the Finanger application.
 * This scene provides users with tools to:
 * - Manually input transaction details
 * - Import transactions from CSV files
 * - Auto-categorize transactions using AI
 * - Download transaction templates
 * The interface features theme customization, internationalization support,
 * and a responsive layout design with dynamic font sizing.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class TransactionScene {
    /** Language service instance for internationalization */
    private static final LanguageService languageService = LanguageService.getInstance();

    /**
     * Creates and returns a transaction scene with default theme settings.
     *
     * @param stage      The stage to display the scene
     * @param width      The initial width of the scene
     * @param height     The initial height of the scene
     * @param loggedUser The currently logged-in user
     * @return A configured Scene object for the transaction interface
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService());
    }

    /**
     * Creates and returns a transaction scene with specified theme settings.
     *
     * @param stage        The stage to display the scene
     * @param width        The initial width of the scene
     * @param height       The initial height of the scene
     * @param loggedUser   The currently logged-in user
     * @param themeService The theme service to use for styling
     * @return A configured Scene object for the transaction interface
     */
    public static Scene createScene(Stage stage, double width, double height, User loggedUser,
            ThemeService themeService) {
        return createScene(stage, width, height, loggedUser, themeService, new CurrencyService("CNY"));
    }

    /**
     * Creates and returns a transaction scene with specified theme and currency
     * settings.
     * The scene includes manual transaction input and CSV file import capabilities.
     *
     * @param stage           The stage to display the scene
     * @param width           The initial width of the scene
     * @param height          The initial height of the scene
     * @param loggedUser      The currently logged-in user
     * @param themeService    The theme service to use for styling
     * @param currencyService The currency service to use for the application
     * @return A configured Scene object for the transaction interface
     */
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

        Label topicLabel = new Label(languageService.getTranslation("manual_import"));
        topicLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;" + themeService.getTextColorStyle());

        // Date picker section
        Label dateLabel = new Label(languageService.getTranslation("transaction_date"));
        dateLabel.setStyle(themeService.getTextColorStyle());

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText(languageService.getTranslation("select_date"));
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

        Label typeLabel = new Label(languageService.getTranslation("transaction_type"));
        typeLabel.setStyle(themeService.getTextColorStyle());
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(
                languageService.getTranslation("income"),
                languageService.getTranslation("expense"));
        typeCombo.setMaxWidth(200);
        typeCombo.setPrefWidth(150);
        typeCombo.setValue(languageService.getTranslation("expense"));
        typeCombo.setFocusTraversable(false);
        typeCombo.getStyleClass().add(themeService.isDayMode() ? "day-theme-combo-box" : "night-theme-combo-box");
        typeCombo.setVisible(false);
        typeCombo.setVisible(true);
        VBox typeBox = new VBox(typeLabel, typeCombo);
        typeBox.setAlignment(Pos.CENTER);

        Label currencyLabel = new Label(languageService.getTranslation("currency"));
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

        Label amountLabel = new Label(languageService.getTranslation("amount"));
        amountLabel.setStyle(themeService.getTextColorStyle());
        TextField amountField = new TextField();
        amountField.setPromptText(languageService.getTranslation("enter_amount"));
        amountField.setMaxWidth(200);
        amountField.setPrefWidth(150);
        amountField.setFocusTraversable(false);
        amountField.getStyleClass().add(themeService.isDayMode() ? "day-theme-text-field" : "night-theme-text-field");
        amountField.setVisible(false);
        amountField.setVisible(true);
        VBox amountBox = new VBox(amountLabel, amountField);
        amountBox.setAlignment(Pos.CENTER);

        Label descriptionLabel = new Label(languageService.getTranslation("description"));
        descriptionLabel.setStyle(themeService.getTextColorStyle());
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText(languageService.getTranslation("enter_description"));
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

        Button autoSortButton = new Button(languageService.getTranslation("auto_sorting"));
        autoSortButton.setStyle(themeService.getButtonStyle() + "-fx-font-weight: bold; " + "-fx-border-radius: 15;");
        autoSortButton.setMaxWidth(100);
        autoSortButton.setPrefWidth(100);

        Label categoryLabel = new Label(languageService.getTranslation("category"));
        categoryLabel.setStyle(themeService.getTextColorStyle());
        TextField categoryField = new TextField();
        categoryField.setPromptText(languageService.getTranslation("category_example"));
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

        Label methodLabel = new Label(languageService.getTranslation("payment_method"));
        methodLabel.setStyle(themeService.getTextColorStyle());
        TextField methodField = new TextField();
        methodField.setPromptText(languageService.getTranslation("payment_method_example"));
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
                alert.setTitle(languageService.getTranslation("warning"));
                alert.setHeaderText(null);
                alert.setContentText(languageService.getTranslation("enter_description_first"));
                alert.showAndWait();
                return;
            }

            try {
                String category = AISortingService.sort(description);
                categoryField.setText(category);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(languageService.getTranslation("error"));
                alert.setHeaderText(null);
                alert.setContentText(languageService.getTranslation("auto_sort_failed") + ": " + e.getMessage());
                alert.showAndWait();
            }
        });

        Button submitManualBtn = new Button(languageService.getTranslation("submit"));
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
                alert.setTitle(languageService.getTranslation("missing_info"));
                alert.setHeaderText(null);
                alert.setContentText(languageService.getTranslation("fill_all_blanks"));
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
                alert.setTitle(languageService.getTranslation("invalid_date"));
                alert.setHeaderText(null);
                alert.setContentText(languageService.getTranslation("invalid_date_format"));
                alert.showAndWait();
                return;
            }

            if (!categoryField.getText().matches("^[a-zA-Z\\s]+$")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(languageService.getTranslation("invalid_category"));
                alert.setHeaderText(null);
                alert.setContentText(languageService.getTranslation("category_english_only"));
                alert.showAndWait();
                return;
            }

            if (!methodField.getText().matches("^[a-zA-Z\\s]+$")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(languageService.getTranslation("invalid_payment_method"));
                alert.setHeaderText(null);
                alert.setContentText(languageService.getTranslation("payment_method_english_only"));
                alert.showAndWait();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(languageService.getTranslation("invalid_amount"));
                alert.setHeaderText(null);
                alert.setContentText(languageService.getTranslation("enter_valid_number"));
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
            alert.setTitle(languageService.getTranslation("transaction_added"));
            alert.setHeaderText(null);
            alert.setContentText(languageService.getTranslation("transaction_success"));
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

        Label promptLabel = new Label(languageService.getTranslation("file_import"));
        promptLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;" + themeService.getTextColorStyle());
        VBox.setMargin(promptLabel, new Insets(10, 0, 0, 0));

        // Improve button styling only
        String buttonBaseStyle = themeService.getButtonStyle() +
                "-fx-font-weight: bold; " +
                "-fx-border-radius: 15; " +
                "-fx-min-height: 35;";

        Button importCSVButton = new Button(languageService.getTranslation("select_file"));
        importCSVButton.setPrefWidth(160);
        importCSVButton.setStyle(buttonBaseStyle);

        importCSVButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(languageService.getTranslation("all_supported_filter"), "*.csv",
                            "*.xlsx"),
                    new FileChooser.ExtensionFilter(languageService.getTranslation("csv_utf8_filter"), "*.csv"),
                    new FileChooser.ExtensionFilter(languageService.getTranslation("csv_filter"), "*.csv"),
                    new FileChooser.ExtensionFilter(languageService.getTranslation("excel_filter"), "*.xlsx"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                TransactionService service = new TransactionService();
                service.importTransactions(loggedUser, file);
            }
        });

        // Add Reference Template button
        Button downloadTemplateButton = new Button(languageService.getTranslation("reference_template"));
        downloadTemplateButton.setPrefWidth(230);
        downloadTemplateButton.setMinWidth(230);
        downloadTemplateButton.setStyle(buttonBaseStyle + "-fx-font-size: 8px;");

        downloadTemplateButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(languageService.getTranslation("save_template_csv"));
            fileChooser.setInitialFileName("template.xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File destFile = fileChooser.showSaveDialog(stage);
            if (destFile != null) {
                try {
                    java.io.InputStream in = TransactionScene.class.getResourceAsStream("/template/template.xlsx");
                    if (in == null) {
                        throw new Exception(languageService.getTranslation("template_not_found"));
                    }
                    java.nio.file.Files.copy(in, destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    in.close();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(languageService.getTranslation("download_complete"));
                    alert.setHeaderText(null);
                    alert.setContentText(languageService.getTranslation("template_download_success"));
                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(languageService.getTranslation("download_failed"));
                    alert.setHeaderText(null);
                    alert.setContentText(
                            languageService.getTranslation("template_download_failed") + ": " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        HBox fileButtonBox = new HBox(importCSVButton, downloadTemplateButton);
        fileButtonBox.setSpacing(10);
        fileButtonBox.setAlignment(Pos.CENTER);

        Label formatLabel = new Label(languageService.getTranslation("csv_format_guide"));
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
        scrollPane.getStyleClass().add("custom-scroll-pane");

        // Apply theme-appropriate background color to scrollPane
        String scrollPaneBackground = themeService.isDayMode()
                ? "-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;"
                : "-fx-background: #3C3C3C; -fx-background-color: #3C3C3C; -fx-border-color: transparent;";
        scrollPane.setStyle(scrollPaneBackground);

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

        // Add specific styles for TextArea controls in night mode
        String textAreaStylesheet = """
                .night-theme-text-field.text-area {
                    -fx-control-inner-background: #3C3C3C;
                }
                .night-theme-text-field.text-area .content {
                    -fx-background-color: #3C3C3C;
                }
                .night-theme-text-field.text-area .text {
                    -fx-fill: white;
                }
                .night-theme-text-field.text-area .scroll-pane {
                    -fx-background-color: #3C3C3C;
                }
                .night-theme-text-field.text-area .viewport {
                    -fx-background-color: #3C3C3C;
                }
                """;

        scene.getStylesheets().add("data:text/css," + componentStylesheet);
        scene.getStylesheets().add("data:text/css," + textAreaStylesheet);

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
                downloadTemplateButton
                        .setStyle(downloadTemplateButton.getStyle() + "-fx-font-size: " + inputFontSize + "px;");

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

                // For Reference Template button, allow even smaller minimum font size and min
                // width
                double refBtnFontSize = Math.max(6, inputFontSize); // allow min 6px
                downloadTemplateButton
                        .setStyle(downloadTemplateButton.getStyle() + "-fx-font-size: " + refBtnFontSize + "px;");

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
