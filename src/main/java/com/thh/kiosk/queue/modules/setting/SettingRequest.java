package com.thh.kiosk.queue.modules.setting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SettingRequest(
        @NotBlank(message = "{setting.error.key_blank}")
        SettingKey key,

        @NotBlank(message = "{setting.error.value_blank}")
        @Size(max = 255, message = "{setting.error.value_max_length}")
        String value
) {

}
