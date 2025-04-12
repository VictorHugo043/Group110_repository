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

    public void updateAllCharts(LocalDate startDate, LocalDate endDate) {
        updateLineChart(startDate, endDate);
        updateBarChart(startDate, endDate);
        updatePieChart(startDate, endDate);
    }

    private void updateLineChart(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = getFilteredTransactions(startDate, endDate);
        long totalDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        List<String> allDates = generateDateList(startDate, endDate);

        Map<String, Double> incomeByDate = transactions.stream()
                .filter(t -> "Income".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(totalDays)),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Double> expenseByDate = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(totalDays)),
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

    private void updateBarChart(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = getFilteredTransactions(startDate, endDate);
        long totalDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        List<String> allDates = generateDateList(startDate, endDate);

        Map<String, Double> incomeByDate = transactions.stream()
                .filter(t -> "Income".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(totalDays)),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Double> expenseByDate = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> LocalDate.parse(t.getTransactionDate()).format(getFormatter(totalDays)),
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

    private void updatePieChart(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = getFilteredTransactions(startDate, endDate);
        Map<String, Double> categoryTotals = transactions.stream()
                .filter(t -> "Expense".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryTotals.forEach((category, amount) ->
                pieChartData.add(new PieChart.Data(category + " " + String.format("%.2f CNY", amount), amount)));
        pieChart.setData(pieChartData);
    }

    private List<Transaction> getFilteredTransactions(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        return transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
                    return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    private List<String> generateDateList(LocalDate startDate, LocalDate endDate) {
        List<String> allDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        long totalDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        DateTimeFormatter formatter = getFormatter(totalDays);
        while (!currentDate.isAfter(endDate)) {
            allDates.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }
        return allDates;
    }

    private DateTimeFormatter getFormatter(long totalDays) {
        return totalDays > 365 ?
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