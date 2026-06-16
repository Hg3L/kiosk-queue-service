package com.thh.kiosk.queue.modules.ticket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketCodeZero {
    NONE("0"),
    ;

    private final String code;
}
