package com.myfinanceapp.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class Transaction {
    private String transactionDate;
    private String transactionType;
    private String currency;
    private double amount;
    private String category;
    private String paymentMethod;

    // Getters and Setters
    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    //重写写一个equals方法，确保交易记录是通过其内容（每一个属性的值一一比较）进行比较判断。
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // 同一个对象，返回true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // 如果是不同类或者obj为空，返回false
        }
        Transaction other = (Transaction) obj;
        return Double.compare(other.amount, amount) == 0 &&  // 比较金额，注意使用Double.compare避免精度问题
                Objects.equals(transactionDate, other.transactionDate) &&
                Objects.equals(transactionType, other.transactionType) &&
                Objects.equals(currency, other.currency) &&
                Objects.equals(category, other.category) &&
                Objects.equals(paymentMethod, other.paymentMethod);
    }
}
