package com.thh.kiosk.queue.modules.ticket.officer;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;
import com.thh.kiosk.queue.modules.ticket.TicketResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.TICKET_ROOT_V1 + "/officers")
@RequiredArgsConstructor
public class TicketOfficerController {

    private final TicketOfficerService ticketOfficerService;

    @PostMapping(EndpointConstants.ID_PATH + "/call-next")
    public ApiResponse<TicketResponse> callNextTicket(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                ticketOfficerService.callNextTicket(id)
        );
    }

    @PostMapping(EndpointConstants.ID_PATH + "/recall")
    public ApiResponse<TicketResponse> recallTicket(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                ticketOfficerService.recallCurrentTicket(id)
        );
    }

    @PostMapping(EndpointConstants.ID_PATH + "/skip")
    public ApiResponse<TicketResponse> skipTicket(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                ticketOfficerService.skipCurrentTicket(id)
        );
    }


}
