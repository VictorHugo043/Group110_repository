package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myfinanceapp.model.Transaction;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    private static final String TRANSACTION_FILE = "src/main/resources/transactions.json";
    private static final Gson gson = new Gson();
    private static final Type TRANSACTION_LIST_TYPE = new TypeToken<List<Transaction>>(){}.getType();

    /**
     * 添加交易记录：检查交易是否重复，若无重复则保存
     */
    public boolean addTransaction(Transaction transaction) {
        //推荐使用loadTransactions()函数检查
        List<Transaction> transactions = loadTransactions();

        // 2. 检查是否有相同的交易
        for (Transaction t : transactions) {
            if (t.equals(transaction)) {
                // 如果交易已经存在，则不进行保存
                return false;
            }
        }

        // 3. 添加新交易记录
        transactions.add(transaction);

        // 4. 保存回JSON文件
        saveTransactions(transactions);
        return true;
    }

    /**
     * 从 JSON 文件加载所有交易记录
     */
    private List<Transaction> loadTransactions() {
        File jsonFile = new File(TRANSACTION_FILE);
        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)) {
            List<Transaction> transactions = gson.fromJson(reader, TRANSACTION_LIST_TYPE);
            return transactions != null ? transactions : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
            //如果是文件读取操作中的错误，可能需要更多的错误提示，或是将异常重新抛出，以便外部调用者知晓文件加载失败。
        }
    }

    /**
     * 从 CSV 文件导入多条交易记录
     * 假设 CSV 文件每行是一个交易记录，格式为：date,type,amount,currency,category,paymentMethod
     */

    public void importTransactionsFromCSV(File file) {
        List<Transaction> transactions = loadTransactions();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    try {
                        String transactionDate = parts[0];
                        String transactionType = parts[1];
                        String currency = parts[2];
                        double amount = Double.parseDouble(parts[3]);
                        String category = parts[4];
                        String paymentMethod = parts[5];

                        // 创建 Transaction 对象并添加到列表
                        Transaction transaction = new Transaction();
                        transaction.setTransactionDate(transactionDate);
                        transaction.setTransactionType(transactionType);
                        transaction.setCurrency(currency);
                        transaction.setAmount(amount);
                        transaction.setCategory(category);
                        transaction.setPaymentMethod(paymentMethod);

                        // !先检查一下原来文件里面有没有和新记录一样的地方，没有再加新纪录
                        transactions.add(transaction);
                    } catch (Exception e) {
                        // 捕获并处理转换错误，如日期格式错误等
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveTransactions(transactions);
    }

    /**
     * 保存多条交易记录到 JSON 文件
     */
    //new FileOutputStream(jsonFile) 默认情况下会清空文件内容并重新写入。因此，写入文件时，会先清空文件，然后写入 transactions 列表中的所有交易记录
    private void saveTransactions(List<Transaction> transactions) {
        File jsonFile = new File(TRANSACTION_FILE);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(jsonFile), StandardCharsets.UTF_8)) {
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
