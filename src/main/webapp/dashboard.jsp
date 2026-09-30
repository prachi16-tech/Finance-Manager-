<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Dashboard - Personal Finance Manager</title>
    <jsp:include page="/includes/header.jsp"/>
</head>
<body>
    <div class="ambient-glow glow-1"></div>
    <div class="ambient-glow glow-2"></div>

    <div class="app-container">
        <!-- Sidebar Navigation -->
        <jsp:include page="/includes/sidebar.jsp"/>

        <!-- Main Content Area -->
        <div class="main-wrapper">
            <!-- Topbar Header -->
            <header class="topbar">
                <div style="display: flex; align-items: center; gap: 16px;">
                    <button class="menu-toggle-btn" id="menuToggle">
                        <i class="bi bi-list"></i>
                    </button>
                    <div class="page-title">
                        <span>Personal Finance Manager</span>
                    </div>
                </div>
                <div class="topbar-actions">
                    <a href="${pageContext.request.contextPath}/transactions" class="btn btn-primary btn-sm">
                        <i class="bi bi-plus-circle-fill"></i> Add Transaction
                    </a>
                </div>
            </header>

            <main class="content-body">
                <!-- Alerts / Feedback -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <i class="bi bi-check-circle-fill"></i>
                        <span>${sessionScope.successMessage}</span>
                    </div>
                    <% session.removeAttribute("successMessage"); %>
                </c:if>
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-triangle-fill"></i>
                        <span>${errorMessage}</span>
                    </div>
                </c:if>

                <!-- Financial Health Overview Cards -->
                <div class="stats-grid">
                    <!-- Total Income -->
                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Total Income</div>
                            <div class="stat-value" style="color: #34d399;">
                                ₹<fmt:formatNumber value="${summary.totalIncome}" pattern="#,##0.00"/>
                            </div>
                            <p style="margin-top: 6px; font-size: 0.8rem; color: var(--text-muted);">
                                Lifetime Earned
                            </p>
                        </div>
                        <div class="stat-icon icon-income">
                            <i class="bi bi-arrow-down-left-circle-fill"></i>
                        </div>
                    </div>

                    <!-- Total Expenses -->
                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Total Expenses</div>
                            <div class="stat-value" style="color: #fb7185;">
                                ₹<fmt:formatNumber value="${summary.totalExpenses}" pattern="#,##0.00"/>
                            </div>
                            <p style="margin-top: 6px; font-size: 0.8rem; color: var(--text-muted);">
                                Lifetime Spent
                            </p>
                        </div>
                        <div class="stat-icon icon-expense">
                            <i class="bi bi-arrow-up-right-circle-fill"></i>
                        </div>
                    </div>

                    <!-- Current Balance -->
                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Current Balance</div>
                            <div class="stat-value" style="color: #818cf8;">
                                ₹<fmt:formatNumber value="${summary.currentBalance}" pattern="#,##0.00"/>
                            </div>
                            <p style="margin-top: 6px; font-size: 0.8rem; color: var(--text-muted);">
                                Income - Expenses
                            </p>
                        </div>
                        <div class="stat-icon icon-balance">
                            <i class="bi bi-wallet2"></i>
                        </div>
                    </div>

                    <!-- Savings Rate -->
                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Savings Rate</div>
                            <div class="stat-value" style="color: #fbbf24;">
                                ${summary.savingsRate}%
                            </div>
                            <p style="margin-top: 6px; font-size: 0.8rem; color: var(--text-muted);">
                                Target: &gt; 20%
                            </p>
                        </div>
                        <div class="stat-icon icon-savings">
                            <i class="bi bi-piggy-bank-fill"></i>
                        </div>
                    </div>
                </div>

                <!-- Rule-Based Financial Insights (Generated by Java) -->
                <div class="insight-box">
                    <div class="insight-icon">
                        <i class="bi bi-lightbulb-fill"></i>
                    </div>
                    <div class="insight-content" style="flex: 1;">
                        <h4>Financial Advisory Insights</h4>
                        <ul class="insight-list">
                            <c:forEach var="insight" items="${summary.financialInsights}">
                                <li>
                                    <i class="bi bi-chevron-right" style="color: #6366f1; font-size: 0.75rem;"></i>
                                    <span>${insight}</span>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>

                <!-- 2-Column Grid: Recent Transactions & Budget Progress -->
                <div class="grid-2col">
                    <!-- Left: Recent Transactions Table -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="bi bi-clock-history" style="color: #6366f1;"></i> Recent Transactions
                            </h3>
                            <a href="${pageContext.request.contextPath}/transactions" class="btn btn-secondary btn-sm">
                                View All <i class="bi bi-arrow-right"></i>
                            </a>
                        </div>

                        <div class="table-responsive">
                            <table class="modern-table">
                                <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Description</th>
                                        <th>Category</th>
                                        <th>Type</th>
                                        <th style="text-align: right;">Amount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty recentTransactions}">
                                            <tr>
                                                <td colspan="5" style="text-align: center; color: var(--text-muted); padding: 30px;">
                                                    No transactions recorded yet. Click "Add Transaction" to start!
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="t" items="${recentTransactions}">
                                                <tr>
                                                    <td>
                                                        <fmt:formatDate value="${t.transactionDate}" pattern="dd MMM yyyy"/>
                                                    </td>
                                                    <td style="font-weight: 600;">${t.description}</td>
                                                    <td>
                                                        <span class="badge badge-category">${t.category}</span>
                                                    </td>
                                                    <td>
                                                        <span class="badge ${t.type == 'INCOME' ? 'badge-income' : 'badge-expense'}">
                                                            ${t.type}
                                                        </span>
                                                    </td>
                                                    <td style="text-align: right; font-weight: 700;" class="${t.type == 'INCOME' ? 'text-income' : 'text-expense'}">
                                                        ${t.type == 'INCOME' ? '+' : '-'}₹<fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- Right: Monthly Budget Status -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="bi bi-pie-chart" style="color: #38bdf8;"></i> Budget Limits (${currentMonthName})
                            </h3>
                            <a href="${pageContext.request.contextPath}/budgets" class="btn btn-secondary btn-sm">
                                Manage
                            </a>
                        </div>

                        <c:choose>
                            <c:when test="${empty budgetSummaries}">
                                <div style="text-align: center; color: var(--text-muted); padding: 40px 10px;">
                                    <p>No budgets set for this month.</p>
                                    <a href="${pageContext.request.contextPath}/budgets" class="btn btn-primary btn-sm" style="margin-top: 12px;">
                                        Set Monthly Budgets
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div style="display: flex; flex-direction: column; gap: 16px;">
                                    <c:forEach var="bs" items="${budgetSummaries}" begin="0" end="4">
                                        <div>
                                            <div style="display: flex; justify-content: space-between; font-size: 0.88rem; font-weight: 600; margin-bottom: 4px;">
                                                <span>${bs.category}</span>
                                                <span class="${bs.exceeded ? 'text-exceeded' : 'text-muted-sec'}">
                                                    ₹<fmt:formatNumber value="${bs.spentAmount}" pattern="#,##0"/> /
                                                    ₹<fmt:formatNumber value="${bs.budgetAmount}" pattern="#,##0"/>
                                                    <c:if test="${bs.exceeded}">
                                                        <span class="badge badge-warning" style="margin-left: 4px; padding: 2px 6px;">Exceeded</span>
                                                    </c:if>
                                                </span>
                                            </div>
                                            <div class="progress-container">
                                                <div class="progress-bar ${bs.percentageUsed > 100 ? 'progress-danger' : (bs.percentageUsed > 80 ? 'progress-warning' : 'progress-safe')}"
                                                     data-width="${bs.percentageUsed > 100 ? 100 : bs.percentageUsed}"
                                                     style="width: 100%;">
                                                </div>
                                            </div>
                                            <div style="display: flex; justify-content: space-between; font-size: 0.75rem; color: var(--text-muted);">
                                                <span>${bs.percentageUsed}% used</span>
                                                <span>
                                                    <c:choose>
                                                        <c:when test="${bs.remainingAmount >= 0}">
                                                            ₹<fmt:formatNumber value="${bs.remainingAmount}" pattern="#,##0"/> left
                                                        </c:when>
                                                        <c:otherwise>
                                                            Over by ₹<fmt:formatNumber value="${bs.spentAmount - bs.budgetAmount}" pattern="#,##0"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
</body>
</html>
