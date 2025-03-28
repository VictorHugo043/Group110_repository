package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.ui.statusscene.StatusScene;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatusService {
    private final User currentUser;
    private final TransactionService txService;
    private final List<Map<String, String>> chatMessages = new ArrayList<>();
    private String currentPeriod = "This Month";
    private final StatusScene scene;
    private final ChartService chartService;

    public StatusService(StatusScene scene, User currentUser) {
        this.scene = scene;
        this.currentUser = currentUser;
        this.txService = new TransactionService();
        this.chartService = new ChartService(scene.lineChart, scene.barChart, scene.pieChart, txService, currentUser);
        initialize();
    }

    private void initialize() {
        // 初始化数据
        updateSummaryLabels("This Month");
        chartService.updateAllCharts("This Month");
        updateTransactions();

        // 绑定事件
        scene.dateCombo.setOnAction(e -> {
            currentPeriod = scene.dateCombo.getValue();
            updateSummaryLabels(currentPeriod);
            chartService.updateAllCharts(currentPeriod);
        });

        scene.chartTypeCombo.setOnAction(e -> {
            scene.chartPane.getChildren().clear(); // 直接使用 scene.chartPane
            if ("Line graph".equals(scene.chartTypeCombo.getValue())) {
                scene.chartPane.getChildren().add(scene.lineChart);
            } else {
                scene.chartPane.getChildren().add(scene.barChart);
            }
            chartService.updateAllCharts(currentPeriod);
        });

        scene.sendBtn.setOnAction(e -> handleAIRequest());
    }

    private void updateSummaryLabels(String period) {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        switch (period) {
            case "Last Month":
                startDate = now.minusMonths(1).withDayOfMonth(1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                break;
            case "All Transactions":
                if (transactions.isEmpty()) {
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                } else {
                    startDate = transactions.stream()
                            .map(t -> LocalDate.parse(t.getTransactionDate()))
                            .min(LocalDate::compareTo)
                            .orElse(now.withDayOfMonth(1));
                    endDate = transactions.stream()
                            .map(t -> LocalDate.parse(t.getTransactionDate()))
                            .max(LocalDate::compareTo)
                            .orElse(now.withDayOfMonth(now.lengthOfMonth()));
                }
                break;
            default: // "This Month"
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
        }

        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;
        transactions = transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
                    return !txDate.isBefore(finalStartDate) && !txDate.isAfter(finalEndDate);
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

        scene.exLabel.setText(String.format("Ex.  %.2f CNY", totalExpense));
        scene.inLabel.setText(String.format("In.  %.2f CNY", totalIncome));
        scene.exLabel.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-border-radius: 30; -fx-background-radius: 30; -fx-padding: 10 20 10 20;");
        scene.inLabel.setStyle("-fx-background-color: #E0F0FF; -fx-text-fill: #3282FA; -fx-border-radius: 30; -fx-background-radius: 30; -fx-padding: 10 20 10 20;");
    }

    private void updateTransactions() {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        scene.transactionsBox.getChildren().clear();
        transactions.stream()
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .forEach(t -> {
                    Label txLabel = new Label(String.format("%s   %s    %.2f CNY",
                            t.getTransactionDate(), t.getCategory(), t.getAmount()));
                    txLabel.setWrapText(true);
                    scene.transactionsBox.getChildren().add(txLabel);
                });
    }

    private void handleAIRequest() {
        String userInput = scene.questionArea.getText().trim();
        if (!userInput.isEmpty()) {
            scene.questionArea.setDisable(true);
            scene.sendBtn.setDisable(true);

            List<Transaction> txList = txService.loadTransactions(currentUser);
            StringBuilder dataSummary = new StringBuilder();
            dataSummary.append("以下是我的财务交易数据，每条格式：Date, Type, Currency, Amount, Category, PaymentMethod:\n");
            for (Transaction tx : txList) {
                dataSummary.append(String.format("- %s, %s, %s, %.2f, %s, %s\n",
                        tx.getTransactionDate(), tx.getTransactionType(), tx.getCurrency(),
                        tx.getAmount(), tx.getCategory(), tx.getPaymentMethod()));
            }
            String systemPrompt = "现在你是我的专属财务管理助手，我希望你解答我有关个人财务的问题。\n" +
                    "这是我的财务数据结构: Transaction Date(YYYY-MM-DD), Type(Income/Expense), Currency, Amount, Category, PaymentMethod.\n" +
                    "下面是我目前的数据：\n" + dataSummary +
                    "\n用户的问题是： " + userInput;

            String answer = AiChatService.chatCompletion(chatMessages, systemPrompt);
            if (answer != null) {
                scene.suggestionsArea.appendText("You: " + userInput + "\nAI: " + answer + "\n\n");
            } else {
                scene.suggestionsArea.appendText("AI 请求失败，未能获取答复\n\n");
            }

            scene.questionArea.clear();
            scene.questionArea.setDisable(false);
            scene.sendBtn.setDisable(false);
        }
    }
}