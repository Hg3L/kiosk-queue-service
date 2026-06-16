package com.thh.kiosk.queue.config.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.thh.kiosk.queue.config.properties.SessionProperties;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class SessionManager {

    private final Cache<String, String> sessionCache;

    public SessionManager(SessionProperties props) {
        this.sessionCache = Caffeine.newBuilder()
                .expireAfterAccess(props.cacheTtlHours(), TimeUnit.HOURS)
                .maximumSize(props.cacheMaxSize())
                .build();
    }

    public String createSession(String userIdentifier) {
        String sessionId = UUID.randomUUID().toString();
        sessionCache.put(sessionId, userIdentifier);
        return sessionId;
    }

    public String getUserIdentifier(String sessionId) {
        if (sessionId == null) return null;
        return sessionCache.getIfPresent(sessionId);
    }

    public void updateSession(String sessionId, String newUserIdentifier) {
        if (sessionCache.getIfPresent(sessionId) != null) {
            sessionCache.put(sessionId, newUserIdentifier);
        }
    }

    public void invalidateSession(String sessionId) {
        if (sessionId != null) {
            sessionCache.invalidate(sessionId);
        }
    }
}
