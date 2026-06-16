package com.thh.kiosk.queue.modules.printer;

import com.thh.kiosk.queue.infrastructure.hardware.HardwarePrinterScanner;
import com.thh.kiosk.queue.modules.printer.dto.TicketPrintData;
import com.thh.kiosk.queue.modules.system.log.AbstractLogWriter;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.system.log.ServiceLogTag;

import org.jspecify.annotations.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ServiceLogTag(LogTag.THERMAL_PRINTER)
public class ThermalPrinter extends AbstractLogWriter {

    private final HardwarePrinterScanner printerScanner;

    @Async
    public void printTicket(TicketPrintData data) {
        log.info("Start print: {}", data.ticketCode());

        try {
            PrintService printService = printerScanner.getThermalPrintService();
            if (printService == null) {
                log.error("Thermal print not found");
                return;
            }

            PrinterJob job = getPrinterJob(data, printService);
            job.print();

            log.info("Print {} success in [{}]", data.ticketCode(), printService.getName());

        } catch (Exception e) {
            log.error("Error when print: {}", data.ticketCode(), e);
        }
    }

    private static @NonNull PrinterJob getPrinterJob(TicketPrintData data, PrintService printService) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(printService);

        TicketReceiptTemplate template = new TicketReceiptTemplate(data);

        double mmToPt = 72.0 / 25.4;
        double w = 80 * mmToPt;
        double h = template.estimatedHeightPt();

        Paper paper = new Paper();
        paper.setSize(w, h);
        paper.setImageableArea(0, 0, w, h);

        PageFormat pf = job.defaultPage();
        pf.setPaper(paper);
        pf.setOrientation(PageFormat.PORTRAIT);

        job.setPrintable(template, pf);
        return job;
    }
}