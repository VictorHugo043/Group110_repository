package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartService {
    private final LineChart<String, Number> lineChart;
    private final BarChart<String, Number> barChart;
    private final PieChart pieChart;
    private final TransactionService txService;
    private final User currentUser;

    public ChartService(LineChart<String, Number> lineChart, BarChart<String, Number> barChart,
                        PieChart pieChart, TransactionService txService, User currentUser) {
        this.lineChart = lineChart;
        this.barChart = barChart;
        this.pieChart = pieChart;
        this.txService = txService;
        this.currentUser = currentUser;
    }

    public void updateAllCharts(String period) {
        updateLineChart(period);
        updateBarChart(period);
        updatePieChart(period);
    }

    private void updateLineChart(String period) {
        List<Transaction> transactions = getFilteredTransactions(period);
        LocalDate startDate = getStartDate(period, transactions);
        LocalDate endDate = getEndDate(period, transactions);
        long totalDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        List<String> allDates = generateDateList(startDate, endDate, period);

        Map<String, Double> incomeByDate = transactions.stream()
                .filter(t -> "Income".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(period)),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Double> expenseByDate = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(period)),
                        Collectors.summingDouble(Transaction::getAmount)
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

    private void updateBarChart(String period) {
        List<Transaction> transactions = getFilteredTransactions(period);
        LocalDate startDate = getStartDate(period, transactions);
        LocalDate endDate = getEndDate(period, transactions);
        long totalDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        List<String> allDates = generateDateList(startDate, endDate, period);

        Map<String, Double> incomeByDate = transactions.stream()
                .filter(t -> "Income".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(period)),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Double> expenseByDate = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(period)),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        barChart.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
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

        barChart.getData().addAll(incomeSeries, expenseSeries);
        adjustXAxis(xAxis, totalDays);
    }

    private void updatePieChart(String period) {
        List<Transaction> transactions = getFilteredTransactions(period);
        Map<String, Double> categoryTotals = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryTotals.forEach((category, amount) ->
                pieChartData.add(new PieChart.Data(category + " " + String.format("%.2f CNY", amount), amount)));
        pieChart.setData(pieChartData);
    }

    private List<Transaction> getFilteredTransactions(String period) {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        LocalDate startDate = getStartDate(period, transactions);
        LocalDate endDate = getEndDate(period, transactions);
        return transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
                    return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    private LocalDate getStartDate(String period, List<Transaction> transactions) {
        LocalDate now = LocalDate.now();
        switch (period) {
            case "Last Month":
                return now.minusMonths(1).withDayOfMonth(1);
            case "All Transactions":
                return transactions.isEmpty() ? now.withDayOfMonth(1) :
                        transactions.stream()
                                .map(t -> LocalDate.parse(t.getTransactionDate()))
                                .min(LocalDate::compareTo)
                                .orElse(now.withDayOfMonth(1));
            default: // "This Month"
                return now.withDayOfMonth(1);
        }
    }

    private LocalDate getEndDate(String period, List<Transaction> transactions) {
        LocalDate now = LocalDate.now();
        switch (period) {
            case "Last Month":
                return now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());
            case "All Transactions":
                return transactions.isEmpty() ? now.withDayOfMonth(now.lengthOfMonth()) :
                        transactions.stream()
                                .map(t -> LocalDate.parse(t.getTransactionDate()))
                                .max(LocalDate::compareTo)
                                .orElse(now.withDayOfMonth(now.lengthOfMonth()));
            default: // "This Month"
                return now.withDayOfMonth(now.lengthOfMonth());
        }
    }

    private List<String> generateDateList(LocalDate startDate, LocalDate endDate, String period) {
        List<String> allDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        DateTimeFormatter formatter = getFormatter(period);
        while (!currentDate.isAfter(endDate)) {
            allDates.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }
        return allDates;
    }

    private DateTimeFormatter getFormatter(String period) {
        return period.equals("All Transactions") ?
                DateTimeFormatter.ofPattern("yyyy-MM-dd") :
                DateTimeFormatter.ofPattern("MM-dd");
    }

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