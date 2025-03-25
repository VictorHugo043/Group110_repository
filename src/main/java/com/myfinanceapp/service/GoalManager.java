package com.myfinanceapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myfinanceapp.model.Goal;
import com.myfinanceapp.model.User;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GoalManager {
    private static final String GOALS_FILE_PATH = "src/main/resources/goals/goals.json";
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    
    public static List<Goal> getUserGoals(User user) {
        File goalsFile = new File(GOALS_FILE_PATH);
        
        if (!goalsFile.exists()) {
            // 如果文件不存在，创建示例目标
            List<Goal> sampleGoals = createSampleGoals();
            try {
                saveGoals(sampleGoals);
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
            return sampleGoals;
        }
        
        try {
            return objectMapper.readValue(goalsFile, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Goal.class));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public static void saveGoals(List<Goal> goals) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(GOALS_FILE_PATH), goals);
    }
    
    public static void addGoal(Goal goal) throws IOException {
        List<Goal> existingGoals = getUserGoals(null); // 这里暂时不区分用户
        
        // 为新目标生成ID
        if (goal.getId() == null || goal.getId().isEmpty()) {
            goal.setId(UUID.randomUUID().toString());
        }
        
        existingGoals.add(goal);
        saveGoals(existingGoals);
    }
    
    public static void updateGoal(Goal updatedGoal) throws IOException {
        List<Goal> existingGoals = getUserGoals(null);
        
        for (int i = 0; i < existingGoals.size(); i++) {
            if (existingGoals.get(i).getId().equals(updatedGoal.getId())) {
                existingGoals.set(i, updatedGoal);
                break;
            }
        }
        
        saveGoals(existingGoals);
    }
    
    public static void deleteGoal(String goalId) throws IOException {
        List<Goal> existingGoals = getUserGoals(null);
        existingGoals.removeIf(goal -> goal.getId().equals(goalId));
        saveGoals(existingGoals);
    }
    
    private static List<Goal> createSampleGoals() {
        List<Goal> sampleGoals = new ArrayList<>();
        
        // 添加储蓄目标示例
        sampleGoals.add(new Goal(
            UUID.randomUUID().toString(),
            "SAVING",
            "Saving Goal",
            1000.0,
            600.0,
            LocalDate.of(2025, 3, 30),
            null
        ));
        
        // 添加债务偿还目标示例
        sampleGoals.add(new Goal(
            UUID.randomUUID().toString(),
            "DEBT_REPAYMENT",
            "Debt Repayment Goal",
            500.0,
            500.0,
            LocalDate.of(2025, 3, 30),
            null
        ));
        
        // 添加预算控制目标示例
        sampleGoals.add(new Goal(
            UUID.randomUUID().toString(),
            "BUDGET_CONTROL",
            "Budget Control Goal",
            2000.0,
            2200.0,
            LocalDate.of(2025, 3, 30),
            "Food expenses"
        ));
        
        return sampleGoals;
    }
}