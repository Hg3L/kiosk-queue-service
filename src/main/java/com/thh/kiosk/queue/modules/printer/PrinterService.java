package com.thh.kiosk.queue.modules.printer;

import com.thh.kiosk.queue.infrastructure.hardware.HardwarePrinterScanner;
import com.thh.kiosk.queue.modules.printer.dto.TicketPrintData;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrinterService {

    private final ThermalPrinterJob thermalPrinterJob;

    private final ThermalEscPosJob thermalEscPosJob;

    private final HardwarePrinterScanner hardwarePrinterScanner;

    public void testPrint(PrinterType printerType) {
        switch (printerType) {
            case ESC_POS:
                thermalEscPosJob.printTicket(
                        new TicketPrintData(
                                "THH Holdings",
                                "9999",
                                "Quầy hành chính"
                        )
                );
                break;
            case JAVA_PRINTER_JOB:
                thermalPrinterJob.printTicket(
                        new TicketPrintData(
                                "THH Holdings",
                                "9999",
                                "Quầy hành chính"
                        )
                );
                break;
            default:
                log.warn("Unsupported printer type: {}", printerType);
        }
    }

    public String getPrinterStatus() {
        return hardwarePrinterScanner.getPrinterStatus();
    }
}
