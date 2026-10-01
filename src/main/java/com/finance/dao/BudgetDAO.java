package com.finance.dao;

import com.finance.model.Budget;
import com.finance.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Budget entities
 */
public class BudgetDAO {

    /**
     * Inserts or updates a budget for a given user, category, month, and year (UPSERT)
     */
    public boolean saveOrUpdate(Budget budget) throws SQLException {
        String checkSql = "SELECT id FROM budgets WHERE user_id = ? AND category = ? AND month = ? AND year = ?";
        String updateSql = "UPDATE budgets SET amount = ? WHERE id = ?";
        String insertSql = "INSERT INTO budgets (user_id, category, amount, month, year) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, budget.getUserId());
            checkStmt.setString(2, budget.getCategory());
            checkStmt.setInt(3, budget.getMonth());
            checkStmt.setInt(4, budget.getYear());
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                int existingId = rs.getInt("id");
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setBigDecimal(1, budget.getAmount());
                updateStmt.setInt(2, existingId);
                return updateStmt.executeUpdate() > 0;
            } else {
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, budget.getUserId());
                insertStmt.setString(2, budget.getCategory());
                insertStmt.setBigDecimal(3, budget.getAmount());
                insertStmt.setInt(4, budget.getMonth());
                insertStmt.setInt(5, budget.getYear());
                return insertStmt.executeUpdate() > 0;
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (checkStmt != null) try { checkStmt.close(); } catch (SQLException ignored) {}
            if (updateStmt != null) try { updateStmt.close(); } catch (SQLException ignored) {}
            if (insertStmt != null) try { insertStmt.close(); } catch (SQLException ignored) {}
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Retrieves all budgets for a user for a specific month and year
     */
    public List<Budget> findByMonthYear(int userId, int month, int year) throws SQLException {
        String sql = "SELECT id, user_id, category, amount, month, year, created_at " +
                     "FROM budgets WHERE user_id = ? AND month = ? AND year = ? ORDER BY category ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Budget> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Budget(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("category"),
                        rs.getBigDecimal("amount"),
                        rs.getInt("month"),
                        rs.getInt("year"),
                        rs.getTimestamp("created_at")
                ));
            }
            return list;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Finds single budget by ID and user ID
     */
    public Budget findById(int id, int userId) throws SQLException {
        String sql = "SELECT id, user_id, category, amount, month, year, created_at " +
                     "FROM budgets WHERE id = ? AND user_id = ?";
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
                return new Budget(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("category"),
                        rs.getBigDecimal("amount"),
                        rs.getInt("month"),
                        rs.getInt("year"),
                        rs.getTimestamp("created_at")
                );
            }
            return null;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Deletes a budget by ID and user ID
     */
    public boolean delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM budgets WHERE id = ? AND user_id = ?";
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

    /**
     * Calculates total allocated budget for a month
     */
    public BigDecimal getTotalBudgetForMonth(int userId, int month, int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM budgets WHERE user_id = ? AND month = ? AND year = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }
}
