package com.thh.kiosk.queue.modules.setting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.SETTING_ROOT_V1)
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    private final ObjectMapper objectMapper;

    @GetMapping
    public ApiResponse<List<SettingResponse>> getAllSettings() {
        return ApiResponse.success(
                settingService.getAllSettings()
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> updateSettings(
            @RequestPart(value = "settings", required = false) @Valid String settingsJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {
        List<SettingRequest> settingRequests =
                Collections.emptyList();

        if (StringUtils.hasText(settingsJson)) {
            settingRequests = objectMapper.readValue(
                    settingsJson,
                    new TypeReference<>() {
                    }
            );
        }
        settingService.updateSettings(settingRequests, file);
        return ApiResponse.success();
    }
}
