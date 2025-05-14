package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for managing and manipulating transaction data in the application.
 * This class provides functionality for:
 * - Loading and displaying transactions
 * - Filtering transactions by various criteria
 * - Sorting transactions
 * - Managing transaction data in a TableView
 * - Updating and deleting transactions
 * 
 * The service integrates with JavaFX components to provide a responsive
 * and interactive transaction management interface.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class TransactionManagementService {
    private final User currentUser;
    private final TransactionService txService;
    private ObservableList<Transaction> allTransactions;
    private FilteredList<Transaction> filteredTransactions;
    private TableView<Transaction> transactionTable;

    // Filter control references
    private ComboBox<String> dateFilter;
    private ComboBox<String> typeFilter;
    private ComboBox<String> currencyFilter;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> paymentMethodFilter;

    /**
     * Constructs a new TransactionManagementService instance.
     *
     * @param currentUser The user whose transactions to manage
     * @param transactionTable The TableView to display transactions
     * @param dateFilter ComboBox for filtering by date
     * @param typeFilter ComboBox for filtering by transaction type
     * @param currencyFilter ComboBox for filtering by currency
     * @param categoryFilter ComboBox for filtering by category
     * @param paymentMethodFilter ComboBox for filtering by payment method
     */
    public TransactionManagementService(User currentUser, TableView<Transaction> transactionTable,
                                        ComboBox<String> dateFilter, ComboBox<String> typeFilter,
                                        ComboBox<String> currencyFilter, ComboBox<String> categoryFilter,
                                        ComboBox<String> paymentMethodFilter) {
        this.currentUser = currentUser;
        this.txService = new TransactionService();
        this.transactionTable = transactionTable;
        this.dateFilter = dateFilter;
        this.typeFilter = typeFilter;
        this.currencyFilter = currencyFilter;
        this.categoryFilter = categoryFilter;
        this.paymentMethodFilter = paymentMethodFilter;

        // Load transaction data and initialize
        loadTransactions();
    }

    /**
     * Gets the observable list of all transactions.
     *
     * @return ObservableList containing all transactions
     */
    public ObservableList<Transaction> getAllTransactions() {
        return allTransactions;
    }

    /**
     * Gets the filtered list of transactions.
     *
     * @return FilteredList containing filtered transactions
     */
    public FilteredList<Transaction> getFilteredTransactions() {
        return filteredTransactions;
    }

    /**
     * Loads transactions for the current user and initializes the data structures.
     */
    public void loadTransactions() {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        allTransactions = FXCollections.observableArrayList(transactions);
        filteredTransactions = new FilteredList<>(allTransactions);
    }

    /**
     * Gets unique values from transactions for a specific field.
     *
     * @param extractor Function to extract the desired field from a transaction
     * @return List of unique values for the specified field
     */
    public List<String> getUniqueValues(java.util.function.Function<Transaction, String> extractor) {
        Set<String> uniqueValues = allTransactions.stream()
                .map(extractor)
                .filter(Objects::nonNull)  // Filter out null values
                .collect(Collectors.toCollection(TreeSet::new));  // Use TreeSet to ensure sorting
        return new ArrayList<>(uniqueValues);
    }

    /**
     * Initializes all filter ComboBoxes with available options.
     */
    public void initializeFilters() {
        // Update options for each filter ComboBox
        updateComboBox(dateFilter, "Date", transaction -> transaction.getTransactionDate());
        updateComboBox(typeFilter, "Type", transaction -> transaction.getTransactionType());
        updateComboBox(currencyFilter, "Currency", transaction -> transaction.getCurrency());
        updateComboBox(categoryFilter, "Category", transaction -> transaction.getCategory());
        updateComboBox(paymentMethodFilter, "Payment Method", transaction -> transaction.getPaymentMethod());
    }

    /**
     * Applies all active filters to the transaction list.
     * Maintains the current sort order while applying filters.
     */
    public void applyFilters() {
        // Temporarily remove sort listener to avoid recursive calls
        boolean hasSortOrder = transactionTable.getSortOrder().size() > 0;
        TableColumn<Transaction, ?> sortColumn = null;
        TableColumn.SortType sortType = null;

        if (hasSortOrder) {
            sortColumn = transactionTable.getSortOrder().get(0);
            sortType = sortColumn.getSortType();
            transactionTable.getSortOrder().clear();
        }

        // Apply filters
        Predicate<Transaction> filter = transaction -> {
            boolean dateMatch = dateFilter.getValue() == null ||
                    dateFilter.getValue().startsWith("All") ||
                    transaction.getTransactionDate().equals(dateFilter.getValue());

            boolean typeMatch = typeFilter.getValue() == null ||
                    typeFilter.getValue().startsWith("All") ||
                    transaction.getTransactionType().equals(typeFilter.getValue());

            boolean currencyMatch = currencyFilter.getValue() == null ||
                    currencyFilter.getValue().startsWith("All") ||
                    transaction.getCurrency().equals(currencyFilter.getValue());

            boolean categoryMatch = categoryFilter.getValue() == null ||
                    categoryFilter.getValue().startsWith("All") ||
                    transaction.getCategory().equals(categoryFilter.getValue());

            boolean paymentMatch = paymentMethodFilter.getValue() == null ||
                    paymentMethodFilter.getValue().startsWith("All") ||
                    transaction.getPaymentMethod().equals(paymentMethodFilter.getValue());

            return dateMatch && typeMatch && currencyMatch && categoryMatch && paymentMatch;
        };

        filteredTransactions.setPredicate(filter);

        // Reapply sorting
        if (hasSortOrder && sortColumn != null) {
            sortColumn.setSortType(sortType);
            transactionTable.getSortOrder().add(sortColumn);
        }
    }

    /**
     * Resets all filters to their default "All" values.
     * Maintains the current sort order while resetting filters.
     */
    public void resetFilters() {
        // Temporarily remove sort listener to avoid recursive calls
        boolean hasSortOrder = transactionTable.getSortOrder().size() > 0;
        TableColumn<Transaction, ?> sortColumn = null;
        TableColumn.SortType sortType = null;

        if (hasSortOrder) {
            sortColumn = transactionTable.getSortOrder().get(0);
            sortType = sortColumn.getSortType();
            transactionTable.getSortOrder().clear();
        }

        dateFilter.setValue("All Date");
        typeFilter.setValue("All Type");
        currencyFilter.setValue("All Currency");
        categoryFilter.setValue("All Category");
        paymentMethodFilter.setValue("All Payment Method");
        filteredTransactions.setPredicate(null);

        // Reapply sorting
        if (hasSortOrder && sortColumn != null) {
            sortColumn.setSortType(sortType);
            transactionTable.getSortOrder().add(sortColumn);
        }
    }

    /**
     * Applies sorting to the filtered transaction data.
     *
     * @param comparator The comparator to use for sorting
     */
    public void applySorting(Comparator<Transaction> comparator) {
        if (comparator == null) return;

        // Apply sorting directly to the data source
        FXCollections.sort(allTransactions, comparator);
    }

    /**
     * Refreshes the options in all filter ComboBoxes while maintaining current selections.
     */
    public void refreshFilterOptions() {
        // Temporarily save current filter values
        String dateValue = dateFilter.getValue();
        String typeValue = typeFilter.getValue();
        String currencyValue = currencyFilter.getValue();
        String categoryValue = categoryFilter.getValue();
        String paymentMethodValue = paymentMethodFilter.getValue();

        // Update options for each filter ComboBox
        updateComboBox(dateFilter, "Date", transaction -> transaction.getTransactionDate());
        updateComboBox(typeFilter, "Type", transaction -> transaction.getTransactionType());
        updateComboBox(currencyFilter, "Currency", transaction -> transaction.getCurrency());
        updateComboBox(categoryFilter, "Category", transaction -> transaction.getCategory());
        updateComboBox(paymentMethodFilter, "Payment Method", transaction -> transaction.getPaymentMethod());

        // Restore previous filter values
        setComboBoxValueSafely(dateFilter, dateValue, "All Date");
        setComboBoxValueSafely(typeFilter, typeValue, "All Type");
        setComboBoxValueSafely(currencyFilter, currencyValue, "All Currency");
        setComboBoxValueSafely(categoryFilter, categoryValue, "All Category");
        setComboBoxValueSafely(paymentMethodFilter, paymentMethodValue, "All Payment Method");

        // Reapply filters
        applyFilters();
    }

    /**
     * Updates a ComboBox with unique values from transactions.
     *
     * @param comboBox The ComboBox to update
     * @param name The name of the filter
     * @param extractor Function to extract values from transactions
     */
    private <T> void updateComboBox(ComboBox<String> comboBox, String name,
                                    java.util.function.Function<Transaction, String> extractor) {
        String currentValue = comboBox.getValue();
        comboBox.getItems().clear();

        Set<String> uniqueValues = allTransactions.stream()
                .map(extractor)
                .collect(Collectors.toSet());

        List<String> items = new ArrayList<>();
        items.add("All " + name);
        if (name.equals("Date")) {
            // Convert date strings to sortable objects
            List<String> sortedDates = new ArrayList<>(uniqueValues);
            sortedDates.sort((date1, date2) -> {
                try {
                    // Assume date format is yyyy-MM-dd
                    String[] parts1 = date1.split("-");
                    String[] parts2 = date2.split("-");

                    // Compare by year, month, day
                    int yearCompare = Integer.compare(
                            Integer.parseInt(parts1[0]),
                            Integer.parseInt(parts2[0])
                    );
                    if (yearCompare != 0) return yearCompare;

                    int monthCompare = Integer.compare(
                            Integer.parseInt(parts1[1]),
                            Integer.parseInt(parts2[1])
                    );
                    if (monthCompare != 0) return monthCompare;

                    return Integer.compare(
                            Integer.parseInt(parts1[2]),
                            Integer.parseInt(parts2[2])
                    );
                } catch (Exception e) {
                    // If parsing fails, fall back to string comparison
                    return date1.compareTo(date2);
                }
            });
            items.addAll(sortedDates);
        } else {
            // Keep other fields as is
            items.addAll(uniqueValues);
        }

        comboBox.getItems().addAll(items);
        setComboBoxValueSafely(comboBox, currentValue, "All " + name);
    }

    /**
     * Safely sets a value in a ComboBox, falling back to a default if the value is not available.
     *
     * @param comboBox The ComboBox to update
     * @param value The value to set
     * @param defaultValue The default value to use if the desired value is not available
     */
    private void setComboBoxValueSafely(ComboBox<String> comboBox, String value, String defaultValue) {
        // Safely set ComboBox value
        if (value != null && comboBox.getItems().contains(value)) {
            comboBox.setValue(value);
        } else {
            comboBox.setValue(defaultValue);
        }
    }

    /**
     * Deletes a transaction after user confirmation.
     *
     * @param transaction The transaction to delete
     * @return true if the transaction was deleted, false if the operation was cancelled
     */
    public boolean deleteTransaction(Transaction transaction) {
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete this transaction?");
        confirmAlert.setContentText("Date: " + transaction.getTransactionDate() +
                "\nType: " + transaction.getTransactionType() +
                "\nAmount: " + transaction.getAmount() + " " + transaction.getCurrency() +
                "\nCategory: " + transaction.getCategory());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Get all transactions
            List<Transaction> transactions = txService.loadTransactions(currentUser);

            // Find and remove the transaction to delete
            transactions.removeIf(tx -> tx.equals(transaction));

            // Save back to file
            txService.saveTransactions(currentUser, transactions);

            // Remove from current table
            allTransactions.remove(transaction);

            // Update filter options
            refreshFilterOptions();

            // Show success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Delete Successful");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Transaction has been successfully deleted!");
            successAlert.showAndWait();
            return true;
        }
        return false;
    }

    /**
     * Updates an existing transaction.
     *
     * @param transaction The transaction to update
     */
    public void updateTransaction(Transaction transaction) {
        // Get all transactions
        List<Transaction> transactions = txService.loadTransactions(currentUser);

        // Find and replace the transaction
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).equals(transaction)) {
                // Remove old
                transactions.remove(i);
                break;
            }
        }

        // Add new
        transactions.add(transaction);

        // Save back to file
        txService.saveTransactions(currentUser, transactions);

        // Update filter options
        refreshFilterOptions();
    }
}