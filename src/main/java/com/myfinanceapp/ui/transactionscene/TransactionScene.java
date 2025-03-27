package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.service.TransactionService;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javafx.geometry.Insets;
import com.myfinanceapp.ui.common.LeftSidebarFactory;



public class TransactionScene {
    public static Scene createScene(Stage stage, double width, double height, User loggedUser) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        VBox sideBar = LeftSidebarFactory.createLeftSidebar(stage,"New",loggedUser);
        root.setLeft(sideBar);

        //中间手动输入部分
        VBox centerBox = new VBox();
        centerBox.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-color: white;" +
                        "-fx-padding: 20;"  // 为右侧栏添加内边距，避免内容与边框紧贴
        );
        Label topicLabel = new Label("Manual Import:");
        topicLabel.setTextFill(Color.DARKBLUE);

        // 设置 topicLabel 和后续内容之间的间距
        topicLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox.setMargin(topicLabel, new Insets(10, 0, 10, 0)); // 增加与下方内容的间距

        //页面一开始光标就focus在date框里面，需要改一下
        //修改这里：确保初始时文本框不自动获得焦点
        Label dateLabel = new Label("Transition Date");
        dateLabel.setTextFill(Color.DARKBLUE);
        TextField dateField = new TextField();
        dateField.setPromptText("yyyy-MM-dd");

        dateField.setMaxWidth(200);  // 设置输入框的最大宽度为 120
        dateField.setPrefWidth(150); // 确保输入框宽度为 120

        //dateField.setPrefWidth(150);
        //dateField.setFocusTraversable(false); // 防止自动获取焦点

        VBox dateBox = new VBox(dateLabel, dateField);
        dateBox.setAlignment(Pos.CENTER);

        Label typeLabel = new Label("Transition Type");
        typeLabel.setTextFill(Color.DARKBLUE);
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Income", "Expense");
        typeCombo.setMaxWidth(200);
        typeCombo.setPrefWidth(150);
        typeCombo.setValue("Expense");  // Set default value
        typeCombo.setFocusTraversable(false); // 防止自动获取焦点
        VBox typeBox = new VBox(typeLabel, typeCombo);
        typeBox.setAlignment(Pos.CENTER);

        Label currencyLabel = new Label("Currency");
        ComboBox<String> currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll("CNY", "USD", "EUR");
        currencyCombo.setMaxWidth(200);
        currencyCombo.setPrefWidth(150);
        currencyCombo.setValue("CNY");  // Set default value
        currencyCombo.setFocusTraversable(false); // 防止自动获取焦点
        VBox currencyBox = new VBox(currencyLabel, currencyCombo);
        currencyBox.setAlignment(Pos.CENTER);

        Label amountLabel = new Label("Amount");
        amountLabel.setTextFill(Color.DARKBLUE);
        TextField amountField = new TextField();
        amountField.setPromptText("Please enter amount");
        amountField.setMaxWidth(200);
        amountField.setPrefWidth(150);
        amountField.setFocusTraversable(false); // 防止自动获取焦点
        VBox amountBox = new VBox(amountLabel, amountField);
        amountBox.setAlignment(Pos.CENTER);


        Label categoryLabel = new Label("Category");
        categoryLabel.setTextFill(Color.DARKBLUE);
        TextField categoryField = new TextField();
        categoryField.setPromptText("e.g., Salary, Rent, Utilities");
        categoryField.setMaxWidth(200);
        categoryField.setPrefWidth(150);
        categoryField.setFocusTraversable(false); // 防止自动获取焦点
        VBox categoryBox = new VBox(categoryLabel, categoryField);
        categoryBox.setAlignment(Pos.CENTER);


        Label methodLabel = new Label("Payment Method");
        methodLabel.setTextFill(Color.DARKBLUE);
        TextField methodField = new TextField();
        methodField.setPromptText("e.g., Cash, PayPal, Bank Transfer");
        methodField.setMaxWidth(200);
        methodField.setPrefWidth(150);
        methodField.setFocusTraversable(false); // 防止自动获取焦点
        VBox methodBox = new VBox(methodLabel, methodField);
        methodBox.setAlignment(Pos.CENTER);

        Button submitManualBtn = new Button("Submit");
        submitManualBtn.setMaxWidth(150);
        submitManualBtn.setPrefWidth(100);
        submitManualBtn.setStyle("-fx-background-color: #E0F0FF; " +
                "-fx-text-fill: #3282FA; -fx-font-weight: bold; " +
                "-fx-border-radius: 15;"); // 新增：按钮的背景色，文本颜色，字体粗细和圆角

        VBox.setMargin(submitManualBtn, new Insets(20, 0, 0, 0));
        submitManualBtn.setAlignment(Pos.CENTER); // 将按钮居中对齐
        submitManualBtn.setOnAction(event -> {

            //！ 传入数据错误的判断及处理（日期是否符合格式要求）
            // 检查所有字段是否已填写
            if (dateField.getText().isEmpty() || 
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
                // 首先检查基本格式
                String dateText = dateField.getText();
                String datePattern = "yyyy-MM-dd";
                SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
                dateFormat.setLenient(false);
                dateFormat.parse(dateText);
                
                // 额外检查月份和日期范围
                String[] dateParts = dateText.split("-");
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
                        case 4: case 6: case 9: case 11:
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
                alert.setContentText("please type in a valid number");
                alert.showAndWait();
                return; // 停止提交过程
            }
            
           /*// 检查交易类型与金额正负是否匹配
            String transactionType = typeCombo.getValue();
            if ((transactionType.equals("Income") && amount <= 0) || 
                (transactionType.equals("Expense") && amount >= 0)) {
                
                // 弹出提示窗口，告知金额与交易类型不匹配
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Amount");
                alert.setHeaderText(null);
                if (transactionType.equals("Income")) {
                    alert.setContentText("Income must be a postive number.");
                } else {
                    alert.setContentText("Expense must be a negative number.");
                }
                alert.showAndWait();
                return; // 停止提交过程
            }*/




            Transaction transaction = new Transaction();

            transaction.setTransactionDate(dateField.getText());
            transaction.setTransactionType(typeCombo.getValue());
            transaction.setCurrency(currencyCombo.getValue());
            transaction.setAmount(Double.parseDouble(amountField.getText()));
            transaction.setCategory(categoryField.getText());
            transaction.setPaymentMethod(methodField.getText());

            TransactionService service = new TransactionService();
            service.addTransaction(loggedUser, transaction); // 传入 username

            // 提交后清空输入框内容
            dateField.clear();  // 清空日期文本框
            //typeCombo.setValue(null);  // 清空类型选择框
            //currencyCombo.setValue(null);  // 清空货币选择框
            amountField.clear();  // 清空金额文本框
            categoryField.clear();  // 清空类别文本框
            methodField.clear();  // 清空支付方式文本框

            //提交成功后弹出一个提示框，通知用户交易已成功保存
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
                categoryBox,
                methodBox,
                submitManualBtn
        );
        centerBox.setSpacing(10);  // 增加整个区域内的元素间距，以确保文本与下一个输入框有空隙
        centerBox.setAlignment(Pos.CENTER);  // 让整个 centerBox 内的元素居中


        //右侧传输csv文件部分
        VBox rightBar = new VBox();
        rightBar.setStyle(
                "-fx-border-color: #3282FA;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-color: white;" +
                        "-fx-padding: 20;"  // 为右侧栏添加内边距，避免内容与边框紧贴
        );
        Label promptLabel = new Label("File Import:");
        VBox.setMargin(promptLabel, new Insets(10, 0, 10, 0)); // 增加与下方内容的间距
        promptLabel.setTextFill(Color.DARKBLUE);
        promptLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Button importCSVButton =  new Button("Select a file");
        importCSVButton.setPrefWidth(100);
        importCSVButton.setStyle("-fx-background-color: #E0F0FF; " +
                "-fx-text-fill: #3282FA; -fx-font-weight: bold; " +
                "-fx-border-radius: 15;"); // 新增：按钮的背景色，文本颜色，字体粗细和圆角


        importCSVButton.setOnAction(event -> {
            //FileChooser 是 JavaFX 提供的一个用于选择文件的控件。fileChooser 会弹出一个文件选择对话框，允许用户浏览文件系统并选择文件。
            FileChooser fileChooser = new FileChooser();
            //通过 getExtensionFilters() 为 FileChooser 添加文件扩展名过滤器。它只允许用户选择 CSV 文件
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                TransactionService service = new TransactionService();
                service.importTransactionsFromCSV(loggedUser, file);
            }
        });

        Label formatLabel = new Label("Your .CSV file should\ncontain the following columns:\n\n" +
                "Transaction Date\n" +
                "(format: YYYY–MM–DD, e.g. 2025–03–15)\n\n" +
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
        formatLabel.setTextFill(Color.DARKBLUE);
        // 设定按钮和文本之间的间距
        VBox.setMargin(formatLabel, new Insets(10, 0, 0, 0)); // 为文本上方添加10的间距
        // 设置所有组件的居中对齐
        rightBar.setAlignment(Pos.CENTER);  // 将右侧栏内容居中
        formatLabel.setAlignment(Pos.CENTER);
        importCSVButton.setAlignment(Pos.CENTER);  // 设置按钮居中

        rightBar.getChildren().addAll(
                promptLabel,
                importCSVButton,
                formatLabel
        );

        // 使用 HBox 来确保中间和右侧的区域均分
        HBox centerAndRight = new HBox(centerBox, rightBar);
        centerAndRight.setSpacing(20);  // 设置两者之间的间距
        centerBox.setPrefWidth(width / 2);  // 手动输入区域占宽度的一半
        rightBar.setPrefWidth(width / 2);  // 文件上传区域占宽度的一半


        root.setCenter(centerAndRight);  // 设置为中心区域

        return new Scene(root, width, height);
    }
}

