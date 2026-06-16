package com.thh.kiosk.queue.modules.dashboard;

import static com.thh.kiosk.queue.core.constant.TimeConstants.VN_ZONE;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.DASHBOARD_ROOT_V1)
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {

        return ApiResponse.success(dashboardService.getDashboardData(date));
    }

    @PostMapping("/export")
    public ApiResponse<Void> exportManualReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) date = LocalDate.now(VN_ZONE);

        dashboardService.exportManualReport(date);
        return ApiResponse.success();
    }
}
