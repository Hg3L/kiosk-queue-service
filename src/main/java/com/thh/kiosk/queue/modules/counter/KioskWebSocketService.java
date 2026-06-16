package com.thh.kiosk.queue.modules.counter;

import static com.thh.kiosk.queue.core.constant.WebSocketConstants.COUNTER_DESTINATION;

import com.thh.kiosk.queue.core.model.enums.CommonStatus;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.ticket.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KioskWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final TicketRepository ticketRepository;
    private final CounterRepository counterRepository;
    private final TicketSequenceManager sequenceManager;

    public void broadcastCounterUpdate(Long counterId) {
        try {
            CounterEntity counter = counterRepository.findByIdAndStatus(counterId, CommonStatus.ACTIVE)
                    .orElse(null);
            if (counter == null) {
                log.warn("{} Cannot found counter with id {}. Skip broadcast.", LogTag.COUNTER, counterId);
                return;
            }

            TicketEntity ticket = ticketRepository.findFirstByCounterIdAndStatus(counterId, TicketStatus.SERVING)
                    .orElse(null);
            String ticketCode = (ticket != null) ? ticket.getTicketCode() : TicketCodeZero.NONE.name();

            long waitingCount = ticketRepository.countByCounterIdAndStatus(counterId, TicketStatus.WAITING);

            String nextCode = sequenceManager.peekNextCode(counter.getPrefix());

            CounterTicketRealtimeDto payload = CounterTicketRealtimeDto.builder()
                    .counterId(counterId)
                    .name(counter.getName())
                    .ticketCode(ticketCode)
                    .waitingCount(waitingCount)
                    .nextAvailableCode(nextCode)
                    .build();

            String destination = COUNTER_DESTINATION + counterId;
            messagingTemplate.convertAndSend(destination, payload);

            log.info("{} Broadcast data to counter id {}: Serving={}, Waiting={}",
                    LogTag.WEBSOCKET,
                    counterId,
                    ticketCode,
                    waitingCount
            );

        } catch (Exception e) {
            log.error("{} Error when broadcast to counter id: [{}]",
                    LogTag.WEBSOCKET,
                    counterId, e
            );
        }
    }
}