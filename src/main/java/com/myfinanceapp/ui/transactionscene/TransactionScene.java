package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.TransactionService;
import com.myfinanceapp.service.ThemeService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import com.myfinanceapp.ui.common.LeftSidebarFactory;
import com.myfinanceapp.service.AISortingService;

import java.io.File;
import java.time.LocalDate;
import java.text.ParseException;

public class TransactionScene {
    // Overloaded method for backward compatibility
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        return createScene(stage, width, height, loggedUser, new ThemeService());
    }

    public static Scene createScene(Stage stage, double width, double height, User loggedUser,
            ThemeService themeService) {
        BorderPane root = new BorderPane();
        root.setStyle(themeService.getCurrentThemeStyle());

        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage, "New", loggedUser, themeService);
        root.setLeft(sideBar);

        // 中间手动输入部分
        VBox centerBox = new VBox();
        centerBox.setStyle(
                "-fx-border-color: #3282fa;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        themeService.getCurrentFormBackgroundStyle() +
                        "-fx-padding: 20;");
        centerBox.setPadding(new Insets(20, 20, 40, 20));
        // 新增：允许垂直扩展
        centerBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(centerBox, Priority.ALWAYS);

        Label topicLabel = new Label("Manual Import:");
        topicLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;" + themeService.getTextColorStyle());
        // VBox.setMargin(topicLabel, new Insets(5, 0, 5, 0)); // 上下边距

        // 日期选择器部分
        Label dateLabel = new Label("Transaction Date");
        dateLabel.setStyle(themeService.getTextColorStyle());

        // 创建DatePicker并设置提示文本
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select date");

        // 设置日期选择器的最大和最小宽度
        datePicker.setMaxWidth(200);
        datePicker.setPrefWidth(150);

        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // 禁用未来的日期
                boolean isFuture = date.isAfter(LocalDate.now());
                setDisable(isFuture);

                // 仅修改文字颜色
                if (isFuture) {
                    setStyle("-fx-text-fill: #808080;"); // 灰色文字
                } else {
                    setStyle(""); // 恢复默认样式
                }
            }
        });

        // 将日期选择器放入VBox中
        VBox dateBox = new VBox(dateLabel, datePicker);
        dateBox.setAlignment(Pos.CENTER);

        Label typeLabel = new Label("Transition Type");
        typeLabel.setStyle(themeService.getTextColorStyle());
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Income", "Expense");
        typeCombo.setMaxWidth(200);
        typeCombo.setPrefWidth(150);
        typeCombo.setValue("Expense"); // Set default value
        typeCombo.setFocusTraversable(false); // 防止自动获取焦点
        VBox typeBox = new VBox(typeLabel, typeCombo);
        typeBox.setAlignment(Pos.CENTER);

        Label currencyLabel = new Label("Currency");
        currencyLabel.setStyle(themeService.getTextColorStyle());
        ComboBox<String> currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll("CNY", "USD", "EUR");
        currencyCombo.setMaxWidth(200);
        currencyCombo.setPrefWidth(150);
        currencyCombo.setValue("CNY"); // Set default value
        currencyCombo.setFocusTraversable(false); // 防止自动获取焦点
        VBox currencyBox = new VBox(currencyLabel, currencyCombo);
        currencyBox.setAlignment(Pos.CENTER);

        Label amountLabel = new Label("Amount");
        amountLabel.setStyle(themeService.getTextColorStyle());
        TextField amountField = new TextField();
        amountField.setPromptText("Please enter amount");
        amountField.setMaxWidth(200);
        amountField.setPrefWidth(150);
        amountField.setFocusTraversable(false); // 防止自动获取焦点
        VBox amountBox = new VBox(amountLabel, amountField);
        amountBox.setAlignment(Pos.CENTER);

        // 添加描述框和自动分类按钮
        Label descriptionLabel = new Label("Description");
        descriptionLabel.setStyle(themeService.getTextColorStyle());
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter transaction description");
        descriptionField.setMaxWidth(200);
        descriptionField.setPrefWidth(150);
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        descriptionField.setFocusTraversable(false);
        VBox descriptionBox = new VBox(descriptionLabel, descriptionField);
        descriptionBox.setAlignment(Pos.CENTER);

        Button autoSortButton = new Button("Auto-sorting");
        autoSortButton.setStyle(themeService.getButtonStyle() + "-fx-font-weight: bold; " + "-fx-border-radius: 15;");
        autoSortButton.setMaxWidth(100);
        autoSortButton.setPrefWidth(100);

        // 修改分类框的布局，将自动分类按钮放在category输入框右边
        Label categoryLabel = new Label("Category");
        categoryLabel.setStyle(themeService.getTextColorStyle());
        TextField categoryField = new TextField();
        categoryField.setPromptText("e.g., Salary, Rent, Utilities");
        categoryField.setMaxWidth(200);
        categoryField.setPrefWidth(150);
        categoryField.setFocusTraversable(false);

        HBox categoryAndButton = new HBox(categoryField, autoSortButton);
        categoryAndButton.setSpacing(5);
        categoryAndButton.setAlignment(Pos.CENTER);
        VBox categoryBox = new VBox(categoryLabel, categoryAndButton);
        categoryBox.setAlignment(Pos.CENTER);

        Label methodLabel = new Label("Payment Method");
        methodLabel.setStyle(themeService.getTextColorStyle());
        TextField methodField = new TextField();
        methodField.setPromptText("e.g., Cash, PayPal, Bank Transfer");
        methodField.setMaxWidth(200);
        methodField.setPrefWidth(150);
        methodField.setFocusTraversable(false); // 防止自动获取焦点
        VBox methodBox = new VBox(methodLabel, methodField);
        methodBox.setAlignment(Pos.CENTER);

        // 按下自动分类按钮后调用AI进行自动分类
        autoSortButton.setOnAction(event -> {
            String description = descriptionField.getText();
            if (description.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a description first");
                alert.showAndWait();
                return;
            }

            try {
                String category = AISortingService.sort(description);
                categoryField.setText(category);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to auto-sort category: " + e.getMessage());
                alert.showAndWait();
            }
        });

        Button submitManualBtn = new Button("Submit");
        submitManualBtn.setMaxWidth(150);
        submitManualBtn.setPrefWidth(100);
        submitManualBtn.setStyle(themeService.getButtonStyle() + "-fx-font-weight: bold; " + "-fx-border-radius: 15;");
        VBox.setMargin(submitManualBtn, new Insets(10, 0, 0, 0));
        submitManualBtn.setAlignment(Pos.CENTER);

        submitManualBtn.setOnAction(event -> {
            // 获取日期选择器的值
            String selectedDate = datePicker.getValue() != null ? datePicker.getValue().toString() : null;

            if (selectedDate == null || selectedDate.isEmpty() ||
                    amountField.getText().isEmpty() ||
                    categoryField.getText().isEmpty() ||
                    methodField.getText().isEmpty()) {
                // 弹出提示窗口，要求填写所有字段
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing Information");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all blanks before submitting");
                alert.showAndWait();
                return; // 停止提交过程
            }

            // 验证日期格式和范围
            try {
                // 此时selectedDate已经是正确格式，无需再次验证
                // 只需检查年月日是否有效
                String[] dateParts = selectedDate.split("-");
                if (dateParts.length == 3) {
                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    int day = Integer.parseInt(dateParts[2]);

                    if (month < 1 || month > 12) {
                        throw new ParseException("Invalid month", 0);
                    }

                    if (day < 1 || day > 31) {
                        throw new ParseException("Invalid day", 0);
                    }

                    // 检查特定月份的天数上限
                    int maxDays;
                    switch (month) {
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            maxDays = 30;
                            break;
                        case 2:
                            // 检查闰年
                            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                            maxDays = isLeapYear ? 29 : 28;
                            break;
                        default:
                            maxDays = 31;
                    }

                    if (day > maxDays) {
                        throw new ParseException("Day exceeds maximum for month", 0);
                    }
                }
            } catch (ParseException | NumberFormatException e) {
                // 弹出提示窗口，告知日期格式错误
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Date");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid date in format yyyy-MM-dd\n" +
                        "Month must be 1-12 and day must be 1-31");
                alert.showAndWait();
                return; // 停止提交过程
            }

            // 验证category和payment method只包含英文单词
            if (!categoryField.getText().matches("^[a-zA-Z\\s]+$")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Category");
                alert.setHeaderText(null);
                alert.setContentText("Category must contain only English letters");
                alert.showAndWait();
                return;
            }

            if (!methodField.getText().matches("^[a-zA-Z\\s]+$")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Payment Method");
                alert.setHeaderText(null);
                alert.setContentText("Payment method must contain only English letters");
                alert.showAndWait();
                return;
            }

            // 验证金额格式
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException e) {
                // 弹出提示窗口，告知金额格式错误
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Amount");
                alert.setHeaderText(null);
                alert.setContentText("Please type in a valid number");
                alert.showAndWait();
                return; // 停止提交过程
            }

            Transaction transaction = new Transaction();
            transaction.setTransactionDate(selectedDate); // 使用选定的日期
            transaction.setTransactionType(typeCombo.getValue());
            transaction.setCurrency(currencyCombo.getValue());
            transaction.setAmount(amount);
            transaction.setCategory(categoryField.getText());
            transaction.setPaymentMethod(methodField.getText());
            transaction.setDescription(descriptionField.getText()); // 添加description字段

            TransactionService service = new TransactionService();
            service.addTransaction(loggedUser, transaction); // 传入 loggedUser

            // 提交后清空输入框内容
            datePicker.setValue(null); // 清空日期选择器
            amountField.clear(); // 清空金额文本框
            categoryField.clear(); // 清空类别文本框
            methodField.clear(); // 清空支付方式文本框
            descriptionField.clear(); // 清空描述框

            // 提交成功后弹出一个提示框，通知用户交易已成功保存
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Transaction Added");
            alert.setHeaderText(null);
            alert.setContentText("Transaction has been successfully added.");
            alert.showAndWait();
        });

        centerBox.getChildren().addAll(
                topicLabel,
                dateBox,
                typeBox,
                currencyBox,
                amountBox,
                descriptionBox,
                categoryBox,
                methodBox,
                submitManualBtn);
        centerBox.setSpacing(10); // 增加整个区域内的元素间距
        centerBox.setAlignment(Pos.CENTER); // 让整个 centerBox 内的元素居中

        // 右侧传输csv文件部分
        // 修改rightBar的VBox设置
        VBox rightBar = new VBox();
        rightBar.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        themeService.getCurrentFormBackgroundStyle() +
                        "-fx-padding: 20;");
        rightBar.setPadding(new Insets(20, 20, 20, 20));
        // 新增：允许垂直扩展
        rightBar.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(rightBar, Priority.ALWAYS);

        Label promptLabel = new Label("File Import:");
        promptLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;" + themeService.getTextColorStyle());
        VBox.setMargin(promptLabel, new Insets(10, 0, 0, 0)); // 增加与下方内容的间距

        Button importCSVButton = new Button("Select a file");
        importCSVButton.setPrefWidth(100);
        importCSVButton.setStyle(themeService.getButtonStyle() + "-fx-font-weight: bold; " + "-fx-border-radius: 15;");

        importCSVButton.setOnAction(event -> {
            // FileChooser 是 JavaFX 提供的一个用于选择文件的控件。fileChooser 会弹出一个文件选择对话框，允许用户浏览文件系统并选择文件。
            FileChooser fileChooser = new FileChooser();
            // 通过 getExtensionFilters() 为 FileChooser 添加文件扩展名过滤器。它只允许用户选择 CSV 文件
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                TransactionService service = new TransactionService();
                service.importTransactionsFromCSV(loggedUser, file);
            }
        });

        Label formatLabel = new Label("Your .CSV file should\ncontain the following columns:\n\n" +
                "Transaction Date\n" +
                "(format: YYYY-MM-DD, e.g. 2025-03-15)\n\n" +
                "Transaction Type\n" +
                "(only: Income / Expense)\n\n" +
                "Currency\n" +
                "(currency type, e.g. CNY, USD)\n\n" +
                "Amount\n" +
                "(number format, e.g. 1234.56)\n\n" +
                "Category\n" +
                "(income and expense category)\n\n" +
                "Payment Method\n" +
                "(payment method)");
        formatLabel.setFont(new Font(11));
        formatLabel.setStyle(themeService.getTextColorStyle());
        VBox.setMargin(formatLabel, new Insets(10, 0, 20, 0)); // 增加与下方内容的间距

        rightBar.getChildren().addAll(
                promptLabel,
                importCSVButton,
                formatLabel);

        rightBar.setSpacing(10); // 增加整个区域内的元素间距
        rightBar.setAlignment(Pos.CENTER); // 让整个 rightBar 内的元素居中

        // 使用 GridPane 来确保两个模块大小一致，并且在窗口拉伸时一起变化
        GridPane centerAndRight = new GridPane();
        centerAndRight.setPadding(new Insets(20, 20, 20, 20));
        centerAndRight.setHgap(20); // 设置两个模块之间的水平间距

        // 添加列约束，确保两列在窗口大小变化时均匀分配空间
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50); // 占据 50% 的宽度
        column1.setFillWidth(true); // 设置列填充整个宽度

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50); // 占据 50% 的宽度
        column2.setFillWidth(true); // 设置列填充整个宽度

        centerAndRight.getColumnConstraints().addAll(column1, column2);

        // 修改GridPane的行约束
        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.setVgrow(Priority.ALWAYS); // 允许行垂直扩展
        rowConstraint.setFillHeight(true);
        centerAndRight.getRowConstraints().add(rowConstraint);
        // 确保内部元素不会限制VBox扩展
        centerBox.setAlignment(Pos.TOP_CENTER); // 顶部居中，允许下方空间扩展
        rightBar.setAlignment(Pos.TOP_CENTER);
        // 将 centerBox 和 rightBar 添加到 GridPane 中
        centerAndRight.add(centerBox, 0, 0); // centerBox 放在第 0 列
        centerAndRight.add(rightBar, 1, 0); // rightBar 放在第 1 列

        // 新增滚动面板
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(centerAndRight);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        String backgroundColor = themeService.isDayMode() ? "white" : "#2A2A2A";
        scrollPane.setStyle(
                "-fx-background: " + backgroundColor + ";" +
                        "-fx-border-color: transparent;" +
                        "-fx-control-inner-background: " + backgroundColor + ";" +
                        "-fx-text-fill: transparent;");
        scrollPane.setPadding(new Insets(0)); // 移除内边距

        // 保持滚动面板的扩展性
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setMaxWidth(Double.MAX_VALUE);
        BorderPane.setMargin(scrollPane, new Insets(0));

        root.setCenter(scrollPane);
        Scene scene = new Scene(root, width, height);

        // 添加一条全局样式：所有 Label 默认为动态颜色
        String labelColor = themeService.isDayMode() ? "darkblue" : "white";
        scene.getStylesheets().add("data:,Label { -fx-text-fill: " + labelColor + "; }");

        return scene;
    }
}