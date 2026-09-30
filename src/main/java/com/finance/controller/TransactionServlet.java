package com.finance.controller;

import com.finance.dao.TransactionDAO;
import com.finance.model.Transaction;
import com.finance.model.User;
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
 * Transaction Management Controller (CRUD + Filters)
 */
@WebServlet(name = "TransactionServlet", urlPatterns = {"/transactions"})
public class TransactionServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();
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
            // AJAX JSON retrieval for editing
            handleGetJson(req, resp, userId);
            return;
        }

        // List / Filter
        String search = req.getParameter("search");
        String category = req.getParameter("category");
        String type = req.getParameter("type");
        String startDateStr = req.getParameter("startDate");
        String endDateStr = req.getParameter("endDate");

        Date startDate = ValidationUtil.parseDate(startDateStr);
        Date endDate = ValidationUtil.parseDate(endDateStr);

        try {
            List<Transaction> transactions = transactionDAO.findFiltered(userId, search, category, type, startDate, endDate);
            BigDecimal totalIncome = transactionDAO.getTotalIncome(userId);
            BigDecimal totalExpenses = transactionDAO.getTotalExpenses(userId);

            req.setAttribute("transactions", transactions);
            req.setAttribute("totalIncome", totalIncome);
            req.setAttribute("totalExpenses", totalExpenses);
            req.setAttribute("search", search);
            req.setAttribute("selectedCategory", category);
            req.setAttribute("selectedType", type);
            req.setAttribute("startDate", startDateStr);
            req.setAttribute("endDate", endDateStr);
            req.setAttribute("categories", ValidationUtil.VALID_CATEGORIES);
            req.setAttribute("paymentMethods", ValidationUtil.VALID_PAYMENT_METHODS);

            req.getRequestDispatcher("/transactions.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error loading transactions: " + e.getMessage());
            req.getRequestDispatcher("/transactions.jsp").forward(req, resp);
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
            } else if ("delete".equalsIgnoreCase(action)) {
                handleDelete(req, resp, userId);
            } else {
                resp.sendRedirect(req.getContextPath() + "/transactions");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.getSession().setAttribute("errorMessage", "Database operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/transactions");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws SQLException, IOException {
        String type = req.getParameter("type");
        String amountStr = req.getParameter("amount");
        String category = req.getParameter("category");
        String description = req.getParameter("description");
        String dateStr = req.getParameter("transactionDate");
        String paymentMethod = req.getParameter("paymentMethod");

        BigDecimal amount = ValidationUtil.parseAmount(amountStr);
        Date date = ValidationUtil.parseDate(dateStr);

        if (ValidationUtil.isEmpty(type) || amount == null || !ValidationUtil.isValidPositiveAmount(amount)
                || ValidationUtil.isEmpty(category) || ValidationUtil.isEmpty(description) || date == null) {
            req.getSession().setAttribute("errorMessage", "Please provide valid transaction details with a positive amount.");
            resp.sendRedirect(req.getContextPath() + "/transactions");
            return;
        }

        Transaction t = new Transaction(userId, type, amount, category, description, date, paymentMethod);
        boolean success = transactionDAO.insert(t);
        if (success) {
            req.getSession().setAttribute("successMessage", "Transaction added successfully.");
        } else {
            req.getSession().setAttribute("errorMessage", "Failed to add transaction.");
        }
        resp.sendRedirect(req.getContextPath() + "/transactions");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws SQLException, IOException {
        String idStr = req.getParameter("id");
        String type = req.getParameter("type");
        String amountStr = req.getParameter("amount");
        String category = req.getParameter("category");
        String description = req.getParameter("description");
        String dateStr = req.getParameter("transactionDate");
        String paymentMethod = req.getParameter("paymentMethod");

        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "Invalid transaction ID.");
            resp.sendRedirect(req.getContextPath() + "/transactions");
            return;
        }

        BigDecimal amount = ValidationUtil.parseAmount(amountStr);
        Date date = ValidationUtil.parseDate(dateStr);

        if (amount == null || !ValidationUtil.isValidPositiveAmount(amount) || date == null ||
                ValidationUtil.isEmpty(description)) {
            req.getSession().setAttribute("errorMessage", "Invalid fields provided for updating transaction.");
            resp.sendRedirect(req.getContextPath() + "/transactions");
            return;
        }

        Transaction t = new Transaction(id, userId, type, amount, category, description, date, paymentMethod, null);
        boolean success = transactionDAO.update(t);
        if (success) {
            req.getSession().setAttribute("successMessage", "Transaction updated successfully.");
        } else {
            req.getSession().setAttribute("errorMessage", "Failed to update transaction.");
        }
        resp.sendRedirect(req.getContextPath() + "/transactions");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws SQLException, IOException {
        String idStr = req.getParameter("id");
        try {
            int id = Integer.parseInt(idStr);
            boolean success = transactionDAO.delete(id, userId);
            if (success) {
                req.getSession().setAttribute("successMessage", "Transaction deleted successfully.");
            } else {
                req.getSession().setAttribute("errorMessage", "Transaction not found or could not be deleted.");
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "Invalid transaction ID.");
        }
        resp.sendRedirect(req.getContextPath() + "/transactions");
    }

    private void handleGetJson(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String idStr = req.getParameter("id");
        try {
            int id = Integer.parseInt(idStr);
            Transaction t = transactionDAO.findById(id, userId);
            if (t != null) {
                resp.getWriter().write(gson.toJson(t));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Transaction not found\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
