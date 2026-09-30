package com.finance.dao;

import com.finance.model.FinancialGoal;
import com.finance.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for FinancialGoal entities
 */
public class FinancialGoalDAO {

    /**
     * Inserts a new financial goal
     */
    public boolean insert(FinancialGoal goal) throws SQLException {
        String sql = "INSERT INTO financial_goals (user_id, goal_name, target_amount, current_amount, deadline) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, goal.getUserId());
            stmt.setString(2, goal.getGoalName());
            stmt.setBigDecimal(3, goal.getTargetAmount());
            stmt.setBigDecimal(4, goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO);
            stmt.setDate(5, goal.getDeadline());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    goal.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Finds single goal by ID and user ID
     */
    public FinancialGoal findById(int id, int userId) throws SQLException {
        String sql = "SELECT id, user_id, goal_name, target_amount, current_amount, deadline, created_at " +
                     "FROM financial_goals WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToGoal(rs);
            }
            return null;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Retrieves all goals for a user
     */
    public List<FinancialGoal> findByUserId(int userId) throws SQLException {
        String sql = "SELECT id, user_id, goal_name, target_amount, current_amount, deadline, created_at " +
                     "FROM financial_goals WHERE user_id = ? ORDER BY deadline ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<FinancialGoal> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToGoal(rs));
            }
            return list;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Updates goal details (name, target amount, deadline)
     */
    public boolean update(FinancialGoal goal) throws SQLException {
        String sql = "UPDATE financial_goals SET goal_name = ?, target_amount = ?, deadline = ? " +
                     "WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, goal.getGoalName());
            stmt.setBigDecimal(2, goal.getTargetAmount());
            stmt.setDate(3, goal.getDeadline());
            stmt.setInt(4, goal.getId());
            stmt.setInt(5, goal.getUserId());

            return stmt.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    /**
     * Updates current saved amount directly or by adding deposit
     */
    public boolean updateSavedAmount(int id, int userId, BigDecimal newCurrentAmount) throws SQLException {
        String sql = "UPDATE financial_goals SET current_amount = ? WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setBigDecimal(1, newCurrentAmount);
            stmt.setInt(2, id);
            stmt.setInt(3, userId);

            return stmt.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    /**
     * Deletes a financial goal by ID and user ID
     */
    public boolean delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM financial_goals WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    private FinancialGoal mapResultSetToGoal(ResultSet rs) throws SQLException {
        return new FinancialGoal(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("goal_name"),
                rs.getBigDecimal("target_amount"),
                rs.getBigDecimal("current_amount"),
                rs.getDate("deadline"),
                rs.getTimestamp("created_at")
        );
    }
}
