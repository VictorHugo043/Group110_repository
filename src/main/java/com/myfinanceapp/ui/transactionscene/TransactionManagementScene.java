package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.StatusService;
import com.myfinanceapp.service.TransactionService;
import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.ui.statusscene.StatusScene;
import com.myfinanceapp.service.ThemeService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransactionManagementScene {
    private final Stage stage;
    private final double width;
    private final double height;
    private final User currentUser;
    private TransactionService txService;
    private ObservableList<Transaction> allTransactions;
    private FilteredList<Transaction> filteredTransactions;
    private TableView<Transaction> transactionTable;
    private ThemeService themeService; // Store ThemeService instance

    // 筛选控件
    private ComboBox<String> dateFilter;
    private ComboBox<String> typeFilter;
    private ComboBox<String> currencyFilter;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> paymentMethodFilter;

    public TransactionManagementScene(Stage stage, double width, double height, User currentUser) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.currentUser = currentUser;
        this.txService = new TransactionService();
    }

    // Overloaded method for backward compatibility
    public Scene createScene() {
        return createScene(new ThemeService());
    }

    public Scene createScene(ThemeService themeService) {
        this.themeService = themeService; // Store the ThemeService instance
        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        // 使用相同的侧边栏
        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "Transactions", currentUser, themeService);
        root.setLeft(sideBar);

        // 主内容区
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setFillWidth(true);
        mainContent.setStyle(themeService.getCurrentThemeStyle());

        // 页面标题
        HBox headerBox = createHeader();

        // 筛选区域
        HBox filterBox = createFilterBox();

        // 交易表格
        createTransactionTable();

        // 返回按钮
        Button backButton = new Button("Back to Status");
        backButton.setStyle(themeService.getButtonStyle());
        backButton.setOnAction(e -> {
            // 获取当前窗口的实际大小
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            // 回到 Status 界面
            StatusScene statusScene = new StatusScene(stage, currentWidth, currentHeight, currentUser);
            stage.setScene(statusScene.createScene(themeService));
            StatusService statusService = new StatusService(statusScene, currentUser);
            stage.setTitle("Finanger - Status");
        });

        mainContent.getChildren().addAll(headerBox, filterBox, transactionTable, backButton);
        root.setCenter(mainContent);

        return new Scene(root, width, height);
    }

    private HBox createHeader() {
        Label title = new Label("Transaction Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;" + themeService.getTextColorStyle());

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private HBox createFilterBox() {
        // 加载交易数据
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        allTransactions = FXCollections.observableArrayList(transactions);

        // 获取各列的唯一值用于筛选
        Set<String> dates = transactions.stream()
                .map(Transaction::getTransactionDate)
                .collect(Collectors.toCollection(TreeSet::new));

        Set<String> types = transactions.stream()
                .map(Transaction::getTransactionType)
                .collect(Collectors.toSet());

        Set<String> currencies = transactions.stream()
                .map(Transaction::getCurrency)
                .collect(Collectors.toSet());

        Set<String> categories = transactions.stream()
                .map(Transaction::getCategory)
                .collect(Collectors.toSet());

        Set<String> paymentMethods = transactions.stream()
                .map(Transaction::getPaymentMethod)
                .collect(Collectors.toSet());

        // 创建筛选下拉框
        dateFilter = createFilterComboBox("Date", new ArrayList<>(dates));
        typeFilter = createFilterComboBox("Type", new ArrayList<>(types));
        currencyFilter = createFilterComboBox("Currency", new ArrayList<>(currencies));
        categoryFilter = createFilterComboBox("Category", new ArrayList<>(categories));
        paymentMethodFilter = createFilterComboBox("Payment Method", new ArrayList<>(paymentMethods));

        // 重置按钮
        Button resetButton = new Button("Reset Filters");
        resetButton.setStyle(themeService.getButtonStyle());
        resetButton.setOnAction(e -> resetFilters());

        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle(themeService.getTextColorStyle());

        HBox filterBox = new HBox(10, filterLabel,
                dateFilter, typeFilter, currencyFilter, categoryFilter, paymentMethodFilter, resetButton);
        filterBox.setPadding(new Insets(10));
        filterBox.setAlignment(Pos.CENTER_LEFT);

        return filterBox;
    }

    private ComboBox<String> createFilterComboBox(String name, List<String> items) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(name);

        // 添加"All"选项
        List<String> allItems = new ArrayList<>();
        allItems.add("All " + name);
        allItems.addAll(items);

        comboBox.getItems().addAll(allItems);
        comboBox.setValue("All " + name);

        // 添加筛选事件
        comboBox.setOnAction(e -> applyFilters());

        return comboBox;
    }

    private void createTransactionTable() {
        transactionTable = new TableView<>();
        transactionTable.setEditable(true);

        // 创建表格列
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        dateCol.setCellFactory(TextFieldTableCell.forTableColumn());
        dateCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setTransactionDate(e.getNewValue());
            updateTransaction(tx);
        });

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        typeCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setTransactionType(e.getNewValue());
            updateTransaction(tx);
        });

        TableColumn<Transaction, String> currencyCol = new TableColumn<>("Currency");
        currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));
        currencyCol.setCellFactory(TextFieldTableCell.forTableColumn());
        currencyCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setCurrency(e.getNewValue());
            updateTransaction(tx);
        });

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        amountCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setAmount(e.getNewValue());
            updateTransaction(tx);
        });

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
        categoryCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setCategory(e.getNewValue());
            updateTransaction(tx);
        });

        TableColumn<Transaction, String> paymentCol = new TableColumn<>("Payment Method");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        paymentCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setPaymentMethod(e.getNewValue());
            updateTransaction(tx);
        });

        TableColumn<Transaction, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionCol.setOnEditCommit(e -> {
            Transaction tx = e.getTableView().getItems().get(e.getTablePosition().getRow());
            tx.setDescription(e.getNewValue());
            updateTransaction(tx);
        });

        // 设置列宽和表格属性
        transactionTable.getColumns().addAll(dateCol, typeCol, currencyCol, amountCol, categoryCol, paymentCol,
                descriptionCol);
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionTable.setPrefHeight(500);

        // 启用双击编辑
        transactionTable.setEditable(true);

        // 设置过滤数据
        filteredTransactions = new FilteredList<>(allTransactions);
        transactionTable.setItems(filteredTransactions);
    }

    private void applyFilters() {
        Predicate<Transaction> filter = transaction -> {
            boolean dateMatch = dateFilter.getValue().startsWith("All") ||
                    transaction.getTransactionDate().equals(dateFilter.getValue());

            boolean typeMatch = typeFilter.getValue().startsWith("All") ||
                    transaction.getTransactionType().equals(typeFilter.getValue());

            boolean currencyMatch = currencyFilter.getValue().startsWith("All") ||
                    transaction.getCurrency().equals(currencyFilter.getValue());

            boolean categoryMatch = categoryFilter.getValue().startsWith("All") ||
                    transaction.getCategory().equals(categoryFilter.getValue());

            boolean paymentMatch = paymentMethodFilter.getValue().startsWith("All") ||
                    transaction.getPaymentMethod().equals(paymentMethodFilter.getValue());

            return dateMatch && typeMatch && currencyMatch && categoryMatch && paymentMatch;
        };

        filteredTransactions.setPredicate(filter);
    }

    private void resetFilters() {
        dateFilter.setValue("All Date");
        typeFilter.setValue("All Type");
        currencyFilter.setValue("All Currency");
        categoryFilter.setValue("All Category");
        paymentMethodFilter.setValue("All Payment Method");
        filteredTransactions.setPredicate(null);
    }

    private void updateTransaction(Transaction transaction) {
        // 获取所有交易
        List<Transaction> transactions = txService.loadTransactions(currentUser);

        // 使用简单的匹配方法找到要修改的交易
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).equals(transaction)) {
                // 删除旧的
                transactions.remove(i);
                break;
            }
        }

        // 添加新的
        transactions.add(transaction);

        // 保存回文件
        txService.saveTransactions(currentUser, transactions);

        // 更新筛选选项
        refreshFilterOptions();

        // 重新应用筛选
        applyFilters();
    }

    private void refreshFilterOptions() {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        allTransactions.setAll(transactions);

        // 保存当前选择的值
        String currentDateFilter = dateFilter.getValue();
        String currentTypeFilter = typeFilter.getValue();
        String currentCurrencyFilter = currencyFilter.getValue();
        String currentCategoryFilter = categoryFilter.getValue();
        String currentPaymentFilter = paymentMethodFilter.getValue();

        // 获取新的唯一值
        Set<String> dates = transactions.stream()
                .map(Transaction::getTransactionDate)
                .collect(Collectors.toCollection(TreeSet::new));

        Set<String> types = transactions.stream()
                .map(Transaction::getTransactionType)
                .collect(Collectors.toSet());

        Set<String> currencies = transactions.stream()
                .map(Transaction::getCurrency)
                .collect(Collectors.toSet());

        Set<String> categories = transactions.stream()
                .map(Transaction::getCategory)
                .collect(Collectors.toSet());

        Set<String> paymentMethods = transactions.stream()
                .map(Transaction::getPaymentMethod)
                .collect(Collectors.toSet());

        // 更新下拉框
        updateComboBox(dateFilter, dates, currentDateFilter, "Date");
        updateComboBox(typeFilter, types, currentTypeFilter, "Type");
        updateComboBox(currencyFilter, currencies, currentCurrencyFilter, "Currency");
        updateComboBox(categoryFilter, categories, currentCategoryFilter, "Category");
        updateComboBox(paymentMethodFilter, paymentMethods, currentPaymentFilter, "Payment Method");
    }

    private void updateComboBox(ComboBox<String> comboBox, Set<String> items, String currentValue, String name) {
        comboBox.getItems().clear();
        comboBox.getItems().add("All " + name);
        comboBox.getItems().addAll(items);

        // 尝试保持当前选择
        if (comboBox.getItems().contains(currentValue)) {
            comboBox.setValue(currentValue);
        } else {
            comboBox.setValue("All " + name);
        }
    }
}