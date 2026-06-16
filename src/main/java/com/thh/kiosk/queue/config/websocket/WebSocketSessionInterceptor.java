package com.thh.kiosk.queue.config.websocket;

import com.thh.kiosk.queue.modules.system.network.ClientAppType;
import com.thh.kiosk.queue.modules.system.network.DeviceSessionService;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketSessionInterceptor implements ChannelInterceptor {

    private final DeviceSessionService sessionService;

    @Override
    public Message<?> preSend(
            @NonNull Message<?> message,
            @NonNull MessageChannel channel
    ) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        StompCommand command = accessor.getCommand();
        String sessionId = accessor.getSessionId();

        if (StompCommand.CONNECT.equals(command)) {
            String deviceTypeStr = accessor.getFirstNativeHeader("device-type");
            String clientIp = accessor.getFirstNativeHeader("client-ip");

            ClientAppType appType = parseAppType(deviceTypeStr);

            sessionService.registerDevice(sessionId, appType, clientIp);

        } else if (StompCommand.DISCONNECT.equals(command)) {
            sessionService.unregisterDevice(sessionId);
        }

        return message;
    }

    private ClientAppType parseAppType(String typeStr) {
        if (typeStr == null || typeStr.isBlank()) {
            return ClientAppType.UNKNOW;
        }
        try {
            return ClientAppType.valueOf(typeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Client type is invalid: '{}'", typeStr);
            return ClientAppType.UNKNOW;
        }
    }
}