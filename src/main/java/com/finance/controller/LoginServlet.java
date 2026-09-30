package com.finance.controller;

import com.finance.dao.UserDAO;
import com.finance.model.User;
import com.finance.util.PasswordUtil;
import com.finance.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Authentication Servlet for User Login
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        // Check for remembered email cookie
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remembered_email".equals(cookie.getName())) {
                    req.setAttribute("rememberedEmail", cookie.getValue());
                    break;
                }
            }
        }

        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String rememberMe = req.getParameter("rememberMe");

        if (ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
            req.setAttribute("errorMessage", "Please provide both email and password.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userDAO.findByEmail(email);

            if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
                // Successful Authentication
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("userName", user.getName());
                session.setAttribute("userEmail", user.getEmail());

                // Handle Remember Me Cookie
                if ("on".equalsIgnoreCase(rememberMe) || "true".equalsIgnoreCase(rememberMe)) {
                    Cookie emailCookie = new Cookie("remembered_email", user.getEmail());
                    emailCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
                    emailCookie.setPath(req.getContextPath());
                    resp.addCookie(emailCookie);
                } else {
                    Cookie emailCookie = new Cookie("remembered_email", "");
                    emailCookie.setMaxAge(0);
                    emailCookie.setPath(req.getContextPath());
                    resp.addCookie(emailCookie);
                }

                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                req.setAttribute("errorMessage", "Invalid email or password.");
                req.setAttribute("email", email);
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Database error occurred. Please check database connection: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
