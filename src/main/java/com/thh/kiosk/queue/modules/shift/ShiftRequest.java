package com.thh.kiosk.queue.modules.shift;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ShiftRequest(
        @NotBlank(message = "{shift.error.name_blank}")
        @Size(max = 50, message = "{shift.error.name_max_length}")
        String name,

        @NotNull(message = "{shift.error.start_time_blank}")
        LocalTime startTime,

        @NotNull(message = "{shift.error.start_time_blank}")
        LocalTime endTime
) {
}
