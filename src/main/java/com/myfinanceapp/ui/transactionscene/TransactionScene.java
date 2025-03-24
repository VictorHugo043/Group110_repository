package com.myfinanceapp.ui.transactionscene;

import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.service.TransactionService;
import com.myfinanceapp.ui.loginscene.LoginScene;
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
import com.myfinanceapp.ui.statusscene.Status;
import javafx.geometry.Insets;


public class TransactionScene {
    public static Scene createScene(Stage stage, double width, double height) {
        BorderPane root = new BorderPane();

        // 左侧导航
        VBox sideBar = new VBox(20);
        sideBar.setStyle("-fx-background-color: #E0F0FF;");
        sideBar.setPrefWidth(150);
        sideBar.setAlignment(Pos.CENTER);
        Label welcomeLabel = new Label("Welcome \nback!");
        welcomeLabel.setFont(new Font(18));

        Label statusLabel = new Label("Status");
        statusLabel.setOnMouseClicked(event -> {
            //点击后跳转到添加交易记录页面
            stage.setScene(Status.createScene(stage, 800, 450));
            stage.setTitle("Finanger - New Transations");
        });

        sideBar.getChildren().addAll(welcomeLabel, statusLabel, new Label("Goals"), new Label("New"), new Label("Settings"));

        Label logoutLabel = new Label("Log out");
        logoutLabel.setTextFill(Color.BLUE); // 或者自定义样式
        logoutLabel.setOnMouseClicked(event -> {
            // 点击后，弹出确认对话框
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?");
            confirm.setHeaderText(null);
            confirm.setTitle("Confirm Logout");

            // 等待用户操作
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // 确认，则跳转回登录界面
                    stage.setScene(LoginScene.createScene(stage, 800, 450));
                    stage.setTitle("Finanger - Login");
                }
            });
        });

        // 将这个 logoutLabel 加到 sideBar 里
        sideBar.getChildren().add(logoutLabel);
        root.setLeft(sideBar);

        //中间手动输入部分
        VBox centerBox = new VBox();
        Label topicLabel = new Label("Manual Import:");
        topicLabel.setTextFill(Color.DARKBLUE);

        //页面一开始光标就focus在date框里面，需要改一下
        Label dateLabel = new Label("Transition Date");
        dateLabel.setTextFill(Color.DARKBLUE);
        TextField dateField = new TextField();
        dateField.setPromptText("yyyy-MM-dd");
        dateField.setPrefWidth(150);
        VBox dateBox = new VBox(dateLabel, dateField);
        dateBox.setAlignment(Pos.CENTER);

        Label typeLabel = new Label("Transition Type");
        typeLabel.setTextFill(Color.DARKBLUE);
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Income", "Expense");
        typeCombo.setPrefWidth(150);
        VBox typeBox = new VBox(typeLabel, typeCombo);
        typeBox.setAlignment(Pos.CENTER);

        Label currencyLabel = new Label("Currency");
        ComboBox<String> currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll("CNY", "USD", "EUR");
        currencyCombo.setPrefWidth(150);
        VBox currencyBox = new VBox(currencyLabel, currencyCombo);
        currencyBox.setAlignment(Pos.CENTER);

        Label amountLabel = new Label("Amount");
        amountLabel.setTextFill(Color.DARKBLUE);
        TextField amountField = new TextField();
        amountField.setPromptText("Please enter amount");
        amountField.setPrefWidth(150);
        VBox amountBox = new VBox(amountLabel, amountField);
        amountBox.setAlignment(Pos.CENTER);

        Label categoryLabel = new Label("Category");
        categoryLabel.setTextFill(Color.DARKBLUE);
        TextField categoryField = new TextField();
        categoryField.setPromptText("e.g., Salary, Rent, Utilities");
        categoryField.setPrefWidth(150);
        VBox categoryBox = new VBox(categoryLabel, categoryField);
        categoryBox.setAlignment(Pos.CENTER);

        Label methodLabel = new Label("Payment Method");
        methodLabel.setTextFill(Color.DARKBLUE);
        TextField methodField = new TextField();
        methodField.setPromptText("e.g., Cash, PayPal, Bank Transfer");
        methodField.setPrefWidth(150);
        VBox methodBox = new VBox(methodLabel, methodField);
        methodBox.setAlignment(Pos.CENTER);

        Button submitManualBtn = new Button("Submit");
        submitManualBtn.setPrefWidth(80);
        submitManualBtn.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        VBox.setMargin(submitManualBtn, new Insets(20, 0, 0, 0));
        submitManualBtn.setOnAction(event -> {

            //！ 传入数据错误的判断及处理（日期是否符合格式要求）

            Transaction transaction = new Transaction();

            transaction.setTransactionDate(dateField.getText());
            transaction.setTransactionType(typeCombo.getValue());
            transaction.setCurrency(currencyCombo.getValue());
            transaction.setAmount(Double.parseDouble(amountField.getText()));
            transaction.setCategory(categoryField.getText());
            transaction.setPaymentMethod(methodField.getText());

            TransactionService transactionService = new TransactionService();
            transactionService.addTransaction(transaction);

            // 提交后清空输入框内容
            dateField.clear();  // 清空日期文本框
            typeCombo.setValue(null);  // 清空类型选择框
            currencyCombo.setValue(null);  // 清空货币选择框
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

        //右侧传输csv文件部分
        VBox rightBar = new VBox();

        Label promptLabel = new Label("Manual Import:");
        promptLabel.setTextFill(Color.DARKBLUE);

        Button importCSVButton =  new Button("Select a file");
        importCSVButton.setPrefWidth(100);
        importCSVButton.setStyle("-fx-background-color: #3377ff; -fx-text-fill: white; -fx-font-weight: bold;");
        importCSVButton.setOnAction(event -> {
            //FileChooser 是 JavaFX 提供的一个用于选择文件的控件。fileChooser 会弹出一个文件选择对话框，允许用户浏览文件系统并选择文件。
            FileChooser fileChooser = new FileChooser();
            //通过 getExtensionFilters() 为 FileChooser 添加文件扩展名过滤器。它只允许用户选择 CSV 文件
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                TransactionService transactionService= new TransactionService();
                transactionService.importTransactionsFromCSV(file);
            }
        });
        //这里需要完善一下
        Label formatLabel = new Label("格式要求..");
        formatLabel.setFont(new Font(20));
        formatLabel.setTextFill(Color.DARKBLUE);

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

