package com.thh.kiosk.queue.modules.system.log.dto;


import com.thh.kiosk.queue.core.model.dto.BaseSearchRequest;
import com.thh.kiosk.queue.modules.system.log.LogComponent;
import com.thh.kiosk.queue.modules.system.log.LogLevel;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class LogFilterRequest extends BaseSearchRequest {
    private LogLevel level;
    private LogComponent component;
    private Instant fromDate;
    private Instant toDate;
}