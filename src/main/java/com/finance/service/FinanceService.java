package com.finance.service;

import com.finance.dao.BudgetDAO;
import com.finance.dao.TransactionDAO;
import com.finance.model.DashboardSummary;
import com.finance.model.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service Layer for Core Finance & Dashboard Calculations
 */
public class FinanceService {

    private final TransactionDAO transactionDAO;
    private final BudgetDAO budgetDAO;

    public FinanceService() {
        this.transactionDAO = new TransactionDAO();
        this.budgetDAO = new BudgetDAO();
    }

    public FinanceService(TransactionDAO transactionDAO, BudgetDAO budgetDAO) {
        this.transactionDAO = transactionDAO;
        this.budgetDAO = budgetDAO;
    }

    /**
     * Computes complete financial summary and rule-based insights for dashboard
     */
    public DashboardSummary getDashboardSummary(int userId) throws SQLException {
        DashboardSummary summary = new DashboardSummary();

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        LocalDate prevMonthDate = now.minusMonths(1);
        int prevMonth = prevMonthDate.getMonthValue();
        int prevYear = prevMonthDate.getYear();

        // 1. Lifetime Totals
        BigDecimal lifetimeIncome = transactionDAO.getTotalIncome(userId);
        BigDecimal lifetimeExpenses = transactionDAO.getTotalExpenses(userId);
        BigDecimal currentBalance = lifetimeIncome.subtract(lifetimeExpenses);

        summary.setTotalIncome(lifetimeIncome);
        summary.setTotalExpenses(lifetimeExpenses);
        summary.setCurrentBalance(currentBalance);

        // 2. Current Month Totals
        BigDecimal monthIncome = transactionDAO.getMonthlyTotal(userId, "INCOME", currentMonth, currentYear);
        BigDecimal monthExpense = transactionDAO.getMonthlyTotal(userId, "EXPENSE", currentMonth, currentYear);
        BigDecimal monthSavings = monthIncome.subtract(monthExpense);

        summary.setMonthlyIncome(monthIncome);
        summary.setMonthlyExpenses(monthExpense);
        summary.setMonthlySavings(monthSavings);

        // 3. Savings Rate = (Savings / Income) * 100
        double savingsRate = 0.0;
        if (monthIncome.compareTo(BigDecimal.ZERO) > 0) {
            if (monthSavings.compareTo(BigDecimal.ZERO) > 0) {
                savingsRate = monthSavings.divide(monthIncome, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")).doubleValue();
            } else {
                savingsRate = 0.0;
            }
        } else if (lifetimeIncome.compareTo(BigDecimal.ZERO) > 0) {
            // Fallback to lifetime savings rate if current month has no income yet
            BigDecimal lifetimeSavings = lifetimeIncome.subtract(lifetimeExpenses);
            if (lifetimeSavings.compareTo(BigDecimal.ZERO) > 0) {
                savingsRate = lifetimeSavings.divide(lifetimeIncome, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")).doubleValue();
            }
        }
        summary.setSavingsRate(Math.round(savingsRate * 10.0) / 10.0);

        // 4. Monthly Expense Change = Current Month Expense - Previous Month Expense
        BigDecimal prevMonthExpense = transactionDAO.getMonthlyTotal(userId, "EXPENSE", prevMonth, prevYear);
        BigDecimal expenseChange = monthExpense.subtract(prevMonthExpense);
        summary.setMonthlyExpenseChange(expenseChange);
        summary.setExpenseIncreased(expenseChange.compareTo(BigDecimal.ZERO) > 0);

        // 5. Category-wise Spending & Top Category
        Map<String, BigDecimal> categoryExpenses = transactionDAO.getCategoryExpensesForMonth(userId, currentMonth, currentYear);
        if (categoryExpenses.isEmpty()) {
            categoryExpenses = transactionDAO.getAllCategoryExpenses(userId);
        }

        String topCategory = "None";
        BigDecimal topAmount = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : categoryExpenses.entrySet()) {
            if (entry.getValue().compareTo(topAmount) > 0) {
                topAmount = entry.getValue();
                topCategory = entry.getKey();
            }
        }
        summary.setTopSpendingCategory(topCategory);
        summary.setTopCategoryAmount(topAmount);

        // 6. Generate Rule-Based Financial Insights (NO AI)
        generateFinancialInsights(summary, userId, currentMonth, currentYear, categoryExpenses);

        return summary;
    }

    /**
     * Generates rule-based financial insights based on deterministic logic
     */
    private void generateFinancialInsights(DashboardSummary summary, int userId, int month, int year,
                                           Map<String, BigDecimal> categoryExpenses) throws SQLException {
        // Rule 1: Expenses exceed Income
        if (summary.getMonthlyExpenses().compareTo(summary.getMonthlyIncome()) > 0 &&
                summary.getMonthlyIncome().compareTo(BigDecimal.ZERO) > 0) {
            summary.addInsight("Your expenses are higher than your income this month. Consider cutting discretionary spending.");
        }

        // Rule 2: Strong Savings Rate (>= 30%)
        if (summary.getSavingsRate() >= 30.0) {
            summary.addInsight("You are maintaining a strong savings rate of " + summary.getSavingsRate() + "%! Keep up the momentum.");
        } else if (summary.getSavingsRate() > 0 && summary.getSavingsRate() < 15.0) {
            summary.addInsight("Your current savings rate is " + summary.getSavingsRate() + "%. Aim for at least 20% to build your safety net.");
        }

        // Rule 3: Top spending category rule
        if (summary.getTopCategoryAmount().compareTo(BigDecimal.ZERO) > 0) {
            summary.addInsight(summary.getTopSpendingCategory() + " is your highest spending category this month (₹" +
                    String.format("%,.2f", summary.getTopCategoryAmount()) + ").");
        }

        // Rule 4: Total budget usage evaluation (> 90%)
        BigDecimal totalBudget = budgetDAO.getTotalBudgetForMonth(userId, month, year);
        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal spent = summary.getMonthlyExpenses();
            double budgetUsage = spent.divide(totalBudget, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100")).doubleValue();
            if (budgetUsage >= 100.0) {
                summary.addInsight("Warning: You have exceeded your total monthly allocated budget by " +
                        String.format("%.1f", (budgetUsage - 100.0)) + "%.");
            } else if (budgetUsage >= 90.0) {
                summary.addInsight("You are close to reaching your budget limit (" + String.format("%.1f", budgetUsage) + "% used).");
            }
        }

        // Rule 5: Positive balance greeting if no warning
        if (summary.getFinancialInsights().isEmpty()) {
            if (summary.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
                summary.addInsight("Your financial health is stable. Balance is positive at ₹" +
                        String.format("%,.2f", summary.getCurrentBalance()) + ".");
            } else {
                summary.addInsight("Start tracking all your daily transactions and set monthly category budgets.");
            }
        }
    }

    public List<Transaction> getRecentTransactions(int userId, int limit) throws SQLException {
        return transactionDAO.getRecentTransactions(userId, limit);
    }
}
