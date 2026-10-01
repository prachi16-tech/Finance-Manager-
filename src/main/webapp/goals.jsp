<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Financial Goals - Personal Finance Manager</title>
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
                        <i class="bi bi-bullseye" style="color: #6366f1;"></i>
                        <span>Financial Goals & Targets</span>
                    </div>
                </div>
                <div class="topbar-actions">
                    <button type="button" class="theme-toggle-btn" title="Toggle Theme" aria-label="Toggle Theme">
                        <i class="bi bi-sun-fill"></i>
                    </button>
                    <button class="btn btn-primary" onclick="openAddGoalModal()">
                        <i class="bi bi-plus-lg"></i> Create Goal
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

                <!-- High-level Summary Cards -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Total Target Ambition</div>
                            <div class="stat-value" style="color: #818cf8;">
                                ₹<fmt:formatNumber value="${totalTarget}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        <div class="stat-icon icon-balance">
                            <i class="bi bi-flag-fill"></i>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Total Amount Saved</div>
                            <div class="stat-value" style="color: #34d399;">
                                ₹<fmt:formatNumber value="${totalSaved}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        <div class="stat-icon icon-income">
                            <i class="bi bi-piggy-bank-fill"></i>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div>
                            <div class="stat-label">Remaining Gap</div>
                            <div class="stat-value" style="color: #fbbf24;">
                                ₹<fmt:formatNumber value="${totalTarget.subtract(totalSaved) > 0 ? totalTarget.subtract(totalSaved) : 0}" pattern="#,##0.00"/>
                            </div>
                        </div>
                        <div class="stat-icon icon-savings">
                            <i class="bi bi-hourglass-split"></i>
                        </div>
                    </div>
                </div>

                <!-- Goals Grid -->
                <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); gap: 24px;">
                    <c:choose>
                        <c:when test="${empty goals}">
                            <div class="card" style="grid-column: 1 / -1; text-align: center; padding: 60px 20px;">
                                <i class="bi bi-bullseye" style="font-size: 2.5rem; color: var(--text-muted); margin-bottom: 12px; display: block;"></i>
                                <h3>No Financial Goals Set Yet</h3>
                                <p style="margin-top: 8px;">Create savings goals for vacations, emergency funds, or gadgets!</p>
                                <button class="btn btn-primary" onclick="openAddGoalModal()" style="margin-top: 16px;">
                                    <i class="bi bi-plus-lg"></i> Create First Goal
                                </button>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="gp" items="${goals}">
                                <div class="card" style="border-top: 4px solid ${gp.achieved ? '#10b981' : '#6366f1'};">
                                    <div class="card-header" style="margin-bottom: 16px;">
                                        <div>
                                            <h3 style="font-size: 1.25rem; font-weight: 700;">${gp.goal.goalName}</h3>
                                            <span style="font-size: 0.82rem; color: var(--text-muted);">
                                                <i class="bi bi-calendar-event"></i> Deadline: <fmt:formatDate value="${gp.goal.deadline}" pattern="dd MMM yyyy"/>
                                            </span>
                                        </div>
                                        <div style="display: flex; gap: 6px;">
                                            <button class="btn btn-secondary btn-icon btn-sm"
                                                    onclick="openEditGoalModal(${gp.goal.id})" title="Edit Goal Details">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <form action="${pageContext.request.contextPath}/goals" method="POST"
                                                  onsubmit="return confirm('Are you sure you want to delete this goal?');" style="display:inline;">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${gp.goal.id}">
                                                <button type="submit" class="btn btn-danger btn-icon btn-sm" title="Delete Goal">
                                                    <i class="bi bi-trash3"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </div>

                                    <!-- Amounts details -->
                                    <div style="display: flex; justify-content: space-between; font-size: 0.95rem; margin-bottom: 8px;">
                                        <span style="color: var(--text-secondary);">Target Amount:</span>
                                        <span style="font-weight: 700;">₹<fmt:formatNumber value="${gp.goal.targetAmount}" pattern="#,##0.00"/></span>
                                    </div>

                                    <div style="display: flex; justify-content: space-between; font-size: 0.95rem; margin-bottom: 8px;">
                                        <span style="color: var(--text-secondary);">Saved So Far:</span>
                                        <span style="font-weight: 700; color: #34d399;">₹<fmt:formatNumber value="${gp.goal.currentAmount}" pattern="#,##0.00"/></span>
                                    </div>

                                    <div style="display: flex; justify-content: space-between; font-size: 0.95rem; margin-bottom: 12px;">
                                        <span style="color: var(--text-secondary);">Remaining Gap:</span>
                                        <span style="font-weight: 700; color: ${gp.achieved ? '#34d399' : '#fb7185'};">
                                            ₹<fmt:formatNumber value="${gp.remainingAmount}" pattern="#,##0.00"/>
                                        </span>
                                    </div>

                                    <!-- Progress Bar -->
                                    <div class="progress-container" style="height: 12px;">
                                        <div class="progress-bar ${gp.achieved ? 'progress-safe' : 'progress-primary'}"
                                             style="width: ${gp.progressPercentage}%;">
                                        </div>
                                    </div>

                                    <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 8px; margin-bottom: 16px;">
                                        <span style="font-size: 0.9rem; font-weight: 700; color: #818cf8;">
                                            ${gp.progressPercentage}% Saved
                                        </span>
                                        <c:choose>
                                            <c:when test="${gp.achieved}">
                                                <span class="badge badge-income"><i class="bi bi-check2-circle"></i> Goal Achieved!</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-size: 0.8rem; color: var(--text-muted);">
                                                    ${gp.monthsRemaining} months left
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <c:if test="${!gp.achieved}">
                                        <div style="background: rgba(255, 255, 255, 0.03); border-radius: var(--radius-sm); padding: 10px 14px; margin-bottom: 16px; font-size: 0.85rem; color: #cbd5e1; border: 1px solid var(--border-color);">
                                            <i class="bi bi-calculator" style="color: #6366f1;"></i>
                                            Save <strong>₹<fmt:formatNumber value="${gp.requiredMonthlySaving}" pattern="#,##0.00"/>/mo</strong> to meet deadline.
                                        </div>
                                    </c:if>

                                    <!-- Action: Add Savings Deposit -->
                                    <button class="btn btn-secondary btn-sm" style="width: 100%;"
                                            onclick="openDepositModal(${gp.goal.id}, '${gp.goal.goalName}')">
                                        <i class="bi bi-cash-coin" style="color: #34d399;"></i> Add Savings Contribution
                                    </button>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>
        </div>
    </div>

    <!-- Create Goal Modal -->
    <div class="modal-overlay" id="addGoalModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Create Financial Goal</h3>
                <button class="modal-close" onclick="closeAddGoalModal()">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/goals" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-label">Goal Name</label>
                        <input type="text" name="goalName" class="form-control" placeholder="e.g. New M3 MacBook Pro, Emergency Fund" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Target Amount (₹)</label>
                        <input type="number" step="0.01" min="1" name="targetAmount" class="form-control" placeholder="e.g. 100000" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Initial Amount Saved (₹)</label>
                        <input type="number" step="0.01" min="0" name="currentAmount" class="form-control" placeholder="0.00" value="0.00">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Target Deadline Date</label>
                        <input type="date" name="deadline" class="form-control" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeAddGoalModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Goal</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Edit Goal Modal -->
    <div class="modal-overlay" id="editGoalModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Edit Financial Goal</h3>
                <button class="modal-close" onclick="closeEditGoalModal()">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/goals" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="editGoalId" name="id">
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-label">Goal Name</label>
                        <input type="text" id="editGoalName" name="goalName" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Target Amount (₹)</label>
                        <input type="number" step="0.01" min="1" id="editTargetAmount" name="targetAmount" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Target Deadline Date</label>
                        <input type="date" id="editDeadline" name="deadline" class="form-control" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeEditGoalModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Deposit Modal -->
    <div class="modal-overlay" id="depositModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="depositGoalTitle">Add Savings Contribution</h3>
                <button class="modal-close" onclick="closeDepositModal()">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/goals" method="POST">
                <input type="hidden" name="action" value="deposit">
                <input type="hidden" id="depositGoalId" name="id">
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-label">Contribution Amount (₹)</label>
                        <input type="number" step="0.01" min="1" id="depositAmount" name="depositAmount" class="form-control" placeholder="e.g. 5000" required autofocus>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeDepositModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Add to Goal</button>
                </div>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
    <script src="${pageContext.request.contextPath}/js/goals.js"></script>
</body>
</html>
