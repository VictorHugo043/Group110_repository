package com.myfinanceapp.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import com.myfinanceapp.security.EncryptionService;
import com.myfinanceapp.security.EncryptionService.EncryptedData;
import com.myfinanceapp.security.EncryptionService.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing financial goals in the application.
 * This class provides functionality for creating, reading, updating, and deleting financial goals,
 * as well as calculating progress and formatting goal-related data.
 * All goal data is encrypted before storage and decrypted when retrieved.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class GoalService {
    private static final String GOALS_DIRECTORY_PATH = "src/main/resources/goals/";
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger logger = LoggerFactory.getLogger(GoalService.class);
    private static final String FIXED_KEY = "MyFinanceAppSecretKey1234567890";  // Fixed encryption key

    /**
     * Retrieves all goals associated with a specific user.
     *
     * @param user The user whose goals are to be retrieved
     * @return A list of goals belonging to the user. Returns an empty list if no goals exist or if an error occurs
     */
    public static List<Goal> getUserGoals(User user) {
        try {
            return getAllGoals(user);
        } catch (IOException e) {
            logger.error("Failed to load goals", e);
            return new ArrayList<>();
        }
    }

    /**
     * Adds a new financial goal for a user.
     *
     * @param goal The goal to be added
     * @param user The user who owns the goal
     * @throws IOException If there is an error saving the goal
     */
    public static void addGoal(Goal goal, User user) throws IOException {
        List<Goal> existingGoals = getAllGoals(user);
        setGoalId(goal);
        setUserId(goal, user);
        existingGoals.add(goal);
        saveGoals(existingGoals, user);
    }

    /**
     * Sets a unique identifier for a goal if one is not already present.
     *
     * @param goal The goal to set the ID for
     */
    private static void setGoalId(Goal goal) {
        if (goal.getId() == null || goal.getId().isEmpty()) {
            goal.setId(UUID.randomUUID().toString());
        }
    }

    /**
     * Associates a goal with a user by setting the user ID.
     *
     * @param goal The goal to set the user ID for
     * @param user The user to associate with the goal
     */
    private static void setUserId(Goal goal, User user) {
        if (user != null && (goal.getUserId() == null || goal.getUserId().isEmpty())) {
            goal.setUserId(user.getUid());
        }
    }

    /**
     * Updates an existing financial goal.
     *
     * @param goal The updated goal data
     * @param loggedUser The user performing the update
     * @throws IOException If there is an error saving the updated goal
     * @throws SecurityException If the user is not authorized to update the goal
     */
    public static void updateGoal(Goal goal, User loggedUser) throws IOException {
        // Validate user permissions
        if (!goal.getUserId().equals(loggedUser.getUid())) {
            throw new SecurityException("User not authorized to update this goal");
        }

        // Get all goals
        List<Goal> goals = getUserGoals(loggedUser);

        // Find and update the goal
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getId().equals(goal.getId())) {
                goals.set(i, goal);
                break;
            }
        }

        // Save updated goals list
        saveGoals(goals, loggedUser);
    }

    /**
     * Deletes a specific goal for a user.
     *
     * @param goalId The ID of the goal to delete
     * @param user The user who owns the goal
     * @throws IOException If there is an error saving the updated goals list
     */
    public static void deleteGoal(String goalId, User user) throws IOException {
        List<Goal> existingGoals = getAllGoals(user);
        existingGoals.removeIf(goal -> goal.getId().equals(goalId));
        saveGoals(existingGoals, user);
    }

    /**
     * Retrieves all goals for a user from the encrypted storage.
     *
     * @param user The user whose goals are to be retrieved
     * @return A list of all goals for the user
     * @throws IOException If there is an error reading or decrypting the goals
     */
    private static List<Goal> getAllGoals(User user) throws IOException {
        File goalsFile = getGoalsFile(user);
        if (!goalsFile.exists() || goalsFile.length() == 0) {
            return new ArrayList<>();
        }
        try {
            String encryptedContent = new String(java.nio.file.Files.readAllBytes(goalsFile.toPath()));
            EncryptedData encryptedData = objectMapper.readValue(encryptedContent, EncryptedData.class);
            SecretKey key = getEncryptionKey(user);
            String decryptedContent = EncryptionService.decrypt(encryptedData, key);
            return objectMapper.readValue(decryptedContent, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, Goal.class));
        } catch (Exception e) {
            logger.error("Error parsing goals file: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Saves a list of goals to encrypted storage.
     *
     * @param goals The list of goals to save
     * @param user The user who owns the goals
     * @throws IOException If there is an error saving or encrypting the goals
     */
    public static void saveGoals(List<Goal> goals, User user) throws IOException {
        ensureDirectoryExists();
        File goalsFile = getGoalsFile(user);
        try {
            String content = objectMapper.writeValueAsString(goals);
            SecretKey key = getEncryptionKey(user);
            EncryptedData encryptedData = EncryptionService.encrypt(content, key);
            String encryptedContent = objectMapper.writeValueAsString(encryptedData);
            java.nio.file.Files.write(goalsFile.toPath(), encryptedContent.getBytes());
        } catch (EncryptionException e) {
            logger.error("Error encrypting goals: " + e.getMessage(), e);
            throw new IOException("Failed to encrypt goals", e);
        }
    }

    /**
     * Ensures that the goals directory exists, creating it if necessary.
     */
    private static void ensureDirectoryExists() {
        File directory = new File(GOALS_DIRECTORY_PATH);
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("Failed to create directory: {}", directory.getAbsolutePath());
        }
    }

    /**
     * Gets the file object for storing a user's goals.
     *
     * @param user The user whose goals file is needed
     * @return A File object representing the user's goals file
     */
    private static File getGoalsFile(User user) {
        String fileName = user.getUid() + ".json";
        return new File(GOALS_DIRECTORY_PATH + fileName);
    }

    /**
     * Derives an encryption key for a user's goals.
     *
     * @param user The user for whom to derive the key
     * @return A SecretKey for encrypting/decrypting the user's goals
     * @throws EncryptionException If there is an error deriving the key
     */
    private static SecretKey getEncryptionKey(User user) throws EncryptionException {
        // Use fixed key and user ID to derive encryption key
        byte[] salt = user.getUid().getBytes();  // Use user ID as salt
        return EncryptionService.deriveKey(FIXED_KEY, salt);
    }

    /**
     * Calculates the progress percentage for a saving goal.
     *
     * @param currentBalance The current amount saved
     * @param targetAmount The target amount to save
     * @return The progress percentage (0-100)
     */
    public static double calculateSavingProgress(double currentBalance, double targetAmount) {
        if (targetAmount <= 0) return 0;
        return Math.min(100, (currentBalance / targetAmount) * 100);
    }

    /**
     * Calculates the progress percentage for a debt repayment goal.
     *
     * @param amountPaid The amount paid towards the debt
     * @param totalDebt The total debt amount
     * @return The progress percentage (0-100)
     */
    public static double calculateDebtProgress(double amountPaid, double totalDebt) {
        if (totalDebt <= 0) return 0;
        return Math.min(100, (amountPaid / totalDebt) * 100);
    }

    /**
     * Calculates the budget usage percentage.
     *
     * @param currentExpense The current expense amount
     * @param budgetAmount The total budget amount
     * @return The budget usage percentage
     */
    public static double calculateBudgetUsage(double currentExpense, double budgetAmount) {
        if (budgetAmount <= 0) return 0;
        return (currentExpense / budgetAmount) * 100;
    }

    /**
     * Formats a number to avoid scientific notation.
     *
     * @param number The number to format
     * @return A formatted string representation of the number
     */
    public static String formatNumber(double number) {
        java.math.BigDecimal bd = new java.math.BigDecimal(number);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        df.setRoundingMode(java.math.RoundingMode.HALF_UP);
        return df.format(bd);
    }

    /**
     * Gets the appropriate color for a goal's progress indicator.
     *
     * @param goalType The type of goal (SAVING, DEBT_REPAYMENT, BUDGET_CONTROL)
     * @param progress The current progress percentage
     * @param isCompleted Whether the goal is completed
     * @return The appropriate color for the progress indicator
     */
    public static javafx.scene.paint.Color getProgressColor(String goalType, double progress, boolean isCompleted) {
        switch (goalType) {
            case "SAVING":
                return javafx.scene.paint.Color.BLUE;
            case "DEBT_REPAYMENT":
                return isCompleted ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.BLUE;
            case "BUDGET_CONTROL":
                return progress > 100 ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.GREEN;
            default:
                return javafx.scene.paint.Color.GRAY;
        }
    }

    /**
     * Gets the text to display for a goal's progress indicator.
     *
     * @param goalType The type of goal (SAVING, DEBT_REPAYMENT, BUDGET_CONTROL)
     * @param progress The current progress percentage
     * @param isCompleted Whether the goal is completed
     * @return The text to display for the progress indicator
     */
    public static String getProgressText(String goalType, double progress, boolean isCompleted) {
        switch (goalType) {
            case "SAVING":
                return String.format("%.1f%%", progress);
            case "DEBT_REPAYMENT":
                return isCompleted ? "✓" : String.format("%.0f%%", progress);
            case "BUDGET_CONTROL":
                return progress > 100 ? "✗" : "✓";
            default:
                return String.format("%.0f%%", progress);
        }
    }

    /**
     * Gets the appropriate font size for a goal's progress indicator.
     *
     * @param goalType The type of goal (SAVING, DEBT_REPAYMENT, BUDGET_CONTROL)
     * @param isCompleted Whether the goal is completed
     * @return The font size for the progress indicator
     */
    public static int getProgressFontSize(String goalType, boolean isCompleted) {
        if (goalType.equals("DEBT_REPAYMENT") && isCompleted) {
            return 24;
        }
        if (goalType.equals("BUDGET_CONTROL")) {
            return 24;
        }
        return 18;
    }
}