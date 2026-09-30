package com.finance.dao;

import com.finance.model.Transaction;
import com.finance.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Transaction entities
 */
public class TransactionDAO {

    /**
     * Inserts a new transaction
     */
    public boolean insert(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, type, amount, category, description, transaction_date, payment_method) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, t.getUserId());
            stmt.setString(2, t.getType().toUpperCase());
            stmt.setBigDecimal(3, t.getAmount());
            stmt.setString(4, t.getCategory());
            stmt.setString(5, t.getDescription());
            stmt.setDate(6, t.getTransactionDate());
            stmt.setString(7, t.getPaymentMethod());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    t.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Finds a single transaction by ID and user ID
     */
    public Transaction findById(int id, int userId) throws SQLException {
        String sql = "SELECT id, user_id, type, amount, category, description, transaction_date, payment_method, created_at " +
                     "FROM transactions WHERE id = ? AND user_id = ?";
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
                return mapResultSetToTransaction(rs);
            }
            return null;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Updates an existing transaction
     */
    public boolean update(Transaction t) throws SQLException {
        String sql = "UPDATE transactions SET type = ?, amount = ?, category = ?, description = ?, " +
                     "transaction_date = ?, payment_method = ? WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, t.getType().toUpperCase());
            stmt.setBigDecimal(2, t.getAmount());
            stmt.setString(3, t.getCategory());
            stmt.setString(4, t.getDescription());
            stmt.setDate(5, t.getTransactionDate());
            stmt.setString(6, t.getPaymentMethod());
            stmt.setInt(7, t.getId());
            stmt.setInt(8, t.getUserId());

            return stmt.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    /**
     * Deletes a transaction by ID and user ID
     */
    public boolean delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
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
     * Searches and filters transactions for a user
     */
    public List<Transaction> findFiltered(int userId, String search, String category, String type,
                                          Date startDate, Date endDate) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id, user_id, type, amount, category, description, ")
                .append("transaction_date, payment_method, created_at FROM transactions WHERE user_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(description) LIKE LOWER(?) OR LOWER(category) LIKE LOWER(?) OR LOWER(payment_method) LIKE LOWER(?)) ");
            String searchPattern = "%" + search.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            sql.append("AND category = ? ");
            params.add(category.trim());
        }

        if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type)) {
            sql.append("AND type = ? ");
            params.add(type.toUpperCase().trim());
        }

        if (startDate != null) {
            sql.append("AND transaction_date >= ? ");
            params.add(startDate);
        }

        if (endDate != null) {
            sql.append("AND transaction_date <= ? ");
            params.add(endDate);
        }

        sql.append("ORDER BY transaction_date DESC, id DESC");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transaction> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToTransaction(rs));
            }
            return list;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Retrieves recent N transactions for a user
     */
    public List<Transaction> getRecentTransactions(int userId, int limit) throws SQLException {
        String sql = "SELECT id, user_id, type, amount, category, description, transaction_date, payment_method, created_at " +
                     "FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC, id DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transaction> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToTransaction(rs));
            }
            return list;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Calculates lifetime total income for a user
     */
    public BigDecimal getTotalIncome(int userId) throws SQLException {
        return getTotalByType(userId, "INCOME");
    }

    /**
     * Calculates lifetime total expenses for a user
     */
    public BigDecimal getTotalExpenses(int userId) throws SQLException {
        return getTotalByType(userId, "EXPENSE");
    }

    private BigDecimal getTotalByType(int userId, String type) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, type);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Calculates monthly total by type for a specific month and year
     */
    public BigDecimal getMonthlyTotal(int userId, String type, int month, int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                     "WHERE user_id = ? AND type = ? AND MONTH(transaction_date) = ? AND YEAR(transaction_date) = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, type);
            stmt.setInt(3, month);
            stmt.setInt(4, year);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Retrieves category-wise expense breakdown for a given month & year
     */
    public Map<String, BigDecimal> getCategoryExpensesForMonth(int userId, int month, int year) throws SQLException {
        String sql = "SELECT category, COALESCE(SUM(amount), 0) as total FROM transactions " +
                     "WHERE user_id = ? AND type = 'EXPENSE' AND MONTH(transaction_date) = ? AND YEAR(transaction_date) = ? " +
                     "GROUP BY category ORDER BY total DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, BigDecimal> map = new LinkedHashMap<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("category"), rs.getBigDecimal("total"));
            }
            return map;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Retrieves overall category-wise expense breakdown for all time
     */
    public Map<String, BigDecimal> getAllCategoryExpenses(int userId) throws SQLException {
        String sql = "SELECT category, COALESCE(SUM(amount), 0) as total FROM transactions " +
                     "WHERE user_id = ? AND type = 'EXPENSE' GROUP BY category ORDER BY total DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, BigDecimal> map = new LinkedHashMap<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("category"), rs.getBigDecimal("total"));
            }
            return map;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Retrieves monthly income and expense totals for the past N months
     */
    public List<Map<String, Object>> getMonthlyTrend(int userId, int numberOfMonths) throws SQLException {
        String sql = "SELECT " +
                     "  DATE_FORMAT(transaction_date, '%Y-%m') as year_month, " +
                     "  DATE_FORMAT(transaction_date, '%b %Y') as month_label, " +
                     "  SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as total_income, " +
                     "  SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as total_expense " +
                     "FROM transactions " +
                     "WHERE user_id = ? AND transaction_date >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
                     "GROUP BY DATE_FORMAT(transaction_date, '%Y-%m'), DATE_FORMAT(transaction_date, '%b %Y') " +
                     "ORDER BY year_month ASC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> results = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, numberOfMonths);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("yearMonth", rs.getString("year_month"));
                row.put("label", rs.getString("month_label"));
                row.put("income", rs.getBigDecimal("total_income"));
                row.put("expense", rs.getBigDecimal("total_expense"));
                BigDecimal inc = rs.getBigDecimal("total_income");
                BigDecimal exp = rs.getBigDecimal("total_expense");
                row.put("savings", inc.subtract(exp));
                results.add(row);
            }
            return results;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("type"),
                rs.getBigDecimal("amount"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getDate("transaction_date"),
                rs.getString("payment_method"),
                rs.getTimestamp("created_at")
        );
    }
}
