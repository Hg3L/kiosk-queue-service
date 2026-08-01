package com.thh.kiosk.queue.modules.audio;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.AUDIO_ROOT_V1)
@RequiredArgsConstructor
public class AudioController {

    private final AudioService audioService;

    @PostMapping(EndpointConstants.TEST_PATH + "/call")
    public ApiResponse<Void> testAudioDevice() {
        audioService.playTicketCall("A0001");
        return ApiResponse.success();
    }

    @PostMapping(EndpointConstants.TEST_PATH + "/call/{ticketCode}")
    public ApiResponse<Void> testAudioDeviceDynamic(@PathVariable String ticketCode) {
        audioService.playTicketCall(ticketCode);
        return ApiResponse.success();
    }

    @GetMapping(EndpointConstants.HEALTH_PATH)
    public ApiResponse<Object> audioStatus(){
        return ApiResponse.builder()
                .data(audioService.getAudioStatus())
                .build();
    }

    @PostMapping("/unlock")
    public ApiResponse<Void> unlockAudio() {
        audioService.unlockAudio();
        return ApiResponse.success();
    }
}
