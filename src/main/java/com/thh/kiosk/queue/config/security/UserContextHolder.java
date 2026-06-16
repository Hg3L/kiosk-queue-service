package com.thh.kiosk.queue.config.security;

public class UserContextHolder {

    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    private static final ThreadLocal<String> CURRENT_SESSION = new ThreadLocal<>();

    public static void setCurrentUser(String userIdentifier) {
        CURRENT_USER.set(userIdentifier);
    }

    public static String getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void setCurrentSessionId(String sessionId) {
        CURRENT_SESSION.set(sessionId);
    }

    public static String getCurrentSessionId() {
        return CURRENT_SESSION.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
        CURRENT_SESSION.remove();
    }
}
