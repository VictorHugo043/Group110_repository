package com.myfinanceapp.model;

import java.time.LocalDate;

/**
 * 表示用户的财务目标。
 * 一个目标可以是储蓄目标、债务偿还或预算控制。
 * 每个目标都有目标金额、当前金额和可选的截止日期。
 */
public class Goal {
    /** 表示储蓄类型的目标 */
    private static final String TYPE_SAVING = "SAVING";
    /** 表示债务偿还类型的目标 */
    private static final String TYPE_DEBT_REPAYMENT = "DEBT_REPAYMENT";
    /** 表示预算控制类型的目标 */
    private static final String TYPE_BUDGET_CONTROL = "BUDGET_CONTROL";
    /** 默认货币单位 */
    private static final String DEFAULT_CURRENCY = "CNY";

    /** 目标的唯一标识符 */
    private String id;
    /** 拥有此目标的用户ID */
    private String userId;
    /** 目标类型(SAVING, DEBT_REPAYMENT, 或 BUDGET_CONTROL) */
    private String type;
    /** 目标标题 */
    private String title;
    /** 目标金额 */
    private double targetAmount;
    /** 当前已完成的金额 */
    private double currentAmount;
    /** 目标完成的截止日期 */
    private LocalDate deadline;
    /** 目标所属类别 */
    private String category;
    /** 货币单位，默认为CNY */
    private String currency = DEFAULT_CURRENCY;

    /**
     * 默认构造函数。
     * 创建一个空的Goal对象，需要通过setter方法设置属性。
     */
    public Goal() {}

    /**
     * 创建一个包含基本属性的Goal对象。
     *
     * @param id 目标的唯一标识符
     * @param userId 拥有此目标的用户ID
     * @param type 目标类型
     * @param title 目标标题
     * @param targetAmount 目标金额
     */
    public Goal(String id, String userId, String type, String title, double targetAmount) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.targetAmount = targetAmount;
    }

    /**
     * 创建一个包含所有属性的Goal对象。
     *
     * @param id 目标的唯一标识符
     * @param userId 拥有此目标的用户ID
     * @param type 目标类型
     * @param title 目标标题
     * @param targetAmount 目标金额
     * @param currentAmount 当前已完成的金额
     * @param deadline 目标完成的截止日期
     * @param category 目标所属类别
     * @param currency 货币单位，如为null则使用默认值CNY
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
     * 获取目标的唯一标识符。
     * @return 目标ID
     */
    public String getId() { return id; }
    
    /**
     * 获取拥有此目标的用户ID。
     * @return 用户ID
     */
    public String getUserId() { return userId; }
    
    /**
     * 获取目标类型。
     * @return 目标类型(SAVING, DEBT_REPAYMENT, 或 BUDGET_CONTROL)
     */
    public String getType() { return type; }
    
    /**
     * 获取目标标题。
     * @return 目标标题
     */
    public String getTitle() { return title; }
    
    /**
     * 获取目标金额。
     * @return 目标金额
     */
    public double getTargetAmount() { return targetAmount; }
    
    /**
     * 获取当前已完成的金额。
     * @return 当前金额
     */
    public double getCurrentAmount() { return currentAmount; }
    
    /**
     * 获取目标完成的截止日期。
     * @return 截止日期
     */
    public LocalDate getDeadline() { return deadline; }
    
    /**
     * 获取目标所属类别。
     * @return 目标类别
     */
    public String getCategory() { return category; }
    
    /**
     * 获取货币单位。
     * @return 货币单位
     */
    public String getCurrency() { return currency; }

    /**
     * 设置目标的唯一标识符。
     * @param id 目标ID
     */
    public void setId(String id) { this.id = id; }
    
    /**
     * 设置拥有此目标的用户ID。
     * @param userId 用户ID
     */
    public void setUserId(String userId) { this.userId = userId; }
    
    /**
     * 设置目标类型。
     * @param type 目标类型(SAVING, DEBT_REPAYMENT, 或 BUDGET_CONTROL)
     */
    public void setType(String type) { this.type = type; }
    
    /**
     * 设置目标标题。
     * @param title 目标标题
     */
    public void setTitle(String title) { this.title = title; }
    
    /**
     * 设置目标金额。
     * @param targetAmount 目标金额
     */
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    
    /**
     * 设置当前已完成的金额。
     * @param currentAmount 当前金额
     */
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    
    /**
     * 设置目标完成的截止日期。
     * @param deadline 截止日期
     */
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    
    /**
     * 设置目标所属类别。
     * @param category 目标类别
     */
    public void setCategory(String category) { this.category = category; }
    
    /**
     * 设置货币单位。
     * @param currency 货币单位
     */
    public void setCurrency(String currency) { this.currency = currency; }

    /**
     * 计算目标完成百分比。
     * 当目标金额为0时返回0。
     * 当完成度超过100%时返回100。
     *
     * @return 目标完成的百分比(0-100)
     */
    public int getProgressPercentage() {
        if (targetAmount == 0) return 0;
        return (int) Math.min(100, (currentAmount / targetAmount) * 100);
    }

    /**
     * 判断目标是否已完成。
     * 对于预算控制类型，当前金额小于等于目标金额时视为完成；
     * 对于其他类型，当前金额大于等于目标金额时视为完成。
     *
     * @return 如果目标已完成返回true，否则返回false
     */
    public boolean isCompleted() {
        return TYPE_BUDGET_CONTROL.equals(type)
                ? currentAmount <= targetAmount
                : currentAmount >= targetAmount;
    }
}