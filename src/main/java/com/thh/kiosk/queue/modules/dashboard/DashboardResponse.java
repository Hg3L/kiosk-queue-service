package com.thh.kiosk.queue.modules.dashboard;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardResponse {

    private KpiStats kpi;

    private List<ChartData> ticketsByHour;

    private List<ChartData> ticketsByService;

    private List<TicketDetailDto> recentTickets;


    @Builder
    public record KpiStats(
            int totalTickets,
            int completedTickets,
            int skippedTickets,
            int waitingTickets,
            double skippedRate,
            long avgWaitTimeSeconds,
            long avgProcessTimeSeconds
    ) {}

    public record ChartData(
            String label,
            int value
    ) {}

    @Builder
    public record TicketDetailDto(
            String ticketCode,
            String counterName,
            String status,
            String createdAt,
            long waitTimeMinutes
    ) {}
}
