package com.myfinanceapp.model;

import java.time.LocalDate;

// 代表一笔交易记录，比如支出/收入
public class Transaction {
    private String category;    // 类别
    private double amount;      // 金额
    private LocalDate date;     // 交易日期
    private String description; // 备注/描述

    // 构造方法
    public Transaction(String category, double amount, LocalDate date, String description) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // getter & setter ...
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
