package com.thh.kiosk.queue.modules.system.log;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogTag {
    public static final String SYSTEM = "[System]";
    public static final String NETWORK = "[Network]";
    public static final String WEBSOCKET = "[WebSocket]";

    /*
    * Modules
    * */
    public static final String AUDIO = "[Audio]";
    public static final String THERMAL_PRINTER = "[Thermal Printer]";
    public static final String AUDIO_FALLBACK = "[Audio Fallback]";
    public static final String COUNTER = "[Counter]";
    public static final String TICKET_OFFICER = "[Ticket Officer]";
    public static final String SETTING = "[Setting]";
    public static final String IMAGE = "[Image]";
    public static final String SHIFT = "[Shift]";
    public static final String TIME_SCHEDULER = "[Time Scheduler]";
    public static final String DASHBOARD = "[Dashboard]";
    public static final String EXPORT_REPORT = "[Export Report]";
}
