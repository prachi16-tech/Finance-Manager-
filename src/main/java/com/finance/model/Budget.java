package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Budget Entity Model
 */
public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String category;
    private BigDecimal amount;
    private int month;
    private int year;
    private Timestamp createdAt;

    public Budget() {
    }

    public Budget(int id, int userId, String category, BigDecimal amount, int month, int year, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.month = month;
        this.year = year;
        this.createdAt = createdAt;
    }

    public Budget(int userId, String category, BigDecimal amount, int month, int year) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.month = month;
        this.year = year;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
