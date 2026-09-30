package com.finance.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Password Hashing Utility using SHA-256 and cryptographic Salt
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Hashes a plain password with a random 16-byte salt
     * Format returned: "hexSalt:hexHash"
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        String saltHex = bytesToHex(salt);
        String hashHex = hashWithSalt(password, salt);
        return saltHex + ":" + hashHex;
    }

    /**
     * Verifies a plain text password against stored hash (salt:hash or legacy raw sha256)
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        if (storedHash.contains(":")) {
            String[] parts = storedHash.split(":");
            if (parts.length == 2) {
                String saltHex = parts[0];
                String expectedHash = parts[1];
                byte[] salt = hexToBytes(saltHex);
                String actualHash = hashWithSalt(password, salt);
                return actualHash.equalsIgnoreCase(expectedHash);
            }
        }

        // Fallback: direct SHA-256 check (for pre-seeded or legacy hashes)
        String directHash = hashSHA256(password);
        return directHash.equalsIgnoreCase(storedHash) || password.equals(storedHash);
    }

    /**
     * Compute SHA-256 of plain text
     */
    public static String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static String hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
