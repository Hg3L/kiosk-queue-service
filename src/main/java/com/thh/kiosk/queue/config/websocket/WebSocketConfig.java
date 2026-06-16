package com.thh.kiosk.queue.config.websocket;

import static com.thh.kiosk.queue.core.constant.WebSocketConstants.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final SessionHandshakeInterceptor sessionHandshakeInterceptor;

    private final WebSocketSessionInterceptor sessionInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Client connect to "ws://localhost:8080/ws-kiosk"
        registry.addEndpoint(ENDPOINT)
                .setAllowedOriginPatterns("*")
                .withSockJS();
                //.addInterceptors(sessionHandshakeInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(CLIENT_DESTINATION);

        registry.setApplicationDestinationPrefixes(SERVER_DESTINATION);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(sessionInterceptor);
    }
}
