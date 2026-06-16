package com.thh.kiosk.queue.modules.counter.dto;

import com.thh.kiosk.queue.core.validation.Ipv4Address;

import jakarta.validation.constraints.NotBlank;

public record SelectCounterRequest (
        @NotBlank(message = "{counter.error.ip_blank}")
        @Ipv4Address
        String ip
) {
}
