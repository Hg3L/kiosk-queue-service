package com.thh.kiosk.queue.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebSocketConstants {

    public static final String ENDPOINT = "ws-kiosk";
    public static final String CLIENT_DESTINATION = "/topic";
    public static final String SERVER_DESTINATION = "/app";

    public static final String COUNTER_DESTINATION = CLIENT_DESTINATION + "/counters/";
    public static final String AUDIO_TV_DESTINATION = CLIENT_DESTINATION + "/audios-tv";
    public static final String AUDIO_STATUS_DESTINATION = CLIENT_DESTINATION + "/audio-status";
    public static final String SETTING_DESTINATION = CLIENT_DESTINATION + "/settings-update";
    public static final String DEVICE_DESTINATION = CLIENT_DESTINATION + "/device";

    public enum Payload {
        COUNTERS_CHANGED,
        RELOAD_REQUIRED,
        TICKETS_RESET,
        SHIFTS_CHANGED
    }
}
