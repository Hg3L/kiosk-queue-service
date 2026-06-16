package com.thh.kiosk.queue.modules.ticket.viewer;

import com.thh.kiosk.queue.core.model.enums.CommonStatus;
import com.thh.kiosk.queue.modules.counter.CounterTicketRealtimeDto;
import com.thh.kiosk.queue.modules.counter.CounterRepository;
import com.thh.kiosk.queue.modules.ticket.*;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketViewerService {

    private final CounterRepository counterRepository;
    private final TicketRepository ticketRepository;
    private final TicketSequenceManager sequenceManager;

    public List<CounterTicketRealtimeDto> getAllCountersData() {

        return counterRepository.findAllByStatus(CommonStatus.ACTIVE)
                .stream().map(counter -> {
                    String ticketCode = ticketRepository.findFirstByCounterIdAndStatus(counter.getId(), TicketStatus.SERVING)
                            .map(TicketEntity::getTicketCode)
                            .orElse(TicketCodeZero.NONE.name());

                    long waitingCount = ticketRepository.countByCounterIdAndStatus(counter.getId(), TicketStatus.WAITING);
                    String nextCode = sequenceManager.peekNextCode(counter.getPrefix());

                    return CounterTicketRealtimeDto.builder()
                            .counterId(counter.getId())
                            .name(counter.getName())
                            .ticketCode(ticketCode)
                            .waitingCount(waitingCount)
                            .nextAvailableCode(nextCode)
                            .build();
                }).collect(Collectors.toList());
    }
}
