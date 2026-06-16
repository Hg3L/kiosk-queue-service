package com.thh.kiosk.queue.modules.counter.dto;

import com.thh.kiosk.queue.core.model.enums.CommonStatus;
import com.thh.kiosk.queue.core.validation.ValidEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCounterRequest(
        @NotBlank(message = "{counter.error.name_blank}")
        @Size(max = 100, message = "{counter.error.name_max_length}")
        String name,

        @NotBlank(message = "{counter.error.status_blank}")
        @ValidEnum(enumClass = CommonStatus.class, message = "{counter.error.status_invalid}")
        String status
) {
}
