package com.finance.controller;

import com.finance.dao.BudgetDAO;
import com.finance.model.Budget;
import com.finance.model.BudgetSummary;
import com.finance.model.User;
import com.finance.service.BudgetService;
import com.finance.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Budget Management Controller
 */
@WebServlet(name = "BudgetServlet", urlPatterns = {"/budgets"})
public class BudgetServlet extends HttpServlet {

    private final BudgetService budgetService = new BudgetService();
    private final BudgetDAO budgetDAO = new BudgetDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        String monthParam = req.getParameter("month");
        String yearParam = req.getParameter("year");

        if (!ValidationUtil.isEmpty(monthParam)) {
            try {
                month = Integer.parseInt(monthParam);
            } catch (NumberFormatException ignored) {}
        }
        if (!ValidationUtil.isEmpty(yearParam)) {
            try {
                year = Integer.parseInt(yearParam);
            } catch (NumberFormatException ignored) {}
        }

        try {
            List<BudgetSummary> summaries = budgetService.getBudgetSummaries(userId, month, year);
            BigDecimal totalBudget = budgetDAO.getTotalBudgetForMonth(userId, month, year);

            BigDecimal totalSpent = BigDecimal.ZERO;
            for (BudgetSummary bs : summaries) {
                totalSpent = totalSpent.add(bs.getSpentAmount());
            }

            req.setAttribute("budgetSummaries", summaries);
            req.setAttribute("totalBudget", totalBudget);
            req.setAttribute("totalSpent", totalSpent);
            req.setAttribute("selectedMonth", month);
            req.setAttribute("selectedYear", year);
            req.setAttribute("categories", ValidationUtil.VALID_CATEGORIES);

            req.getRequestDispatcher("/budgets.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error loading budgets: " + e.getMessage());
            req.getRequestDispatcher("/budgets.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();
        String action = req.getParameter("action");

        try {
            if ("save".equalsIgnoreCase(action)) {
                handleSave(req, resp, userId);
            } else if ("delete".equalsIgnoreCase(action)) {
                handleDelete(req, resp, userId);
            } else {
                resp.sendRedirect(req.getContextPath() + "/budgets");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.getSession().setAttribute("errorMessage", "Database operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/budgets");
        }
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws SQLException, IOException {
        String category = req.getParameter("category");
        String amountStr = req.getParameter("amount");
        String monthStr = req.getParameter("month");
        String yearStr = req.getParameter("year");

        BigDecimal amount = ValidationUtil.parseAmount(amountStr);
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        try {
            if (!ValidationUtil.isEmpty(monthStr)) month = Integer.parseInt(monthStr);
            if (!ValidationUtil.isEmpty(yearStr)) year = Integer.parseInt(yearStr);
        } catch (NumberFormatException ignored) {}

        if (ValidationUtil.isEmpty(category) || amount == null || !ValidationUtil.isValidPositiveAmount(amount)) {
            req.getSession().setAttribute("errorMessage", "Please provide a valid category and budget amount greater than zero.");
            resp.sendRedirect(req.getContextPath() + "/budgets?month=" + month + "&year=" + year);
            return;
        }

        Budget budget = new Budget(userId, category, amount, month, year);
        boolean success = budgetService.saveOrUpdateBudget(budget);
        if (success) {
            req.getSession().setAttribute("successMessage", "Budget for " + category + " saved successfully.");
        } else {
            req.getSession().setAttribute("errorMessage", "Failed to save budget.");
        }
        resp.sendRedirect(req.getContextPath() + "/budgets?month=" + month + "&year=" + year);
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws SQLException, IOException {
        String idStr = req.getParameter("id");
        String monthStr = req.getParameter("month");
        String yearStr = req.getParameter("year");

        try {
            int id = Integer.parseInt(idStr);
            boolean success = budgetService.deleteBudget(id, userId);
            if (success) {
                req.getSession().setAttribute("successMessage", "Budget deleted successfully.");
            } else {
                req.getSession().setAttribute("errorMessage", "Could not delete budget.");
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "Invalid budget ID.");
        }
        resp.sendRedirect(req.getContextPath() + "/budgets?month=" + monthStr + "&year=" + yearStr);
    }
}
