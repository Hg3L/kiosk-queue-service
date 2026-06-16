package com.thh.kiosk.queue.modules.audio.strategy;

import java.util.List;

public interface AudioPlaybackStrategy {

    AudioPlaybackMode getSupportedMode();

    void play(String ticketCode, List<String> playlist);
}
