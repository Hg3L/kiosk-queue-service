package com.thh.kiosk.queue.modules.reset;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.RESET_TIME_ROOT_V1)
@RequiredArgsConstructor
public class ResetTimeController {

    private final ResetTimeService resetTimeService;

    @GetMapping
    public ApiResponse<ResetConfigResponse> getConfig() {
        return ApiResponse.success(resetTimeService.getConfig());
    }

    @PutMapping
    public ApiResponse<ResetConfigResponse> updateConfig(
            @RequestBody @Valid ResetConfigRequest request
    ) {
        return ApiResponse.success(
                resetTimeService.updateConfig(
                        request
                )
        );
    }
}
