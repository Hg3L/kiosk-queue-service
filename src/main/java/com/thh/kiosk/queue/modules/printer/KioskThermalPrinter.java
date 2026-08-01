package com.thh.kiosk.queue.modules.printer;

import com.thh.kiosk.queue.modules.printer.dto.TicketPrintData;

public interface KioskThermalPrinter {

    void printTicket(TicketPrintData data);
}
