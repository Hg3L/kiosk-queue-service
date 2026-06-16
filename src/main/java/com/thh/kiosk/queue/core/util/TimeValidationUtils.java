package com.thh.kiosk.queue.core.util;

import java.time.LocalTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeValidationUtils {

    public static boolean isTimeInsideShift(LocalTime time, LocalTime start, LocalTime end) {
        if (start.equals(end)) return false;

        if (start.isBefore(end)) {
            // Ex 08:00 -> 17:00 in [start, end)
            return !time.isBefore(start) && time.isBefore(end);
        } else {
            // Ex 22:00 -> 06:00 in [start, 24:00) U [00:00, end)
            return !time.isBefore(start) || time.isBefore(end);
        }
    }
}
