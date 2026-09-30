package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Transaction Entity Model
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String type; // 'INCOME' or 'EXPENSE'
    private BigDecimal amount;
    private String category;
    private String description;
    private Date transactionDate;
    private String paymentMethod;
    private Timestamp createdAt;

    public Transaction() {
    }

    public Transaction(int id, int userId, String type, BigDecimal amount, String category,
                       String description, Date transactionDate, String paymentMethod, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.transactionDate = transactionDate;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    public Transaction(int userId, String type, BigDecimal amount, String category,
                       String description, Date transactionDate, String paymentMethod) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.transactionDate = transactionDate;
        this.paymentMethod = paymentMethod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type != null ? type.toUpperCase() : null;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isIncome() {
        return "INCOME".equalsIgnoreCase(this.type);
    }

    public boolean isExpense() {
        return "EXPENSE".equalsIgnoreCase(this.type);
    }
}
