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

public class GoalFormService {
    private static final Logger logger = LoggerFactory.getLogger(GoalFormService.class);
    private static final String DEFAULT_GOAL_TITLE = "New Goal";

    /**
     * 验证表单数据
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
     * 创建新的目标对象
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
     * 获取目标类型
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
     * 显示错误提示
     */
    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 解析字符串为double
     */
    private static double parseDouble(String text) throws NumberFormatException {
        return Double.parseDouble(text);
    }
} 