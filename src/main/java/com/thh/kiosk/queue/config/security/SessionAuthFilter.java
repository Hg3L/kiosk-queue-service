package com.thh.kiosk.queue.config.security;

import com.thh.kiosk.queue.config.properties.SessionProperties;
import com.thh.kiosk.queue.core.constant.RoleSessionConstants;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {

    private final SessionManager sessionManager;
    private final SessionProperties props;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String sessionId = extractSessionFromCookie(request);
        if (sessionId == null || sessionManager.getUserIdentifier(sessionId) == null) {
            sessionId = sessionManager.createSession(RoleSessionConstants.CITIZEN);

            Cookie newCookie = new Cookie(props.cookieName(), sessionId);
            newCookie.setHttpOnly(true);
            newCookie.setPath("/");
            newCookie.setMaxAge(props.cookieMaxAgeInSeconds());

            response.addCookie(newCookie);
        }

        String userIdentifier = sessionManager.getUserIdentifier(sessionId);
        try {
            UserContextHolder.setCurrentUser(userIdentifier);
            UserContextHolder.setCurrentSessionId(sessionId);
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private String extractSessionFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> props.cookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
