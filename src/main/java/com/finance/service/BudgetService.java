package com.finance.service;

import com.finance.dao.BudgetDAO;
import com.finance.dao.TransactionDAO;
import com.finance.model.Budget;
import com.finance.model.BudgetSummary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service Layer for Budget Management & Calculations
 */
public class BudgetService {

    private final BudgetDAO budgetDAO;
    private final TransactionDAO transactionDAO;

    public BudgetService() {
        this.budgetDAO = new BudgetDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public BudgetService(BudgetDAO budgetDAO, TransactionDAO transactionDAO) {
        this.budgetDAO = budgetDAO;
        this.transactionDAO = transactionDAO;
    }

    /**
     * Gets budget summaries with actual spent vs budget for a given month and year
     */
    public List<BudgetSummary> getBudgetSummaries(int userId, int month, int year) throws SQLException {
        List<Budget> budgets = budgetDAO.findByMonthYear(userId, month, year);
        Map<String, BigDecimal> actualExpenses = transactionDAO.getCategoryExpensesForMonth(userId, month, year);

        List<BudgetSummary> summaries = new ArrayList<>();
        Set<String> processedCategories = new HashSet<>();

        // Process defined budgets
        for (Budget b : budgets) {
            String category = b.getCategory();
            processedCategories.add(category.toLowerCase());

            BigDecimal budgetAmount = b.getAmount();
            BigDecimal spent = actualExpenses.getOrDefault(category, BigDecimal.ZERO);
            BigDecimal remaining = budgetAmount.subtract(spent);

            double percentageUsed = 0.0;
            if (budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
                percentageUsed = spent.divide(budgetAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")).doubleValue();
            }

            boolean exceeded = spent.compareTo(budgetAmount) > 0;

            summaries.add(new BudgetSummary(
                    b.getId(),
                    category,
                    budgetAmount,
                    spent,
                    remaining,
                    Math.round(percentageUsed * 10.0) / 10.0,
                    exceeded
            ));
        }

        // Include categories that have expenses but no budget defined yet
        for (Map.Entry<String, BigDecimal> entry : actualExpenses.entrySet()) {
            String category = entry.getKey();
            if (!processedCategories.contains(category.toLowerCase())) {
                BigDecimal spent = entry.getValue();
                summaries.add(new BudgetSummary(
                        0, // Not explicitly created yet
                        category,
                        BigDecimal.ZERO,
                        spent,
                        spent.negate(),
                        100.0,
                        true
                ));
            }
        }

        return summaries;
    }

    public boolean saveOrUpdateBudget(Budget budget) throws SQLException {
        return budgetDAO.saveOrUpdate(budget);
    }

    public boolean deleteBudget(int id, int userId) throws SQLException {
        return budgetDAO.delete(id, userId);
    }
}
