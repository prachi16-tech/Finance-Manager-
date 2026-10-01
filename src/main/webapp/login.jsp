<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login - Personal Finance Manager</title>
    <jsp:include page="/includes/header.jsp"/>
</head>
<body class="auth-page">
    <div class="ambient-glow glow-1"></div>
    <div class="ambient-glow glow-2"></div>

    <div class="auth-card" style="position: relative;">
        <div style="position: absolute; top: 20px; right: 20px;">
            <button type="button" class="theme-toggle-btn" title="Toggle Theme" aria-label="Toggle Theme" style="width: 32px; height: 32px; font-size: 1rem;">
                <i class="bi bi-sun-fill"></i>
            </button>
        </div>
        <div class="auth-header">
            <div class="brand-logo" style="justify-content: center; margin-bottom: 12px;">
                <div class="brand-icon">₹</div>
                <span>FinManager</span>
            </div>
            <h1>Welcome Back</h1>
            <p>Access your real-time financial dashboard</p>
        </div>

        <!-- Success Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success">
                <i class="bi bi-check-circle-fill"></i>
                <span>${sessionScope.successMessage}</span>
            </div>
            <% session.removeAttribute("successMessage"); %>
        </c:if>
        <c:if test="${param.registered == 'true'}">
            <div class="alert alert-success">
                <i class="bi bi-check-circle-fill"></i>
                <span>Account registered successfully! Please log in.</span>
            </div>
        </c:if>
        <c:if test="${param.logged_out == 'true'}">
            <div class="alert alert-success">
                <i class="bi bi-info-circle-fill"></i>
                <span>You have been logged out securely.</span>
            </div>
        </c:if>

        <!-- Error Messages -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <span>${errorMessage}</span>
            </div>
        </c:if>
        <c:if test="${param.error == 'session_expired'}">
            <div class="alert alert-danger">
                <i class="bi bi-clock-history"></i>
                <span>Your session has expired. Please log in again.</span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="POST">
            <div class="form-group">
                <label class="form-label" for="email">Email Address</label>
                <div style="position: relative;">
                    <input type="email" id="email" name="email" class="form-control"
                           placeholder="e.g. mayur@example.com"
                           value="${not empty email ? email : (not empty rememberedEmail ? rememberedEmail : '')}" required autofocus>
                </div>
            </div>

            <div class="form-group">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                    <label class="form-label" for="password" style="margin-bottom: 0;">Password</label>
                </div>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter your password" required>
            </div>

            <div class="form-group" style="display: flex; align-items: center; justify-content: space-between;">
                <label style="display: flex; align-items: center; gap: 8px; font-size: 0.88rem; color: var(--text-secondary); cursor: pointer;">
                    <input type="checkbox" name="rememberMe" value="on" ${not empty rememberedEmail ? 'checked' : ''} style="accent-color: var(--primary);">
                    Remember me
                </label>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 12px; margin-top: 10px; font-size: 1rem;">
                <i class="bi bi-box-arrow-in-right"></i> Sign In to Dashboard
            </button>
        </form>

        <div style="text-align: center; margin-top: 24px; padding-top: 20px; border-top: 1px solid var(--border-color); font-size: 0.9rem; color: var(--text-secondary);">
            Don't have an account?
            <a href="${pageContext.request.contextPath}/register" style="font-weight: 700; color: #818cf8;">Create Account</a>
        </div>
    </div>
</body>
</html>
