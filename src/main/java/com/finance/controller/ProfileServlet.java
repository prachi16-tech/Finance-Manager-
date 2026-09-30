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
 * User Profile & Security Settings Controller
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");
        try {
            User latestUser = userDAO.findById(sessionUser.getId());
            if (latestUser != null) {
                session.setAttribute("user", latestUser);
                req.setAttribute("userProfile", latestUser);
            }
            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error loading user profile: " + e.getMessage());
            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
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
            if ("updateInfo".equalsIgnoreCase(action)) {
                handleUpdateInfo(req, resp, userId, user);
            } else if ("changePassword".equalsIgnoreCase(action)) {
                handleChangePassword(req, resp, userId, user);
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.getSession().setAttribute("errorMessage", "Database update failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/profile");
        }
    }

    private void handleUpdateInfo(HttpServletRequest req, HttpServletResponse resp, int userId, User user)
            throws SQLException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");

        if (ValidationUtil.isEmpty(name) || ValidationUtil.isEmpty(email) || !ValidationUtil.isValidEmail(email)) {
            req.getSession().setAttribute("errorMessage", "Please provide a valid name and email address.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (userDAO.emailExists(email, userId)) {
            req.getSession().setAttribute("errorMessage", "This email address is already used by another account.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        boolean success = userDAO.updateProfile(userId, name, email);
        if (success) {
            user.setName(name);
            user.setEmail(email);
            req.getSession().setAttribute("userName", name);
            req.getSession().setAttribute("userEmail", email);
            req.getSession().setAttribute("successMessage", "Profile information updated successfully.");
        } else {
            req.getSession().setAttribute("errorMessage", "Failed to update profile.");
        }
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp, int userId, User user)
            throws SQLException, IOException {
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (ValidationUtil.isEmpty(currentPassword) || ValidationUtil.isEmpty(newPassword) || ValidationUtil.isEmpty(confirmPassword)) {
            req.getSession().setAttribute("errorMessage", "All password fields are required.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
            req.getSession().setAttribute("errorMessage", "Current password does not match.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            req.getSession().setAttribute("errorMessage", "New password must be at least 6 characters long.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.getSession().setAttribute("errorMessage", "New passwords do not match.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
        boolean success = userDAO.updatePassword(userId, hashedNewPassword);
        if (success) {
            user.setPassword(hashedNewPassword);
            req.getSession().setAttribute("successMessage", "Password changed successfully.");
        } else {
            req.getSession().setAttribute("errorMessage", "Failed to change password.");
        }
        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
