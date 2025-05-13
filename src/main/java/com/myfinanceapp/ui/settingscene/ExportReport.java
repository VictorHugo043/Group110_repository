package com.myfinanceapp.ui.settingscene;

import com.myfinanceapp.model.User;
import com.myfinanceapp.service.ExportReportService;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.service.TransactionService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.common.SettingsTopBarFactory;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.CurrencyService;
import com.myfinanceapp.service.LanguageService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * "Export Report" interface: Allows users to select a date range and export a financial report as a PDF.
 */
public class ExportReport {
    private static final LanguageService languageService = LanguageService.getInstance();
    private static User currentUser;
    private static ExportReportService reportService;

    // 重载方法，兼容旧的调用方式
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService(), new CurrencyService("CNY"));
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService) {
        return createScene(stage, width, height, loggedUser, themeService, new CurrencyService("CNY"));
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser, ThemeService themeService, CurrencyService currencyService) {
        // Store current logged-in user
        currentUser = loggedUser;
        if (currentUser == null) {
            throw new IllegalStateException("No logged user!");
        }

        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // Left sidebar: "Settings" selected
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Settings", loggedUser, themeService, currencyService);
        root.setLeft(sideBar);

        // Center content
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);

        // Main container
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);

        // Top tab bar: "Export Report" selected
        HBox topBar = SettingsTopBarFactory.createTopBar(stage, "Export Report", loggedUser, themeService, currencyService);

        // Bottom rounded container
        VBox outerBox = new VBox(20);
        outerBox.setPadding(new Insets(25));
        outerBox.setAlignment(Pos.TOP_LEFT);
        outerBox.setMaxWidth(600);
        outerBox.setMaxHeight(400);
        outerBox.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 0 0 12 12;" +
                        "-fx-background-radius: 0 0 12 12;" +
                        themeService.getCurrentFormBackgroundStyle()
        );

        // Date range selection
        ImageView calendarIcon = new ImageView();
        calendarIcon.setFitWidth(24);
        calendarIcon.setFitHeight(24);
        try {
            Image icon = new Image(Objects.requireNonNull(ExportReport.class.getResource("/pictures/calendar_icon.png")).toExternalForm());
            calendarIcon.setImage(icon);
        } catch (Exception e) {
            // Fallback: Leave icon empty if resource is not found
        }

        Label dateRangeLabel = new Label(languageService.getTranslation("select_date_range"));
        dateRangeLabel.setStyle(
                "-fx-text-fill: #3282FA;" +
                        "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;"
        );

        HBox dateRangeHeader = new HBox(10, calendarIcon, dateRangeLabel);
        dateRangeHeader.setAlignment(Pos.CENTER_LEFT);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText(languageService.getTranslation("start_date"));
        // Apply theme style class
        startDatePicker.getStyleClass().add(themeService.isDayMode() ? "day-theme-date-picker" : "night-theme-date-picker");

        Label toLabel = new Label(languageService.getTranslation("to"));
        // Apply theme text color
        toLabel.setStyle(themeService.getTextColorStyle());

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText(languageService.getTranslation("end_date"));
        // Apply theme style class
        endDatePicker.getStyleClass().add(themeService.isDayMode() ? "day-theme-date-picker" : "night-theme-date-picker");

        HBox datePickerRow = new HBox(10, startDatePicker, toLabel, endDatePicker);
        datePickerRow.setAlignment(Pos.CENTER_LEFT);

        // Export button
        Button exportButton = new Button(languageService.getTranslation("export_report"));
        exportButton.setStyle(themeService.getButtonStyle());

        // Back to Status button
        Button backBtn = new Button(languageService.getTranslation("back_to_status"));
        backBtn.setStyle(themeService.getButtonStyle());
        backBtn.setOnAction(e -> {
            StatusScene statusScene = new StatusScene(stage, width, height, loggedUser);
            stage.setScene(statusScene.createScene(themeService, currencyService));
            StatusService statusService = new StatusService(statusScene, loggedUser, currencyService, languageService);
            stage.setTitle("Finanger - " + languageService.getTranslation("status"));
        });

        // Export functionality
        TransactionService txService = new TransactionService();
        reportService = new ExportReportService(txService, currentUser, currencyService);
        exportButton.setOnAction(e -> {
            exportButton.setDisable(true); // Disable button during export
            reportService.handleExport(stage, startDatePicker.getValue(), endDatePicker.getValue())
                    .thenRun(() -> Platform.runLater(() -> {
                        showAlert(languageService.getTranslation("success"), languageService.getTranslation("export_success"));
                        exportButton.setDisable(false);
                    }))
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            String errorMsg = languageService.getTranslation("export_failed") + ": " + throwable.getMessage();
                            System.err.println(errorMsg);
                            throwable.printStackTrace();
                            showAlert(languageService.getTranslation("error"), errorMsg);
                            exportButton.setDisable(false);
                        });
                        return null;
                    });
        });

        // Handle window close to ensure executor shutdown
        stage.setOnCloseRequest(event -> {
            if (reportService != null) {
                reportService.shutdown();
            }
        });

        // Assemble outerBox content
        outerBox.getChildren().addAll(
                dateRangeHeader,
                datePickerRow,
                exportButton,
                backBtn
        );

        container.getChildren().addAll(topBar, outerBox);
        centerBox.getChildren().add(container);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, width, height);
        // Add dynamic theme stylesheet for DatePicker
        scene.getStylesheets().add("data:text/css," + themeService.getThemeStylesheet());

        return scene;
    }

    /**
     * Simple alert dialog.
     */
    private static void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}