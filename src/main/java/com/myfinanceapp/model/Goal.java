package com.myfinanceapp.model;

import java.time.LocalDate;

public class Goal {
    private static final String TYPE_SAVING = "SAVING";
    private static final String TYPE_DEBT_REPAYMENT = "DEBT_REPAYMENT";
    private static final String TYPE_BUDGET_CONTROL = "BUDGET_CONTROL";

    private String id;
    private String userId;
    private String type;
    private String title;
    private double targetAmount;
    private double currentAmount;
    private LocalDate deadline;
    private String category;

    // No-argument constructor
    public Goal() {}

    // Private constructor for builder pattern
    private Goal(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.type = builder.type;
        this.title = builder.title;
        this.targetAmount = builder.targetAmount;
        this.currentAmount = builder.currentAmount;
        this.deadline = builder.deadline;
        this.category = builder.category;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public LocalDate getDeadline() { return deadline; }
    public String getCategory() { return category; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public void setCategory(String category) { this.category = category; }

    // Helper methods
    public int getProgressPercentage() {
        if (targetAmount == 0) return 0;
        return (int) Math.min(100, (currentAmount / targetAmount) * 100);
    }

    public boolean isCompleted() {
        return TYPE_BUDGET_CONTROL.equals(type)
                ? currentAmount <= targetAmount
                : currentAmount >= targetAmount;
    }

    // Builder class
    public static class Builder {
        private String id;
        private String userId;
        private String type;
        private String title;
        private double targetAmount;
        private double currentAmount;
        private LocalDate deadline;
        private String category;

        public Builder id(String id) { this.id = id; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder targetAmount(double targetAmount) { this.targetAmount = targetAmount; return this; }
        public Builder currentAmount(double currentAmount) { this.currentAmount = currentAmount; return this; }
        public Builder deadline(LocalDate deadline) { this.deadline = deadline; return this; }
        public Builder category(String category) { this.category = category; return this; }

        public Goal build() {
            return new Goal(this);
        }
    }
}