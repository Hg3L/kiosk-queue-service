package com.thh.kiosk.queue.modules.printer;

import com.thh.kiosk.queue.infrastructure.hardware.HardwarePrinterScanner;
import com.thh.kiosk.queue.modules.printer.dto.TicketPrintData;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintService {

    private final ThermalPrinter thermalPrinter;
    private final HardwarePrinterScanner hardwarePrinterScanner;

    public void testPrint() {
        thermalPrinter.printTicket(
                new TicketPrintData(
                        "THH Holdings",
                        "A9999",
                        "Quầy hành chính"
                )
        );
    }

    public String getPrinterStatus() {
        return hardwarePrinterScanner.getPrinterStatus();
    }
}
