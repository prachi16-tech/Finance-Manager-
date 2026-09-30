<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Transactions - Personal Finance Manager</title>
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
                        <i class="bi bi-arrow-left-right" style="color: #6366f1;"></i>
                        <span>Transaction Management</span>
                    </div>
                </div>
                <div class="topbar-actions">
                    <button class="btn btn-primary" onclick="openAddModal()">
                        <i class="bi bi-plus-lg"></i> Add Transaction
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

                <!-- Filter and Search Bar -->
                <form action="${pageContext.request.contextPath}/transactions" method="GET" class="filter-bar">
                    <div class="filter-item" style="flex: 2; min-width: 200px;">
                        <input type="text" name="search" class="form-control" placeholder="Search description, category..."
                               value="${search}">
                    </div>

                    <div class="filter-item">
                        <select name="type" class="form-select">
                            <option value="">All Types</option>
                            <option value="INCOME" ${selectedType == 'INCOME' ? 'selected' : ''}>Income</option>
                            <option value="EXPENSE" ${selectedType == 'EXPENSE' ? 'selected' : ''}>Expense</option>
                        </select>
                    </div>

                    <div class="filter-item">
                        <select name="category" class="form-select">
                            <option value="">All Categories</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat}" ${selectedCategory == cat ? 'selected' : ''}>${cat}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-item">
                        <input type="date" name="startDate" class="form-control" value="${startDate}" title="Start Date">
                    </div>

                    <div class="filter-item">
                        <input type="date" name="endDate" class="form-control" value="${endDate}" title="End Date">
                    </div>

                    <div style="display: flex; gap: 8px;">
                        <button type="submit" class="btn btn-secondary">
                            <i class="bi bi-funnel-fill"></i> Filter
                        </button>
                        <a href="${pageContext.request.contextPath}/transactions" class="btn btn-secondary btn-icon" title="Reset Filters">
                            <i class="bi bi-arrow-counterclockwise"></i>
                        </a>
                    </div>
                </form>

                <!-- Transactions Data Card & Table -->
                <div class="card">
                    <div class="card-header">
                        <h3 class="card-title">
                            <span>All Records (${transactions != null ? transactions.size() : 0})</span>
                        </h3>
                    </div>

                    <div class="table-responsive">
                        <table class="modern-table">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Description</th>
                                    <th>Category</th>
                                    <th>Payment Method</th>
                                    <th>Type</th>
                                    <th style="text-align: right;">Amount</th>
                                    <th style="text-align: center;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty transactions}">
                                        <tr>
                                            <td colspan="7" style="text-align: center; color: var(--text-muted); padding: 40px;">
                                                No transactions matching your criteria.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="t" items="${transactions}">
                                            <tr>
                                                <td>
                                                    <fmt:formatDate value="${t.transactionDate}" pattern="dd MMM yyyy"/>
                                                </td>
                                                <td style="font-weight: 600;">${t.description}</td>
                                                <td>
                                                    <span class="badge badge-category">${t.category}</span>
                                                </td>
                                                <td>
                                                    <span style="color: var(--text-secondary); font-size: 0.88rem;">
                                                        <i class="bi bi-credit-card-2-front"></i> ${t.paymentMethod}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span class="badge ${t.type == 'INCOME' ? 'badge-income' : 'badge-expense'}">
                                                        ${t.type}
                                                    </span>
                                                </td>
                                                <td style="text-align: right; font-weight: 700; font-size: 1rem; color: ${t.type == 'INCOME' ? '#34d399' : '#fb7185'};">
                                                    ${t.type == 'INCOME' ? '+' : '-'}₹<fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/>
                                                </td>
                                                <td style="text-align: center;">
                                                    <div style="display: inline-flex; gap: 8px;">
                                                        <button class="btn btn-secondary btn-icon btn-sm" onclick="openEditModal(${t.id})" title="Edit Transaction">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </button>
                                                        <form action="${pageContext.request.contextPath}/transactions" method="POST"
                                                              onsubmit="return confirm('Are you sure you want to delete this transaction?');" style="display:inline;">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="id" value="${t.id}">
                                                            <button type="submit" class="btn btn-danger btn-icon btn-sm" title="Delete Transaction">
                                                                <i class="bi bi-trash3-fill"></i>
                                                            </button>
                                                        </form>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <!-- Add Transaction Modal -->
    <div class="modal-overlay" id="addTransactionModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Add New Transaction</h3>
                <button class="modal-close" onclick="closeAddModal()">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/transactions" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-label">Transaction Type</label>
                        <select name="type" class="form-select" required>
                            <option value="EXPENSE">Expense</option>
                            <option value="INCOME">Income</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Amount (₹)</label>
                        <input type="number" step="0.01" min="0.01" name="amount" class="form-control" placeholder="0.00" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Category</label>
                        <select name="category" class="form-select" required>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat}">${cat}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Description</label>
                        <input type="text" name="description" class="form-control" placeholder="e.g. Grocery store, Salary payout" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Transaction Date</label>
                        <input type="date" id="addTransactionDate" name="transactionDate" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Payment Method</label>
                        <select name="paymentMethod" class="form-select" required>
                            <c:forEach var="pm" items="${paymentMethods}">
                                <option value="${pm}">${pm}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeAddModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Transaction</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Edit Transaction Modal -->
    <div class="modal-overlay" id="editTransactionModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Edit Transaction</h3>
                <button class="modal-close" onclick="closeEditModal()">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/transactions" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="editId" name="id">
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-label">Transaction Type</label>
                        <select id="editType" name="type" class="form-select" required>
                            <option value="EXPENSE">Expense</option>
                            <option value="INCOME">Income</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Amount (₹)</label>
                        <input type="number" step="0.01" min="0.01" id="editAmount" name="amount" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Category</label>
                        <select id="editCategory" name="category" class="form-select" required>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat}">${cat}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Description</label>
                        <input type="text" id="editDescription" name="description" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Transaction Date</label>
                        <input type="date" id="editTransactionDate" name="transactionDate" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Payment Method</label>
                        <select id="editPaymentMethod" name="paymentMethod" class="form-select" required>
                            <c:forEach var="pm" items="${paymentMethods}">
                                <option value="${pm}">${pm}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeEditModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Transaction</button>
                </div>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
    <script src="${pageContext.request.contextPath}/js/transactions.js"></script>
</body>
</html>
