package com.myfinanceapp.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a financial transaction in the system.
 * A transaction contains information about the date, type, amount, and other details
 * of a financial operation.
 *
 * @author SE_Group110
 * @version 4.0
 */
public class Transaction {
    /** Date when the transaction occurred */
    private String transactionDate;
    /** Type of the transaction (e.g., INCOME, EXPENSE) */
    private String transactionType;
    /** Currency of the transaction amount */
    private String currency;
    /** Amount of the transaction */
    private double amount;
    /** Category of the transaction (e.g., FOOD, TRANSPORT) */
    private String category;
    /** Method of payment used for the transaction */
    private String paymentMethod;
    /** Additional description or notes about the transaction */
    private String description;

    /**
     * Gets the date of the transaction.
     * @return The transaction date as a string
     */
    public String getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the date of the transaction.
     * @param transactionDate The transaction date to set
     */
    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the type of the transaction.
     * @return The transaction type
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the type of the transaction.
     * @param transactionType The transaction type to set
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Gets the currency of the transaction.
     * @return The currency code
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency of the transaction.
     * @param currency The currency code to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Gets the amount of the transaction.
     * @return The transaction amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the transaction.
     * @param amount The transaction amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the category of the transaction.
     * @return The transaction category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the transaction.
     * @param category The transaction category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the payment method used for the transaction.
     * @return The payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method used for the transaction.
     * @param paymentMethod The payment method to set
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the description of the transaction.
     * @return The transaction description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the transaction.
     * @param description The transaction description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Compares this transaction with another object for equality.
     * Two transactions are considered equal if all their properties match exactly.
     * Uses Double.compare for amount comparison to handle floating-point precision.
     *
     * @param obj The object to compare with
     * @return true if the transactions are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Same object reference
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Different class or null object
        }
        Transaction other = (Transaction) obj;
        return Double.compare(other.amount, amount) == 0 && // Compare amount using Double.compare to handle precision
                Objects.equals(transactionDate, other.transactionDate) &&
                Objects.equals(transactionType, other.transactionType) &&
                Objects.equals(currency, other.currency) &&
                Objects.equals(category, other.category) &&
                Objects.equals(paymentMethod, other.paymentMethod);
    }
}
