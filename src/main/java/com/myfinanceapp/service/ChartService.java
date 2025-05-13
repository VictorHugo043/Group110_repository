package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing financial data visualization through various chart types.
 * This class provides functionality for:
 * - Line charts showing income and expense trends
 * - Bar charts for comparing income and expenses
 * - Pie charts for expense category distribution
 * - Dynamic date range visualization
 * - Automatic chart scaling and formatting
 * 
 * The service handles data aggregation, currency conversion, and chart updates
 * based on selected date ranges.
 */
public class ChartService {
    private final LineChart<String, Number> lineChart;
    private final BarChart<String, Number> barChart;
    private final PieChart pieChart;
    private final TransactionService txService;
    private final User currentUser;
    private final CurrencyService currencyService;

    /**
     * Constructs a new ChartService instance.
     *
     * @param lineChart The line chart for showing trends
     * @param barChart The bar chart for comparisons
     * @param pieChart The pie chart for category distribution
     * @param txService Service for accessing transaction data
     * @param currentUser The user whose data to visualize
     * @param currencyService Service for currency conversion
     */
    public ChartService(LineChart<String, Number> lineChart, BarChart<String, Number> barChart,
                        PieChart pieChart, TransactionService txService, User currentUser, CurrencyService currencyService) {
        this.lineChart = lineChart;
        this.barChart = barChart;
        this.pieChart = pieChart;
        this.txService = txService;
        this.currentUser = currentUser;
        this.currencyService = currencyService;
    }

    /**
     * Updates all charts with data for the specified date range.
     *
     * @param startDate The start date for the data range
     * @param endDate The end date for the data range
     */
    public void updateAllCharts(LocalDate startDate, LocalDate endDate) {
        updateLineChart(startDate, endDate);
        updateBarChart(startDate, endDate);
        updatePieChart(startDate, endDate);
    }

    /**
     * Updates the line chart with income and expense trends.
     * Shows daily data points with proper date formatting based on the date range.
     *
     * @param startDate The start date for the data range
     * @param endDate The end date for the data range
     */
    private void updateLineChart(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = getFilteredTransactions(startDate, endDate);
        long totalDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        List<String> allDates = generateDateList(startDate, endDate, totalDays);

        Map<String, Double> incomeByDate = transactions.stream()
                .filter(t -> "Income".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(totalDays)),
                        Collectors.summingDouble(t -> currencyService.convertCurrency(t.getAmount(), t.getCurrency()))
                ));

        Map<String, Double> expenseByDate = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(totalDays)),
                        Collectors.summingDouble(t -> currencyService.convertCurrency(t.getAmount(), t.getCurrency()))
                ));

        lineChart.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
        xAxis.getCategories().clear();
        xAxis.setCategories(FXCollections.observableArrayList(allDates));

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        for (String date : allDates) {
            incomeSeries.getData().add(new XYChart.Data<>(date, incomeByDate.getOrDefault(date, 0.0)));
            expenseSeries.getData().add(new XYChart.Data<>(date, expenseByDate.getOrDefault(date, 0.0)));
        }

        lineChart.getData().addAll(incomeSeries, expenseSeries);
        adjustXAxis(xAxis, totalDays);
    }

    /**
     * Updates the bar chart with grouped income and expense data.
     * Groups data points based on the date range length for better visualization.
     *
     * @param startDate The start date for the data range
     * @param endDate The end date for the data range
     */
    private void updateBarChart(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = getFilteredTransactions(startDate, endDate);
        long totalDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        int groupDays = calculateGroupDays(totalDays);
        List<String> groupedDates = generateGroupedDateList(startDate, endDate, groupDays);

        Map<String, Double> incomeByDate = new HashMap<>();
        Map<String, Double> expenseByDate = new HashMap<>();

        for (Transaction t : transactions) {
            LocalDate txDate = LocalDate.parse(t.getTransactionDate());
            String groupedDate = getGroupedDate(txDate, startDate, groupDays, totalDays);
            double convertedAmount = currencyService.convertCurrency(t.getAmount(), t.getCurrency());

            if ("Income".equals(t.getTransactionType())) {
                incomeByDate.merge(groupedDate, convertedAmount, Double::sum);
            } else if ("Expense".equals(t.getTransactionType())) {
                expenseByDate.merge(groupedDate, convertedAmount, Double::sum);
            }
        }

        barChart.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        xAxis.getCategories().clear();
        xAxis.setCategories(FXCollections.observableArrayList(groupedDates));

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        for (String date : groupedDates) {
            incomeSeries.getData().add(new XYChart.Data<>(date, incomeByDate.getOrDefault(date, 0.0)));
            expenseSeries.getData().add(new XYChart.Data<>(date, expenseByDate.getOrDefault(date, 0.0)));
        }

        barChart.getData().addAll(incomeSeries, expenseSeries);
        adjustXAxis(xAxis, groupedDates.size());
    }

    /**
     * Updates the pie chart with expense category distribution.
     * Shows the proportion of expenses in each category with currency conversion.
     *
     * @param startDate The start date for the data range
     * @param endDate The end date for the data range
     */
    private void updatePieChart(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = getFilteredTransactions(startDate, endDate);
        Map<String, Double> categoryTotals = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(t -> currencyService.convertCurrency(t.getAmount(), t.getCurrency()))
                ));
        String currencySymbol = currencyService.getSelectedCurrency();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryTotals.forEach((category, amount) ->
                pieChartData.add(new PieChart.Data(category + " " + String.format("%.2f %s", amount, currencySymbol), amount)));
        pieChart.setData(pieChartData);
    }

    /**
     * Filters transactions for the specified date range.
     *
     * @param startDate The start date for filtering
     * @param endDate The end date for filtering
     * @return List of transactions within the date range
     */
    private List<Transaction> getFilteredTransactions(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        return transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
                    return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * Generates a list of dates between start and end dates.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @param totalDays The total number of days in the range
     * @return List of formatted date strings
     */
    private List<String> generateDateList(LocalDate startDate, LocalDate endDate, long totalDays) {
        List<String> allDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        DateTimeFormatter formatter = getFormatter(totalDays);
        while (!currentDate.isAfter(endDate)) {
            allDates.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }
        return allDates;
    }

    /**
     * Generates a list of grouped dates for bar chart visualization.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @param groupDays The number of days to group together
     * @return List of formatted date range strings
     */
    private List<String> generateGroupedDateList(LocalDate startDate, LocalDate endDate, int groupDays) {
        List<String> groupedDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        long totalDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        DateTimeFormatter formatter = getFormatter(totalDays);

        while (!currentDate.isAfter(endDate)) {
            LocalDate groupEnd = currentDate.plusDays(groupDays - 1);
            if (groupEnd.isAfter(endDate)) {
                groupEnd = endDate;
            }
            String label = currentDate.format(formatter);
            if (!groupEnd.equals(currentDate)) {
                label += " to " + groupEnd.format(formatter);
            }
            groupedDates.add(label);
            currentDate = groupEnd.plusDays(1);
        }
        return groupedDates;
    }

    /**
     * Gets the grouped date label for a specific date.
     *
     * @param date The date to get the group for
     * @param startDate The start date of the range
     * @param groupDays The number of days in each group
     * @param totalDays The total number of days in the range
     * @return Formatted date range string for the group
     */
    private String getGroupedDate(LocalDate date, LocalDate startDate, int groupDays, long totalDays) {
        long daysSinceStart = date.toEpochDay() - startDate.toEpochDay();
        long groupIndex = daysSinceStart / groupDays;
        LocalDate groupStart = startDate.plusDays(groupIndex * groupDays);
        LocalDate groupEnd = groupStart.plusDays(groupDays - 1);
        if (groupEnd.isAfter(date)) {
            groupEnd = date;
        }
        DateTimeFormatter formatter = getFormatter(totalDays);
        String label = groupStart.format(formatter);
        if (!groupEnd.equals(groupStart)) {
            label += " to " + groupEnd.format(formatter);
        }
        return label;
    }

    /**
     * Calculates the number of days to group together based on the total date range.
     *
     * @param totalDays The total number of days in the range
     * @return The number of days to group together
     */
    private int calculateGroupDays(long totalDays) {
        if (totalDays <= 30) return 1;
        if (totalDays <= 90) return 3;
        if (totalDays <= 180) return 7;
        return 14;
    }

    /**
     * Gets the appropriate date formatter based on the date range length.
     *
     * @param totalDays The total number of days in the range
     * @return DateTimeFormatter for formatting dates
     */
    private DateTimeFormatter getFormatter(long totalDays) {
        return totalDays > 365 ?
                DateTimeFormatter.ofPattern("yyyy-MM-dd") :
                DateTimeFormatter.ofPattern("MM-dd");
    }

    /**
     * Adjusts the X-axis appearance based on the number of data points.
     *
     * @param xAxis The CategoryAxis to adjust
     * @param totalDays The total number of days in the range
     */
    private void adjustXAxis(CategoryAxis xAxis, long totalDays) {
        if (totalDays > 30) {
            xAxis.setTickLabelRotation(45);
            xAxis.setTickLabelsVisible(true);
        } else {
            xAxis.setTickLabelRotation(0);
            xAxis.setTickLabelsVisible(true);
        }
    }
}