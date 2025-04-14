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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service class for handling financial report export functionality.
 */
public class ExportReportService {

    private final TransactionService txService;
    private final User currentUser;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ExportReportService(TransactionService txService, User currentUser) {
        this.txService = txService;
        this.currentUser = currentUser;
    }

    /**
     * Handles the export process: validates dates, generates charts, and saves PDF.
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
                ChartService chartService = new ChartService(lineChart, barChart, pieChart, txService, currentUser);
                chartService.updateAllCharts(startDate, endDate);

                // Generate PDF only if file is selected
                generatePDF(file, startDate, endDate, lineChart, pieChart);
            } catch (Exception e) {
                System.err.println("Error during export: ");
                e.printStackTrace();
                throw new RuntimeException("Failed to export report: " + e.getMessage(), e);
            } finally {
                // Shut down the executor after each export
                shutdownExecutor();
            }
        }, executorService);
    }

    /**
     * Generates the PDF report with charts, summary, and transaction details.
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
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double totalExpense = transactions.stream()
                    .filter(t -> "Expense".equals(t.getTransactionType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            Map<String, Double> expenseByCategory = transactions.stream()
                    .filter(t -> "Expense".equals(t.getTransactionType()))
                    .collect(Collectors.groupingBy(
                            Transaction::getCategory,
                            Collectors.summingDouble(Transaction::getAmount)
                    ));

            String topCategory = expenseByCategory.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            document.add(new Paragraph("\n3. Financial Summary").setFontSize(14).setBold());
            document.add(new Paragraph(String.format("Total Income: %.2f CNY", totalIncome)));
            document.add(new Paragraph(String.format("Total Expense: %.2f CNY", totalExpense)));
            document.add(new Paragraph(String.format("Net Balance: %.2f CNY", totalIncome - totalExpense)));
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
                table.addCell(t.getTransactionDate());
                table.addCell(t.getTransactionType());
                table.addCell(String.format("%.2f", t.getAmount()));
                table.addCell(t.getCurrency());
                table.addCell(t.getCategory());
                table.addCell(t.getPaymentMethod());
            }

            document.add(table);
        }
    }

    /**
     * Captures a JavaFX chart as an image for PDF inclusion.
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
     * Shuts down the executor service after each export.
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
     * Shuts down the executor service when the service is no longer needed.
     */
    public void shutdown() {
        shutdownExecutor();
    }
}