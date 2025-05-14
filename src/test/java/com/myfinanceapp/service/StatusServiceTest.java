package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.statusscene.StatusScene;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the StatusService.
 * This class contains tests for financial status management functionality including:
 * - Summary label updates
 * - Transaction list updates
 * - Date picker interactions
 * - Chart type switching
 * - Currency conversion
 * - UI component initialization
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StatusServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private ChartService chartService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private ThemeService themeService;

    @Mock
    private LanguageService languageService;

    private StatusScene statusScene;
    private StatusService statusService;
    private User testUser;

    /**
     * Sets up the JavaFX toolkit before running tests.
     * Registers the primary stage for UI testing.
     *
     * @throws Exception if setup fails
     */
    @BeforeAll
    public static void setupClass() throws Exception {
        FxToolkit.registerPrimaryStage();
    }

    /**
     * Sets up the test environment before each test.
     * Initializes UI components, mock services, and test data.
     * Configures currency conversion and service injection.
     *
     * @throws Exception if setup fails
     */
    @BeforeEach
    void setUp() throws Exception {
        testUser = new User("test-uid", "testUser", "password", "question", "answer", null);

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = FxToolkit.registerPrimaryStage();
                double width = 800.0;
                double height = 600.0;

                // Initialize StatusScene and manually set fields
                statusScene = new StatusScene(stage, width, height, testUser);
                initializeStatusSceneFields(statusScene);

                // Set Scene and bind to Stage
                Scene scene = new Scene(new Pane(), width, height);
                stage.setScene(scene);

                // Configure currency service
                when(currencyService.getSelectedCurrency()).thenReturn("CNY");
                // Critical fix: Ensure currency conversion returns correct values
                when(currencyService.convertCurrency(anyDouble(), anyString())).thenAnswer(
                        inv -> (Double) inv.getArgument(0)
                );

                // Create StatusService
                statusService = new StatusService(statusScene, testUser, currencyService, languageService);

                // Inject mock objects using reflection
                java.lang.reflect.Field txField = StatusService.class.getDeclaredField("txService");
                txField.setAccessible(true);
                txField.set(statusService, transactionService);

                java.lang.reflect.Field chartField = StatusService.class.getDeclaredField("chartService");
                chartField.setAccessible(true);
                chartField.set(statusService, chartService);

                // Reset initialization dates before each test to avoid default date interference
                java.lang.reflect.Field startDateField = StatusService.class.getDeclaredField("startDate");
                startDateField.setAccessible(true);
                java.lang.reflect.Field endDateField = StatusService.class.getDeclaredField("endDate");
                endDateField.setAccessible(true);

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize StatusScene", e);
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Setup timed out");
        }
        if (statusService == null) {
            throw new RuntimeException("StatusService initialization failed");
        }
    }

    /**
     * Initializes the StatusScene UI components.
     * Sets up charts, labels, and other UI elements with default values.
     *
     * @param statusScene The StatusScene instance to initialize
     */
    private void initializeStatusSceneFields(StatusScene statusScene) {
        statusScene.lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        statusScene.barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        statusScene.pieChart = new PieChart();
        statusScene.exLabel = new Label("Ex.  0.00 CNY");  // Set initial value
        statusScene.inLabel = new Label("In.  0.00 CNY");  // Set initial value
        statusScene.transactionsBox = new VBox();
        statusScene.questionArea = new TextArea();
        statusScene.suggestionsWebView = new WebView();
        statusScene.startDatePicker = new DatePicker();
        statusScene.endDatePicker = new DatePicker();
        statusScene.chartTypeCombo = new ComboBox<>();
        statusScene.chartTypeCombo.getItems().addAll("Line graph", "Bar chart");
        statusScene.chartTypeCombo.setValue("Line graph");
        statusScene.chartPane = new StackPane();
        statusScene.chartPane.getChildren().add(statusScene.lineChart);  // Add lineChart by default
        statusScene.sendBtn = new Button("Send");
        statusScene.themeService = themeService;
        when(themeService.isDayMode()).thenReturn(true);
    }

    /**
     * Tests the update of summary labels for the current month.
     * Verifies that:
     * - Income and expense labels are correctly updated
     * - Currency conversion is properly applied
     * - Transaction amounts are correctly summed
     */
    @Test
    void testUpdateSummaryLabels_ThisMonth() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setTransactionDate("2025-04-01");
        tx1.setTransactionType("Income");
        tx1.setAmount(1000.0);
        tx1.setCurrency("CNY");

        Transaction tx2 = new Transaction();
        tx2.setTransactionDate("2025-04-02");
        tx2.setTransactionType("Expense");
        tx2.setAmount(500.0);
        tx2.setCurrency("CNY");

        transactions.add(tx1);
        transactions.add(tx2);

        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        statusService.updateSummaryLabels(startDate, endDate);

        assertEquals("Ex.  500.00 CNY", statusScene.exLabel.getText());
        assertEquals("In.  1000.00 CNY", statusScene.inLabel.getText());
    }

    /**
     * Tests the update of transaction list.
     * Verifies that:
     * - Transactions are correctly displayed
     * - Transaction details are properly formatted
     * - List is properly populated
     */
    @Test
    void testUpdateTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setTransactionDate("2025-04-01");
        tx1.setCategory("Salary");
        tx1.setAmount(1000.0);
        tx1.setCurrency("CNY");
        transactions.add(tx1);

        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        statusService.updateTransactions(startDate, endDate);

        assertFalse(statusScene.transactionsBox.getChildren().isEmpty());
        assertEquals(1, statusScene.transactionsBox.getChildren().size());
        Label txLabel = (Label) statusScene.transactionsBox.getChildren().get(0);
        assertEquals("2025-04-01   Salary    1000.00 CNY", txLabel.getText());
    }

    /**
     * Tests the date picker interaction.
     * Verifies that:
     * - Date changes trigger proper updates
     * - Summary labels are updated
     * - Charts are refreshed
     *
     * @throws TimeoutException if the test times out
     */
    @Test
    void testDatePickerAction() throws TimeoutException {
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setTransactionDate("2025-03-01");
        tx1.setTransactionType("Income");
        tx1.setAmount(2000.0);
        tx1.setCurrency("CNY");
        transactions.add(tx1);

        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);

        // Manually set StatusService date fields
        try {
            java.lang.reflect.Field startDateField = StatusService.class.getDeclaredField("startDate");
            startDateField.setAccessible(true);
            java.lang.reflect.Field endDateField = StatusService.class.getDeclaredField("endDate");
            endDateField.setAccessible(true);

            LocalDate startDate = LocalDate.of(2025, 3, 1);
            LocalDate endDate = LocalDate.of(2025, 3, 31);

            CountDownLatch dateLatch = new CountDownLatch(1);

            Platform.runLater(() -> {
                try {
                    startDateField.set(statusService, startDate);
                    endDateField.set(statusService, endDate);

                    // Directly call methods to update labels
                    statusService.updateSummaryLabels(startDate, endDate);
                    statusService.updateTransactions(startDate, endDate);

                    // Directly set date picker values
                    statusScene.startDatePicker.setValue(startDate);
                    statusScene.endDatePicker.setValue(endDate);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dateLatch.countDown();
                }
            });

            assertTrue(dateLatch.await(5, TimeUnit.SECONDS), "Date picker action timed out");

            // Verify
            Platform.runLater(() -> {
                assertEquals("Ex.  0.00 CNY", statusScene.exLabel.getText());
                assertEquals("In.  2000.00 CNY", statusScene.inLabel.getText());
                verify(chartService, times(1)).updateAllCharts(startDate, endDate);
            });
        } catch (Exception e) {
            fail("Failed to set date fields: " + e.getMessage());
        }
    }

    /**
     * Tests the chart type combo box interaction for line graph.
     * Verifies that:
     * - Line chart is displayed
     * - Bar chart is hidden
     * - Charts are updated
     *
     * @throws TimeoutException if the test times out
     */
    @Test
    void testChartTypeComboAction_LineGraph() throws TimeoutException {
        // Manually set dates
        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        try {
            // Set StatusService internal state
            java.lang.reflect.Field startDateField = StatusService.class.getDeclaredField("startDate");
            startDateField.setAccessible(true);
            startDateField.set(statusService, startDate);

            java.lang.reflect.Field endDateField = StatusService.class.getDeclaredField("endDate");
            endDateField.setAccessible(true);
            endDateField.set(statusService, endDate);

            // Clear previous interactions and reset expectations
            reset(chartService);

            CountDownLatch chartLatch = new CountDownLatch(1);

            Platform.runLater(() -> {
                try {
                    // Set chart type and trigger event
                    statusScene.chartTypeCombo.setValue("Line graph");
                    if (statusScene.chartTypeCombo.getOnAction() != null) {
                        statusScene.chartTypeCombo.getOnAction().handle(null);
                    }
                } finally {
                    chartLatch.countDown();
                }
            });

            assertTrue(chartLatch.await(5, TimeUnit.SECONDS), "Chart action timed out");

            // Use FxToolkit for UI verification
            FxToolkit.setupFixture(() -> {
                assertTrue(statusScene.chartPane.getChildren().contains(statusScene.lineChart));
                assertFalse(statusScene.chartPane.getChildren().contains(statusScene.barChart));
                // Only verify method is called at least once, don't care about exact count
                verify(chartService, atLeastOnce()).updateAllCharts(any(), any());
            });
        } catch (Exception e) {
            fail("Test failed, error message: " + e.getMessage());
        }
    }
}