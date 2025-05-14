package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.service.ThemeService;
import com.myfinanceapp.service.LanguageService;
import com.myfinanceapp.service.TransactionManagementService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.CurrencyService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.util.List;

/**
 * A comprehensive transaction management interface for the Finanger application.
 * This scene provides users with tools to:
 * - View and filter transactions
 * - Edit transaction details
 * - Delete transactions
 * - Sort transactions by various criteria
 * The interface features theme customization, internationalization support,
 * and a responsive table layout with dynamic filtering capabilities.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class TransactionManagementScene {
    private static final LanguageService languageService = LanguageService.getInstance();
    private final Stage stage;
    private final double width;
    private final double height;
    private final User currentUser;
    private TableView<Transaction> transactionTable;
    private ThemeService themeService;
    private CurrencyService currencyService;
    private TransactionManagementService service;

    // Filter controls
    private ComboBox<String> dateFilter;
    private ComboBox<String> typeFilter;
    private ComboBox<String> currencyFilter;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> paymentMethodFilter;

    /**
     * Constructs a new TransactionManagementScene with the specified parameters.
     *
     * @param stage The stage to display the scene
     * @param width The initial width of the scene
     * @param height The initial height of the scene
     * @param currentUser The currently logged-in user
     */
    public TransactionManagementScene(Stage stage, double width, double height, User currentUser) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.currentUser = currentUser;
    }

    /**
     * Creates and returns a transaction management scene with default theme settings.
     *
     * @return A configured Scene object for the transaction management interface
     */
    public Scene createScene() {
        return createScene(new ThemeService());
    }

    /**
     * Creates and returns a transaction management scene with specified theme settings.
     *
     * @param themeService The theme service to use for styling
     * @return A configured Scene object for the transaction management interface
     */
    public Scene createScene(ThemeService themeService) {
        return createScene(themeService, new CurrencyService("CNY"));
    }

    /**
     * Creates and returns a transaction management scene with specified theme and currency settings.
     * The scene includes a comprehensive transaction table with filtering and sorting capabilities.
     *
     * @param themeService The theme service to use for styling
     * @param currencyService The currency service to use for the application
     * @return A configured Scene object for the transaction management interface
     */
    public Scene createScene(ThemeService themeService, CurrencyService currencyService) {
        this.themeService = themeService;
        this.currencyService = currencyService;
        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // Use the same sidebar
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Status", currentUser, themeService, currencyService);
        root.setLeft(sideBar);

        // Main content area
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setFillWidth(true);
        mainContent.setStyle(themeService.getCurrentThemeStyle());

        // Page title
        HBox headerBox = createHeader();

        // Filter area
        HBox filterBox = createFilterBox();

        // Transaction table
        createTransactionTable();

        // Initialize service
        this.service = new TransactionManagementService(
                currentUser, transactionTable, dateFilter, typeFilter,
                currencyFilter, categoryFilter, paymentMethodFilter);

        // Set table data source
        transactionTable.setItems(service.getFilteredTransactions());

        // Call after setting data source
        transactionTable.setItems(service.getFilteredTransactions());
        service.initializeFilters();  // Initialize filter options

        // Back button
        Button backButton = new Button(languageService.getTranslation("back_to_status"));
        backButton.setStyle(themeService.getButtonStyle());
        backButton.setOnAction(e -> {
            // Get current window size
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            // Return to Status scene
            StatusScene statusScene = new StatusScene(stage, currentWidth, currentHeight, currentUser);
            Scene scene = statusScene.createScene(themeService, currencyService);
            stage.setScene(scene);
            StatusService statusService = new StatusService(statusScene, currentUser, currencyService, languageService);
            stage.setTitle("Finanger - " + languageService.getTranslation("status"));
        });

        mainContent.getChildren().addAll(headerBox, filterBox, transactionTable, backButton);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, width, height);
        // Add dynamic theme stylesheet for ComboBox
        scene.getStylesheets().add("data:text/css," + themeService.getThemeStylesheet());

        return scene;
    }

    /**
     * Creates the header section of the transaction management scene.
     *
     * @return An HBox containing the header components
     */
    private HBox createHeader() {
        Label title = new Label(languageService.getTranslation("manage_transactions"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;" + themeService.getTextColorStyle());

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    /**
     * Creates the filter controls section allowing users to filter transactions
     * by various criteria such as date, type, currency, category, and payment method.
     *
     * @return An HBox containing the filter controls
     */
    private HBox createFilterBox() {
        // Create filter combo boxes
        dateFilter = createFilterComboBox(languageService.getTranslation("date"));
        typeFilter = createFilterComboBox(languageService.getTranslation("transaction_type"));
        currencyFilter = createFilterComboBox(languageService.getTranslation("currency"));
        categoryFilter = createFilterComboBox(languageService.getTranslation("category"));
        paymentMethodFilter = createFilterComboBox(languageService.getTranslation("payment_method"));

        // Reset button
        Button resetButton = new Button(languageService.getTranslation("reset_filter"));
        resetButton.setStyle(themeService.getButtonStyle());
        resetButton.setOnAction(e -> service.resetFilters());

        Label filterLabel = new Label(languageService.getTranslation("filter") + ":");
        filterLabel.setStyle(themeService.getTextColorStyle());

        HBox filterBox = new HBox(10, filterLabel,
                dateFilter, typeFilter, currencyFilter, categoryFilter, paymentMethodFilter, resetButton);
        filterBox.setPadding(new Insets(10));
        filterBox.setAlignment(Pos.CENTER_LEFT);

        return filterBox;
    }

    /**
     * Creates a filter combo box with the specified name and applies theme styling.
     *
     * @param name The name/label for the filter combo box
     * @return A configured ComboBox for filtering
     */
    private ComboBox<String> createFilterComboBox(String name) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(name);
        comboBox.setValue(languageService.getTranslation("all"));
        // Apply theme style class
        comboBox.getStyleClass().add(themeService.isDayMode() ? "day-theme-combo-box" : "night-theme-combo-box");

        // 添加筛选事件
        comboBox.setOnAction(e -> {
            if (service != null) {
                service.applyFilters();
            }
        });

        return comboBox;
    }

    /**
     * Creates and configures the main transaction table with columns for all transaction properties
     * and functionality for editing and deleting transactions.
     */
    private void createTransactionTable() {
        transactionTable = new TableView<>();
        transactionTable.setEditable(true);

        // Apply table styles
        transactionTable.getStylesheets().add("data:text/css," + themeService.getTableStyle());
        transactionTable.getStylesheets().add("data:text/css," + themeService.getTableHeaderStyle());

        // Enable table sorting
        transactionTable.setSortPolicy(tableView -> {
            if (tableView.getComparator() != null) {
                // Sort the original data source directly to avoid calling applyFilters
                FXCollections.sort(service.getAllTransactions(), tableView.getComparator());
                return true;
            }
            return true;
        });

        // Create table columns
        TableColumn<Transaction, String> dateCol = new TableColumn<>(languageService.getTranslation("date"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
        dateCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setTransactionDate(e.getNewValue());
            service.updateTransaction(tx);
        });
        dateCol.setComparator((date1, date2) -> {
            try {
                return date1.compareTo(date2);
            } catch (Exception e) {
                return 0;
            }
        });
        dateCol.setSortable(true);

        TableColumn<Transaction, String> typeCol = new TableColumn<>(languageService.getTranslation("transaction_type"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        typeCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setTransactionType(e.getNewValue());
            service.updateTransaction(tx);
        });
        typeCol.setSortable(true);

        TableColumn<Transaction, String> currencyCol = new TableColumn<>(languageService.getTranslation("currency"));
        currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));
        currencyCol.setCellFactory(TextFieldTableCell.forTableColumn());
        currencyCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setCurrency(e.getNewValue());
            service.updateTransaction(tx);
        });
        currencyCol.setSortable(true);

        TableColumn<Transaction, Double> amountCol = new TableColumn<>(languageService.getTranslation("amount"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        amountCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setAmount(e.getNewValue());
            service.updateTransaction(tx);
        });
        amountCol.setSortable(true);

        TableColumn<Transaction, String> categoryCol = new TableColumn<>(languageService.getTranslation("category"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
        categoryCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setCategory(e.getNewValue());
            service.updateTransaction(tx);
        });
        categoryCol.setSortable(true);

        TableColumn<Transaction, String> paymentCol = new TableColumn<>(languageService.getTranslation("payment_method"));
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        paymentCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setPaymentMethod(e.getNewValue());
            service.updateTransaction(tx);
        });
        paymentCol.setSortable(true);

        TableColumn<Transaction, String> descriptionCol = new TableColumn<>(languageService.getTranslation("description"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setDescription(e.getNewValue());
            service.updateTransaction(tx);
        });
        descriptionCol.setSortable(false);

        // Add table sort listener
        transactionTable.getSortOrder().addListener((ListChangeListener<TableColumn<Transaction, ?>>) c -> {
            if (service != null && !transactionTable.getSortOrder().isEmpty()) {
                TableColumn<Transaction, ?> column = transactionTable.getSortOrder().get(0);
                service.applySorting(transactionTable.getComparator());
            }
        });

        // Add delete action column
        TableColumn<Transaction, Void> actionCol = new TableColumn<>(languageService.getTranslation("action"));
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button(languageService.getTranslation("delete"));
            {
                deleteButton.setStyle(themeService.getButtonStyle() + "-fx-font-size: 12px;");
                deleteButton.setOnAction(event -> {
                    Transaction tx = getTableView().getItems().get(getIndex());
                    service.deleteTransaction(tx);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        actionCol.setSortable(false); // Action column doesn't need sorting

        // Set column widths and table properties
        transactionTable.getColumns().addAll(dateCol, typeCol, currencyCol, amountCol, categoryCol, paymentCol,
                descriptionCol, actionCol);
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionTable.setPrefHeight(500);

        // Enable click-to-edit
        transactionTable.setEditable(true);
    }
}