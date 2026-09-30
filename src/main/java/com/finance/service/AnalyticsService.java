package com.finance.service;

import com.finance.dao.TransactionDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service Layer for Analytics & Chart.js Data Aggregations
 */
public class AnalyticsService {

    private final TransactionDAO transactionDAO;

    public AnalyticsService() {
        this.transactionDAO = new TransactionDAO();
    }

    public AnalyticsService(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    /**
     * Aggregates all analytics datasets for a user
     */
    public Map<String, Object> getAnalyticsData(int userId) throws SQLException {
        Map<String, Object> analytics = new LinkedHashMap<>();

        // 1. Monthly Trend Data (Last 6 Months)
        List<Map<String, Object>> trendList = transactionDAO.getMonthlyTrend(userId, 6);

        List<String> monthLabels = new ArrayList<>();
        List<BigDecimal> incomeTrend = new ArrayList<>();
        List<BigDecimal> expenseTrend = new ArrayList<>();
        List<BigDecimal> savingsTrend = new ArrayList<>();

        for (Map<String, Object> row : trendList) {
            monthLabels.add((String) row.get("label"));
            incomeTrend.add((BigDecimal) row.get("income"));
            expenseTrend.add((BigDecimal) row.get("expense"));
            savingsTrend.add((BigDecimal) row.get("savings"));
        }

        analytics.put("monthLabels", monthLabels);
        analytics.put("incomeTrend", incomeTrend);
        analytics.put("expenseTrend", expenseTrend);
        analytics.put("savingsTrend", savingsTrend);

        // 2. Category-Wise Expenses
        Map<String, BigDecimal> categoryExpenses = transactionDAO.getAllCategoryExpenses(userId);
        List<String> categoryLabels = new ArrayList<>(categoryExpenses.keySet());
        List<BigDecimal> categoryAmounts = new ArrayList<>(categoryExpenses.values());

        analytics.put("categoryLabels", categoryLabels);
        analytics.put("categoryAmounts", categoryAmounts);

        // 3. Lifetime High-Level Stats
        BigDecimal totalIncome = transactionDAO.getTotalIncome(userId);
        BigDecimal totalExpenses = transactionDAO.getTotalExpenses(userId);
        BigDecimal netSavings = totalIncome.subtract(totalExpenses);

        analytics.put("totalIncome", totalIncome);
        analytics.put("totalExpenses", totalExpenses);
        analytics.put("netSavings", netSavings);

        return analytics;
    }
}
