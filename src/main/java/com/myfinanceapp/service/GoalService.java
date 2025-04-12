package com.myfinanceapp.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GoalService {
    private static final String GOALS_DIRECTORY_PATH = "src/main/resources/goals/";
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger logger = LoggerFactory.getLogger(GoalService.class);

    /**
     * 获取用户的目标列表
     */
    public static List<Goal> getUserGoals(User user) {
        try {
            return getAllGoals(user);
        } catch (IOException e) {
            logger.error("加载目标失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 添加新目标
     */
    public static void addGoal(Goal goal, User user) throws IOException {
        List<Goal> existingGoals = getAllGoals(user);
        setGoalId(goal);
        setUserId(goal, user);
        existingGoals.add(goal);
        saveGoals(existingGoals, user);
    }

    /**
     * 设置目标ID（如果未提供则生成）
     */
    private static void setGoalId(Goal goal) {
        if (goal.getId() == null || goal.getId().isEmpty()) {
            goal.setId(UUID.randomUUID().toString());
        }
    }

    /**
     * 设置用户ID（如果未提供且用户存在）
     */
    private static void setUserId(Goal goal, User user) {
        if (user != null && (goal.getUserId() == null || goal.getUserId().isEmpty())) {
            goal.setUserId(user.getUid());
        }
    }

    /**
     * 更新目标
     */
    public static void updateGoal(Goal updatedGoal, User user) throws IOException {
        List<Goal> existingGoals = getAllGoals(user);
        existingGoals = existingGoals.stream()
                .map(goal -> goal.getId().equals(updatedGoal.getId()) ? updatedGoal : goal)
                .collect(Collectors.toList());
        saveGoals(existingGoals, user);
    }

    /**
     * 删除目标
     */
    public static void deleteGoal(String goalId, User user) throws IOException {
        List<Goal> existingGoals = getAllGoals(user);
        existingGoals.removeIf(goal -> goal.getId().equals(goalId));
        saveGoals(existingGoals, user);
    }

    /**
     * 获取所有目标（不过滤）
     */
    private static List<Goal> getAllGoals(User user) throws IOException {
        File goalsFile = getGoalsFile(user);
        if (!goalsFile.exists() || goalsFile.length() == 0) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(goalsFile, objectMapper.getTypeFactory().constructCollectionType(List.class, Goal.class));
        } catch (Exception e) {
            logger.error("Error parsing goals file: " + e.getMessage(), e);
            // If there's a parsing error, return an empty list
            return new ArrayList<>();
        }
    }

    /**
     * 保存目标到文件
     */
    public static void saveGoals(List<Goal> goals, User user) throws IOException {
        ensureDirectoryExists();
        File goalsFile = getGoalsFile(user);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(goalsFile, goals);
    }

    /**
     * 确保目录存在
     */
    private static void ensureDirectoryExists() {
        File directory = new File(GOALS_DIRECTORY_PATH);
        if (!directory.exists() && !directory.mkdirs()) {
            logger.error("创建目录失败: {}", directory.getAbsolutePath());
        }
    }

    /**
     * 获取目标文件
     */
    private static File getGoalsFile(User user) {
        String fileName = user.getUid() + ".json";
        return new File(GOALS_DIRECTORY_PATH + fileName);
    }

    /**
     * 计算储蓄目标的进度百分比
     */
    public static double calculateSavingProgress(double currentBalance, double targetAmount) {
        if (targetAmount <= 0) return 0;
        return Math.min(100, (currentBalance / targetAmount) * 100);
    }

    /**
     * 计算债务还款目标的进度百分比
     */
    public static double calculateDebtProgress(double amountPaid, double totalDebt) {
        if (totalDebt <= 0) return 0;
        return Math.min(100, (amountPaid / totalDebt) * 100);
    }

    /**
     * 计算预算使用百分比
     */
    public static double calculateBudgetUsage(double currentExpense, double budgetAmount) {
        if (budgetAmount <= 0) return 0;
        return (currentExpense / budgetAmount) * 100;
    }

    /**
     * 格式化数字，避免科学计数法
     */
    public static String formatNumber(double number) {
        java.math.BigDecimal bd = new java.math.BigDecimal(number);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        df.setRoundingMode(java.math.RoundingMode.HALF_UP);
        return df.format(bd);
    }

    /**
     * 获取目标进度指示器的颜色
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
     * 获取目标进度指示器的文本
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
     * 获取目标进度指示器的字体大小
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