package com.finance.controller;

import com.finance.dao.UserDAO;
import com.finance.model.User;
import com.finance.util.PasswordUtil;
import com.finance.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * User Registration Servlet
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        req.setAttribute("name", name);
        req.setAttribute("email", email);

        // 1. Validation
        if (ValidationUtil.isEmpty(name) || ValidationUtil.isEmpty(email) ||
                ValidationUtil.isEmpty(password) || ValidationUtil.isEmpty(confirmPassword)) {
            req.setAttribute("errorMessage", "All fields are required.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            req.setAttribute("errorMessage", "Please provide a valid email address.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            req.setAttribute("errorMessage", "Password must be at least 6 characters long.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Passwords do not match.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        try {
            // 2. Check Duplicate Email
            if (userDAO.findByEmail(email) != null) {
                req.setAttribute("errorMessage", "Email address is already registered. Please login.");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }

            // 3. Hash Password and Insert
            String hashedPassword = PasswordUtil.hashPassword(password);
            User newUser = new User(name.trim(), email.trim().toLowerCase(), hashedPassword);

            int userId = userDAO.register(newUser);
            if (userId > 0) {
                req.getSession().setAttribute("successMessage", "Account created successfully! You can now log in.");
                resp.sendRedirect(req.getContextPath() + "/login?registered=true");
            } else {
                req.setAttribute("errorMessage", "Could not register account. Please try again.");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Database error occurred: " + e.getMessage());
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
