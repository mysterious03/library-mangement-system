package controller;

import model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory HTTP Session Manager for Pure Java SE Web Server.
 * Implements role-based security tokens and flash messages.
 */
public class SessionManager {

    private static final Map<String, User> sessions = new ConcurrentHashMap<>();
    private static final Map<String, String> flashSuccess = new ConcurrentHashMap<>();
    private static final Map<String, String> flashError = new ConcurrentHashMap<>();

    public static String createSession(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public static User getUser(String token) {
        if (token == null) return null;
        return sessions.get(token);
    }

    public static void destroySession(String token) {
        if (token != null) {
            sessions.remove(token);
            flashSuccess.remove(token);
            flashError.remove(token);
        }
    }

    public static void setFlashSuccess(String token, String msg) {
        if (token != null) flashSuccess.put(token, msg);
    }

    public static void setFlashError(String token, String msg) {
        if (token != null) flashError.put(token, msg);
    }

    public static String getAndClearFlashSuccess(String token) {
        if (token == null) return null;
        return flashSuccess.remove(token);
    }

    public static String getAndClearFlashError(String token) {
        if (token == null) return null;
        return flashError.remove(token);
    }
}
