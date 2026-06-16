package com.thh.kiosk.queue.modules.system.log;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;
import com.thh.kiosk.queue.core.model.dto.SliceResponse;
import com.thh.kiosk.queue.modules.system.log.dto.LogFilterRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.SYSTEM_ROOT_V1 + "/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogQueryService logQueryService;

    @GetMapping
    public ApiResponse<SliceResponse<LogResponse>> searchLogs(LogFilterRequest request) {
        return ApiResponse.success(
                logQueryService.searchLogs(request)
        );
    }
}
