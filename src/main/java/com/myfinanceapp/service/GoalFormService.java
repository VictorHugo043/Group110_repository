package com.myfinanceapp.service;

import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Service class for handling financial goal form operations.
 * This class provides functionality for:
 * - Validating goal form inputs
 * - Creating new financial goals
 * - Managing goal types and categories
 * - Error handling and user feedback
 *
 * @author SE_Group110
 * @version 4.0
 */
public class GoalFormService {
    private static final Logger logger = LoggerFactory.getLogger(GoalFormService.class);
    private static final String DEFAULT_GOAL_TITLE = "New Goal";

    /**
     * Validates the goal form data.
     * Checks for:
     * - User login status
     * - Valid amount format and value
     * - Future deadline date
     *
     * @param loggedUser The currently logged-in user
     * @param titleField The goal title input field
     * @param amountField The target amount input field
     * @param typeCombo The goal type selection combo box
     * @param categoryField The goal category input field
     * @param deadlinePicker The deadline date picker
     * @return true if all validations pass, false otherwise
     */
    public static boolean validateForm(User loggedUser, TextField titleField, TextField amountField, 
                                     ComboBox<String> typeCombo, TextField categoryField, DatePicker deadlinePicker) {
        // Check if user is logged in
        if (loggedUser == null) {
            showErrorAlert("User not logged in");
            return false;
        }
        
        // Validate amount
        try {
            double amount = parseDouble(amountField.getText());
            if (amount <= 0) {
                showErrorAlert("Amount must be greater than zero");
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid amount format - please enter a valid number");
            return false;
        }
        
        // Validate deadline
        if (deadlinePicker.getValue().isBefore(LocalDate.now())) {
            showErrorAlert("Deadline must be in the future");
            return false;
        }
        
        return true;
    }

    /**
     * Creates a new Goal object with the provided information.
     *
     * @param loggedUser The user creating the goal
     * @param title The goal title
     * @param amount The target amount
     * @param type The goal type
     * @param category The goal category
     * @param deadline The goal deadline
     * @param currency The currency for the goal
     * @return A new Goal object with the specified properties
     */
    public static Goal createNewGoal(User loggedUser, String title, String amount, String type, 
                                   String category, LocalDate deadline, String currency) {
        Goal newGoal = new Goal();
        newGoal.setId(java.util.UUID.randomUUID().toString());
        newGoal.setUserId(loggedUser.getUid());
        newGoal.setType(getGoalType(type));
        newGoal.setTitle(title.isEmpty() ? DEFAULT_GOAL_TITLE : title);
        newGoal.setTargetAmount(Double.parseDouble(amount));
        newGoal.setCurrentAmount(0.0);
        newGoal.setDeadline(deadline);
        newGoal.setCategory(category);
        newGoal.setCurrency(currency);
        return newGoal;
    }

    /**
     * Determines the goal type based on the user's selection.
     *
     * @param selection The user's goal type selection
     * @return The standardized goal type string
     */
    private static String getGoalType(String selection) {
        if (selection.startsWith("Saving")) {
            return "SAVING";
        } else if (selection.startsWith("Debt")) {
            return "DEBT_REPAYMENT";
        } else {
            return "BUDGET_CONTROL";
        }
    }

    /**
     * Displays an error alert dialog with the specified message.
     *
     * @param message The error message to display
     */
    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Parses a string to a double value.
     *
     * @param text The string to parse
     * @return The parsed double value
     * @throws NumberFormatException If the string cannot be parsed to a double
     */
    private static double parseDouble(String text) throws NumberFormatException {
        return Double.parseDouble(text);
    }
} 