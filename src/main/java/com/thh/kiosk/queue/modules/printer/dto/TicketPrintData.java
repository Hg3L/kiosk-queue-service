package com.thh.kiosk.queue.modules.printer.dto;

import lombok.Builder;

@Builder
public record TicketPrintData(
        String title,
        String ticketCode,
        String counterName
) {}
