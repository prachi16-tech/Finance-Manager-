package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Goal Progress DTO containing calculated progress, remaining amount and monthly target
 */
public class GoalProgress implements Serializable {
    private static final long serialVersionUID = 1L;

    private FinancialGoal goal;
    private BigDecimal remainingAmount;
    private double progressPercentage;
    private long daysRemaining;
    private long monthsRemaining;
    private BigDecimal requiredMonthlySaving;
    private boolean achieved;

    public GoalProgress() {
    }

    public GoalProgress(FinancialGoal goal, BigDecimal remainingAmount, double progressPercentage,
                        long daysRemaining, long monthsRemaining, BigDecimal requiredMonthlySaving, boolean achieved) {
        this.goal = goal;
        this.remainingAmount = remainingAmount;
        this.progressPercentage = progressPercentage;
        this.daysRemaining = daysRemaining;
        this.monthsRemaining = monthsRemaining;
        this.requiredMonthlySaving = requiredMonthlySaving;
        this.achieved = achieved;
    }

    public FinancialGoal getGoal() {
        return goal;
    }

    public void setGoal(FinancialGoal goal) {
        this.goal = goal;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public long getMonthsRemaining() {
        return monthsRemaining;
    }

    public void setMonthsRemaining(long monthsRemaining) {
        this.monthsRemaining = monthsRemaining;
    }

    public BigDecimal getRequiredMonthlySaving() {
        return requiredMonthlySaving;
    }

    public void setRequiredMonthlySaving(BigDecimal requiredMonthlySaving) {
        this.requiredMonthlySaving = requiredMonthlySaving;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }
}
