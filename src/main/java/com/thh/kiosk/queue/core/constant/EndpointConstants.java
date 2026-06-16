package com.thh.kiosk.queue.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EndpointConstants {

    /*
     * Module
     * */
    public static final String SYSTEM = "/systems";
    public static final String COUNTER = "/counters";
    public static final String TICKET = "/tickets";
    public static final String AUDIO = "/audios";
    public static final String PRINTER = "/printers";
    public static final String SETTING = "/settings";
    public static final String SHIFT = "/shifts";
    public static final String RESET_TIME = "/reset-times";
    public static final String DASHBOARD = "/dashboards";

     /*
     * Root
     * */
    public static final String SYSTEM_ROOT_V1 = ApiVersion.V1 + SYSTEM;
    public static final String COUNTER_ROOT_V1 = ApiVersion.V1 + COUNTER;
    public static final String TICKET_ROOT_V1 = ApiVersion.V1 + TICKET;
    public static final String AUDIO_ROOT_V1 = ApiVersion.V1 + AUDIO;
    public static final String PRINTER_ROOT_V1 = ApiVersion.V1 + PRINTER;
    public static final String SETTING_ROOT_V1 = ApiVersion.V1 + SETTING;
    public static final String SHIFT_ROOT_V1 = ApiVersion.V1 + SHIFT;
    public static final String RESET_TIME_ROOT_V1 = ApiVersion.V1 + RESET_TIME;
    public static final String DASHBOARD_ROOT_V1 =  ApiVersion.V1 + DASHBOARD;
    /*
    * Common
    * */
    public static final String HEALTH_PATH = "/health";
    public static final String ID_PATH = "/{id}";
    public static final String TEST_PATH = "/test";

}
