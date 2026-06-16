package com.thh.kiosk.queue.modules.system.network;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.SYSTEM_ROOT_V1 + "/local-networks")
@RequiredArgsConstructor
public class SystemLocalNetworkController {

    private final LocalNetworkService localNetworkService;

    private final DeviceSessionService deviceSessionService;

    @GetMapping("/ip")
    public ApiResponse<String> getLocalIpAddress() {
        String localIp = localNetworkService.getIp();
        return ApiResponse.success(localIp);
    }

    @GetMapping("/devices")
    public ApiResponse<List<DeviceSessionDto>> getActiveDevices() {
        return ApiResponse.success(deviceSessionService.getAllActiveDevices());
    }
}
