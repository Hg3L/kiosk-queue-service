package com.thh.kiosk.queue.infrastructure.hardware;

import com.thh.kiosk.queue.modules.printer.PrinterStatus;

import org.springframework.stereotype.Component;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;

@Component
public class HardwarePrinterScanner {

    public PrintService getThermalPrintService() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : printServices) {
            if (!isVirtualPrinter(service.getName())) {
                return service;
            }
        }
        return null;
    }

    private boolean isVirtualPrinter(String printerName) {
        if (printerName == null) return true;
        String lowerName = printerName.toLowerCase();
        return lowerName.contains("pdf") ||
                lowerName.contains("xps") ||
                lowerName.contains("onenote") ||
                lowerName.contains("fax") ||
                lowerName.contains("microsoft") ||
                lowerName.contains("webex") ||
                lowerName.contains("snagit");
    }

    public String getPrinterStatus() {
        PrintService defaultService = getThermalPrintService();
        if (defaultService == null) {
            return HardwareStatus.DISCONNECTED.name();
        }

        PrinterStateReasons stateReasons = defaultService.getAttribute(PrinterStateReasons.class);

        if (stateReasons != null) {
            if (stateReasons.containsKey(PrinterStateReason.MEDIA_EMPTY) ||
                    stateReasons.containsKey(PrinterStateReason.MEDIA_NEEDED)) {
                return "OUT_OF_PAPER";
            }

            if (stateReasons.containsKey(PrinterStateReason.MEDIA_JAM) ||
                    stateReasons.containsKey(PrinterStateReason.DOOR_OPEN) ||
                    stateReasons.containsKey(PrinterStateReason.COVER_OPEN) ||
                    stateReasons.containsKey(PrinterStateReason.SHUTDOWN) ||
                    stateReasons.containsKey(PrinterStateReason.PAUSED) ||
                    stateReasons.containsKey(PrinterStateReason.TIMED_OUT)) {
                return HardwareStatus.ERROR.name();
            }
        }

        return HardwareStatus.CONNECTED.name();
    }
}