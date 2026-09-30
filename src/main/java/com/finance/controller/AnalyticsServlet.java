package com.finance.controller;

import com.finance.model.User;
import com.finance.service.AnalyticsService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Analytics and Chart.js Controller
 */
@WebServlet(name = "AnalyticsServlet", urlPatterns = {"/analytics"})
public class AnalyticsServlet extends HttpServlet {

    private final AnalyticsService analyticsService = new AnalyticsService();
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
        String format = req.getParameter("format");

        try {
            Map<String, Object> data = analyticsService.getAnalyticsData(userId);

            if ("json".equalsIgnoreCase(format)) {
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(gson.toJson(data));
                return;
            }

            req.setAttribute("analyticsDataJson", gson.toJson(data));
            req.setAttribute("analyticsData", data);
            req.getRequestDispatcher("/analytics.jsp").forward(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error aggregating analytics datasets: " + e.getMessage());
            req.getRequestDispatcher("/analytics.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
