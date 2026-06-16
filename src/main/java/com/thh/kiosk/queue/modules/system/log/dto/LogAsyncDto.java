package com.thh.kiosk.queue.modules.system.log.dto;

import com.thh.kiosk.queue.modules.system.log.LogComponent;
import com.thh.kiosk.queue.modules.system.log.LogLevel;

import java.util.Map;

public record LogAsyncDto(
        LogLevel level,

        LogComponent component,

        String action,

        String sessionId,

        String message,

        Map<String, Object> details
) {
}
