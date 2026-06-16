package com.thh.kiosk.queue.modules.ticket.viewer;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;
import com.thh.kiosk.queue.modules.counter.CounterTicketRealtimeDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.TICKET_ROOT_V1 + "/viewers")
@RequiredArgsConstructor
public class TicketViewerController {

    private final TicketViewerService ticketViewerService;

    @GetMapping
    public ApiResponse<List<CounterTicketRealtimeDto>> getTicketViewers() {
        return ApiResponse.success(
                ticketViewerService.getAllCountersData()
        );
    }
}
