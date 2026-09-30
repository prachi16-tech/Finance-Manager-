package com.finance.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Input Validation Utility
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public static final List<String> VALID_CATEGORIES = Arrays.asList(
            "Food", "Transport", "Shopping", "Bills", "Entertainment",
            "Healthcare", "Education", "Travel", "Salary", "Freelance", "Other"
    );

    public static final List<String> VALID_PAYMENT_METHODS = Arrays.asList(
            "Cash", "UPI", "Credit Card", "Debit Card", "Bank Transfer", "Other"
    );

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isValidNonNegativeAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static boolean isValidCategory(String category) {
        if (isEmpty(category)) {
            return false;
        }
        return VALID_CATEGORIES.stream().anyMatch(c -> c.equalsIgnoreCase(category.trim()));
    }

    public static boolean isValidPaymentMethod(String method) {
        if (isEmpty(method)) {
            return false;
        }
        return VALID_PAYMENT_METHODS.stream().anyMatch(m -> m.equalsIgnoreCase(method.trim()));
    }

    public static boolean isValidTransactionType(String type) {
        return "INCOME".equalsIgnoreCase(type) || "EXPENSE".equalsIgnoreCase(type);
    }

    public static Date parseDate(String dateStr) {
        if (isEmpty(dateStr)) {
            return null;
        }
        try {
            return Date.valueOf(dateStr.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static BigDecimal parseAmount(String amountStr) {
        if (isEmpty(amountStr)) {
            return null;
        }
        try {
            return new BigDecimal(amountStr.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
