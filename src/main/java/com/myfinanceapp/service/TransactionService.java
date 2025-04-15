package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;     // <-- import
import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;

import javafx.scene.control.Alert;

import java.io.*;
import java.lang.reflect.Type;               // <-- import
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    private static final String TRANSACTION_DIR = "src/main/resources/transaction/";
    private static final Gson gson = new Gson();

    public boolean addTransaction(User user, Transaction newTx) {
        List<Transaction> allTxs = loadTransactions(user);
        for (Transaction t : allTxs) {
            if (t.equals(newTx)) {
                return false;
            }
        }
        allTxs.add(newTx);
        saveTransactions(user, allTxs);
        return true;
    }

    /**
     * 读取 <UID>.json 文件：其中存的格式是 JSON数组 [ {...}, {...} ]
     */
    public List<Transaction> loadTransactions(User user){
        File dir = new File(TRANSACTION_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File jsonFile = new File(dir, user.getUid() + ".json");
        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(jsonFile, StandardCharsets.UTF_8)) {
            // 这里 TypeToken
            Type listType = new TypeToken<List<Transaction>>() {}.getType();
            // 显式泛型可避免编译冲突
            List<Transaction> txList = gson.<List<Transaction>>fromJson(reader, listType);

            return (txList != null) ? txList : new ArrayList<>();
        } catch (IOException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public void importTransactionsFromCSV(User user, File csvFile) {
        List<Transaction> allTxs = loadTransactions(user);
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
             // 读取 CSV 文件的表头
            String headerLine = br.readLine();
            if (headerLine == null) {
                return;
            }

            String[] headers = headerLine.split(",");
             // 分割表头字段，并验证表头格式是否符合预期
            boolean isValidHeader = validateHeader(headers);
             // 如果表头不符合预期格式，弹出错误提示并终止导入
            if (!isValidHeader) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid CSV Format");
                alert.setHeaderText(null);
                alert.setContentText("The CSV file must contain the following headers:\n" +
                        "Transaction Date, Transaction Type, Currency, Amount, Category, Payment Method");
                alert.showAndWait();
                return;
            }
            // 循环读取每一行数据，直到文件结束
            while ((line = br.readLine()) != null) {
                // 按照逗号分割每一行数据
                String[] parts = line.split(",");
                 // 检查每行数据是否包含 6 列
                if (parts.length == 6) {
                    // 创建新的交易记录对象
                    Transaction tx = new Transaction();
                    tx.setTransactionDate(parts[0].trim());
                    tx.setTransactionType(parts[1].trim());
                    tx.setCurrency(parts[2].trim());
                    try {
                        tx.setAmount(Double.parseDouble(parts[3].trim()));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    tx.setCategory(parts[4].trim());
                    tx.setPaymentMethod(parts[5].trim());
                     // 检查是否存在重复的交易记录
                    boolean dup = false;
                    for (Transaction existing : allTxs) {
                        if (existing.equals(tx)) {
                            dup = true;
                            break;
                        }
                    }
                     // 如果没有重复交易，添加新交易记录
                    if (!dup) {
                        allTxs.add(tx);
                    }
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CSV Import Successful");
            alert.setHeaderText(null);
            alert.setContentText("Transactions have been successfully imported from the CSV file.");
            alert.showAndWait();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Read Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while reading the CSV file.");
            alert.showAndWait();
            e.printStackTrace();
        }
        // 将更新后的交易记录保存到本地文件
        saveTransactions(user, allTxs);
    }

    //定义了一个 validateHeader方法，用于验证 CSV 文件的表头是否符合预期格式。
    private boolean validateHeader(String[] headers) {
        if (headers.length != 6) {
            return false;
        }
        return headers[0].trim().equals("Transaction Date") &&
                headers[1].trim().equals("Transaction Type") &&
                headers[2].trim().equals("Currency") &&
                headers[3].trim().equals("Amount") &&
                headers[4].trim().equals("Category") &&
                headers[5].trim().equals("Payment Method");
    }
    

//将用户的交易记录保存到一个 JSON 文件中
    private void saveTransactions(User user, List<Transaction> transactions) {
        File dir = new File(TRANSACTION_DIR);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File jsonFile = new File(dir, user.getUid() + ".json");

        // 覆盖写 => JSON数组
        try (Writer writer = new FileWriter(jsonFile, StandardCharsets.UTF_8, false)) {
            gson.toJson(transactions, writer);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
