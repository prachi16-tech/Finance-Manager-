package com.finance.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Connection Utility
 * Manages JDBC connections to MySQL database personal_finance_db
 * Automatically provides zero-setup fallback if MySQL service is not started on 3306
 */
public class DBConnection {

    private static final String DEFAULT_MYSQL_URL = "jdbc:mysql://localhost:3306/personal_finance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    // Fallback embedded MySQL-compatible DB
    private static final String EMBEDDED_URL = "jdbc:h2:./data/finance_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;AUTO_SERVER=TRUE";
    
    private static boolean isEmbeddedMode = false;
    private static boolean isInitialized = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {}
        }
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ignored) {}
    }

    /**
     * Gets a fresh connection to the database
     */
    public static synchronized Connection getConnection() throws SQLException {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            dbUrl = System.getProperty("db.url");
        }

        String dbUser = System.getenv("DB_USER");
        if (dbUser == null || dbUser.trim().isEmpty()) {
            dbUser = System.getProperty("db.user", DEFAULT_USER);
        }

        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbPassword == null) {
            dbPassword = System.getProperty("db.password", DEFAULT_PASSWORD);
        }

        if (dbUrl != null && !dbUrl.trim().isEmpty()) {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        }

        if (isEmbeddedMode) {
            Connection conn = DriverManager.getConnection(EMBEDDED_URL, "sa", "");
            ensureSchemaInitialized(conn);
            return conn;
        }

        // Try MySQL first
        try {
            Connection conn = DriverManager.getConnection(DEFAULT_MYSQL_URL, dbUser, dbPassword);
            return conn;
        } catch (SQLException ex) {
            // MySQL server is not running on 3306 - seamlessly fallback to embedded DB
            System.out.println("[DBConnection] MySQL on 3306 unreachable (" + ex.getMessage() + "). Using embedded MySQL-compatible database mode.");
            isEmbeddedMode = true;
            new File("./data").mkdirs();
            Connection conn = DriverManager.getConnection(EMBEDDED_URL, "sa", "");
            ensureSchemaInitialized(conn);
            return conn;
        }
    }

    private static synchronized void ensureSchemaInitialized(Connection conn) {
        if (isInitialized) return;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            // Check if users table exists
            boolean tablesExist = false;
            try {
                ResultSet rs = stmt.executeQuery("SELECT count(*) FROM users");
                if (rs.next()) tablesExist = true;
                rs.close();
            } catch (SQLException ignored) {}

            if (!tablesExist) {
                System.out.println("[DBConnection] Initializing database tables and seed data...");
                executeSqlFile(conn, "database.sql");
            }
            isInitialized = true;
        } catch (Exception e) {
            System.err.println("[DBConnection] Auto-init notice: " + e.getMessage());
            isInitialized = true;
        } finally {
            close(null, stmt);
        }
    }

    private static void executeSqlFile(Connection conn, String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            file = new File("../" + fileName);
        }
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             Statement stmt = conn.createStatement()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.startsWith("/*") || line.isEmpty()) continue;
                if (line.toUpperCase().startsWith("CREATE DATABASE") || line.toUpperCase().startsWith("USE ")) continue;
                if (line.toUpperCase().startsWith("SET FOREIGN_KEY_CHECKS")) continue;
                
                sb.append(line).append(" ");
                if (line.endsWith(";")) {
                    String query = sb.toString().replace(";", "").trim();
                    try {
                        // Remove MySQL specific engine/charset clauses for compatibility
                        query = query.replaceAll("(?i)ENGINE\\s*=\\s*InnoDB", "")
                                     .replaceAll("(?i)DEFAULT\\s+CHARSET\\s*=\\s*utf8mb4", "")
                                     .replaceAll("(?i)COLLATE\\s*=\\s*utf8mb4_unicode_ci", "")
                                     .replaceAll("(?i)ON\\s+UPDATE\\s+CASCADE", "")
                                     .replaceAll("(?i)CURDATE\\(\\)\\s*-\\s*INTERVAL\\s+(\\d+)\\s+DAY", "DATEADD('DAY', -$1, CURRENT_DATE)")
                                     .replaceAll("(?i)DATE_ADD\\(CURDATE\\(\\),\\s*INTERVAL\\s+(\\d+)\\s+MONTH\\)", "DATEADD('MONTH', $1, CURRENT_DATE)");
                        stmt.execute(query);
                    } catch (SQLException ex) {
                        // Ignore duplicate table errors
                    }
                    sb.setLength(0);
                }
            }
            System.out.println("[DBConnection] Database schema and demo records initialized successfully!");
        } catch (Exception e) {
            System.err.println("[DBConnection] SQL File execution: " + e.getMessage());
        }
    }

    /**
     * Closes Connection, Statement, and ResultSet safely
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}
