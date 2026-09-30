<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Profile Settings - Personal Finance Manager</title>
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
                        <i class="bi bi-person-gear" style="color: #6366f1;"></i>
                        <span>Account & Security Settings</span>
                    </div>
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

                <!-- Profile Overview Header Card -->
                <div class="card" style="margin-bottom: 28px; display: flex; align-items: center; gap: 24px; flex-wrap: wrap;">
                    <div class="user-avatar" style="width: 72px; height: 72px; font-size: 1.8rem;">
                        ${sessionScope.userName != null ? sessionScope.userName.substring(0, 1).toUpperCase() : 'U'}
                    </div>
                    <div>
                        <h2 style="font-size: 1.5rem; margin-bottom: 4px;">${sessionScope.userName}</h2>
                        <p style="color: var(--text-secondary); margin-bottom: 6px;">${sessionScope.userEmail}</p>
                        <span class="badge badge-category" style="font-size: 0.8rem;">
                            <i class="bi bi-calendar3"></i> Member Since:
                            <fmt:formatDate value="${userProfile.createdAt}" pattern="dd MMMM yyyy"/>
                        </span>
                    </div>
                </div>

                <div class="grid-equal-2col">
                    <!-- Update Information Card -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="bi bi-person-lines-fill" style="color: #6366f1;"></i> Personal Information
                            </h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/profile" method="POST">
                            <input type="hidden" name="action" value="updateInfo">
                            <div class="form-group">
                                <label class="form-label">Full Name</label>
                                <input type="text" name="name" class="form-control"
                                       value="${not empty userProfile.name ? userProfile.name : sessionScope.userName}" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Email Address</label>
                                <input type="email" name="email" class="form-control"
                                       value="${not empty userProfile.email ? userProfile.email : sessionScope.userEmail}" required>
                            </div>

                            <button type="submit" class="btn btn-primary" style="margin-top: 10px;">
                                <i class="bi bi-check2"></i> Save Profile Details
                            </button>
                        </form>
                    </div>

                    <!-- Change Password Card -->
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">
                                <i class="bi bi-shield-lock-fill" style="color: #f59e0b;"></i> Security & Password
                            </h3>
                        </div>
                        <form action="${pageContext.request.contextPath}/profile" method="POST">
                            <input type="hidden" name="action" value="changePassword">
                            <div class="form-group">
                                <label class="form-label">Current Password</label>
                                <input type="password" name="currentPassword" class="form-control" placeholder="Enter existing password" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label">New Password</label>
                                <input type="password" name="newPassword" class="form-control" placeholder="At least 6 characters" minlength="6" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Confirm New Password</label>
                                <input type="password" name="confirmPassword" class="form-control" placeholder="Re-type new password" minlength="6" required>
                            </div>

                            <button type="submit" class="btn btn-secondary" style="margin-top: 10px;">
                                <i class="bi bi-key-fill"></i> Update Password
                            </button>
                        </form>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
</body>
</html>
