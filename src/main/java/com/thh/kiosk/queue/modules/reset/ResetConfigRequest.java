package com.thh.kiosk.queue.modules.reset;

import com.thh.kiosk.queue.core.validation.AbsolutePath;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetConfigRequest(

        @NotNull(message = "{reset_time.error.blank}")
        LocalTime resetTime,

        @NotBlank(message = "{reset_time.error.export_path_blank}")
        @Size(max = 500, message = "{reset_time.error.export_path_max_length}")
        @AbsolutePath
        String exportPath
) {
}
