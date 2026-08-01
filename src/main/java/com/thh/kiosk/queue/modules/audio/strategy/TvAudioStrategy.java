package com.thh.kiosk.queue.modules.audio.strategy;

import com.thh.kiosk.queue.core.constant.WebSocketConstants;
import com.thh.kiosk.queue.modules.system.log.LogTag;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TvAudioStrategy
        implements AudioPlaybackStrategy
{
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public AudioPlaybackMode getSupportedMode() {
        return AudioPlaybackMode.TV;
    }

    @Override
    public void play(String ticketCode, List<String> playlist) {

        try {
            String destination = WebSocketConstants.AUDIO_TV_DESTINATION;
            messagingTemplate.convertAndSend(destination, playlist);
            log.info("{} Broadcast socket audio LAN to call ticket: {}",
                    LogTag.WEBSOCKET,
                    ticketCode);
        } catch (Exception e) {
            log.error("{} Error when broadcast socket audio", LogTag.WEBSOCKET, e);
            throw new RuntimeException("Failed to broadcast audio to TV", e);
        }
    }
}
