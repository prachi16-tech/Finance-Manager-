<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Budgets - Personal Finance Manager</title>
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
                        <i class="bi bi-pie-chart-fill" style="color: #6366f1;"></i>
                        <span>Monthly Budget Planner</span>
                    </div>
                </div>
                <div class="topbar-actions">
                    <button class="btn btn-primary" onclick="openBudgetModal('', '')">
                        <i class="bi bi-plus-lg"></i> Set Budget
                    </button>
                </div>
            </header>

            <main class="content-body">
                <!-- Notifications -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <i class="bi bi-check-circle-fill"></i>
                        <span>${sessionScope.successMessage}</span>
                    </div>
                    <% session.removeAttribute("successMessage"); %>
                </c:if>
                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-triangle-fill"></i>
                        <span>${sessionScope.errorMessage}</span>
                    </div>
                    <% session.removeAttribute("errorMessage"); %>
                </c:if>

                <!-- Month / Year Period Selector -->
                <form action="${pageContext.request.contextPath}/budgets" method="GET" class="filter-bar">
                    <div class="filter-item" style="display: flex; align-items: center; gap: 12px;">
                        <label class="form-label" style="margin-bottom: 0; white-space: nowrap;">Target Month & Year:</label>
                        <select name="month" class="form-select" style="max-width: 160px;">
                            <c:forEach var="m" begin="1" end="12">
                                <option value="${m}" ${selectedMonth == m ? 'selected' : ''}>
                                    ${java.time.Month.of(m).name()}
                                </option>
                            </c:forEach>
                        </select>
                        <select name="year" class="form-select" style="max-width: 120px;">
                            <c:forEach var="y" begin="2023" end="2030">
                                <option value="${y}" ${selectedYear == y ? 'selected' : ''}>${y}</option>
                            </c:forEach>
                        </select>
                        <button type="submit" class="btn btn-secondary">
                            <i class="bi bi-arrow-repeat"></i> Load Period
                        </button>
                    </div>
                </form>

                <!-- High-level Summary Cards -->
                <div class="stats-grid" style="grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));">
                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Total Allocated Budget</div>
                            <div class="stat-value" style="color: #818cf8;">
                                ₹<fmt:formatNumber value="${totalBudget}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        <div class="stat-icon icon-balance">
                            <i class="bi bi-cash-stack"></i>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Actual Spent This Month</div>
                            <div class="stat-value" style="color: #fb7185;">
                                ₹<fmt:formatNumber value="${totalSpent}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        <div class="stat-icon icon-expense">
                            <i class="bi bi-cart-x-fill"></i>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Remaining Overall</div>
                            <div class="stat-value" style="color: ${totalBudget.subtract(totalSpent) >= 0 ? '#34d399' : '#f43f5e'};">
                                ₹<fmt:formatNumber value="${totalBudget.subtract(totalSpent)}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        <div class="stat-icon icon-income">
                            <i class="bi bi-shield-check"></i>
                        </div>
                    </div>
                </div>

                <!-- Category Budgets Grid -->
                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 20px;">
                    <c:choose>
                        <c:when test="${empty budgetSummaries}">
                            <div class="card" style="grid-column: 1 / -1; text-align: center; padding: 60px 20px;">
                                <i class="bi bi-pie-chart" style="font-size: 2.5rem; color: var(--text-muted); margin-bottom: 12px; display: block;"></i>
                                <h3>No Budgets Set For This Period</h3>
                                <p style="margin-top: 8px;">Allocate budget caps to control expenses across categories.</p>
                                <button class="btn btn-primary" onclick="openBudgetModal('', '')" style="margin-top: 16px;">
                                    <i class="bi bi-plus-lg"></i> Create First Budget
                                </button>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="b" items="${budgetSummaries}">
                                <div class="card" style="border-left: 4px solid ${b.exceeded ? '#f43f5e' : (b.percentageUsed > 80 ? '#f59e0b' : '#10b981')};">
                                    <div class="card-header" style="margin-bottom: 12px;">
                                        <div>
                                            <h4 style="font-size: 1.15rem; font-weight: 700;">${b.category}</h4>
                                            <span style="font-size: 0.8rem; color: var(--text-muted);">Monthly Cap</span>
                                        </div>
                                        <div style="display: flex; gap: 6px;">
                                            <button class="btn btn-secondary btn-icon btn-sm"
                                                    onclick="openBudgetModal('${b.category}', '${b.budgetAmount}')" title="Edit Budget">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <c:if test="${b.budgetId > 0}">
                                                <form action="${pageContext.request.contextPath}/budgets" method="POST"
                                                      onsubmit="return confirm('Delete budget limit for ${b.category}?');" style="display:inline;">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${b.budgetId}">
                                                    <input type="hidden" name="month" value="${selectedMonth}">
                                                    <input type="hidden" name="year" value="${selectedYear}">
                                                    <button type="submit" class="btn btn-danger btn-icon btn-sm" title="Delete">
                                                        <i class="bi bi-trash3"></i>
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </div>

                                    <!-- Numbers Detail -->
                                    <div style="display: flex; justify-content: space-between; font-size: 0.95rem; margin-bottom: 6px;">
                                        <span style="color: var(--text-secondary);">Budget:</span>
                                        <span style="font-weight: 700;">₹<fmt:formatNumber value="${b.budgetAmount}" pattern="#,##0.00"/></span>
                                    </div>
                                    <div style="display: flex; justify-content: space-between; font-size: 0.95rem; margin-bottom: 6px;">
                                        <span style="color: var(--text-secondary);">Spent:</span>
                                        <span style="font-weight: 700; color: ${b.exceeded ? '#fb7185' : 'var(--text-primary)'};">
                                            ₹<fmt:formatNumber value="${b.spentAmount}" pattern="#,##0.00"/>
                                        </span>
                                    </div>
                                    <div style="display: flex; justify-content: space-between; font-size: 0.95rem; margin-bottom: 12px;">
                                        <span style="color: var(--text-secondary);">Remaining:</span>
                                        <span style="font-weight: 700; color: ${b.remainingAmount >= 0 ? '#34d399' : '#f43f5e'};">
                                            ₹<fmt:formatNumber value="${b.remainingAmount}" pattern="#,##0.00"/>
                                        </span>
                                    </div>

                                    <!-- Progress Bar -->
                                    <div class="progress-container">
                                        <div class="progress-bar ${b.exceeded ? 'progress-danger' : (b.percentageUsed > 80 ? 'progress-warning' : 'progress-safe')}"
                                             style="width: ${b.percentageUsed > 100 ? 100 : b.percentageUsed}%;">
                                        </div>
                                    </div>

                                    <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 8px;">
                                        <span style="font-size: 0.85rem; font-weight: 700; color: ${b.exceeded ? '#fb7185' : 'var(--text-muted)'};">
                                            ${b.percentageUsed}% Used
                                        </span>
                                        <c:choose>
                                            <c:when test="${b.exceeded}">
                                                <span class="badge badge-warning">
                                                    <i class="bi bi-exclamation-triangle-fill"></i> Budget Exceeded
                                                </span>
                                            </c:when>
                                            <c:when test="${b.percentageUsed >= 90}">
                                                <span class="badge badge-warning">Near Limit</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-income">On Track</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>
        </div>
    </div>

    <!-- Set / Edit Budget Modal -->
    <div class="modal-overlay" id="budgetModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Set Category Budget</h3>
                <button class="modal-close" onclick="closeBudgetModal()">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/budgets" method="POST">
                <input type="hidden" name="action" value="save">
                <input type="hidden" name="month" value="${selectedMonth}">
                <input type="hidden" name="year" value="${selectedYear}">
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-label">Category</label>
                        <select id="budgetCategory" name="category" class="form-select" required>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat}">${cat}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Monthly Limit (₹)</label>
                        <input type="number" step="0.01" min="1" id="budgetAmount" name="amount" class="form-control"
                               placeholder="e.g. 5000" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeBudgetModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Budget</button>
                </div>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
    <script src="${pageContext.request.contextPath}/js/budgets.js"></script>
</body>
</html>
