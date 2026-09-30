package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard Financial Summary DTO
 */
public class DashboardSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal currentBalance;
    private double savingsRate;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal monthlySavings;
    private BigDecimal monthlyExpenseChange; // Current month expense - Prev month expense
    private boolean expenseIncreased;
    private String topSpendingCategory;
    private BigDecimal topCategoryAmount;
    private List<String> financialInsights = new ArrayList<>();

    public DashboardSummary() {
        this.totalIncome = BigDecimal.ZERO;
        this.totalExpenses = BigDecimal.ZERO;
        this.currentBalance = BigDecimal.ZERO;
        this.savingsRate = 0.0;
        this.monthlyIncome = BigDecimal.ZERO;
        this.monthlyExpenses = BigDecimal.ZERO;
        this.monthlySavings = BigDecimal.ZERO;
        this.monthlyExpenseChange = BigDecimal.ZERO;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(double savingsRate) {
        this.savingsRate = savingsRate;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(BigDecimal monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public BigDecimal getMonthlySavings() {
        return monthlySavings;
    }

    public void setMonthlySavings(BigDecimal monthlySavings) {
        this.monthlySavings = monthlySavings;
    }

    public BigDecimal getMonthlyExpenseChange() {
        return monthlyExpenseChange;
    }

    public void setMonthlyExpenseChange(BigDecimal monthlyExpenseChange) {
        this.monthlyExpenseChange = monthlyExpenseChange;
    }

    public boolean isExpenseIncreased() {
        return expenseIncreased;
    }

    public void setExpenseIncreased(boolean expenseIncreased) {
        this.expenseIncreased = expenseIncreased;
    }

    public String getTopSpendingCategory() {
        return topSpendingCategory;
    }

    public void setTopSpendingCategory(String topSpendingCategory) {
        this.topSpendingCategory = topSpendingCategory;
    }

    public BigDecimal getTopCategoryAmount() {
        return topCategoryAmount;
    }

    public void setTopCategoryAmount(BigDecimal topCategoryAmount) {
        this.topCategoryAmount = topCategoryAmount;
    }

    public List<String> getFinancialInsights() {
        return financialInsights;
    }

    public void setFinancialInsights(List<String> financialInsights) {
        this.financialInsights = financialInsights;
    }

    public void addInsight(String insight) {
        if (this.financialInsights == null) {
            this.financialInsights = new ArrayList<>();
        }
        this.financialInsights.add(insight);
    }
}
