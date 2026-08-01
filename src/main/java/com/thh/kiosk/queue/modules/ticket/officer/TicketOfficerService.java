package com.thh.kiosk.queue.modules.ticket.officer;

import com.thh.kiosk.queue.core.exception.BusinessException;
import com.thh.kiosk.queue.core.exception.ErrorCode;
import com.thh.kiosk.queue.core.model.enums.CommonStatus;
import com.thh.kiosk.queue.modules.audio.AudioService;
import com.thh.kiosk.queue.modules.counter.KioskWebSocketService;
import com.thh.kiosk.queue.modules.counter.CounterRepository;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.system.log.ServiceLogTag;
import com.thh.kiosk.queue.modules.ticket.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ServiceLogTag(LogTag.TICKET_OFFICER)
public class TicketOfficerService {

    private final TicketRepository ticketRepository;

    private final CounterRepository counterRepository;

    private final AudioService audioService;

    private final TicketMapper ticketMapper;

    private final KioskWebSocketService kioskWebSocketService;

    @Transactional
    public TicketResponse callNextTicket(Long counterId) {

        if(!counterRepository.existsByIdAndStatus(counterId, CommonStatus.ACTIVE)) {
            log.warn("Counter with id {} not found", counterId);
            throw new BusinessException(ErrorCode.COUNTER_NOT_FOUND);
        }

        ticketRepository.findFirstByCounterIdAndStatus(counterId, TicketStatus.SERVING)
                .ifPresent(ticket -> {
                    ticket.markAsCompleted();
                    ticketRepository.save(ticket);
                });

        Optional<TicketEntity> nextTicketOpt = ticketRepository.findFirstByCounterIdAndStatusOrderByCreatedAtAsc(
                counterId,
                TicketStatus.WAITING
        );

        if (nextTicketOpt.isPresent()) {
            TicketEntity nextTicket = nextTicketOpt.get();
            nextTicket.markAsServing();
            ticketRepository.save(nextTicket);

            kioskWebSocketService.broadcastCounterUpdate(counterId);
            audioService.playTicketCall(nextTicket.getTicketCode());

            return ticketMapper.toTicketResponse(nextTicket);
        } else {
            kioskWebSocketService.broadcastCounterUpdate(counterId);
            return TicketResponse.builder()
                    .ticketCode(TicketCodeZero.NONE.name())
                    .build();
        }
    }


    public TicketResponse recallCurrentTicket(Long counterId) {
        TicketEntity currentTicket = ticketRepository.findFirstByCounterIdAndStatus(counterId, TicketStatus.SERVING)
                .orElseThrow(() -> {
                    log.warn("Recall ticket failed. No ticket with status {} found for counter id {}",
                            TicketStatus.SERVING,
                            counterId
                    );
                    return new BusinessException(ErrorCode.TICKET_NOT_FOUND);
                });

        kioskWebSocketService.broadcastCounterUpdate(counterId);
        audioService.playTicketCall(currentTicket.getTicketCode());
        return ticketMapper.toTicketResponse(currentTicket);
    }


    @Transactional
    public TicketResponse skipCurrentTicket(Long counterId) {
        TicketEntity currentTicket = ticketRepository.findFirstByCounterIdAndStatus(counterId, TicketStatus.SERVING)
                .orElseThrow(() -> {
                    log.warn("Skip ticket failed. No ticket with status {} found for counter id {}",
                            TicketStatus.SERVING,
                            counterId
                    );
                    return new BusinessException(ErrorCode.TICKET_NOT_FOUND);
                });
        currentTicket.markAsSkipped();
        ticketRepository.save(currentTicket);

        Optional<TicketEntity> nextTicketOpt = ticketRepository.findFirstByCounterIdAndStatusOrderByCreatedAtAsc(counterId, TicketStatus.WAITING);

        if (nextTicketOpt.isPresent()) {
            TicketEntity nextTicket = nextTicketOpt.get();
            nextTicket.markAsServing();
            ticketRepository.save(nextTicket);
            kioskWebSocketService.broadcastCounterUpdate(counterId);
            audioService.playTicketCall(nextTicket.getTicketCode());

            return ticketMapper.toTicketResponse(nextTicket);
        } else {
            kioskWebSocketService.broadcastCounterUpdate(counterId);

            return TicketResponse.builder()
                    .ticketCode(TicketCodeZero.NONE.name())
                    .build();
        }
    }
}
