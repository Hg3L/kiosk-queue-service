package com.thh.kiosk.queue.modules.system.network;

import static com.thh.kiosk.queue.core.constant.WebSocketConstants.DEVICE_DESTINATION;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeviceSessionService {

    private final Map<String, DeviceSessionDto> activeSessions = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public DeviceSessionService(@Lazy SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void registerDevice(String sessionId, ClientAppType appType, String clientIp) {
        DeviceSessionDto dto = DeviceSessionDto.builder()
                .sessionId(sessionId)
                .clientAppType(appType != null ? appType : ClientAppType.UNKNOW)
                .clientIp(clientIp != null ? clientIp : "Unknown IP")
                .connectedAt(LocalDateTime.now())
                .build();

        activeSessions.put(sessionId, dto);
        log.info("Device connected - Client type: {}, IP: {}, Session: {}",
                appType, clientIp, sessionId);
        broadcastDevices();
    }

    public void unregisterDevice(String sessionId) {
        DeviceSessionDto removed = activeSessions.remove(sessionId);
        if (removed != null) {
            log.info("Client type disconnected - Client type: {}, IP: {}",
                    removed.getClientAppType(), removed.getClientIp());
            broadcastDevices();
        }
    }

    public List<DeviceSessionDto> getAllActiveDevices() {
        return new ArrayList<>(activeSessions.values());
    }

    public boolean hasActiveViewer() {
        return activeSessions.values().stream()
                .anyMatch(device -> device.getClientAppType() == ClientAppType.VIEWER);
    }

    private void broadcastDevices() {
        messagingTemplate.convertAndSend(DEVICE_DESTINATION, getAllActiveDevices());
    }
}