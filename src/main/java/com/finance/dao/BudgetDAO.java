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
        String sql = "INSERT INTO budgets (user_id, category, amount, month, year) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, budget.getUserId());
            stmt.setString(2, budget.getCategory());
            stmt.setBigDecimal(3, budget.getAmount());
            stmt.setInt(4, budget.getMonth());
            stmt.setInt(5, budget.getYear());

            return stmt.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, stmt);
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
