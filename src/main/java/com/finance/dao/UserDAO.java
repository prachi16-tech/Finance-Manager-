package com.finance.dao;

import com.finance.model.User;
import com.finance.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for User entities
 */
public class UserDAO {

    /**
     * Registers a new user
     * @return generated user id or -1 on failure
     */
    public int register(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail().toLowerCase().trim());
            stmt.setString(3, user.getPassword());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    user.setId(generatedId);
                    return generatedId;
                }
            }
            return -1;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Finds user by email address
     */
    public User findByEmail(String email) throws SQLException {
        if (email == null) return null;
        String sql = "SELECT id, name, email, password, created_at FROM users WHERE LOWER(email) = LOWER(?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getTimestamp("created_at")
                );
            }
            return null;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Finds user by primary key ID
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT id, name, email, password, created_at FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getTimestamp("created_at")
                );
            }
            return null;
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Checks if an email is already registered
     */
    public boolean emailExists(String email, int excludeUserId) throws SQLException {
        String sql = "SELECT id FROM users WHERE LOWER(email) = LOWER(?) AND id <> ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.trim());
            stmt.setInt(2, excludeUserId);
            rs = stmt.executeQuery();
            return rs.next();
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }

    /**
     * Updates user's name and email
     */
    public boolean updateProfile(int userId, String name, String email) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name.trim());
            stmt.setString(2, email.trim().toLowerCase());
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }

    /**
     * Updates user password
     */
    public boolean updatePassword(int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, stmt);
        }
    }
}
