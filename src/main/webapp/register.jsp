<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Account - Personal Finance Manager</title>
    <jsp:include page="/includes/header.jsp"/>
</head>
<body class="auth-page">
    <div class="ambient-glow glow-1"></div>
    <div class="ambient-glow glow-2"></div>

    <div class="auth-card">
        <div class="auth-header">
            <div class="brand-logo" style="justify-content: center; margin-bottom: 12px;">
                <div class="brand-icon">₹</div>
                <span>FinManager</span>
            </div>
            <h1>Create Account</h1>
            <p>Start mastering your personal wealth and budgets</p>
        </div>

        <!-- Error Alert -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="POST">
            <div class="form-group">
                <label class="form-label" for="name">Full Name</label>
                <input type="text" id="name" name="name" class="form-control"
                       placeholder="e.g. Mayur Patil"
                       value="${not empty name ? name : ''}" required autofocus>
            </div>

            <div class="form-group">
                <label class="form-label" for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="e.g. mayur@example.com"
                       value="${not empty email ? email : ''}" required>
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="At least 6 characters" required minlength="6">
            </div>

            <div class="form-group">
                <label class="form-label" for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                       placeholder="Re-enter password" required minlength="6">
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 12px; margin-top: 10px; font-size: 1rem;">
                <i class="bi bi-person-plus-fill"></i> Register Account
            </button>
        </form>

        <div style="text-align: center; margin-top: 24px; padding-top: 20px; border-top: 1px solid var(--border-color); font-size: 0.9rem; color: var(--text-secondary);">
            Already have an account?
            <a href="${pageContext.request.contextPath}/login" style="font-weight: 700; color: #818cf8;">Sign In</a>
        </div>
    </div>
</body>
</html>
