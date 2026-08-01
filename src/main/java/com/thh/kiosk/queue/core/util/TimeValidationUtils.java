package com.thh.kiosk.queue.core.util;

import java.time.LocalTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeValidationUtils {

    public static boolean isTimeInsideShift(LocalTime time, LocalTime start, LocalTime end) {
        if (time == null || start == null || end == null) {
            return false;
        }

        if (!start.isBefore(end)) {
            return false;
        }

        return !time.isBefore(start) && time.isBefore(end);
    }
}
