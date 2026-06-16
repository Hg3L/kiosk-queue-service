package com.thh.kiosk.queue.modules.system.log.dto;

import com.thh.kiosk.queue.modules.system.log.LogComponent;

import java.util.Map;

import lombok.Builder;

@Builder
public record LogParamDto(
        LogComponent component,
        String action,
        String message,
        Map<String, Object> details
) {}
