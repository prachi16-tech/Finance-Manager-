<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Financial Analytics - Personal Finance Manager</title>
    <jsp:include page="/includes/header.jsp"/>
    <!-- Chart.js CDN -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
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
                        <i class="bi bi-graph-up-arrow" style="color: #6366f1;"></i>
                        <span>Financial Analytics & Visual Trends</span>
                    </div>
                </div>
                <div class="topbar-actions">
                    <button type="button" class="theme-toggle-btn" title="Toggle Theme" aria-label="Toggle Theme">
                        <i class="bi bi-sun-fill"></i>
                    </button>
                </div>
            </header>

            <main class="content-body">
                <!-- Notifications -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-triangle-fill"></i>
                        <span>${errorMessage}</span>
                    </div>
                </c:if>

                <!-- High-level Analytics Metrics -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Total Cash Inflow</div>
                            <div class="stat-value" style="color: #34d399;">
                                ₹<fmt:formatNumber value="${analyticsData.totalIncome}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        <div class="stat-icon icon-income">
                            <i class="bi bi-graph-up"></i>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Total Cash Outflow</div>
                            <div class="stat-value" style="color: #fb7185;">
                                ₹<fmt:formatNumber value="${analyticsData.totalExpenses}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        <div class="stat-icon icon-expense">
                            <i class="bi bi-graph-down"></i>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Net Lifetime Accumulation</div>
                            <div class="stat-value" style="color: #818cf8;">
                                ₹<fmt:formatNumber value="${analyticsData.netSavings}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        <div class="stat-icon icon-balance">
                            <i class="bi bi-bank2"></i>
                        </div>
                    </div>
                </div>

                <!-- 2x2 Grid for Charts -->
                <div class="grid-equal-2col">
                    <!-- Chart 1: Income vs Expenses -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="bi bi-bar-chart-fill" style="color: #6366f1;"></i> Income vs Expenses (Last 6 Months)
                            </h3>
                        </div>
                        <div style="height: 320px; position: relative;">
                            <canvas id="incomeVsExpenseChart"></canvas>
                        </div>
                    </div>

                    <!-- Chart 2: Category-Wise Expenses -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="bi bi-pie-chart-fill" style="color: #ec4899;"></i> Category-Wise Expenses
                            </h3>
                        </div>
                        <div style="height: 320px; position: relative;">
                            <canvas id="categoryDoughnutChart"></canvas>
                        </div>
                    </div>

                    <!-- Chart 3: Monthly Expenses Trend -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="bi bi-calendar3-range" style="color: #f43f5e;"></i> Monthly Spending Volume
                            </h3>
                        </div>
                        <div style="height: 320px; position: relative;">
                            <canvas id="monthlyExpenseChart"></canvas>
                        </div>
                    </div>

                    <!-- Chart 4: Savings Trend -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="bi bi-activity" style="color: #10b981;"></i> Net Savings Trajectory
                            </h3>
                        </div>
                        <div style="height: 320px; position: relative;">
                            <canvas id="savingsTrendChart"></canvas>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <!-- Data container for Chart.js -->
    <div id="analyticsPayload" data-analytics="${analyticsDataJson}" style="display:none;"></div>
    <script>
        const dataElement = document.getElementById('analyticsPayload');
        const rawAnalyticsData = dataElement ? JSON.parse(dataElement.getAttribute('data-analytics')) : {};
    </script>
    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
    <script src="${pageContext.request.contextPath}/js/analytics.js"></script>
</body>
</html>
