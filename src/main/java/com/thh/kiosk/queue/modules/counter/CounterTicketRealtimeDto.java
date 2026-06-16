package com.thh.kiosk.queue.modules.counter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CounterTicketRealtimeDto {
    private Long counterId;
    private String name;
    private String ticketCode;
    private long waitingCount;
    private String nextAvailableCode;
}
