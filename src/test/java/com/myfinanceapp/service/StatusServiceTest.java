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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
public class StatusServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private ChartService chartService;

    @Mock
    private CurrencyService currencyService;

    private StatusService statusService;
    private StatusScene statusScene;
    private User testUser;

    @BeforeAll
    static void setupJavaFX() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
    }

    @BeforeEach
    void setUp() throws Exception {
        testUser = new User("test-uid", "testUser", "password", "question", "answer", null);

        when(currencyService.getSelectedCurrency()).thenReturn("USD");
        when(currencyService.convertCurrency(anyDouble(), anyString())).thenAnswer(invocation ->
                invocation.getArgument(0)); // Return same amount for simplicity

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = FxToolkit.registerPrimaryStage();
                double width = 800.0;
                double height = 600.0;

                // Initialize StatusScene and set fields
                statusScene = new StatusScene(stage, width, height, testUser);
                initializeStatusSceneFields(statusScene);

                // Set Scene and bind to Stage
                Scene scene = new Scene(new Pane(), width, height);
                stage.setScene(scene);

                // Create StatusService
                statusService = new StatusService(statusScene, testUser, currencyService);

                // Inject mock objects using reflection
                java.lang.reflect.Field txField = StatusService.class.getDeclaredField("txService");
                txField.setAccessible(true);
                txField.set(statusService, transactionService);

                java.lang.reflect.Field chartField = StatusService.class.getDeclaredField("chartService");
                chartField.setAccessible(true);
                chartField.set(statusService, chartService);

                latch.countDown();
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize StatusScene", e);
            }
        });

        latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
        if (statusService == null) {
            throw new RuntimeException("StatusService initialization failed");
        }
    }

    // Manually initialize StatusScene fields
    private void initializeStatusSceneFields(StatusScene statusScene) {
        statusScene.lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        statusScene.barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        statusScene.pieChart = new PieChart();
        statusScene.exLabel = new Label();
        statusScene.inLabel = new Label();
        statusScene.transactionsBox = new VBox();
        statusScene.questionArea = new TextArea();
        statusScene.suggestionsWebView = new javafx.scene.web.WebView();
        statusScene.startDatePicker = new javafx.scene.control.DatePicker();
        statusScene.endDatePicker = new javafx.scene.control.DatePicker();
        statusScene.chartTypeCombo = new ComboBox<>();
        statusScene.chartPane = new javafx.scene.layout.StackPane();
        statusScene.sendBtn = new Button();
        statusScene.themeService = mock(ThemeService.class);
        when(statusScene.themeService.isDayMode()).thenReturn(true);
    }

    @Test
    void testInitialize_setsDefaultDatePickerValues() {
        LocalDate today = LocalDate.now();
        LocalDate expectedStartDate = today.withDayOfMonth(1);
        LocalDate expectedEndDate = today;

        assertEquals(expectedStartDate, statusScene.startDatePicker.getValue());
        assertEquals(expectedEndDate, statusScene.endDatePicker.getValue());
        verify(chartService).updateAllCharts(expectedStartDate, expectedEndDate);
    }

    @Test
    void testUpdateSummaryLabels_ThisMonth() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setTransactionDate("2025-04-01");
        tx1.setTransactionType("Income");
        tx1.setAmount(1000.0);
        tx1.setCurrency("USD");
        Transaction tx2 = new Transaction();
        tx2.setTransactionDate("2025-04-02");
        tx2.setTransactionType("Expense");
        tx2.setAmount(500.0);
        tx2.setCurrency("USD");
        transactions.add(tx1);
        transactions.add(tx2);

        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);
        when(currencyService.getSelectedCurrency()).thenReturn("USD");

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        statusService.updateSummaryLabels(startDate, endDate);

        assertEquals("Ex.  500.00 USD", statusScene.exLabel.getText());
        assertEquals("In.  1000.00 USD", statusScene.inLabel.getText());
        assertEquals("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-border-radius: 30; -fx-background-radius: 30; -fx-padding: 10 20 10 20;",
                statusScene.exLabel.getStyle());
    }

    @Test
    void testUpdateTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setTransactionDate("2025-04-01");
        tx1.setCategory("Salary");
        tx1.setAmount(1000.0);
        tx1.setCurrency("USD");
        transactions.add(tx1);

        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);
        when(currencyService.getSelectedCurrency()).thenReturn("USD");

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        statusService.updateTransactions(startDate, endDate);

        assertFalse(statusScene.transactionsBox.getChildren().isEmpty());
        assertEquals(1, statusScene.transactionsBox.getChildren().size());
        Label txLabel = (Label) statusScene.transactionsBox.getChildren().get(0);
        assertEquals("2025-04-01   Salary    1000.00 USD", txLabel.getText());
    }

    @Test
    void testHandleAIRequest_Success() throws TimeoutException {
        List<Transaction> transactions = new ArrayList<>();
        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);
        mockStatic(AiChatService.class);
        when(AiChatService.chatCompletion(anyList(), anyString())).thenReturn("Some response");

        Platform.runLater(() -> {
            statusScene.questionArea.setText("How much did I spend?");
            statusService.handleAIRequest();
        });

        FxToolkit.setupFixture(() -> {
            assertFalse(statusScene.suggestionsWebView.getEngine().getDocument().getDocumentElement().getTextContent().isEmpty(),
                    "Suggestions area should contain a response");
            assertTrue(statusScene.questionArea.getText().isEmpty(), "Question area should be cleared");
            assertFalse(statusScene.questionArea.isDisable(), "Question area should be enabled");
            assertFalse(statusScene.sendBtn.isDisable(), "Send button should be enabled");
        });
    }

    @Test
    void testHandleAIRequest_Failure() throws TimeoutException {
        List<Transaction> transactions = new ArrayList<>();
        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);
        mockStatic(AiChatService.class);
        when(AiChatService.chatCompletion(anyList(), anyString())).thenReturn(null);

        Platform.runLater(() -> {
            statusScene.questionArea.setText("How much did I spend?");
            statusService.handleAIRequest();
        });

        FxToolkit.setupFixture(() -> {
            assertTrue(statusScene.suggestionsWebView.getEngine().getDocument().getDocumentElement().getTextContent()
                    .contains("AI 请求失败"), "Suggestions area should show failure message");
            assertTrue(statusScene.questionArea.getText().isEmpty(), "Question area should be cleared");
            assertFalse(statusScene.questionArea.isDisable(), "Question area should be enabled");
            assertFalse(statusScene.sendBtn.isDisable(), "Send button should be enabled");
        });
    }

    @Test
    void testDatePickerAction() throws TimeoutException {
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setTransactionDate("2025-03-01");
        tx1.setTransactionType("Income");
        tx1.setAmount(2000.0);
        tx1.setCurrency("USD");
        transactions.add(tx1);

        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);
        when(currencyService.getSelectedCurrency()).thenReturn("USD");

        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 31);

        Platform.runLater(() -> {
            statusScene.startDatePicker.setValue(startDate);
            statusScene.endDatePicker.setValue(endDate);
        });

        FxToolkit.setupFixture(() -> {
            assertEquals("Ex.  0.00 USD", statusScene.exLabel.getText());
            assertEquals("In.  2000.00 USD", statusScene.inLabel.getText());
            verify(chartService).updateAllCharts(startDate, endDate);
        });
    }

    @Test
    void testDatePickerAction_InvalidDateRange() throws TimeoutException {
        List<Transaction> transactions = new ArrayList<>();
        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);

        LocalDate invalidStartDate = LocalDate.of(2025, 4, 15);
        LocalDate endDate = LocalDate.of(2025, 4, 1);

        Platform.runLater(() -> {
            statusScene.endDatePicker.setValue(endDate);
            statusScene.startDatePicker.setValue(invalidStartDate);
        });

        FxToolkit.setupFixture(() -> {
            assertEquals(endDate, statusScene.startDatePicker.getValue(), "Start date should be reset to previous valid value");
        });
    }

    @Test
    void testChartTypeComboAction_LineGraph() throws TimeoutException {
        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        Platform.runLater(() -> {
            statusScene.chartTypeCombo.setValue("Line graph");
            statusScene.chartTypeCombo.getOnAction().handle(null);
        });

        FxToolkit.setupFixture(() -> {
            assertTrue(statusScene.chartPane.getChildren().contains(statusScene.lineChart));
            assertFalse(statusScene.chartPane.getChildren().contains(statusScene.barChart));
            verify(chartService).updateAllCharts(startDate, endDate);
        });
    }

    @Test
    void testChartTypeComboAction_BarGraph() throws TimeoutException {
        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        Platform.runLater(() -> {
            statusScene.chartTypeCombo.setValue("Bar graph");
            statusScene.chartTypeCombo.getOnAction().handle(null);
        });

        FxToolkit.setupFixture(() -> {
            assertFalse(statusScene.chartPane.getChildren().contains(statusScene.lineChart));
            assertTrue(statusScene.chartPane.getChildren().contains(statusScene.barChart));
            verify(chartService).updateAllCharts(startDate, endDate);
        });
    }

    @Test
    void testInitializeWelcomeMessage() {
        String expectedMessage = "Welcome to use the financial assistant. Please feel free to ask any financial questions you have.";
        assertFalse(statusScene.suggestionsWebView.getEngine().getDocument().getDocumentElement().getTextContent().isEmpty());
        assertTrue(statusScene.chatHistory.stream().anyMatch(msg ->
                msg.get("role").equals("assistant") && msg.get("content").equals(expectedMessage)));
    }
}