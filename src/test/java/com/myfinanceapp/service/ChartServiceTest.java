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

/**
 * Unit test class for the ChartService.
 * This class contains tests for chart generation and update functionality including:
 * - Line chart updates
 * - Bar chart updates
 * - Pie chart updates
 * - Currency conversion handling
 * - Empty data handling
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(ApplicationExtension.class)
class ChartServiceTest {

    private ChartService chartService;
    private TransactionService mockTxService;
    private LineChart<String, Number> lineChart;
    private BarChart<String, Number> barChart;
    private PieChart pieChart;
    private User mockUser;
    private CurrencyService mockCurrencyService;

    /**
     * Initializes the JavaFX toolkit before running tests.
     * This ensures proper JavaFX environment setup.
     */
    @BeforeAll
    static void initToolkit() {
        // This ensures the JavaFX toolkit is initialized before any tests run
    }

    /**
     * Sets up the test environment with real JavaFX components and mocked services.
     * Initializes charts and configures mock behavior for services.
     *
     * @param stage The JavaFX stage (unused in this context)
     */
    @Start
    void start(javafx.stage.Stage stage) {
        // Initialize real JavaFX components instead of mocks
        lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        pieChart = new PieChart();

        mockTxService = Mockito.mock(TransactionService.class);
        mockUser = Mockito.mock(User.class);
        mockCurrencyService = Mockito.mock(CurrencyService.class);

        // Mock currency service behavior
        when(mockCurrencyService.getSelectedCurrency()).thenReturn("USD");
        when(mockCurrencyService.convertCurrency(anyDouble(), anyString())).thenAnswer(invocation ->
                invocation.getArgument(0)); // Return same amount for simplicity

        chartService = new ChartService(lineChart, barChart, pieChart, mockTxService, mockUser, mockCurrencyService);
    }

    /**
     * Tests updating all charts with valid transaction data.
     * Verifies that:
     * - Line chart shows both income and expense series
     * - Bar chart shows both income and expense series
     * - Pie chart shows expense categories
     */
    @Test
    void updateAllCharts_updatesAllChartsWithCorrectDateRange() {
        List<Transaction> transactions = createSampleTransactions();
        when(mockTxService.loadTransactions(mockUser)).thenReturn(transactions);

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        chartService.updateAllCharts(startDate, endDate);

        // Verify line chart
        assertEquals(2, lineChart.getData().size()); // Income and Expense series
        assertTrue(lineChart.getData().stream().anyMatch(series -> series.getName().equals("Income")));
        assertTrue(lineChart.getData().stream().anyMatch(series -> series.getName().equals("Expense")));

        // Verify bar chart
        assertEquals(2, barChart.getData().size()); // Income and Expense series
        assertTrue(barChart.getData().stream().anyMatch(series -> series.getName().equals("Income")));
        assertTrue(barChart.getData().stream().anyMatch(series -> series.getName().equals("Expense")));

        // Verify pie chart
        assertFalse(pieChart.getData().isEmpty());
        assertTrue(pieChart.getData().stream().anyMatch(d -> d.getName().contains("Food")));
    }

    /**
     * Tests chart updates with empty transaction data.
     * Verifies that:
     * - Line chart shows empty series
     * - Bar chart shows empty series
     * - Pie chart remains empty
     */
    @Test
    void updateAllCharts_handlesEmptyTransactions() {
        when(mockTxService.loadTransactions(mockUser)).thenReturn(new ArrayList<>());

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        chartService.updateAllCharts(startDate, endDate);

        // Verify line chart
        assertEquals(2, lineChart.getData().size()); // Still creates empty series
        assertTrue(lineChart.getData().get(0).getData().stream().allMatch(d -> d.getYValue().doubleValue() == 0.0));
        assertTrue(lineChart.getData().get(1).getData().stream().allMatch(d -> d.getYValue().doubleValue() == 0.0));

        // Verify bar chart
        assertEquals(2, barChart.getData().size());
        assertTrue(barChart.getData().get(0).getData().stream().allMatch(d -> d.getYValue().doubleValue() == 0.0));
        assertTrue(barChart.getData().get(1).getData().stream().allMatch(d -> d.getYValue().doubleValue() == 0.0));

        // Verify pie chart
        assertTrue(pieChart.getData().isEmpty()); // No expenses = empty pie
    }

    /**
     * Tests chart updates with currency conversion.
     * Verifies that:
     * - Line chart shows converted values
     * - Bar chart shows converted values
     * - Pie chart shows converted values
     */
    @Test
    void updateAllCharts_handlesCurrencyConversion() {
        List<Transaction> transactions = createSampleTransactions();
        when(mockTxService.loadTransactions(mockUser)).thenReturn(transactions);
        when(mockCurrencyService.convertCurrency(1000.0, "CNY")).thenReturn(140.8);
        when(mockCurrencyService.convertCurrency(50.0, "CNY")).thenReturn(7.04);
        when(mockCurrencyService.convertCurrency(500.0, "CNY")).thenReturn(70.4);

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        chartService.updateAllCharts(startDate, endDate);

        // Verify line chart
        assertTrue(lineChart.getData().get(0).getData().stream()
                .anyMatch(d -> Math.abs(d.getYValue().doubleValue() - 140.8) < 0.001)); // Income
        assertTrue(lineChart.getData().get(1).getData().stream()
                .anyMatch(d -> Math.abs(d.getYValue().doubleValue() - 7.04) < 0.001)); // Expense

        // Verify bar chart
        assertTrue(barChart.getData().get(0).getData().stream()
                .anyMatch(d -> Math.abs(d.getYValue().doubleValue() - 140.8) < 0.001)); // Income
        assertTrue(barChart.getData().get(1).getData().stream()
                .anyMatch(d -> Math.abs(d.getYValue().doubleValue() - 7.04) < 0.001)); // Expense

        // Verify pie chart
        assertTrue(pieChart.getData().stream()
                .anyMatch(d -> Math.abs(d.getPieValue() - 7.04) < 0.001));
    }

    /**
     * Creates sample transaction data for testing.
     * Includes:
     * - One income transaction (Salary)
     * - Two expense transactions (Food and Rent)
     *
     * @return List of sample transactions
     */
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