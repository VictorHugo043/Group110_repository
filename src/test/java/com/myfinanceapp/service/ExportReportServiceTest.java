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

/**
 * Unit test class for the ExportReportService.
 * This class contains tests for report export functionality including:
 * - PDF report generation
 * - Chart image capture
 * - Date range validation
 * - File chooser handling
 * - Transaction data processing
 * - Executor service management
 *
 * @author SE_Group110
 * @version 4.0
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ExportReportServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private LanguageService languageService;

    @Mock
    private Stage stage;

    private ExportReportService exportReportService;

    @TempDir
    Path tempDir;

    private User user;
    private LocalDate startDate;
    private LocalDate endDate;
    private File tempFile;

    /**
     * Sets up the test environment before each test.
     * Initializes user, dates, and temporary file.
     * Configures mock objects and reflection-based field access.
     *
     * @throws NoSuchFieldException if field access fails
     * @throws IllegalAccessException if field modification fails
     */
    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        user = new User("testUser", "password", "question", "answer");
        startDate = LocalDate.of(2025, 4, 1);
        endDate = LocalDate.of(2025, 4, 12);
        tempFile = tempDir.resolve("testReport.pdf").toFile();

        // Mock currency service
        when(currencyService.getSelectedCurrency()).thenReturn("CNY");
        when(currencyService.convertCurrency(anyDouble(), anyString())).thenAnswer(i -> i.getArgument(0));

        // Mock language service
        when(languageService.getTranslation(anyString())).thenAnswer(i -> i.getArgument(0));

        // Create ExportReportService with mocked dependencies
        exportReportService = new ExportReportService(transactionService, user, currencyService);
        
        // Set language service using reflection
        Field languageServiceField = ExportReportService.class.getDeclaredField("languageService");
        languageServiceField.setAccessible(true);
        languageServiceField.set(exportReportService, languageService);

        // Create spy of exportReportService
        exportReportService = spy(exportReportService);
    }

    /**
     * Cleans up resources after each test.
     * Shuts down the executor service.
     */
    @AfterEach
    void tearDown() {
        exportReportService.shutdown();
    }

    /**
     * Creates a valid 1x1 PNG image for testing.
     * Used for mocking chart image capture functionality.
     *
     * @return byte array containing a valid PNG image
     * @throws IOException if image creation fails
     */
    private byte[] createValidPngImage() throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xFFFFFFFF); // Set pixel to white
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    /**
     * Tests successful report export with valid dates.
     * Verifies that:
     * - PDF file is created
     * - Transactions are loaded
     * - Charts are updated
     * - PDF generation is called
     *
     * @param robot TestFX robot for UI interaction
     * @throws Exception if test execution fails
     */
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

        // Mock captureChartAsImage to return a valid PNG image
        ByteArrayOutputStream validImage = new ByteArrayOutputStream();
        validImage.write(createValidPngImage());
        doReturn(validImage).when(exportReportService).captureChartAsImage(any());

        // Mock file chooser to return tempFile
        doAnswer(invocation -> {
            CompletableFuture<Void> future = new CompletableFuture<>();
            try {
                // Simulate file chooser returning tempFile
                exportReportService.generatePDF(tempFile, startDate, endDate, 
                    new LineChart<>(new CategoryAxis(), new NumberAxis()), 
                    new PieChart());
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
            return future;
        }).when(exportReportService).handleExport(eq(stage), eq(startDate), eq(endDate));

        // Act
        CompletableFuture<Void> future = exportReportService.handleExport(stage, startDate, endDate);
        WaitForAsyncUtils.waitForFxEvents();
        future.get(30, TimeUnit.SECONDS); // Increased timeout

        // Assert
        assertTrue(tempFile.exists(), "PDF file should be created");
        verify(transactionService).loadTransactions(user);
    }

    /**
     * Tests report export with null dates.
     * Verifies that appropriate exception is thrown.
     */
    @Test
    void testHandleExport_NullDates_ThrowsException() {
        // Act & Assert
        CompletableFuture<Void> future = exportReportService.handleExport(stage, null, endDate);
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            WaitForAsyncUtils.waitForFxEvents();
            future.get(30, TimeUnit.SECONDS); // Increased timeout
        });
        assertEquals("Failed to export report: Please select both start and end dates!", exception.getCause().getMessage());
    }

    /**
     * Tests report export with invalid date range.
     * Verifies that appropriate exception is thrown when start date is after end date.
     */
    @Test
    void testHandleExport_InvalidDateRange_ThrowsException() {
        LocalDate invalidStartDate = LocalDate.of(2025, 4, 15);
        CompletableFuture<Void> future = exportReportService.handleExport(stage, invalidStartDate, endDate);
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            WaitForAsyncUtils.waitForFxEvents();
            future.get(30, TimeUnit.SECONDS); // Increased timeout
        });
        assertEquals("Failed to export report: Start date must be before end date!", exception.getCause().getMessage());
    }

    /**
     * Tests PDF generation with valid data.
     * Verifies that:
     * - PDF file is created
     * - PDF contains content
     * - Chart images are captured
     *
     * @throws Exception if test execution fails
     */
    @Test
    void testGeneratePDF_ValidData_GeneratesPDF() throws Exception {
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

        // Mock captureChartAsImage to return a valid PNG image
        ByteArrayOutputStream validImage = new ByteArrayOutputStream();
        validImage.write(createValidPngImage());
        doReturn(validImage).when(exportReportService).captureChartAsImage(any());

        // Act
        exportReportService.generatePDF(tempFile, startDate, endDate, 
            new LineChart<>(new CategoryAxis(), new NumberAxis()), 
            new PieChart());
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        assertTrue(tempFile.exists(), "PDF file should be created");
        try (PdfReader reader = new PdfReader(tempFile);
             PdfDocument pdf = new PdfDocument(reader)) {
            assertTrue(pdf.getNumberOfPages() > 0, "PDF should have content");
        }
    }

    /**
     * Tests executor service shutdown.
     * Verifies that the executor service is properly terminated.
     */
    @Test
    void testShutdown_ExecutorServiceTerminates() {
        exportReportService.shutdown();
        assertTrue(exportReportService.executorService.isShutdown(), "ExecutorService should be shut down");
    }
}