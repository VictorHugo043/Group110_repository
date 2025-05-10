package com.myfinanceapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myfinanceapp.model.Transaction;
import com.myfinanceapp.model.User;
import com.myfinanceapp.security.EncryptionService;
import com.myfinanceapp.security.EncryptionService.EncryptedData;
import com.myfinanceapp.security.EncryptionService.EncryptionException;

import javafx.scene.control.Alert;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.SecretKey;
import java.nio.file.Files;

public class TransactionService {
    private static final String TRANSACTION_DIR = "src/main/resources/transaction/";
    private static final Gson gson = new Gson();
    private static final String FIXED_KEY = "MyFinanceAppSecretKey1234567890"; // Fixed key

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
     * Read <UID>.json file: which contains a JSON array format [ {...}, {...} ]
     */
    public List<Transaction> loadTransactions(User user) {
        File dir = new File(TRANSACTION_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File jsonFile = new File(dir, user.getUid() + ".json");
        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        try {
            // Read the entire file content
            String content = new String(Files.readAllBytes(jsonFile.toPath()), StandardCharsets.UTF_8);
            EncryptedData encryptedData = gson.fromJson(content, EncryptedData.class);

            // Get encryption key derived from user ID
            SecretKey key = getEncryptionKey(user);

            // Decrypt the content
            String decryptedContent = EncryptionService.decrypt(encryptedData, key);

            Type listType = new TypeToken<List<Transaction>>() {
            }.getType();
            List<Transaction> txList = gson.<List<Transaction>>fromJson(decryptedContent, listType);

            return (txList != null) ? txList : new ArrayList<>();
        } catch (IOException | EncryptionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void importTransactionsFromCSV(User user, File csvFile) {
        List<Transaction> allTxs = loadTransactions(user);
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            // Read the CSV file header
            String headerLine = br.readLine();
            if (headerLine == null) {
                return;
            }

            String[] headers = headerLine.split(",");
            // Split header fields and validate if the header format meets expectations
            boolean isValidHeader = validateHeader(headers);
            // If the header doesn't match the expected format, display an error prompt and
            // abort import
            if (!isValidHeader) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid CSV Format");
                alert.setHeaderText(null);
                alert.setContentText("The CSV file must contain the following headers:\n" +
                        "Transaction Date, Transaction Type, Currency, Amount, Description, Category, Payment Method");
                alert.showAndWait();
                return;
            }
            // Loop through each line of data until the end of file
            while ((line = br.readLine()) != null) {
                // Split each line of data by comma
                String[] parts = line.split(",");
                // Check if each line of data contains 7 columns
                if (parts.length == 7) {
                    // Create a new transaction record object
                    Transaction tx = new Transaction();
                    // Convert date format from YYYY/M/D to YYYY-MM-DD
                    String[] dateParts = parts[0].trim().split("/");
                    String formattedDate = String.format("%s-%02d-%02d",
                            dateParts[0],
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[2]));
                    tx.setTransactionDate(formattedDate);
                    tx.setTransactionType(parts[1].trim());
                    tx.setCurrency(parts[2].trim());
                    try {
                        tx.setAmount(Double.parseDouble(parts[3].trim()));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    tx.setDescription(parts[4].trim());
                    tx.setCategory(parts[5].trim());
                    tx.setPaymentMethod(parts[6].trim());
                    // Check for duplicate transaction records
                    boolean dup = false;
                    for (Transaction existing : allTxs) {
                        if (existing.equals(tx)) {
                            dup = true;
                            break;
                        }
                    }
                    // If no duplicate transaction exists, add the new transaction record
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
        // Save the updated transaction records to local file
        saveTransactions(user, allTxs);
    }

    // Define a validateHeader method to verify if the CSV file header meets the
    // expected format
    private boolean validateHeader(String[] headers) {
        if (headers.length != 7) {
            return false;
        }
        return headers[0].trim().equals("Transaction Date") &&
                headers[1].trim().equals("Transaction Type") &&
                headers[2].trim().equals("Currency") &&
                headers[3].trim().equals("Amount") &&
                headers[4].trim().equals("Description") &&
                headers[5].trim().equals("Category") &&
                headers[6].trim().equals("Payment Method");
    }

    // Save the user's transaction records to a JSON file
    public void saveTransactions(User user, List<Transaction> transactions) {
        File dir = new File(TRANSACTION_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File jsonFile = new File(dir, user.getUid() + ".json");

        try {
            // Convert transactions to JSON string
            String jsonContent = gson.toJson(transactions);

            // Get encryption key derived from user ID
            SecretKey key = getEncryptionKey(user);

            // Encrypt the content
            EncryptedData encryptedData = EncryptionService.encrypt(jsonContent, key);

            // Save encrypted content as JSON
            String content = gson.toJson(encryptedData);
            Files.write(jsonFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException | EncryptionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get encryption key
     */
    private static SecretKey getEncryptionKey(User user) throws EncryptionException {
        // Use fixed key and user ID to derive encryption key
        byte[] salt = user.getUid().getBytes(); // Use user ID as salt value
        return EncryptionService.deriveKey(FIXED_KEY, salt);
    }
}
