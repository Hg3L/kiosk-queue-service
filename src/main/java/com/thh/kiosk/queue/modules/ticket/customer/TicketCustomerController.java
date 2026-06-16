package com.thh.kiosk.queue.modules.ticket.customer;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;
import com.thh.kiosk.queue.modules.ticket.TicketResponse;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.TICKET_ROOT_V1 + "/customers")
@RequiredArgsConstructor
public class TicketCustomerController {

    private final TicketCustomerService ticketCustomerService;

    @PostMapping(EndpointConstants.ID_PATH)
    public ApiResponse<TicketResponse> getTicket(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                ticketCustomerService.generateTicket(id)
        );
    }
}
