package com.finance.controller;

import com.finance.model.FinancialGoal;
import com.finance.model.GoalProgress;
import com.finance.model.User;
import com.finance.service.GoalService;
import com.finance.util.ValidationUtil;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Financial Goals Controller
 */
@WebServlet(name = "GoalServlet", urlPatterns = {"/goals"})
public class GoalServlet extends HttpServlet {

    private final GoalService goalService = new GoalService();
    private final Gson gson = new Gson();

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
        String action = req.getParameter("action");

        if ("getJson".equalsIgnoreCase(action)) {
            handleGetJson(req, resp, userId);
            return;
        }

        try {
            List<GoalProgress> goalProgressList = goalService.getGoalProgressList(userId);
            BigDecimal totalTarget = BigDecimal.ZERO;
            BigDecimal totalSaved = BigDecimal.ZERO;

            for (GoalProgress gp : goalProgressList) {
                totalTarget = totalTarget.add(gp.getGoal().getTargetAmount());
                totalSaved = totalSaved.add(gp.getGoal().getCurrentAmount() != null ? gp.getGoal().getCurrentAmount() : BigDecimal.ZERO);
            }

            req.setAttribute("goals", goalProgressList);
            req.setAttribute("totalTarget", totalTarget);
            req.setAttribute("totalSaved", totalSaved);

            req.getRequestDispatcher("/goals.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error loading financial goals: " + e.getMessage());
            req.getRequestDispatcher("/goals.jsp").forward(req, resp);
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
            if ("add".equalsIgnoreCase(action)) {
                handleAdd(req, resp, userId);
            } else if ("update".equalsIgnoreCase(action)) {
                handleUpdate(req, resp, userId);
            } else if ("deposit".equalsIgnoreCase(action)) {
                handleDeposit(req, resp, userId);
            } else if ("delete".equalsIgnoreCase(action)) {
                handleDelete(req, resp, userId);
            } else {
                resp.sendRedirect(req.getContextPath() + "/goals");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.getSession().setAttribute("errorMessage", "Database operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/goals");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws SQLException, IOException {
        String goalName = req.getParameter("goalName");
        String targetAmountStr = req.getParameter("targetAmount");
        String currentAmountStr = req.getParameter("currentAmount");
        String deadlineStr = req.getParameter("deadline");

        BigDecimal targetAmount = ValidationUtil.parseAmount(targetAmountStr);
        BigDecimal currentAmount = ValidationUtil.parseAmount(currentAmountStr);
        if (currentAmount == null) currentAmount = BigDecimal.ZERO;
        Date deadline = ValidationUtil.parseDate(deadlineStr);

        if (ValidationUtil.isEmpty(goalName) || targetAmount == null || !ValidationUtil.isValidPositiveAmount(targetAmount)
                || deadline == null) {
            req.getSession().setAttribute("errorMessage", "Please provide a valid goal name, positive target amount, and deadline.");
            resp.sendRedirect(req.getContextPath() + "/goals");
            return;
        }

        FinancialGoal goal = new FinancialGoal(userId, goalName, targetAmount, currentAmount, deadline);
        boolean success = goalService.createGoal(goal);
        if (success) {
            req.getSession().setAttribute("successMessage", "Financial goal created successfully!");
        } else {
            req.getSession().setAttribute("errorMessage", "Failed to create goal.");
        }
        resp.sendRedirect(req.getContextPath() + "/goals");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws SQLException, IOException {
        String idStr = req.getParameter("id");
        String goalName = req.getParameter("goalName");
        String targetAmountStr = req.getParameter("targetAmount");
        String deadlineStr = req.getParameter("deadline");

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "Invalid goal ID.");
            resp.sendRedirect(req.getContextPath() + "/goals");
            return;
        }

        BigDecimal targetAmount = ValidationUtil.parseAmount(targetAmountStr);
        Date deadline = ValidationUtil.parseDate(deadlineStr);

        if (ValidationUtil.isEmpty(goalName) || targetAmount == null || !ValidationUtil.isValidPositiveAmount(targetAmount)
                || deadline == null) {
            req.getSession().setAttribute("errorMessage", "Invalid parameters for updating goal.");
            resp.sendRedirect(req.getContextPath() + "/goals");
            return;
        }

        FinancialGoal goal = new FinancialGoal(id, userId, goalName, targetAmount, null, deadline, null);
        boolean success = goalService.updateGoal(goal);
        if (success) {
            req.getSession().setAttribute("successMessage", "Goal updated successfully.");
        } else {
            req.getSession().setAttribute("errorMessage", "Failed to update goal.");
        }
        resp.sendRedirect(req.getContextPath() + "/goals");
    }

    private void handleDeposit(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws SQLException, IOException {
        String idStr = req.getParameter("id");
        String amountStr = req.getParameter("depositAmount");

        try {
            int id = Integer.parseInt(idStr);
            BigDecimal deposit = ValidationUtil.parseAmount(amountStr);
            if (deposit == null || !ValidationUtil.isValidPositiveAmount(deposit)) {
                req.getSession().setAttribute("errorMessage", "Please enter a valid deposit amount.");
                resp.sendRedirect(req.getContextPath() + "/goals");
                return;
            }

            boolean success = goalService.addDeposit(id, userId, deposit);
            if (success) {
                req.getSession().setAttribute("successMessage", "Savings contribution of ₹" +
                        String.format("%,.2f", deposit) + " added successfully!");
            } else {
                req.getSession().setAttribute("errorMessage", "Failed to add contribution.");
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "Invalid goal ID.");
        }
        resp.sendRedirect(req.getContextPath() + "/goals");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws SQLException, IOException {
        String idStr = req.getParameter("id");
        try {
            int id = Integer.parseInt(idStr);
            boolean success = goalService.deleteGoal(id, userId);
            if (success) {
                req.getSession().setAttribute("successMessage", "Financial goal deleted.");
            } else {
                req.getSession().setAttribute("errorMessage", "Could not delete goal.");
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "Invalid goal ID.");
        }
        resp.sendRedirect(req.getContextPath() + "/goals");
    }

    private void handleGetJson(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String idStr = req.getParameter("id");
        try {
            int id = Integer.parseInt(idStr);
            FinancialGoal goal = goalService.getGoalById(id, userId);
            if (goal != null) {
                resp.getWriter().write(gson.toJson(goal));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Goal not found\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
