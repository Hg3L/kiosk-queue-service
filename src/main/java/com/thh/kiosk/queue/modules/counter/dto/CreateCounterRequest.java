package com.thh.kiosk.queue.modules.counter.dto;

import com.thh.kiosk.queue.core.validation.PrefixCounter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCounterRequest (

        @NotBlank(message = "{counter.error.name_blank}")
        @Size(max = 100, message = "{counter.error.name_max_length}")
        String name,

        @NotBlank(message = "{counter.error.prefix_blank}")
        @PrefixCounter
        String prefix
) {
}
