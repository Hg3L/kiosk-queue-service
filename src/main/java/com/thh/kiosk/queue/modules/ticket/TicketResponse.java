package com.thh.kiosk.queue.modules.ticket;

import com.thh.kiosk.queue.core.model.dto.BaseDataResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketResponse extends BaseDataResponse {
    String ticketCode;
}