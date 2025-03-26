package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;     // <-- import
import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;

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
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    Transaction tx = new Transaction();
                    tx.setTransactionDate(parts[0]);
                    tx.setTransactionType(parts[1]);
                    tx.setCurrency(parts[2]);
                    tx.setAmount(Double.parseDouble(parts[3]));
                    tx.setCategory(parts[4]);
                    tx.setPaymentMethod(parts[5]);

                    boolean dup = false;
                    for (Transaction existing : allTxs) {
                        if (existing.equals(tx)) {
                            dup = true;
                            break;
                        }
                    }
                    if (!dup) {
                        allTxs.add(tx);
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        saveTransactions(user, allTxs);
    }

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
