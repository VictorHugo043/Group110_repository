package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 每个用户单独一个文件:
 *   src/main/resources/transaction/<UID>.json
 * 存储格式: 每个交易占一行, 行内是 JSON 对象 (NDJSON / JSONL)
 */
public class TransactionService {

    private static final String TRANSACTION_DIR = "src/main/resources/transaction/";
    private static final Gson gson = new Gson();

    /**
     * 添加一条交易记录:
     *  1) 从 <UID>.json 读取所有交易
     *  2) 检查是否已存在
     *  3) 不存在则追加写入 (换行 + JSON)
     */

    public boolean addTransaction(User user, Transaction newTx) {
        // 1. 先从用户专属文件加载所有交易
        List<Transaction> allTxs = loadTransactions(user);

        // 2. 检查是否重复
        for(Transaction t: allTxs) {
            if(t.equals(newTx)) {
                // 重复则不再添加
                return false;
            }
        }

        // 3. 添加
        allTxs.add(newTx);

        // 4. 保存回 JSON
        saveTransactions(user, allTxs);
        return true;
    }

    /**
     * 从 <UID>.json 读取所有交易(每行一个JSON), parse成Transaction, 返回List
     */
    private static final Type TYPE_TRAN_LIST = new TypeToken<List<Transaction>>(){}.getType();

    private List<Transaction> loadTransactions(User user){
        // 1) 准备路径
        File dir = new File(TRANSACTION_DIR);
        if(!dir.exists()){
            dir.mkdirs();
        }
        // 文件名 = UID + ".json"
        File jsonFile = new File(dir, user.getUid() + ".json");
        if(!jsonFile.exists()){
            // 不存在 => return new ArrayList<>();
            return new ArrayList<>();
        }

        // 存在 => 读整个 JSON 数组
        try(Reader reader = new FileReader(jsonFile)){
            List<Transaction> list = gson.fromJson(reader, TYPE_TRAN_LIST);
            return list != null ? list : new ArrayList<>();
        } catch(IOException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * CSV 导入 => 读取 CSV => 对每条记录 => 若没重复 => append
     */
    public void importTransactionsFromCSV(User user, File csvFile) {
        List<Transaction> allTxs = loadTransactions(user);
        // 解析 CSV
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while((line = br.readLine()) != null){
                String[] parts = line.split(",");
                if(parts.length == 6){
                    Transaction tx = new Transaction();
                    tx.setTransactionDate(parts[0]);
                    tx.setTransactionType(parts[1]);
                    tx.setCurrency(parts[2]);
                    tx.setAmount(Double.parseDouble(parts[3]));
                    tx.setCategory(parts[4]);
                    tx.setPaymentMethod(parts[5]);
                    // 也可先检查重复
                    // or just add
                    allTxs.add(tx);
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }

        // 全部写回
        saveTransactions(user, allTxs);
    }

    /**
     * 追加写入: 在 <UID>.json 末尾加一行 JSON
     */
    private void appendTransactionToFile(String uid, Transaction tx){
        File file = getUserFile(uid);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
            // gson.toJson(tx) => {"transactionDate":"...","transactionType":"..."}
            bw.write(gson.toJson(tx));
            bw.newLine();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 获取用户对应的NDJSON文件对象
     */
    private File getUserFile(String uid){
        return new File(TRANSACTION_DIR + uid + ".json");
    }

    private void saveTransactions(User user, List<Transaction> transactions){
        File dir = new File(TRANSACTION_DIR);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File jsonFile = new File(dir, user.getUid() + ".json");

        try(Writer writer = new FileWriter(jsonFile, false)){
            // false => 覆盖写
            gson.toJson(transactions, writer);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
