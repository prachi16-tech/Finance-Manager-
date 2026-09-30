package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Financial Goal Entity Model
 */
public class FinancialGoal implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Date deadline;
    private Timestamp createdAt;

    public FinancialGoal() {
    }

    public FinancialGoal(int id, int userId, String goalName, BigDecimal targetAmount,
                         BigDecimal currentAmount, Date deadline, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.createdAt = createdAt;
    }

    public FinancialGoal(int userId, String goalName, BigDecimal targetAmount,
                         BigDecimal currentAmount, Date deadline) {
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
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

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
