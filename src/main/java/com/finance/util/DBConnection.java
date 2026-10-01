package com.finance.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Connection Utility
 * Manages JDBC connections to MySQL database personal_finance_db
 * Automatically provides zero-setup fallback and self-healing schema creation
 */
public class DBConnection {

    private static final String DEFAULT_MYSQL_URL = "jdbc:mysql://localhost:3306/personal_finance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    // Fallback embedded MySQL-compatible DB
    private static final String EMBEDDED_URL = "jdbc:h2:./data/finance_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;AUTO_SERVER=TRUE;NON_KEYWORDS=MONTH,YEAR,VALUE";
    
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
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            ensureSchemaInitialized(conn);
            return conn;
        }

        if (isEmbeddedMode) {
            Connection conn = DriverManager.getConnection(EMBEDDED_URL, "sa", "");
            ensureSchemaInitialized(conn);
            return conn;
        }

        // Try MySQL first
        try {
            Connection conn = DriverManager.getConnection(DEFAULT_MYSQL_URL, dbUser, dbPassword);
            ensureSchemaInitialized(conn);
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

    /**
     * Ensures all required tables (users, transactions, budgets, financial_goals)
     * are created and seed data is populated.
     */
    public static synchronized void ensureSchemaInitialized(Connection conn) {
        if (isInitialized) return;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();

            // 1. Create 'users' table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    name VARCHAR(100) NOT NULL," +
                "    email VARCHAR(150) NOT NULL UNIQUE," +
                "    password VARCHAR(255) NOT NULL," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // 2. Create 'transactions' table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    user_id INT NOT NULL," +
                "    type VARCHAR(10) NOT NULL," +
                "    amount DECIMAL(12,2) NOT NULL," +
                "    category VARCHAR(50) NOT NULL," +
                "    description VARCHAR(255) NOT NULL," +
                "    transaction_date DATE NOT NULL," +
                "    payment_method VARCHAR(50) NOT NULL," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );

            // 3. Create 'budgets' table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS budgets (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    user_id INT NOT NULL," +
                "    category VARCHAR(50) NOT NULL," +
                "    amount DECIMAL(12,2) NOT NULL," +
                "    `month` INT NOT NULL," +
                "    `year` INT NOT NULL," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    CONSTRAINT uq_user_category_month_year UNIQUE (user_id, category, `month`, `year`)," +
                "    CONSTRAINT fk_budgets_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );

            // 4. Create 'financial_goals' table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS financial_goals (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    user_id INT NOT NULL," +
                "    goal_name VARCHAR(150) NOT NULL," +
                "    target_amount DECIMAL(12,2) NOT NULL," +
                "    current_amount DECIMAL(12,2) DEFAULT 0.00," +
                "    deadline DATE NOT NULL," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );

            // Check if demo user exists
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM users WHERE email = 'mayur@example.com'");
            boolean hasUser = false;
            if (rs.next() && rs.getInt(1) > 0) {
                hasUser = true;
            }
            rs.close();

            if (!hasUser) {
                // Seed Demo User
                stmt.executeUpdate(
                    "INSERT INTO users (id, name, email, password) VALUES " +
                    "(1, 'Mayur Patil', 'mayur@example.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f')"
                );

                // Seed Transactions
                stmt.executeUpdate(
                    "INSERT INTO transactions (user_id, type, amount, category, description, transaction_date, payment_method) VALUES " +
                    "(1, 'INCOME', 65000.00, 'Salary', 'Monthly Tech Job Salary', CURRENT_DATE, 'Bank Transfer')," +
                    "(1, 'INCOME', 15000.00, 'Freelance', 'Web Design Project Freelance', CURRENT_DATE, 'UPI')," +
                    "(1, 'EXPENSE', 12000.00, 'Bills', 'Apartment Rent & Utilities', CURRENT_DATE, 'Bank Transfer')," +
                    "(1, 'EXPENSE', 5400.00, 'Food', 'Monthly Grocery Store', CURRENT_DATE, 'Credit Card')," +
                    "(1, 'EXPENSE', 1850.00, 'Food', 'Weekend Dinner with Friends', CURRENT_DATE, 'UPI')," +
                    "(1, 'EXPENSE', 3200.00, 'Transport', 'Fuel & Metro Recharge', CURRENT_DATE, 'UPI')," +
                    "(1, 'EXPENSE', 4500.00, 'Shopping', 'Noise Cancelling Earbuds', CURRENT_DATE, 'Credit Card')," +
                    "(1, 'EXPENSE', 2100.00, 'Entertainment', 'Movie & Streaming Subscriptions', CURRENT_DATE, 'Debit Card')," +
                    "(1, 'EXPENSE', 1500.00, 'Healthcare', 'Dental Checkup & Medicines', CURRENT_DATE, 'Cash')," +
                    "(1, 'INCOME', 5000.00, 'Other', 'Stock Dividend Payout', CURRENT_DATE, 'Bank Transfer')"
                );

                // Seed Budgets for current month & year
                java.time.LocalDate now = java.time.LocalDate.now();
                int m = now.getMonthValue();
                int y = now.getYear();
                stmt.executeUpdate(
                    "INSERT INTO budgets (user_id, category, amount, `month`, `year`) VALUES " +
                    "(1, 'Food', 8000.00, " + m + ", " + y + ")," +
                    "(1, 'Transport', 4000.00, " + m + ", " + y + ")," +
                    "(1, 'Shopping', 5000.00, " + m + ", " + y + ")," +
                    "(1, 'Bills', 15000.00, " + m + ", " + y + ")," +
                    "(1, 'Entertainment', 3000.00, " + m + ", " + y + ")," +
                    "(1, 'Healthcare', 2500.00, " + m + ", " + y + ")"
                );

                // Seed Goals
                stmt.executeUpdate(
                    "INSERT INTO financial_goals (user_id, goal_name, target_amount, current_amount, deadline) VALUES " +
                    "(1, 'New M3 MacBook Pro', 140000.00, 85000.00, DATEADD('MONTH', 6, CURRENT_DATE))," +
                    "(1, 'Emergency Fund 6-Months', 200000.00, 120000.00, DATEADD('MONTH', 12, CURRENT_DATE))," +
                    "(1, 'Japan Holiday Trip', 180000.00, 45000.00, DATEADD('MONTH', 18, CURRENT_DATE))"
                );
            }

            // Ensure budgets exist for the current user if table is empty
            ResultSet rsBudgets = stmt.executeQuery("SELECT count(*) FROM budgets WHERE user_id = 1");
            if (rsBudgets.next() && rsBudgets.getInt(1) == 0) {
                java.time.LocalDate now = java.time.LocalDate.now();
                int m = now.getMonthValue();
                int y = now.getYear();
                stmt.executeUpdate(
                    "INSERT INTO budgets (user_id, category, amount, `month`, `year`) VALUES " +
                    "(1, 'Food', 8000.00, " + m + ", " + y + ")," +
                    "(1, 'Transport', 4000.00, " + m + ", " + y + ")," +
                    "(1, 'Shopping', 5000.00, " + m + ", " + y + ")," +
                    "(1, 'Bills', 15000.00, " + m + ", " + y + ")," +
                    "(1, 'Entertainment', 3000.00, " + m + ", " + y + ")," +
                    "(1, 'Healthcare', 2500.00, " + m + ", " + y + ")"
                );
            }
            rsBudgets.close();

            isInitialized = true;
            System.out.println("[DBConnection] Schema verified: users, transactions, budgets, financial_goals ready.");
        } catch (Exception e) {
            System.err.println("[DBConnection] Schema check exception: " + e.getMessage());
            isInitialized = true;
        } finally {
            close(null, stmt);
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
