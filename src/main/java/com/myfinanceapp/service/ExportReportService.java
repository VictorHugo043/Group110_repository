package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.myfinanceapp.security.EncryptionService;
import com.myfinanceapp.security.EncryptionService.EncryptedData;
import com.myfinanceapp.security.EncryptionService.EncryptionException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

/**
 * Service class for handling financial report export functionality.
 * This class provides comprehensive functionality for generating and exporting
 * financial reports in PDF format, including:
 * - Transaction summaries
 * - Financial charts and visualizations
 * - Detailed transaction listings
 * - Currency conversion
 * - Secure data handling
 */
public class ExportReportService {

    private final TransactionService txService;
    private final User currentUser;
    private final CurrencyService currencyService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Gson gson = new Gson();
    private static final String FIXED_KEY = "MyFinanceAppSecretKey1234567890";  // Fixed encryption key
    final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Constructs a new ExportReportService instance.
     *
     * @param txService Service for handling transaction data
     * @param currentUser The user for whom to generate reports
     * @param currencyService Service for handling currency conversions
     */
    public ExportReportService(TransactionService txService, User currentUser, CurrencyService currencyService) {
        this.txService = txService;
        this.currentUser = currentUser;
        this.currencyService = currencyService;
    }

    /**
     * Derives an encryption key for the current user's data.
     * Uses a fixed key and the user's ID to create a unique encryption key.
     *
     * @return A SecretKey for encrypting/decrypting the user's data
     * @throws EncryptionException If there is an error deriving the key
     */
    private SecretKey getEncryptionKey() throws EncryptionException {
        byte[] salt = currentUser.getUid().getBytes();
        return EncryptionService.deriveKey(FIXED_KEY, salt);
    }

    /**
     * Loads and decrypts transaction data for the current user.
     *
     * @return A list of transactions for the current user
     * @throws IOException If there is an error reading the transaction file
     * @throws EncryptionException If there is an error decrypting the data
     */
    private List<Transaction> loadTransactions() throws IOException, EncryptionException {
        String filePath = "src/main/resources/transaction/" + currentUser.getUid() + ".json";
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

        // Parse encrypted data from JSON
        EncryptedData encryptedData = gson.fromJson(content, EncryptedData.class);

        // Get encryption key derived from user ID
        SecretKey key = getEncryptionKey();

        // Decrypt the content
        String decryptedContent = EncryptionService.decrypt(encryptedData, key);

        // Parse the decrypted content into List<Transaction>
        Type listType = new TypeToken<List<Transaction>>() {}.getType();
        return gson.fromJson(decryptedContent, listType);
    }

    /**
     * Handles the export process for financial reports.
     * This method:
     * - Validates date ranges
     * - Opens a file chooser for saving the report
     * - Generates charts and visualizations
     * - Creates and saves the PDF report
     *
     * @param stage The JavaFX stage for showing the file chooser
     * @param startDate The start date for the report period
     * @param endDate The end date for the report period
     * @return A CompletableFuture that completes when the export is finished
     * @throws IllegalArgumentException If the date range is invalid
     * @throws RuntimeException If there is an error during export
     */
    public CompletableFuture<Void> handleExport(Stage stage, LocalDate startDate, LocalDate endDate) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Please select both start and end dates!");
                }
                if (startDate.isAfter(endDate)) {
                    throw new IllegalArgumentException("Start date must be before end date!");
                }

                // Open file chooser on JavaFX thread
                File[] fileHolder = new File[1];
                Platform.runLater(() -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Financial Report");
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
                    // Set default filename: FinancialReport_Username_StartDate_EndDate.pdf
                    String username = currentUser.getUsername();
                    String startDateStr = startDate.format(FILE_DATE_FORMATTER);
                    String endDateStr = endDate.format(FILE_DATE_FORMATTER);
                    String defaultFileName = String.format("FinancialReport_%s_%s_%s.pdf", username, startDateStr, endDateStr);
                    fileChooser.setInitialFileName(defaultFileName);
                    fileHolder[0] = fileChooser.showSaveDialog(stage);
                    synchronized (fileHolder) {
                        fileHolder.notify();
                    }
                });

                // Wait for file chooser result
                synchronized (fileHolder) {
                    if (fileHolder[0] == null) {
                        fileHolder.wait();
                    }
                }

                File file = fileHolder[0];
                if (file == null) {
                    return; // Exit early if user cancels the file chooser
                }

                // Initialize charts for ChartService only if file is selected
                LineChart<String, Number> lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
                BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
                PieChart pieChart = new PieChart();
                ChartService chartService = new ChartService(lineChart, barChart, pieChart, txService, currentUser, currencyService);
                chartService.updateAllCharts(startDate, endDate);

                // Generate PDF only if file is selected
                generatePDF(file, startDate, endDate, lineChart, pieChart);
            } catch (Exception e) {
                System.err.println("Error during export: ");
                e.printStackTrace();
                throw new RuntimeException("Failed to export report: " + e.getMessage(), e);
            }
        }, executorService);
    }

    /**
     * Generates a PDF report containing financial data, charts, and transaction details.
     * The report includes:
     * - Title and user information
     * - Date range
     * - Income and expense trends
     * - Expense category breakdown
     * - Financial summary
     * - Detailed transaction listing
     *
     * @param file The file to save the PDF to
     * @param startDate The start date for the report period
     * @param endDate The end date for the report period
     * @param lineChart The chart showing income/expense trends
     * @param pieChart The chart showing expense categories
     * @throws Exception If there is an error generating the PDF
     */
    void generatePDF(File file, LocalDate startDate, LocalDate endDate,
                     LineChart<String, Number> lineChart, PieChart pieChart) throws Exception {
        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            document.add(new Paragraph("Financial Report")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            // Add username
            document.add(new Paragraph("User: " + currentUser.getUsername())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));
            // Date range
            document.add(new Paragraph(String.format("Date Range: %s to %s", startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER)))
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            // 1. Visualizations
            document.add(new Paragraph("\n1. Income and Expense Trend").setFontSize(14).setBold());
            ByteArrayOutputStream lineChartImage = captureChartAsImage(lineChart);
            document.add(new Image(ImageDataFactory.create(lineChartImage.toByteArray()))
                    .setWidth(500)
                    .setAutoScaleHeight(true));

            document.add(new Paragraph("\n2. Expense by Category").setFontSize(14).setBold());
            ByteArrayOutputStream pieChartImage = captureChartAsImage(pieChart);
            document.add(new Image(ImageDataFactory.create(pieChartImage.toByteArray()))
                    .setWidth(300)
                    .setAutoScaleHeight(true));

            // 2. Summary
            List<Transaction> transactions = txService.loadTransactions(currentUser).stream()
                    .filter(t -> {
                        LocalDate txDate = LocalDate.parse(t.getTransactionDate(), DATE_FORMATTER);
                        return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
                    })
                    .collect(Collectors.toList());

            double totalIncome = transactions.stream()
                    .filter(t -> "Income".equals(t.getTransactionType()))
                    .mapToDouble(t -> currencyService.convertCurrency(t.getAmount(), t.getCurrency()))
                    .sum();
            double totalExpense = transactions.stream()
                    .filter(t -> "Expense".equals(t.getTransactionType()))
                    .mapToDouble(t -> currencyService.convertCurrency(t.getAmount(), t.getCurrency()))
                    .sum();

            Map<String, Double> expenseByCategory = transactions.stream()
                    .filter(t -> "Expense".equals(t.getTransactionType()))
                    .collect(Collectors.groupingBy(
                            Transaction::getCategory,
                            Collectors.summingDouble(t -> currencyService.convertCurrency(t.getAmount(), t.getCurrency()))
                    ));

            String topCategory = expenseByCategory.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            String currencySymbol = currencyService.getSelectedCurrency();
            document.add(new Paragraph("\n3. Financial Summary").setFontSize(14).setBold());
            document.add(new Paragraph(String.format("Total Income: %.2f %s", totalIncome, currencySymbol)));
            document.add(new Paragraph(String.format("Total Expense: %.2f %s", totalExpense, currencySymbol)));
            document.add(new Paragraph(String.format("Net Balance: %.2f %s", totalIncome - totalExpense, currencySymbol)));
            document.add(new Paragraph(String.format("Top Expense Category: %s", topCategory)));

            // 3. Transaction Details
            document.add(new Paragraph("\n4. Transaction Details").setFontSize(14).setBold());
            Table table = new Table(new float[]{100, 80, 80, 80, 100, 100});
            table.addHeaderCell("Date");
            table.addHeaderCell("Type");
            table.addHeaderCell("Amount");
            table.addHeaderCell("Currency");
            table.addHeaderCell("Category");
            table.addHeaderCell("Payment Method");

            for (Transaction t : transactions) {
                double convertedAmount = currencyService.convertCurrency(t.getAmount(), t.getCurrency());
                table.addCell(t.getTransactionDate());
                table.addCell(t.getTransactionType());
                table.addCell(String.format("%.2f %s", convertedAmount, currencySymbol));
                table.addCell(t.getCurrency());
                table.addCell(t.getCategory());
                table.addCell(t.getPaymentMethod());
            }

            document.add(table);
        }
    }

    /**
     * Captures a JavaFX chart as an image for inclusion in the PDF report.
     *
     * @param chart The JavaFX chart to capture
     * @return A ByteArrayOutputStream containing the chart image
     * @throws Exception If there is an error capturing the chart
     */
    ByteArrayOutputStream captureChartAsImage(javafx.scene.Node chart) throws Exception {
        // Ensure chart has a size for rendering
        chart.setStyle("-fx-background-color: white;");
        if (chart instanceof Chart) {
            ((Chart) chart).setAnimated(false);
        }

        // Cast to Region to set size
        if (!(chart instanceof Region)) {
            throw new IllegalArgumentException("Chart must be a Region subclass");
        }
        ((Region) chart).setPrefWidth(600);
        ((Region) chart).setPrefHeight(400);

        // Create a temporary scene to force rendering
        Pane tempPane = new Pane(chart);
        Scene tempScene = new Scene(tempPane, 600, 400);

        // Take snapshot on JavaFX thread
        CompletableFuture<ByteArrayOutputStream> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                // Force layout
                tempPane.layout();

                // Small delay to ensure rendering (especially for PieChart)
                Thread.sleep(100); // Wait for rendering to complete

                SnapshotParameters params = new SnapshotParameters();
                WritableImage image = chart.snapshot(params, null);
                if (image == null) {
                    throw new IllegalStateException("Failed to capture chart snapshot");
                }

                // Convert to PNG
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", baos);
                future.complete(baos);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        // Wait for the snapshot to complete
        try {
            return future.get();
        } catch (Exception e) {
            System.err.println("Error capturing chart image: ");
            e.printStackTrace();
            throw new Exception("Failed to capture chart image: " + e.getMessage(), e);
        }
    }

    /**
     * Shuts down the executor service used for asynchronous operations.
     * Waits for pending tasks to complete before shutting down.
     */
    private void shutdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Shuts down the service and its resources.
     * Should be called when the service is no longer needed.
     */
    public void shutdown() {
        shutdownExecutor();
    }
}