package com.thh.kiosk.queue.modules.ticket.customer;

import static com.thh.kiosk.queue.modules.setting.SettingKey.KIOSK_TITLE;

import com.thh.kiosk.queue.core.exception.BusinessException;
import com.thh.kiosk.queue.core.exception.ErrorCode;
import com.thh.kiosk.queue.core.model.enums.CommonStatus;
import com.thh.kiosk.queue.modules.printer.ThermalEscPosJob;
import com.thh.kiosk.queue.modules.printer.ThermalPrinterJob;
import com.thh.kiosk.queue.modules.printer.dto.TicketPrintData;
import com.thh.kiosk.queue.modules.counter.KioskWebSocketService;
import com.thh.kiosk.queue.modules.counter.CounterEntity;
import com.thh.kiosk.queue.modules.counter.CounterRepository;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.setting.SettingService;
import com.thh.kiosk.queue.modules.ticket.*;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketCustomerService {

    private final TicketMapper ticketMapper;

    private final SettingService settingService;

    private final TicketRepository ticketRepository;

    private final CounterRepository counterRepository;

    private final TicketSequenceManager sequenceManager;

    private final KioskWebSocketService kioskWebSocketService;

    private final ThermalPrinterJob thermalPrinterJob;

    private final ThermalEscPosJob thermalEscPosJob;

    public TicketResponse generateTicket(Long counterId) {
        CounterEntity counter = counterRepository.findByIdAndStatus(
                counterId,
                CommonStatus.ACTIVE
        ).orElseThrow(() -> {
                log.warn("{} Counter with id {} not found", LogTag.COUNTER, counterId);
                return new BusinessException(ErrorCode.COUNTER_NOT_FOUND);
        });

        String newTicketCode = sequenceManager.generateNextCode(counter.getPrefix());

        TicketEntity newTicket = new TicketEntity();
        newTicket.setTicketCode(newTicketCode);
        newTicket.setStatus(TicketStatus.WAITING);
        newTicket.setCounter(counter);
        counter.addTicket(newTicket);
        ticketRepository.save(newTicket);
        log.info("Ticket generated: {}", newTicket.getTicketCode());

        kioskWebSocketService.broadcastCounterUpdate(counterId);
        String title = settingService.getSettingValues(KIOSK_TITLE).getFirst().getValue();

        thermalEscPosJob.printTicket(
                TicketPrintData.builder()
                        .title(title)
                        .ticketCode(newTicketCode)
                        .counterName(counter.getName())
                        .build()
        );

        return ticketMapper.toTicketResponse(newTicket);
    }
}
