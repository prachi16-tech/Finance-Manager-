<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    String currentURI = request.getRequestURI();
    String activeTab = "";
    if (currentURI.contains("dashboard")) activeTab = "dashboard";
    else if (currentURI.contains("transactions")) activeTab = "transactions";
    else if (currentURI.contains("budgets")) activeTab = "budgets";
    else if (currentURI.contains("goals")) activeTab = "goals";
    else if (currentURI.contains("analytics")) activeTab = "analytics";
    else if (currentURI.contains("profile")) activeTab = "profile";
    request.setAttribute("activeTab", activeTab);
%>

<aside class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <div class="brand-logo">
            <div class="brand-icon">₹</div>
            <span>FinManager</span>
        </div>
    </div>

    <ul class="sidebar-menu">
        <li class="sidebar-item ${activeTab == 'dashboard' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/dashboard">
                <i class="bi bi-grid-1x2-fill"></i>
                <span>Dashboard</span>
            </a>
        </li>
        <li class="sidebar-item ${activeTab == 'transactions' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/transactions">
                <i class="bi bi-arrow-left-right"></i>
                <span>Transactions</span>
            </a>
        </li>
        <li class="sidebar-item ${activeTab == 'budgets' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/budgets">
                <i class="bi bi-pie-chart-fill"></i>
                <span>Budgets</span>
            </a>
        </li>
        <li class="sidebar-item ${activeTab == 'goals' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/goals">
                <i class="bi bi-bullseye"></i>
                <span>Goals</span>
            </a>
        </li>
        <li class="sidebar-item ${activeTab == 'analytics' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/analytics">
                <i class="bi bi-graph-up-arrow"></i>
                <span>Analytics</span>
            </a>
        </li>
        <li class="sidebar-item ${activeTab == 'profile' ? 'active' : ''}">
            <a href="${pageContext.request.contextPath}/profile">
                <i class="bi bi-person-circle"></i>
                <span>Profile</span>
            </a>
        </li>
        <li class="sidebar-item" style="margin-top: auto;">
            <a href="${pageContext.request.contextPath}/logout" style="color: #f43f5e;">
                <i class="bi bi-box-arrow-right"></i>
                <span>Logout</span>
            </a>
        </li>
    </ul>

    <div class="sidebar-footer">
        <div class="user-snippet">
            <div class="user-avatar">
                ${sessionScope.userName != null ? sessionScope.userName.substring(0, 1).toUpperCase() : 'U'}
            </div>
            <div class="user-info">
                <div class="user-name">${sessionScope.userName != null ? sessionScope.userName : 'User'}</div>
                <div class="user-email">${sessionScope.userEmail != null ? sessionScope.userEmail : 'user@example.com'}</div>
            </div>
        </div>
    </div>
</aside>
