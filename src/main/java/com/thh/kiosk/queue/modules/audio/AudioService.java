package com.thh.kiosk.queue.modules.audio;

import com.thh.kiosk.queue.core.constant.WebSocketConstants;
import com.thh.kiosk.queue.infrastructure.hardware.HardwareStatus;
import com.thh.kiosk.queue.modules.audio.strategy.AudioPlaybackMode;
import com.thh.kiosk.queue.modules.audio.strategy.AudioStrategyResolver;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.system.log.ServiceLogTag;
import com.thh.kiosk.queue.modules.system.network.DeviceSessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
@ServiceLogTag(LogTag.AUDIO)
public class AudioService {

    private final AudioStrategyResolver resolver;
    private final DeviceSessionService sessionService;
    private final SimpMessagingTemplate messagingTemplate;

    private final AtomicBoolean isPlaying = new AtomicBoolean(false);

    @Async
    public void playTicketCall(String ticketCode) {
        isPlaying.set(true);
        broadcastAudioStatus(true);

        List<String> playlist = buildPlaylist(ticketCode);
        log.info("Start calling ticket : {}", ticketCode);

        try {
            resolver.resolve(AudioPlaybackMode.TV).play(ticketCode, playlist);
        } catch (RuntimeException e) {
            log.warn("Failed to play audio on TV, fallback to Kiosk : {}", e.getMessage());
            try {
                resolver.resolve(AudioPlaybackMode.KIOSK).play(ticketCode, playlist);
            } finally {
                unlockAudio();
            }
        }
    }

    public void unlockAudio() {
        if (isPlaying.compareAndSet(true, false)) {
            broadcastAudioStatus(false);
        }
    }

    private void broadcastAudioStatus(boolean status) {
        try {
            messagingTemplate.convertAndSend(WebSocketConstants.AUDIO_STATUS_DESTINATION, Map.of("isPlaying", status));
            log.info("Broadcasted audio lock status to UI: isPlaying={}", status);
        } catch (Exception e) {
            log.error("Failed to broadcast audio status", e);
        }
    }

    public String getAudioStatus() {
        if (sessionService.hasActiveViewer()) {
            return HardwareStatus.CONNECTED.name();
        }
        return HardwareStatus.DISCONNECTED.name();
    }

    private List<String> buildPlaylist(String ticketCode) {
        List<String> list = new ArrayList<>();
        String prefixCode = "";

        list.add("audio/start.wav");

        for (char c : ticketCode.toCharArray()) {
            if (Character.isLetter(c)) {
                prefixCode = String.valueOf(c);
                //list.add("audio/alphabet/" + prefixCode + ".wav");
            } else if (Character.isDigit(c)) {
                list.add("audio/number/" + c + ".wav");
            }
        }

        if (!prefixCode.isEmpty()) {
            list.add("audio/counter/" + prefixCode + ".wav");
        }

        return list;
    }
}