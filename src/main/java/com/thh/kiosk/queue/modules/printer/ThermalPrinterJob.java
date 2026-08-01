package com.thh.kiosk.queue.modules.printer;

import com.thh.kiosk.queue.infrastructure.hardware.HardwarePrinterScanner;
import com.thh.kiosk.queue.modules.printer.dto.TicketPrintData;
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
public class ThermalPrinterJob {

    private static final double PAPER_WIDTH_MM = 80;
    private static final double PRINTABLE_WIDTH_MM = 72.1;
    private static final double SIDE_MARGIN_MM = (PAPER_WIDTH_MM - PRINTABLE_WIDTH_MM) / 2;

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

        TicketPrinterJobTemplate template = new TicketPrinterJobTemplate(data);

        double mmToPt = 72.0 / 25.4;
        double paperW = PAPER_WIDTH_MM * mmToPt;
        double printableW = PRINTABLE_WIDTH_MM * mmToPt;
        double marginX = SIDE_MARGIN_MM * mmToPt;
        double h = template.estimatedHeightPt();

        Paper paper = new Paper();
        paper.setSize(paperW, h);
        paper.setImageableArea(marginX, 0, printableW, h);

        PageFormat pf = job.defaultPage();
        pf.setPaper(paper);
        pf.setOrientation(PageFormat.PORTRAIT);

        pf = job.validatePage(pf);

        log.info("Validated page: imageableX={}, imageableY={}, imageableW={}, imageableH={}, paperW={}",
                pf.getImageableX(), pf.getImageableY(), pf.getImageableWidth(), pf.getImageableHeight(),
                pf.getPaper().getWidth());

        job.setPrintable(template, pf);
        return job;
    }
}