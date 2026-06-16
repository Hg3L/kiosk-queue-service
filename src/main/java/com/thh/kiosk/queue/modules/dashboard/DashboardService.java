package com.thh.kiosk.queue.modules.dashboard;

import static com.thh.kiosk.queue.core.constant.TimeConstants.TIME_FORMATTER;
import static com.thh.kiosk.queue.core.constant.TimeConstants.VN_ZONE;

import com.thh.kiosk.queue.modules.reset.ReportExportService;
import com.thh.kiosk.queue.modules.reset.ResetTimeEntity;
import com.thh.kiosk.queue.modules.reset.ResetTimeRepository;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.system.log.ServiceLogTag;
import com.thh.kiosk.queue.modules.ticket.TicketEntity;
import com.thh.kiosk.queue.modules.ticket.TicketRepository;
import com.thh.kiosk.queue.modules.ticket.TicketStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ServiceLogTag(LogTag.DASHBOARD)
public class DashboardService {

    private final TicketRepository ticketRepository;

    private final ResetTimeRepository resetTimeRepository;

    private final ReportExportService reportExportService;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData(LocalDate date) {

        if (date == null) {
            date = LocalDate.now(VN_ZONE);
        }

        Instant startOfDay = date.atStartOfDay(VN_ZONE).toInstant();
        Instant endOfDay = date.atTime(LocalTime.MAX).atZone(VN_ZONE).toInstant();

        List<TicketEntity> tickets = ticketRepository.findAllTicketsForReport(startOfDay, endOfDay);

        int total = tickets.size();
        int complete = 0;
        int skipped = 0;
        int waiting = 0;

        long totalWaitSeconds = 0;
        long totalProcessSeconds = 0;
        int waitCount = 0;
        int processCount = 0;

        for (TicketEntity t : tickets) {

            if (TicketStatus.COMPLETED.equals(t.getStatus())) complete++;
            else if (TicketStatus.SKIPPED.equals(t.getStatus())) skipped++;
            else if (TicketStatus.WAITING.equals(t.getStatus())) waiting++;

            if (t.getServingAt() != null && t.getCreatedAt() != null) {
                totalWaitSeconds += Duration.between(t.getCreatedAt(), t.getServingAt()).getSeconds();
                waitCount++;
            }

            else if (TicketStatus.SKIPPED.equals(t.getStatus()) && t.getCompletedAt() != null && t.getCreatedAt() != null) {
                totalWaitSeconds += Duration.between(t.getCreatedAt(), t.getCompletedAt()).getSeconds();
                waitCount++;
            }

            if (TicketStatus.COMPLETED.equals(t.getStatus()) && t.getServingAt() != null && t.getCompletedAt() != null) {
                totalProcessSeconds += Duration.between(t.getServingAt(), t.getCompletedAt()).getSeconds();
                processCount++;
            }
        }

        double skippedRate = total == 0 ? 0 : Math.round(((double) skipped / total) * 100.0 * 10.0) / 10.0;
        long avgWaitSeconds = waitCount == 0 ? 0 : totalWaitSeconds / waitCount;
        long avgProcessSeconds = processCount == 0 ? 0 : totalProcessSeconds / processCount;

        DashboardResponse.KpiStats kpi = DashboardResponse.KpiStats.builder()
                .totalTickets(total)
                .completedTickets(complete)
                .skippedTickets(skipped)
                .waitingTickets(waiting)
                .skippedRate(skippedRate)
                .avgWaitTimeSeconds(avgWaitSeconds)
                .avgProcessTimeSeconds(avgProcessSeconds)
                .build();

        Map<Integer, Long> hourMap = tickets.stream()
                .filter(t -> t.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCreatedAt().atZone(VN_ZONE).getHour(),
                        Collectors.counting()
                ));

        List<DashboardResponse.ChartData> hourlyChart = hourMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new DashboardResponse.ChartData(String.format("%02d:00", e.getKey()), e.getValue().intValue()))
                .toList();

        Map<String, Long> serviceMap = tickets.stream()
                .filter(t -> t.getCounter() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCounter().getName(),
                        Collectors.counting()
                ));

        List<DashboardResponse.ChartData> serviceChart = serviceMap.entrySet().stream()
                .map(e -> new DashboardResponse.ChartData(e.getKey(), e.getValue().intValue()))
                .toList();

        List<DashboardResponse.TicketDetailDto> tableData = tickets.stream()
                .sorted((t1, t2) -> {
                    Instant time1 = t1.getCreatedAt() != null ? t1.getCreatedAt() : Instant.MIN;
                    Instant time2 = t2.getCreatedAt() != null ? t2.getCreatedAt() : Instant.MIN;
                    return time2.compareTo(time1);
                })
                .limit(100)
                .map(t -> {
                    long waitMins = 0;
                    if (t.getServingAt() != null && t.getCreatedAt() != null) {
                        waitMins = Duration.between(t.getCreatedAt(), t.getServingAt()).toMinutes();
                    } else if (TicketStatus.SKIPPED.equals(t.getStatus()) && t.getCompletedAt() != null && t.getCreatedAt() != null) {
                        waitMins = Duration.between(t.getCreatedAt(), t.getCompletedAt()).toMinutes();
                    }

                    return DashboardResponse.TicketDetailDto.builder()
                            .ticketCode(t.getTicketCode())
                            .counterName(t.getCounter() != null ? t.getCounter().getName() : "Chưa gắn quầy")
                            .status(t.getStatus() != null ? t.getStatus().name() : "UNKNOWN")
                            .createdAt(t.getCreatedAt() != null ? TIME_FORMATTER.format(t.getCreatedAt()) : "")
                            .waitTimeMinutes(waitMins)
                            .build();
                })
                .toList();

        return DashboardResponse.builder()
                .kpi(kpi)
                .ticketsByHour(hourlyChart)
                .ticketsByService(serviceChart)
                .recentTickets(tableData)
                .build();
    }

    public void exportManualReport(LocalDate date) {
        Instant startOfDay = date.atStartOfDay(VN_ZONE).toInstant();
        Instant endOfDay = date.atTime(LocalTime.MAX).atZone(VN_ZONE).toInstant();

        List<TicketEntity> ticketsToExport = ticketRepository.findAllTicketsForReport(startOfDay, endOfDay);

        ResetTimeEntity config = resetTimeRepository.findById(1L).orElse(new ResetTimeEntity());
        String exportPath = config.getExportPath();

        // Kích hoạt Engine tạo Excel
        reportExportService.exportDailyReport(ticketsToExport, exportPath, date);
    }
}
