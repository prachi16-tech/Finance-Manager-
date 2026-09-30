-- ==========================================================
-- Personal Finance Manager - Database Setup Script
-- Database: personal_finance_db
-- ==========================================================

-- 1. Create Database if not exists
CREATE DATABASE IF NOT EXISTS personal_finance_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE personal_finance_db;

-- 2. Drop existing tables in reverse dependency order for clean recreation
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS financial_goals;
DROP TABLE IF EXISTS budgets;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- 3. Create 'users' table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Create 'transactions' table
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    transaction_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);
CREATE INDEX idx_transactions_user_type ON transactions(user_id, type);
CREATE INDEX idx_transactions_user_category ON transactions(user_id, category);

-- 5. Create 'budgets' table
CREATE TABLE budgets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    month INT NOT NULL CHECK (month BETWEEN 1 AND 12),
    year INT NOT NULL CHECK (year >= 2000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_category_month_year UNIQUE (user_id, category, month, year),
    CONSTRAINT fk_budgets_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_budgets_user_period ON budgets(user_id, year, month);

-- 6. Create 'financial_goals' table
CREATE TABLE financial_goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    goal_name VARCHAR(150) NOT NULL,
    target_amount DECIMAL(12,2) NOT NULL,
    current_amount DECIMAL(12,2) DEFAULT 0.00,
    deadline DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_goals_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_goals_user ON financial_goals(user_id);

-- ==========================================================
-- 7. Seed Sample Data for Testing & Demo
-- Demo Account Credentials:
-- Email: mayur@example.com
-- Password: Password@123 (SHA-256 hashed with salt: "00112233445566778899aabbccddeeff:a5e4d29ca230559f338d17b20e0ffb9e672728f32daec0ebce5b31bf4e680a6b")
-- PasswordUtil format: salt:hash or raw fallback supported
-- ==========================================================

INSERT INTO users (id, name, email, password) VALUES
(1, 'Mayur Patil', 'mayur@example.com', 'ff7bd97b1a7789ddd2775122fd6817f3173672da9f802ceec57f284325bf589f');

-- Sample Transactions for Demo User
INSERT INTO transactions (user_id, type, amount, category, description, transaction_date, payment_method) VALUES
(1, 'INCOME', 65000.00, 'Salary', 'Monthly Tech Job Salary', CURDATE() - INTERVAL 25 DAY, 'Bank Transfer'),
(1, 'INCOME', 15000.00, 'Freelance', 'Web Design Project Freelance', CURDATE() - INTERVAL 12 DAY, 'UPI'),
(1, 'EXPENSE', 12000.00, 'Bills', 'Apartment Rent & Utilities', CURDATE() - INTERVAL 22 DAY, 'Bank Transfer'),
(1, 'EXPENSE', 5400.00, 'Food', 'Monthly Grocery Store', CURDATE() - INTERVAL 18 DAY, 'Credit Card'),
(1, 'EXPENSE', 1850.00, 'Food', 'Weekend Dinner with Friends', CURDATE() - INTERVAL 10 DAY, 'UPI'),
(1, 'EXPENSE', 3200.00, 'Transport', 'Fuel & Metro Recharge', CURDATE() - INTERVAL 8 DAY, 'UPI'),
(1, 'EXPENSE', 4500.00, 'Shopping', 'Noise Cancelling Earbuds', CURDATE() - INTERVAL 6 DAY, 'Credit Card'),
(1, 'EXPENSE', 2100.00, 'Entertainment', 'Movie & Streaming Subscriptions', CURDATE() - INTERVAL 4 DAY, 'Debit Card'),
(1, 'EXPENSE', 1500.00, 'Healthcare', 'Dental Checkup & Medicines', CURDATE() - INTERVAL 2 DAY, 'Cash'),
(1, 'INCOME', 5000.00, 'Other', 'Stock Dividend Payout', CURDATE() - INTERVAL 1 DAY, 'Bank Transfer');

-- Sample Budgets for Current Month & Year
INSERT INTO budgets (user_id, category, amount, month, year) VALUES
(1, 'Food', 8000.00, MONTH(CURDATE()), YEAR(CURDATE())),
(1, 'Transport', 4000.00, MONTH(CURDATE()), YEAR(CURDATE())),
(1, 'Shopping', 5000.00, MONTH(CURDATE()), YEAR(CURDATE())),
(1, 'Bills', 15000.00, MONTH(CURDATE()), YEAR(CURDATE())),
(1, 'Entertainment', 3000.00, MONTH(CURDATE()), YEAR(CURDATE())),
(1, 'Healthcare', 2500.00, MONTH(CURDATE()), YEAR(CURDATE()));

-- Sample Financial Goals for Demo User
INSERT INTO financial_goals (user_id, goal_name, target_amount, current_amount, deadline) VALUES
(1, 'New M3 MacBook Pro', 140000.00, 85000.00, DATE_ADD(CURDATE(), INTERVAL 6 MONTH)),
(1, 'Emergency Fund 6-Months', 200000.00, 120000.00, DATE_ADD(CURDATE(), INTERVAL 12 MONTH)),
(1, 'Japan Holiday Trip', 180000.00, 45000.00, DATE_ADD(CURDATE(), INTERVAL 18 MONTH));
