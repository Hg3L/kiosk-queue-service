package com.thh.kiosk.queue.config.websocket;

import com.thh.kiosk.queue.config.properties.SessionProperties;
import com.thh.kiosk.queue.config.security.SessionManager;

import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionHandshakeInterceptor implements HandshakeInterceptor {

    private final SessionManager sessionManager;

    private final SessionProperties sessionProperties;

    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes
    ) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            Cookie[] cookies = servletRequest.getServletRequest().getCookies();

            if (cookies != null) {
                String sessionId = Arrays.stream(cookies)
                        .filter(c -> sessionProperties.cookieName().equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);

                if (sessionId != null && sessionManager.getUserIdentifier(sessionId) != null) {
                    attributes.put(sessionProperties.cookieName(), sessionId);
                    return true;
                }
            }
        }
        log.warn("No session cookie found or session invalid for WebSocket handshake.");
        return false;
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @Nullable Exception exception
    ) {
        log.info("WebSocket handshake completed. Session ID: {}", request.getHeaders().getFirst("Cookie"));
    }
}
