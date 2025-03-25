package com.myfinanceapp.model;

import java.time.LocalDate;

public class Goal {
    private String id;
    private String type; // "SAVING", "DEBT_REPAYMENT", "BUDGET_CONTROL"
    private String title;
    private double targetAmount;
    private double currentAmount;
    private LocalDate deadline;
    private String category; // 仅用于预算控制目标

    public Goal() {
    }

    public Goal(String id, String type, String title, double targetAmount, 
               double currentAmount, LocalDate deadline, String category) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.category = category;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    // Helper methods
    public int getProgressPercentage() {
        if (targetAmount <= 0) return 0;
        return (int) Math.min(100, (currentAmount / targetAmount) * 100);
    }
    
    public boolean isCompleted() {
        if ("BUDGET_CONTROL".equals(type)) {
            return currentAmount <= targetAmount;
        }
        return currentAmount >= targetAmount;
    }
}