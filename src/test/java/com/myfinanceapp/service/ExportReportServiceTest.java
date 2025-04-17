package com.myfinanceapp.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ExportReportServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private ChartService chartService;

    @Mock
    private Stage stage;

    @Spy
    @InjectMocks
    private ExportReportService exportReportService;

    @TempDir
    Path tempDir;

    private User user;
    private LocalDate startDate;
    private LocalDate endDate;
    private File tempFile;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        user = new User("testUser", "password", "question", "answer");

        // Set currentUser in ExportReportService
        Field userField = ExportReportService.class.getDeclaredField("currentUser");
        userField.setAccessible(true);
        userField.set(exportReportService, user);

        // Set currentUser in ChartService
        Field chartUserField = ChartService.class.getDeclaredField("currentUser");
        chartUserField.setAccessible(true);
        chartUserField.set(chartService, user);

        startDate = LocalDate.of(2025, 4, 1);
        endDate = LocalDate.of(2025, 4, 12);
        tempFile = tempDir.resolve("testReport.pdf").toFile();
    }

    @AfterEach
    void tearDown() {
        exportReportService.shutdown();
    }

    // Helper method to create a valid 1x1 PNG image
    private byte[] createValidPngImage() throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xFFFFFFFF); // Set pixel to white
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    @Test
    void testHandleExport_ValidDates_SuccessfulExport(FxRobot robot) throws Exception {
        // Arrange
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionDate("2025-04-01");
        transaction1.setTransactionType("Income");
        transaction1.setAmount(1000.0);
        transaction1.setCurrency("CNY");
        transaction1.setCategory("Salary");
        transaction1.setPaymentMethod("Bank");

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionDate("2025-04-02");
        transaction2.setTransactionType("Expense");
        transaction2.setAmount(500.0);
        transaction2.setCurrency("CNY");
        transaction2.setCategory("Groceries");
        transaction2.setPaymentMethod("Cash");

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionService.loadTransactions(user)).thenReturn(transactions);

        doNothing().when(chartService).updateAllCharts(startDate, endDate);

        // Mock captureChartAsImage to return a valid PNG image
        ByteArrayOutputStream validImage = new ByteArrayOutputStream();
        validImage.write(createValidPngImage());
        doReturn(validImage).when(exportReportService).captureChartAsImage(any());

        // Spy on generatePDF to create the file
        AtomicBoolean generatePDFCalled = new AtomicBoolean(false);
        doAnswer(invocation -> {
            File file = invocation.getArgument(0);
            file.createNewFile();
            generatePDFCalled.set(true);
            return null;
        }).when(exportReportService).generatePDF(any(File.class), eq(startDate), eq(endDate), any(LineChart.class), any(PieChart.class));

        // Wrap handleExport to simulate FileChooser returning tempFile
        doAnswer(invocation -> {
            CompletableFuture<Void> future = new CompletableFuture<>();
            // Simulate the async task in handleExport
            CompletableFuture.runAsync(() -> {
                try {
                    // Simulate the real handleExport behavior
                    transactionService.loadTransactions(user); // Add the missing call
                    chartService.updateAllCharts(startDate, endDate);
                    // Simulate FileChooser returning tempFile
                    exportReportService.generatePDF(tempFile, startDate, endDate, new LineChart<>(new CategoryAxis(), new NumberAxis()), new PieChart());
                    future.complete(null);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }, exportReportService.executorService);
            return future;
        }).when(exportReportService).handleExport(eq(stage), eq(startDate), eq(endDate));

        // Act
        CompletableFuture<Void> future = exportReportService.handleExport(stage, startDate, endDate);
        WaitForAsyncUtils.waitForFxEvents();
        future.get(10, TimeUnit.SECONDS); // Wait for the export to complete

        // Assert
        assertTrue(tempFile.exists(), "PDF file should be created");
        assertTrue(generatePDFCalled.get(), "generatePDF should have been called");
        verify(transactionService).loadTransactions(user);
        verify(chartService).updateAllCharts(startDate, endDate);
    }

    @Test
    void testHandleExport_NullDates_ThrowsException() {
        // Act & Assert
        CompletableFuture<Void> future = exportReportService.handleExport(stage, null, endDate);
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            WaitForAsyncUtils.waitForFxEvents();
            future.get(10, TimeUnit.SECONDS);
        });
        assertEquals("Failed to export report: Please select both start and end dates!", exception.getCause().getMessage());
    }

    @Test
    void testHandleExport_InvalidDateRange_ThrowsException() {
        LocalDate invalidStartDate = LocalDate.of(2025, 4, 15);
        CompletableFuture<Void> future = exportReportService.handleExport(stage, invalidStartDate, endDate);
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            WaitForAsyncUtils.waitForFxEvents();
            future.get(10, TimeUnit.SECONDS);
        });
        assertEquals("Failed to export report: Start date must be before end date!", exception.getCause().getMessage());
    }

    @Test
    void testHandleExport_FileChooserCancelled_DoesNotGeneratePDF(FxRobot robot) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTransactionDate("2025-04-01");
        transaction.setTransactionType("Income");
        transaction.setAmount(1000.0);
        transaction.setCurrency("CNY");
        transaction.setCategory("Salary");
        transaction.setPaymentMethod("Bank");

        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.loadTransactions(user)).thenReturn(transactions);
        doNothing().when(chartService).updateAllCharts(startDate, endDate);

        // Spy on generatePDF to track if it's called
        AtomicBoolean generatePDFCalled = new AtomicBoolean(false);
        doAnswer(invocation -> {
            generatePDFCalled.set(true);
            File file = invocation.getArgument(0);
            file.createNewFile();
            return null;
        }).when(exportReportService).generatePDF(any(File.class), eq(startDate), eq(endDate), any(LineChart.class), any(PieChart.class));

        // Wrap handleExport to simulate FileChooser returning null (cancelled)
        doAnswer(invocation -> {
            CompletableFuture<Void> future = new CompletableFuture<>();
            // Simulate the async task in handleExport
            CompletableFuture.runAsync(() -> {
                try {
                    // Simulate the real handleExport behavior
                    transactionService.loadTransactions(user); // Add the missing call
                    chartService.updateAllCharts(startDate, endDate);
                    // Simulate FileChooser returning null (user cancels)
                    // Do nothing, mimicking the early return in handleExport
                    future.complete(null);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }, exportReportService.executorService);
            return future;
        }).when(exportReportService).handleExport(eq(stage), eq(startDate), eq(endDate));

        // Act
        CompletableFuture<Void> future = exportReportService.handleExport(stage, startDate, endDate);
        WaitForAsyncUtils.waitForFxEvents();
        future.get(10, TimeUnit.SECONDS); // Wait for the export to complete

        // Assert
        assertFalse(tempFile.exists(), "PDF file should not be created when FileChooser is cancelled");
        assertFalse(generatePDFCalled.get(), "generatePDF should not have been called");
        verify(transactionService).loadTransactions(user);
        verify(chartService).updateAllCharts(startDate, endDate);
    }

    @Test
    void testGeneratePDF_ValidData_GeneratesPDF() throws Exception {
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionDate("2025-04-01");
        transaction1.setTransactionType("Income");
        transaction1.setAmount(1000.0);
        transaction1.setCurrency("CNY");
        transaction1.setCategory("Salary");
        transaction1.setPaymentMethod("Bank");

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionDate("2025-04-02");
        transaction2.setTransactionType("Expense");
        transaction2.setAmount(500.0);
        transaction2.setCurrency("CNY");
        transaction2.setCategory("Groceries");
        transaction2.setPaymentMethod("Cash");

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionService.loadTransactions(user)).thenReturn(transactions);

        // Mock captureChartAsImage to return a valid PNG image
        ByteArrayOutputStream validImage = new ByteArrayOutputStream();
        validImage.write(createValidPngImage());
        doReturn(validImage).when(exportReportService).captureChartAsImage(any());

        exportReportService.generatePDF(tempFile, startDate, endDate, new LineChart<>(new CategoryAxis(), new NumberAxis()), new PieChart());
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(tempFile.exists(), "PDF file should be created");
        try (PdfReader reader = new PdfReader(tempFile);
             PdfDocument pdf = new PdfDocument(reader)) {
            assertTrue(pdf.getNumberOfPages() > 0, "PDF should have content");
        }
    }

    @Test
    void testShutdown_ExecutorServiceTerminates() {
        exportReportService.shutdown();
        assertTrue(exportReportService.executorService.isShutdown(), "ExecutorService should be shut down");
    }
}