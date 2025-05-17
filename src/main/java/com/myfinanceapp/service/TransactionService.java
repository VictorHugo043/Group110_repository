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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.SecretKey;
import java.nio.file.Files;

// Apache POI imports for Excel processing
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Service class for managing financial transactions in the application.
 * This class provides functionality for:
 * - Adding new transactions
 * - Loading existing transactions
 * - Importing transactions from multiple file formats (CSV, CSV UTF-8, Excel)
 * - Saving transactions with encryption
 * - Validating transaction data
 * 
 * All transaction data is encrypted before storage and decrypted when retrieved
 * to ensure data security.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class TransactionService {
    /** Directory where transaction files are stored */
    private static final String TRANSACTION_DIR = "src/main/resources/transaction/";

    /** Gson instance for JSON serialization/deserialization */
    private static final Gson gson = new Gson();

    /** Fixed encryption key for transaction data */
    private static final String FIXED_KEY = "MyFinanceAppSecretKey1234567890";

    /**
     * Adds a new transaction for a user.
     * Checks for duplicates before adding the transaction.
     *
     * @param user  The user who owns the transaction
     * @param newTx The new transaction to add
     * @return true if the transaction was added successfully, false if it was a
     *         duplicate
     */
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
     * Loads all transactions for a specific user from the encrypted storage.
     * The transactions are stored in a JSON file named after the user's UID.
     *
     * @param user The user whose transactions to load
     * @return A list of transactions for the user, or an empty list if none exist
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

    /**
     * Imports transactions from a file for a user.
     * Supports CSV files (both UTF-8 and default encoding) and Excel (xlsx) files.
     * Validates the file format and handles duplicate transactions.
     * Shows appropriate alerts for success or failure.
     *
     * @param user The user to import transactions for
     * @param file The file containing the transactions (CSV or Excel)
     */
    public void importTransactions(User user, File file) {
        LanguageService languageService = LanguageService.getInstance();
        List<Transaction> allTxs = loadTransactions(user);
        String fileName = file.getName().toLowerCase();

        try {
            if (fileName.endsWith(".csv")) {
                // Try UTF-8 first, fall back to default encoding if that fails
                try {
                    importFromCSV(user, file, StandardCharsets.UTF_8, allTxs);
                } catch (Exception e) {
                    // If UTF-8 fails, try with default charset
                    importFromCSV(user, file, Charset.defaultCharset(), allTxs);
                }
            } else if (fileName.endsWith(".xlsx")) {
                importFromExcel(user, file, allTxs);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(languageService.getTranslation("unsupported_file_format"));
                alert.setHeaderText(null);
                alert.setContentText(languageService.getTranslation("unsupported_file_message"));
                alert.showAndWait();
                return;
            }

            // Save transactions after successful import
            saveTransactions(user, allTxs);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(languageService.getTranslation("import_successful"));
            alert.setHeaderText(null);
            alert.setContentText(languageService.getTranslation("import_success_message"));
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(languageService.getTranslation("file_read_error"));
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Imports transactions from a CSV file with the specified charset.
     *
     * @param user    The user to import transactions for
     * @param csvFile The CSV file containing the transactions
     * @param charset The charset to use for reading the file
     * @param allTxs  The list of existing transactions to add to
     * @throws IOException If there is an error reading the file
     */
    private void importFromCSV(User user, File csvFile, Charset charset, List<Transaction> allTxs) throws IOException {
        LanguageService languageService = LanguageService.getInstance();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), charset))) {
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
                throw new IOException(languageService.getTranslation("invalid_csv_format") + ": " +
                        "The CSV file must contain the following headers:\n" +
                        "Transaction Date, Transaction Type, Currency, Amount, Description, Category, Payment Method");
            }

            // Loop through each line of data until the end of file
            while ((line = br.readLine()) != null) {
                Transaction tx = processCSVLine(line);
                if (tx != null) {
                    addTransactionIfNotDuplicate(tx, allTxs);
                }
            }
        }
    }

    /**
     * Imports transactions from an Excel file.
     *
     * @param user      The user to import transactions for
     * @param excelFile The Excel file containing the transactions
     * @param allTxs    The list of existing transactions to add to
     * @throws IOException If there is an error reading the file
     */
    private void importFromExcel(User user, File excelFile, List<Transaction> allTxs) throws IOException {
        LanguageService languageService = LanguageService.getInstance();

        try (FileInputStream fis = new FileInputStream(excelFile);
                Workbook workbook = new XSSFWorkbook(fis)) {

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Get the header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return;
            }

            // Validate header
            String[] headers = new String[7];
            for (int i = 0; i < 7; i++) {
                Cell cell = headerRow.getCell(i);
                headers[i] = (cell != null) ? cell.toString().trim() : "";
            }

            boolean isValidHeader = validateHeader(headers);
            if (!isValidHeader) {
                throw new IOException(languageService.getTranslation("invalid_xlsx_format") + ": " +
                        "The Excel file must contain the following headers:\n" +
                        "Transaction Date, Transaction Type, Currency, Amount, Description, Category, Payment Method");
            }

            // Process each row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Transaction tx = processExcelRow(row);
                    if (tx != null) {
                        addTransactionIfNotDuplicate(tx, allTxs);
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException(languageService.getTranslation("excel_read_error") + ": " + e.getMessage(), e);
        }
    }

    /**
     * Processes a CSV line and converts it to a Transaction.
     *
     * @param line The CSV line to process
     * @return A Transaction object, or null if the line could not be processed
     */
    private Transaction processCSVLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 7) {
            return null;
        }

        try {
            Transaction tx = new Transaction();
            // Process date - handle both yyyy/MM/dd and yyyy-MM-dd formats
            String datePart = parts[0].trim();
            if (datePart.contains("/")) {
                String[] dateParts = datePart.split("/");
                if (dateParts.length == 3) {
                    String formattedDate = String.format("%s-%02d-%02d",
                            dateParts[0],
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[2]));
                    tx.setTransactionDate(formattedDate);
                }
            } else {
                tx.setTransactionDate(datePart);
            }

            tx.setTransactionType(parts[1].trim());
            tx.setCurrency(parts[2].trim());
            tx.setAmount(Double.parseDouble(parts[3].trim()));
            tx.setDescription(parts[4].trim());
            tx.setCategory(parts[5].trim());
            tx.setPaymentMethod(parts[6].trim());

            return tx;
        } catch (Exception e) {
            return null; // Skip this line if there's any error
        }
    }

    /**
     * Processes an Excel row and converts it to a Transaction.
     *
     * @param row The Excel row to process
     * @return A Transaction object, or null if the row could not be processed
     */
    private Transaction processExcelRow(Row row) {
        try {
            Transaction tx = new Transaction();

            // Process date
            Cell dateCell = row.getCell(0);
            if (dateCell == null)
                return null;

            String dateStr;
            if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                java.util.Date date = dateCell.getDateCellValue();
                java.time.LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                dateStr = localDate.toString(); // Format as yyyy-MM-dd
            } else {
                dateStr = dateCell.toString().trim();
                // Handle slash format if needed
                if (dateStr.contains("/")) {
                    String[] dateParts = dateStr.split("/");
                    if (dateParts.length == 3) {
                        dateStr = String.format("%s-%02d-%02d",
                                dateParts[0],
                                Integer.parseInt(dateParts[1]),
                                Integer.parseInt(dateParts[2]));
                    }
                }
            }
            tx.setTransactionDate(dateStr);

            // Process other fields
            tx.setTransactionType(getCellValueAsString(row.getCell(1)));
            tx.setCurrency(getCellValueAsString(row.getCell(2)));

            // Process amount
            Cell amountCell = row.getCell(3);
            if (amountCell != null) {
                if (amountCell.getCellType() == CellType.NUMERIC) {
                    tx.setAmount(amountCell.getNumericCellValue());
                } else {
                    tx.setAmount(Double.parseDouble(amountCell.toString().trim()));
                }
            } else {
                return null; // Amount is required
            }

            tx.setDescription(getCellValueAsString(row.getCell(4)));
            tx.setCategory(getCellValueAsString(row.getCell(5)));
            tx.setPaymentMethod(getCellValueAsString(row.getCell(6)));

            return tx;
        } catch (Exception e) {
            return null; // Skip this row if there's any error
        }
    }

    /**
     * Gets a cell value as string, handling different cell types.
     *
     * @param cell The cell to get the value from
     * @return The cell value as string
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getStringCellValue().trim();
                    case NUMERIC:
                        return String.valueOf(cell.getNumericCellValue());
                    default:
                        return "";
                }
            default:
                return "";
        }
    }

    /**
     * Adds a transaction to the list if it's not a duplicate.
     *
     * @param tx           The transaction to add
     * @param transactions The list of transactions to check for duplicates
     * @return true if the transaction was added, false if it was a duplicate
     */
    private boolean addTransactionIfNotDuplicate(Transaction tx, List<Transaction> transactions) {
        for (Transaction existing : transactions) {
            if (existing.equals(tx)) {
                return false;
            }
        }
        transactions.add(tx);
        return true;
    }

    /**
     * Validates the header row of a CSV file to ensure it matches the expected
     * format.
     *
     * @param headers The array of header strings from the CSV file
     * @return true if the headers match the expected format, false otherwise
     */
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

    /**
     * Saves a list of transactions for a user to encrypted storage.
     * The transactions are encrypted before being saved to ensure data security.
     *
     * @param user         The user who owns the transactions
     * @param transactions The list of transactions to save
     * @throws IOException         If there is an error writing to the file
     * @throws EncryptionException If there is an error encrypting the data
     */
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
     * Derives an encryption key for a user's transactions.
     * Uses a fixed key and the user's ID to create a unique encryption key.
     *
     * @param user The user for whom to derive the key
     * @return A SecretKey for encrypting/decrypting the user's transactions
     * @throws EncryptionException If there is an error deriving the key
     */
    private static SecretKey getEncryptionKey(User user) throws EncryptionException {
        // Use fixed key and user ID to derive encryption key
        byte[] salt = user.getUid().getBytes(); // Use user ID as salt value
        return EncryptionService.deriveKey(FIXED_KEY, salt);
    }

    /**
     * Deprecated method to maintain backwards compatibility.
     * Please use importTransactions() instead.
     * 
     * @param user    The user to import transactions for
     * @param csvFile The CSV file containing the transactions
     * @deprecated Use {@link #importTransactions(User, File)} instead
     */
    @Deprecated
    public void importTransactionsFromCSV(User user, File csvFile) {
        importTransactions(user, csvFile);
    }
}
