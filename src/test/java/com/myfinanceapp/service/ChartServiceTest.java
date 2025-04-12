package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class ChartServiceTest {

    private ChartService chartService;
    private TransactionService mockTxService;
    private LineChart<String, Number> lineChart;
    private BarChart<String, Number> barChart;
    private PieChart pieChart;
    private User mockUser;

    @BeforeAll
    static void initToolkit() {
        // This ensures the JavaFX toolkit is initialized before any tests run
    }

    @Start
    void start(javafx.stage.Stage stage) {
        // Initialize real JavaFX components instead of mocks
        lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        pieChart = new PieChart();

        mockTxService = Mockito.mock(TransactionService.class);
        mockUser = Mockito.mock(User.class);

        chartService = new ChartService(lineChart, barChart, pieChart, mockTxService, mockUser);
    }

    @Test
    void updateAllCharts_updatesAllChartsWithCorrectDateRange() {
        List<Transaction> transactions = createSampleTransactions();
        when(mockTxService.loadTransactions(mockUser)).thenReturn(transactions);

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        chartService.updateAllCharts(startDate, endDate);

        assertEquals(2, lineChart.getData().size()); // Income and Expense series
        assertEquals(2, barChart.getData().size());  // Income and Expense series
        assertFalse(pieChart.getData().isEmpty());
    }

    @Test
    void updateAllCharts_handlesEmptyTransactions() {
        when(mockTxService.loadTransactions(mockUser)).thenReturn(new ArrayList<>());

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        chartService.updateAllCharts(startDate, endDate);

        assertEquals(2, lineChart.getData().size()); // Still creates empty series
        assertEquals(2, barChart.getData().size());
        assertTrue(pieChart.getData().isEmpty());    // No expenses = empty pie
    }

    @Test
    void updateAllCharts_filtersDateRangeCorrectly() {
        List<Transaction> transactions = createSampleTransactions();
        when(mockTxService.loadTransactions(mockUser)).thenReturn(transactions);

        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 31);

        chartService.updateAllCharts(startDate, endDate);

        ObservableList<PieChart.Data> pieData = pieChart.getData();
        assertTrue(pieData.stream().anyMatch(d -> d.getName().contains("Rent")));
        assertFalse(pieData.stream().anyMatch(d -> d.getName().contains("Food")));
    }

    private List<Transaction> createSampleTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        Transaction income = new Transaction();
        income.setTransactionDate("2025-04-01");
        income.setTransactionType("Income");
        income.setCurrency("CNY");
        income.setAmount(1000.0);
        income.setCategory("Salary");
        income.setPaymentMethod("Bank Transfer");

        Transaction foodExpense = new Transaction();
        foodExpense.setTransactionDate("2025-04-11");
        foodExpense.setTransactionType("Expense");
        foodExpense.setCurrency("CNY");
        foodExpense.setAmount(50.0);
        foodExpense.setCategory("Food");
        foodExpense.setPaymentMethod("Cash");

        Transaction rentExpense = new Transaction();
        rentExpense.setTransactionDate("2025-03-01");
        rentExpense.setTransactionType("Expense");
        rentExpense.setCurrency("CNY");
        rentExpense.setAmount(500.0);
        rentExpense.setCategory("Rent");
        rentExpense.setPaymentMethod("Bank Card");

        transactions.add(income);
        transactions.add(foodExpense);
        transactions.add(rentExpense);

        return transactions;
    }
}