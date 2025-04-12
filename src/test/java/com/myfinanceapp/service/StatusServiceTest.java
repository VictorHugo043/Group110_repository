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

    private StatusService statusService;
    private StatusScene statusScene;
    private User testUser;

    @BeforeAll
    static void setupJavaFX() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
    }

    @BeforeEach
    void setUp() throws Exception {
        testUser = new User("test-uid", "testUser", "password", "question", "answer");

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = FxToolkit.registerPrimaryStage();
                double width = 800.0;
                double height = 600.0;

                // 初始化 StatusScene 并手动设置字段
                statusScene = new StatusScene(stage, width, height, testUser);
                initializeStatusSceneFields(statusScene);

                // 设置 Scene 并绑定到 Stage
                Scene scene = new Scene(new Pane(), width, height); // 临时根节点
                stage.setScene(scene);

                // 创建 StatusService
                statusService = new StatusService(statusScene, testUser);

                // 使用反射注入 mock 对象
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

    // 手动初始化 StatusScene 的字段
    private void initializeStatusSceneFields(StatusScene statusScene) {
        statusScene.lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        statusScene.barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        statusScene.pieChart = new PieChart();
        statusScene.exLabel = new Label();
        statusScene.inLabel = new Label();
        statusScene.transactionsBox = new VBox();
        statusScene.questionArea = new TextArea();
        statusScene.suggestionsArea = new TextArea();
        statusScene.startDatePicker = new javafx.scene.control.DatePicker(); // 替换 dateCombo
        statusScene.endDatePicker = new javafx.scene.control.DatePicker();   // 替换 dateCombo
        statusScene.chartTypeCombo = new ComboBox<>();
        statusScene.chartPane = new javafx.scene.layout.StackPane();
        statusScene.sendBtn = new Button();
    }

    @Test
    void testInitialize_setsDefaultDatePickerValues() {
        LocalDate today = LocalDate.now();
        LocalDate expectedStartDate = today.withDayOfMonth(1);
        LocalDate expectedEndDate = today;

        assertEquals(expectedStartDate, statusScene.startDatePicker.getValue());
        assertEquals(expectedEndDate, statusScene.endDatePicker.getValue());
    }

    @Test
    void testUpdateSummaryLabels_ThisMonth() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setTransactionDate("2025-04-01");
        tx1.setTransactionType("Income");
        tx1.setAmount(1000.0);
        Transaction tx2 = new Transaction();
        tx2.setTransactionDate("2025-04-02");
        tx2.setTransactionType("Expense");
        tx2.setAmount(500.0);
        transactions.add(tx1);
        transactions.add(tx2);

        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        statusService.updateSummaryLabels(startDate, endDate);

        assertEquals("Ex.  500.00 CNY", statusScene.exLabel.getText());
        assertEquals("In.  1000.00 CNY", statusScene.inLabel.getText());
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
            assertFalse(statusScene.suggestionsArea.getText().isEmpty(), "Suggestions area should contain a response");
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
        transactions.add(tx1);

        when(transactionService.loadTransactions(testUser)).thenReturn(transactions);

        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 31);

        Platform.runLater(() -> {
            // 直接改变值以触发 valueProperty 监听器
            statusScene.startDatePicker.setValue(startDate);
            statusScene.endDatePicker.setValue(endDate);
        });

        FxToolkit.setupFixture(() -> {
            assertEquals("Ex.  0.00 CNY", statusScene.exLabel.getText());
            assertEquals("In.  2000.00 CNY", statusScene.inLabel.getText());
            verify(chartService).updateAllCharts(startDate, endDate);
        });
    }

    @Test
    void testChartTypeComboAction_LineGraph() throws TimeoutException {
        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 4, 12);

        Platform.runLater(() -> {
            statusScene.chartTypeCombo.setValue("Line graph");
            statusScene.chartTypeCombo.getOnAction().handle(null); // 触发事件
        });

        FxToolkit.setupFixture(() -> {
            assertTrue(statusScene.chartPane.getChildren().contains(statusScene.lineChart));
            assertFalse(statusScene.chartPane.getChildren().contains(statusScene.barChart));
            verify(chartService).updateAllCharts(startDate, endDate);
        });
    }
}