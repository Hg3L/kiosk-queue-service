package com.thh.kiosk.queue.modules.system.network;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeviceSessionDto {
    private String sessionId;
    private ClientAppType clientAppType;
    private String clientIp;
    private LocalDateTime connectedAt;
}
