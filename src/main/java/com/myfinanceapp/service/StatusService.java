package com.myfinanceapp.service;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
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

public class StatusService {
    private final User currentUser;
    private final TransactionService txService;
    private final List<Map<String, String>> chatMessages = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private final StatusScene scene;
    private final ChartService chartService;
    private final Parser mdParser = Parser.builder().build();
    private final HtmlRenderer mdRenderer = HtmlRenderer.builder().build();

    public StatusService(StatusScene scene, User currentUser) {
        this.scene = scene;
        this.currentUser = currentUser;
        this.txService = new TransactionService();
        this.chartService = new ChartService(scene.lineChart, scene.barChart, scene.pieChart, txService, currentUser);
        initialize();
    }

    private void initialize() {
        // 初始化日期为本月1日起到今天
        LocalDate today = LocalDate.now();
        startDate = today.withDayOfMonth(1);
        endDate = today;

        // 设置 DatePicker 默认值
        scene.startDatePicker.setValue(startDate);
        scene.endDatePicker.setValue(endDate);

        // 限制 DatePicker 不可选择未来日期
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

        // 初始化数据
        updateSummaryLabels(startDate, endDate);
        chartService.updateAllCharts(startDate, endDate);
        updateTransactions(startDate, endDate);

        // 绑定日期选择事件
        ChangeListener<LocalDate> dateChangeListener = (obs, oldValue, newValue) -> {
            if (scene.startDatePicker.getValue() != null && scene.endDatePicker.getValue() != null) {
                startDate = scene.startDatePicker.getValue();
                endDate = scene.endDatePicker.getValue();
                if (!startDate.isAfter(endDate)) {
                    updateSummaryLabels(startDate, endDate);
                    chartService.updateAllCharts(startDate, endDate);
                    updateTransactions(startDate, endDate);
                } else {
                    // 如果开始日期晚于结束日期，重置为旧值
                    scene.startDatePicker.setValue(oldValue);
                    startDate = oldValue;
                }
            }
        };

        scene.startDatePicker.valueProperty().addListener(dateChangeListener);
        scene.endDatePicker.valueProperty().addListener(dateChangeListener);

        // 图表类型切换
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

        // 添加初始化欢迎消息
        initializeWelcomeMessage();
    }

    private void initializeWelcomeMessage() {
        String welcomeMsg = "Welcome to use the financial assistant. Please feel free to ask any financial questions you have.";

        // 添加到聊天历史
        Map<String, String> aiMsg = new HashMap<>();
        aiMsg.put("role", "assistant");
        aiMsg.put("content", welcomeMsg);
        chatMessages.add(aiMsg);

        // 更新StatusScene的聊天历史
        scene.chatHistory = chatMessages;

        // 显示欢迎消息
        Node doc = mdParser.parse(welcomeMsg);
        String welcomeHtml = mdRenderer.render(doc);
        updateWebView(welcomeHtml);
    }

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

    void updateTransactions(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = txService.loadTransactions(currentUser);
        scene.transactionsBox.getChildren().clear();
        transactions.stream()
                .filter(t -> {
                    LocalDate txDate = LocalDate.parse(t.getTransactionDate());
                    return !txDate.isBefore(startDate) && !txDate.isAfter(endDate);
                })
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .forEach(t -> {
                    Label txLabel = new Label(String.format("%s   %s    %.2f CNY",
                            t.getTransactionDate(), t.getCategory(), t.getAmount()));
                    txLabel.setWrapText(true);
                    scene.transactionsBox.getChildren().add(txLabel);
                });
    }

    void handleAIRequest() {
        String userInput = scene.questionArea.getText().trim();
        if (!userInput.isEmpty()) {
            scene.questionArea.setDisable(true);
            scene.sendBtn.setDisable(true);

            // 保存用户问题到聊天历史
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userInput);
            chatMessages.add(userMsg);

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
                // 保存AI回答到聊天历史
                Map<String, String> aiMsg = new HashMap<>();
                aiMsg.put("role", "assistant");
                aiMsg.put("content", answer);
                chatMessages.add(aiMsg);
                // 更新StatusScene的聊天历史
                scene.chatHistory = chatMessages;
                // 清空webview
                updateWebView("");

                // 使用统一的分块处理方式
                new Thread(() -> {
                    try {
                        // 使用渐进式显示
                        int chunkSize = 100;
                        StringBuilder partialBuilder = new StringBuilder();

                        for (int i = 0; i <= answer.length(); i += chunkSize) {
                            int end = Math.min(i + chunkSize, answer.length());
                            String currentChunk = answer.substring(0, end);

                            Node doc = mdParser.parse(currentChunk);

                            final String html = mdRenderer.render(doc);
                            Platform.runLater(() -> updateWebView(html));

                            try { Thread.sleep(50); } catch(InterruptedException e){}
                        }

                        // 最后完整显示一次，确保全部内容都显示出来
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
                // 出错
                Node doc = mdParser.parse("AI 请求失败，未能获取答复");
                String failHtml = mdRenderer.render(doc);
                updateWebView(failHtml);

                scene.questionArea.setDisable(false);
                scene.sendBtn.setDisable(false);
            }

            scene.questionArea.clear();
        }
    }

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

        // 使用JavaScript滚动到底部
        scene.suggestionsWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                scene.suggestionsWebView.getEngine().executeScript(
                        "window.scrollTo(0, document.body.scrollHeight);"
                );
            }
        });
    }
}