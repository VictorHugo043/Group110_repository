package com.myfinanceapp.model;

import java.time.LocalDate;

/**
 * Represents a user's financial goal.
 * A goal can be a savings target, debt repayment, or budget control.
 * Each goal has a target amount, current amount, and optional deadline.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class Goal {
    /** Represents a savings type goal */
    private static final String TYPE_SAVING = "SAVING";
    /** Represents a debt repayment type goal */
    private static final String TYPE_DEBT_REPAYMENT = "DEBT_REPAYMENT";
    /** Represents a budget control type goal */
    private static final String TYPE_BUDGET_CONTROL = "BUDGET_CONTROL";
    /** Default currency unit */
    private static final String DEFAULT_CURRENCY = "CNY";

    /** Unique identifier for the goal */
    private String id;
    /** ID of the user who owns this goal */
    private String userId;
    /** Goal type (SAVING, DEBT_REPAYMENT, or BUDGET_CONTROL) */
    private String type;
    /** Goal title */
    private String title;
    /** Target amount for the goal */
    private double targetAmount;
    /** Current amount achieved */
    private double currentAmount;
    /** Deadline for goal completion */
    private LocalDate deadline;
    /** Category of the goal */
    private String category;
    /** Currency unit, defaults to CNY */
    private String currency = DEFAULT_CURRENCY;

    /**
     * Default constructor.
     * Creates an empty Goal object, properties need to be set using setter methods.
     */
    public Goal() {}

    /**
     * Creates a Goal object with basic properties.
     *
     * @param id Unique identifier for the goal
     * @param userId ID of the user who owns this goal
     * @param type Goal type
     * @param title Goal title
     * @param targetAmount Target amount for the goal
     */
    public Goal(String id, String userId, String type, String title, double targetAmount) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.targetAmount = targetAmount;
    }

    /**
     * Creates a Goal object with all properties.
     *
     * @param id Unique identifier for the goal
     * @param userId ID of the user who owns this goal
     * @param type Goal type
     * @param title Goal title
     * @param targetAmount Target amount for the goal
     * @param currentAmount Current amount achieved
     * @param deadline Deadline for goal completion
     * @param category Category of the goal
     * @param currency Currency unit, if null defaults to CNY
     */
    public Goal(String id, String userId, String type, String title, 
               double targetAmount, double currentAmount, LocalDate deadline,
               String category, String currency) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.category = category;
        this.currency = currency != null ? currency : DEFAULT_CURRENCY;
    }

    /**
     * Gets the unique identifier of the goal.
     * @return Goal ID
     */
    public String getId() { return id; }
    
    /**
     * Gets the ID of the user who owns this goal.
     * @return User ID
     */
    public String getUserId() { return userId; }
    
    /**
     * Gets the type of the goal.
     * @return Goal type (SAVING, DEBT_REPAYMENT, or BUDGET_CONTROL)
     */
    public String getType() { return type; }
    
    /**
     * Gets the title of the goal.
     * @return Goal title
     */
    public String getTitle() { return title; }
    
    /**
     * Gets the target amount of the goal.
     * @return Target amount
     */
    public double getTargetAmount() { return targetAmount; }
    
    /**
     * Gets the current amount achieved.
     * @return Current amount
     */
    public double getCurrentAmount() { return currentAmount; }
    
    /**
     * Gets the deadline for goal completion.
     * @return Deadline
     */
    public LocalDate getDeadline() { return deadline; }
    
    /**
     * Gets the category of the goal.
     * @return Goal category
     */
    public String getCategory() { return category; }
    
    /**
     * Gets the currency unit.
     * @return Currency unit
     */
    public String getCurrency() { return currency; }

    /**
     * Sets the unique identifier of the goal.
     * @param id Goal ID
     */
    public void setId(String id) { this.id = id; }
    
    /**
     * Sets the ID of the user who owns this goal.
     * @param userId User ID
     */
    public void setUserId(String userId) { this.userId = userId; }
    
    /**
     * Sets the type of the goal.
     * @param type Goal type (SAVING, DEBT_REPAYMENT, or BUDGET_CONTROL)
     */
    public void setType(String type) { this.type = type; }
    
    /**
     * Sets the title of the goal.
     * @param title Goal title
     */
    public void setTitle(String title) { this.title = title; }
    
    /**
     * Sets the target amount of the goal.
     * @param targetAmount Target amount
     */
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    
    /**
     * Sets the current amount achieved.
     * @param currentAmount Current amount
     */
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    
    /**
     * Sets the deadline for goal completion.
     * @param deadline Deadline
     */
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    
    /**
     * Sets the category of the goal.
     * @param category Goal category
     */
    public void setCategory(String category) { this.category = category; }
    
    /**
     * Sets the currency unit.
     * @param currency Currency unit
     */
    public void setCurrency(String currency) { this.currency = currency; }

    /**
     * Calculates the goal completion percentage.
     * Returns 0 if target amount is 0.
     * Returns 100 if completion exceeds 100%.
     *
     * @return Goal completion percentage (0-100)
     */
    public int getProgressPercentage() {
        if (targetAmount == 0) return 0;
        return (int) Math.min(100, (currentAmount / targetAmount) * 100);
    }

    /**
     * Determines if the goal is completed.
     * For budget control type, goal is considered complete when current amount is less than or equal to target amount;
     * For other types, goal is considered complete when current amount is greater than or equal to target amount.
     *
     * @return true if the goal is completed, false otherwise
     */
    public boolean isCompleted() {
        return TYPE_BUDGET_CONTROL.equals(type)
                ? currentAmount <= targetAmount
                : currentAmount >= targetAmount;
    }
}