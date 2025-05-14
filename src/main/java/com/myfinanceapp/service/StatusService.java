package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.model.Goal;
import com.myfinanceapp.ui.statusscene.StatusScene;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.DateCell;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;
import javafx.application.Platform;

/**
 * Service class for managing the financial status dashboard of the application.
 * This class handles the display and interaction of financial data including:
 * - Transaction summaries
 * - Financial charts and graphs
 * - Date range selection
 * - AI-powered financial analysis
 * - Real-time data updates
 *
 * The service integrates multiple components including transaction data,
 * financial goals, currency conversion, and AI chat functionality to provide
 * a comprehensive financial overview.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class StatusService {
    private final User currentUser;
    private final TransactionService txService;
    private final GoalService goalService;
    private final List<Map<String, String>> chatMessages = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private final StatusScene scene;
    private final ChartService chartService;
    private final Parser mdParser = Parser.builder().build();
    private final HtmlRenderer mdRenderer = HtmlRenderer.builder().build();
    private final CurrencyService currencyService;
    private final LanguageService languageService;

    /**
     * Constructs a new StatusService instance.
     *
     * @param scene The StatusScene instance to manage
     * @param currentUser The currently logged-in user
     * @param currencyService Service for handling currency conversions
     * @param languageService Service for handling internationalization
     */
    public StatusService(StatusScene scene, User currentUser, CurrencyService currencyService, LanguageService languageService) {
        this.scene = scene;
        this.currentUser = currentUser;
        this.txService = new TransactionService();
        this.goalService = new GoalService();
        this.currencyService = currencyService;
        this.languageService = languageService;
        this.chartService = new ChartService(scene.lineChart, scene.barChart, scene.pieChart, txService, currentUser, currencyService);
        initialize();
    }

    /**
     * Initializes the status dashboard with default settings and event handlers.
     * Sets up date pickers, initializes charts, and establishes event listeners
     * for user interactions.
     */
    void initialize() {
        // Initialize dates from the first day of current month to today
        LocalDate today = LocalDate.now();
        startDate = today.withDayOfMonth(1);
        endDate = today;

        // Set DatePicker default values
        scene.startDatePicker.setValue(startDate);
        scene.endDatePicker.setValue(endDate);

        // Restrict DatePicker to prevent future date selection
        scene.startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(today));
            }
        });
        scene.endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(today));
            }
        });

        // Initialize data
        updateSummaryLabels(startDate, endDate);
        chartService.updateAllCharts(startDate, endDate);
        updateTransactions(startDate, endDate);

        // Bind date selection events
        ChangeListener<LocalDate> dateChangeListener = (obs, oldValue, newValue) -> {
            if (scene.startDatePicker.getValue() != null && scene.endDatePicker.getValue() != null) {
                startDate = scene.startDatePicker.getValue();
                endDate = scene.endDatePicker.getValue();
                if (!startDate.isAfter(endDate)) {
                    updateSummaryLabels(startDate, endDate);
                    chartService.updateAllCharts(startDate, endDate);
                    updateTransactions(startDate, endDate);
                } else {
                    // If start date is after end date, reset to old value
                    scene.startDatePicker.setValue(oldValue);
                    startDate = oldValue;
                }
            }
        };

        scene.startDatePicker.valueProperty().addListener(dateChangeListener);
        scene.endDatePicker.valueProperty().addListener(dateChangeListener);

        // Chart type switching
        scene.chartTypeCombo.setOnAction(e -> {
            scene.chartPane.getChildren().clear();
            if ("Line graph".equals(scene.chartTypeCombo.getValue())) {
                scene.chartPane.getChildren().add(scene.lineChart);
            } else {
                scene.chartPane.getChildren().add(scene.barChart);
            }
            chartService.updateAllCharts(startDate, endDate);
        });

        scene.sendBtn.setOnAction(e -> handleAIRequest());

        // Add initial welcome message
        initializeWelcomeMessage();
    }

    /**
     * Initializes and displays the welcome message in the chat interface.
     * The message is retrieved from the language service and rendered using Markdown.
     */
    private void initializeWelcomeMessage() {
        String welcomeMsg = languageService.getTranslation("welcome_message");

        // Add to chat history
        Map<String, String> aiMsg = new HashMap<>();
        aiMsg.put("role", "assistant");
        aiMsg.put("content", welcomeMsg);
        chatMessages.add(aiMsg);

        // Update StatusScene's chat history
        scene.chatHistory = chatMessages;

        // Display welcome message
        Node doc = mdParser.parse(welcomeMsg);
        String welcomeHtml = mdRenderer.render(doc);
        updateWebView(welcomeHtml);
    }

    /**
     * Updates the summary labels showing total income and expenses for the selected date range.
     *
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     */
    void updateSummaryLabels(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        transactions = transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
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

        String currencySymbol = currencyService.getSelectedCurrency();
        scene.exLabel.setText(String.format("Ex.  %.2f %s", totalExpense, currencySymbol));
        scene.inLabel.setText(String.format("In.  %.2f %s", totalIncome, currencySymbol));
        scene.exLabel.setStyle(
                "-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-border-radius: 30; -fx-background-radius: 30; -fx-padding: 10 20 10 20;");
        scene.inLabel.setStyle(
                "-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-border-radius: 30; -fx-background-radius: 30; -fx-padding: 10 20 10 20;");
    }

    /**
     * Updates the transaction list display for the selected date range.
     * Transactions are sorted by date in descending order and displayed with
     * converted currency amounts.
     *
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     */
    void updateTransactions(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        scene.transactionsBox.getChildren().clear();
        String currencySymbol = currencyService.getSelectedCurrency();
        transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
                    return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
                })
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .forEach(t -> {
                    double convertedAmount = currencyService.convertCurrency(t.getAmount(), t.getCurrency());
                    Label txLabel = new Label(String.format("%s   %s    %.2f %s",
                            t.getTransactionDate(), t.getCategory(), convertedAmount, currencySymbol));
                    txLabel.setWrapText(true);
                    scene.transactionsBox.getChildren().add(txLabel);
                });
    }

    /**
     * Handles AI chat requests from the user.
     * Processes the user's question, gathers relevant financial data,
     * and displays the AI's response in a progressive manner.
     */
    void handleAIRequest() {
        String userInput = scene.questionArea.getText().trim();
        if (!userInput.isEmpty()) {
            scene.questionArea.setDisable(true);
            scene.sendBtn.setDisable(true);

            // Save user question to chat history
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userInput);
            chatMessages.add(userMsg);

            // Get transaction data
            List<Transaction> txList = txService.loadTransactions(currentUser);
            StringBuilder dataSummary = new StringBuilder();
            dataSummary.append("Here is my financial transaction data, each entry format: Date, Type, Currency, Amount, Category, PaymentMethod:\n");
            for (Transaction tx : txList) {
                dataSummary.append(String.format("- %s, %s, %s, %.2f, %s, %s\n",
                        tx.getTransactionDate(), tx.getTransactionType(), tx.getCurrency(),
                        tx.getAmount(), tx.getCategory(), tx.getPaymentMethod()));
            }

            // Get goal data
            List<Goal> goalsList = goalService.getUserGoals(currentUser);
            dataSummary.append("\nHere is my financial goal data, each entry format: Type, Title, Target Amount, Current Amount, Deadline, Category, Currency:\n");
            for (Goal goal : goalsList) {
                dataSummary.append(String.format("- %s, %s, %.2f, %.2f, %s, %s, %s\n",
                        goal.getType(), goal.getTitle(), goal.getTargetAmount(),
                        goal.getCurrentAmount(), goal.getDeadline(), goal.getCategory(),
                        goal.getCurrency()));
            }

            String systemPrompt = "You are now my personal financial management assistant. I want you to answer my questions about personal finance. Please note that you should only answer questions related to personal finance or financial knowledge. If the user asks questions unrelated to their personal finance or financial knowledge, you should decline to answer. All responses should be in English.\n" +
                    "Here is my financial data structure:\n" +
                    "1. Transaction data: Transaction Date(YYYY-MM-DD), Type(Income/Expense), Currency, Amount, Category, PaymentMethod\n" +
                    "2. Goal data: Type(SAVING/DEBT_REPAYMENT/BUDGET_CONTROL), Title, Target Amount, Current Amount, Deadline, Category, Currency\n" +
                    "Here is my current data:\n" + dataSummary +
                    "\nUser's question: " + userInput;

            String answer = AiChatService.chatCompletion(chatMessages, systemPrompt);
            if (answer != null) {
                // Save AI response to chat history
                Map<String, String> aiMsg = new HashMap<>();
                aiMsg.put("role", "assistant");
                aiMsg.put("content", answer);
                chatMessages.add(aiMsg);
                // Update StatusScene's chat history
                scene.chatHistory = chatMessages;
                // Clear webview
                updateWebView("");

                // Use unified chunk processing
                new Thread(() -> {
                    try {
                        // Use progressive display
                        int chunkSize = 100;
                        StringBuilder partialBuilder = new StringBuilder();

                        for (int i = 0; i <= answer.length(); i += chunkSize) {
                            int end = Math.min(i + chunkSize, answer.length());
                            String currentChunk = answer.substring(0, end);

                            Node doc = mdParser.parse(currentChunk);

                            final String html = mdRenderer.render(doc);
                            Platform.runLater(() -> updateWebView(html));

                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                            }
                        }

                        // Final complete display to ensure all content is shown
                        Node finalDoc = mdParser.parse(answer);
                        String finalHtml = mdRenderer.render(finalDoc);
                        Platform.runLater(() -> updateWebView(finalHtml));
                    } finally {
                        Platform.runLater(() -> {
                            scene.questionArea.setDisable(false);
                            scene.sendBtn.setDisable(false);
                        });
                    }
                }).start();
            } else {
                // Error occurred
                Node doc = mdParser.parse("AI request failed, unable to get response");
                String failHtml = mdRenderer.render(doc);
                updateWebView(failHtml);

                scene.questionArea.setDisable(false);
                scene.sendBtn.setDisable(false);
            }

            scene.questionArea.clear();
        }
    }

    /**
     * Updates the WebView component with the provided HTML content.
     * This method is used to display formatted chat messages and AI responses.
     *
     * @param html The HTML content to display
     */
    private void updateWebView(String html) {
        // Wrap the HTML content in a proper HTML structure with a theme-based body
        String wrappedHtml = "<!DOCTYPE html><html><head>" +
                "<meta charset='UTF-8'>" +
                "</head><body style='background-color: " +
                (scene.themeService.isDayMode() ? "white" : "#3C3C3C") +
                "; color: " +
                (scene.themeService.isDayMode() ? "black" : "white") +
                "; margin: 0; padding: 0;'>" +
                "<div class='chat-history'>" +
                html +
                "</div></body></html>";
        scene.suggestionsWebView.getEngine().loadContent(wrappedHtml);

        // Use JavaScript to scroll to bottom
        scene.suggestionsWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                scene.suggestionsWebView.getEngine().executeScript(
                        "window.scrollTo(0, document.body.scrollHeight);");
            }
        });
    }
}