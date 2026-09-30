# Personal Finance Manager - Advanced Java Mini Project

A modern, full-stack **Personal Finance & Budget Management Web Application** built strictly using **Core Java, Java Servlets, JSP, JDBC, MySQL, MVC Architecture, and Apache Tomcat** (No Spring Boot, No React, No AI).

---

## 🌟 Key Features

1. **Dashboard & Rule-based Financial Insights**
   - Real-time Total Income, Total Expenses, Current Balance, and Savings Rate dynamically calculated from MySQL.
   - Deterministic rule-based financial advisory insights generated entirely in Java (e.g., budget warnings, high spending categories, savings rate benchmarks).
   - Recent transaction history and monthly budget progress widgets.

2. **Transaction Management (Complete CRUD)**
   - Add, View, Edit, and Delete Income & Expense transactions.
   - Dynamic search, category filters, type filters, and custom date range filters.
   - Support for multiple payment methods (UPI, Bank Transfer, Credit Card, Debit Card, Cash).

3. **Monthly Budget Planning**
   - Set monthly spending caps for individual categories (Food, Transport, Bills, Shopping, etc.).
   - Visual progress bars showing percentage used, remaining amounts, and "⚠ Budget Exceeded" alerts.

4. **Financial Goals Tracking**
   - Create financial goals with target amounts and deadlines (e.g., New Laptop, Vacation, Emergency Fund).
   - Real-time progress percentage and Java-computed required monthly savings target.
   - Add direct savings contributions/deposits to any goal.

5. **Financial Analytics & Visualizations (Chart.js)**
   - Monthly Spending Volume (6-Month Trend).
   - Income vs Expenses Comparison (Grouped Bar Chart).
   - Category-wise Expense Breakdown (Doughnut Chart).
   - Net Savings Trajectory (Gradient Line Chart).

6. **Authentication & Profile Security**
   - User Registration and Login with `HttpSession` management.
   - Secure SHA-256 password hashing with cryptographic salt in `PasswordUtil.java`.
   - Profile management (Update Name, Email, and Change Password with old password validation).

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 11 / 17, Java Servlets (`javax.servlet`), JDBC |
| **Architecture** | MVC (Model - View - Controller) + DAO + Service Layer |
| **Database** | MySQL (`personal_finance_db`) with Prepared Statements |
| **View / UI** | JSP, JSTL, HTML5, CSS3 (Modern Dark Fintech Design), JavaScript |
| **Charts** | Chart.js |
| **Server** | Apache Tomcat 9 / 10 |
| **Build Tool** | Apache Maven |

---

## 📂 Project Architecture & Packages

```text
PersonalFinanceManager/
├── pom.xml
├── database.sql
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/finance/
        │       ├── controller/
        │       │   ├── LoginServlet.java
        │       │   ├── RegisterServlet.java
        │       │   ├── LogoutServlet.java
        │       │   ├── DashboardServlet.java
        │       │   ├── TransactionServlet.java
        │       │   ├── BudgetServlet.java
        │       │   ├── GoalServlet.java
        │       │   ├── AnalyticsServlet.java
        │       │   └── ProfileServlet.java
        │       ├── model/
        │       │   ├── User.java
        │       │   ├── Transaction.java
        │       │   ├── Budget.java
        │       │   ├── FinancialGoal.java
        │       │   ├── BudgetSummary.java
        │       │   ├── DashboardSummary.java
        │       │   └── GoalProgress.java
        │       ├── dao/
        │       │   ├── UserDAO.java
        │       │   ├── TransactionDAO.java
        │       │   ├── BudgetDAO.java
        │       │   └── FinancialGoalDAO.java
        │       ├── service/
        │       │   ├── FinanceService.java
        │       │   ├── BudgetService.java
        │       │   ├── GoalService.java
        │       │   └── AnalyticsService.java
        │       └── util/
        │           ├── DBConnection.java
        │           ├── PasswordUtil.java
        │           └── ValidationUtil.java
        └── webapp/
            ├── WEB-INF/
            │   └── web.xml
            ├── css/
            │   └── style.css
            ├── js/
            │   ├── dashboard.js
            │   ├── transactions.js
            │   ├── budgets.js
            │   ├── goals.js
            │   └── analytics.js
            ├── includes/
            │   ├── header.jsp
            │   └── sidebar.jsp
            ├── login.jsp
            ├── register.jsp
            ├── dashboard.jsp
            ├── transactions.jsp
            ├── budgets.jsp
            ├── goals.jsp
            ├── analytics.jsp
            └── profile.jsp
```

---

## 🚀 Setup & Execution Guide

### Step 1: Database Setup (MySQL)
1. Open MySQL Workbench, phpMyAdmin, or MySQL CLI.
2. Execute the included [database.sql](file:///c:/Users/prachiharal_16/Desktop/SIH2026/Mayur%20Mini%20Project/database.sql) script:
   ```sql
   SOURCE c:/Users/prachiharal_16/Desktop/SIH2026/Mayur Mini Project/database.sql;
   ```
   Or copy-paste the contents of `database.sql` and run it in your MySQL client.
3. Verify that the database `personal_finance_db` and tables (`users`, `transactions`, `budgets`, `financial_goals`) are created.

### Step 2: Configure Database Credentials (if needed)
In [DBConnection.java](file:///c:/Users/prachiharal_16/Desktop/SIH2026/Mayur%20Mini%20Project/src/main/java/com/finance/util/DBConnection.java), verify the MySQL connection settings:
- **Default URL**: `jdbc:mysql://localhost:3306/personal_finance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- **Default Username**: `root`
- **Default Password**: ` ` (blank) or your MySQL password.

### Step 3: Run in Eclipse / IntelliJ / NetBeans / Tomcat

#### Running in Eclipse IDE:
1. File -> Import -> Maven -> Existing Maven Projects -> Select this folder.
2. Right-click project -> **Run As** -> **Run on Server** -> Select **Apache Tomcat 9.0**.

#### Running in IntelliJ IDEA:
1. Open Project -> Select `pom.xml`.
2. Add Configuration -> Tomcat Server -> Local -> Select Tomcat installation directory.
3. In Deployment tab -> Add Artifact: `PersonalFinanceManager:war exploded`.
4. Click Run / Debug.

#### Running via Maven Command Line:
```bash
mvn clean package
```
Copy `target/PersonalFinanceManager.war` to your Apache Tomcat `webapps/` directory and start Tomcat.

---

## 🔑 Demo Account Credentials

| Attribute | Value |
|---|---|
| **Email** | `mayur@example.com` |
| **Password** | `Password@123` |
| **Sample Data** | Pre-seeded with realistic income, expense records, budgets, and savings goals. |

---

## 🔒 Security & Code Quality Measures
- **Prepared Statements**: All SQL queries utilize `PreparedStatement` with parameterized inputs to completely prevent SQL injection attacks.
- **Password Hashing**: Passwords are cryptographically hashed using SHA-256 with 16-byte random salt.
- **Session Protection**: All internal views (`/dashboard`, `/transactions`, `/budgets`, `/goals`, `/analytics`, `/profile`) validate active `HttpSession` state.
- **Separation of Concerns**: Strict adherence to MVC architecture — JSPs contain zero business logic or SQL queries.
