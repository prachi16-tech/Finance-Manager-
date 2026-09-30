package com.finance.controller;

import com.finance.model.BudgetSummary;
import com.finance.model.DashboardSummary;
import com.finance.model.Transaction;
import com.finance.model.User;
import com.finance.service.BudgetService;
import com.finance.service.FinanceService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Main Dashboard Controller
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final FinanceService financeService = new FinanceService();
    private final BudgetService budgetService = new BudgetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=session_expired");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        try {
            // Fetch dynamically computed financial summary and rule-based insights
            DashboardSummary summary = financeService.getDashboardSummary(userId);
            List<Transaction> recentTransactions = financeService.getRecentTransactions(userId, 6);
            List<BudgetSummary> budgetSummaries = budgetService.getBudgetSummaries(userId, currentMonth, currentYear);

            req.setAttribute("summary", summary);
            req.setAttribute("recentTransactions", recentTransactions);
            req.setAttribute("budgetSummaries", budgetSummaries);
            req.setAttribute("currentMonthName", now.getMonth().name());
            req.setAttribute("currentYear", currentYear);

            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error loading dashboard metrics from database: " + e.getMessage());
            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
