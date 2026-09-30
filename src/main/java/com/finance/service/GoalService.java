package com.finance.service;

import com.finance.dao.FinancialGoalDAO;
import com.finance.model.FinancialGoal;
import com.finance.model.GoalProgress;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer for Financial Goal Calculations
 */
public class GoalService {

    private final FinancialGoalDAO goalDAO;

    public GoalService() {
        this.goalDAO = new FinancialGoalDAO();
    }

    public GoalService(FinancialGoalDAO goalDAO) {
        this.goalDAO = goalDAO;
    }

    /**
     * Retrieves all goals for a user with calculated progress, remaining amount and monthly target
     */
    public List<GoalProgress> getGoalProgressList(int userId) throws SQLException {
        List<FinancialGoal> goals = goalDAO.findByUserId(userId);
        List<GoalProgress> progressList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (FinancialGoal goal : goals) {
            BigDecimal target = goal.getTargetAmount();
            BigDecimal current = goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO;
            BigDecimal remaining = target.subtract(current);
            if (remaining.compareTo(BigDecimal.ZERO) < 0) {
                remaining = BigDecimal.ZERO;
            }

            // Progress Percentage = (Current / Target) * 100
            double progress = 0.0;
            if (target.compareTo(BigDecimal.ZERO) > 0) {
                progress = current.divide(target, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")).doubleValue();
                if (progress > 100.0) progress = 100.0;
            }

            // Time Remaining Calculation
            LocalDate deadline = goal.getDeadline().toLocalDate();
            long daysRemaining = ChronoUnit.DAYS.between(today, deadline);
            long monthsRemaining = ChronoUnit.MONTHS.between(today, deadline);
            if (monthsRemaining < 1) {
                monthsRemaining = 1; // at least 1 month denominator
            }

            // Required Monthly Saving = Remaining / Months Remaining
            BigDecimal requiredMonthly = BigDecimal.ZERO;
            if (remaining.compareTo(BigDecimal.ZERO) > 0 && monthsRemaining > 0) {
                requiredMonthly = remaining.divide(new BigDecimal(monthsRemaining), 2, RoundingMode.HALF_UP);
            }

            boolean isAchieved = current.compareTo(target) >= 0;

            progressList.add(new GoalProgress(
                    goal,
                    remaining,
                    Math.round(progress * 10.0) / 10.0,
                    daysRemaining,
                    monthsRemaining,
                    requiredMonthly,
                    isAchieved
            ));
        }

        return progressList;
    }

    public boolean createGoal(FinancialGoal goal) throws SQLException {
        return goalDAO.insert(goal);
    }

    public FinancialGoal getGoalById(int id, int userId) throws SQLException {
        return goalDAO.findById(id, userId);
    }

    public boolean updateGoal(FinancialGoal goal) throws SQLException {
        return goalDAO.update(goal);
    }

    public boolean updateSavedAmount(int id, int userId, BigDecimal newSavedAmount) throws SQLException {
        return goalDAO.updateSavedAmount(id, userId, newSavedAmount);
    }

    public boolean addDeposit(int id, int userId, BigDecimal depositAmount) throws SQLException {
        FinancialGoal goal = goalDAO.findById(id, userId);
        if (goal == null) return false;
        BigDecimal newAmount = (goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO).add(depositAmount);
        return goalDAO.updateSavedAmount(id, userId, newAmount);
    }

    public boolean deleteGoal(int id, int userId) throws SQLException {
        return goalDAO.delete(id, userId);
    }
}
