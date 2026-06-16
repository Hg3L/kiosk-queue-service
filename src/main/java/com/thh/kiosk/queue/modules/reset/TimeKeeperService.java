package com.thh.kiosk.queue.modules.reset;

import static com.thh.kiosk.queue.core.constant.TimeConstants.VN_ZONE;

import com.thh.kiosk.queue.core.constant.WebSocketConstants;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.system.log.ServiceLogTag;
import com.thh.kiosk.queue.modules.ticket.TicketEntity;
import com.thh.kiosk.queue.modules.ticket.TicketRepository;
import com.thh.kiosk.queue.modules.ticket.TicketSequenceManager;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ServiceLogTag(LogTag.TIME_SCHEDULER)
public class TimeKeeperService {

    private final TicketRepository ticketRepository;
    private final TicketSequenceManager sequenceManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final ReportExportService reportExportService;

    private final ThreadPoolTaskScheduler taskScheduler;
    private final ResetTimeRepository resetTimeRepository;

    private final ApplicationContext applicationContext;

    private ScheduledFuture<?> scheduledTask;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationStartup() {
        log.info("System is booting.Start reset counter...");
        applicationContext.getBean(TimeKeeperService.class).performResetAndCleanupLogic();

        LocalTime configuredTime = resetTimeRepository.getCurrentResetTime();
        rescheduleResetTask(configuredTime);
    }

    public void rescheduleResetTask(LocalTime newTime) {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }

        String cron = String.format("%d %d %d * * *", newTime.getSecond(), newTime.getMinute(), newTime.getHour());

        scheduledTask = taskScheduler.schedule(
                () -> applicationContext.getBean(TimeKeeperService.class).performResetAndCleanupLogic(),
                new CronTrigger(cron, TimeZone.getTimeZone(VN_ZONE))
        );
        log.info("Automatic reset time schedule at: {}", newTime);
    }

    @Transactional
    public void performResetAndCleanupLogic() {
        log.info("Start export report and reset count each counter...");

        ResetTimeEntity config = resetTimeRepository.findById(1L).orElse(new ResetTimeEntity());
        LocalTime resetTime = config.getResetTime() != null ? config.getResetTime() : LocalTime.MIDNIGHT;

        LocalTime nowTime = LocalTime.now(VN_ZONE);
        LocalDate logicalDate = nowTime.isBefore(resetTime) ? LocalDate.now(VN_ZONE).minusDays(1) : LocalDate.now(VN_ZONE);
        Instant boundary = logicalDate.atTime(resetTime).atZone(VN_ZONE).toInstant();

        try {
            List<TicketEntity> ticketsToExport = ticketRepository.findTicketsForDailyReport(boundary);

            if (!ticketsToExport.isEmpty()) {
                log.info("Found {} tickets. Start export report...", ticketsToExport.size());
                reportExportService.exportDailyReport(ticketsToExport, config.getExportPath(), logicalDate);
            } else {
                log.info("Ticket count is zero. Skip export excel report.");
            }
        } catch (Exception e) {
            log.error("Error when export report:", e);
        }

        int deletedCount = ticketRepository.softDeleteAllTicketsBefore(boundary, Instant.now());
        if (deletedCount > 0) log.info("Soft deleted {} old ticket", deletedCount);

        sequenceManager.resetAllSequences();
        messagingTemplate.convertAndSend(
                WebSocketConstants.SETTING_DESTINATION,
                WebSocketConstants.Payload.TICKETS_RESET.name()
        );

        log.info("Finished export report and reset count each counter.");
    }
}