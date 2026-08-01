package com.thh.kiosk.queue.modules.printer;

import com.thh.kiosk.queue.infrastructure.hardware.HardwarePrinterScanner;
import com.thh.kiosk.queue.modules.printer.dto.TicketPrintData;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.system.log.ServiceLogTag;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.print.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ServiceLogTag(LogTag.THERMAL_PRINTER)
public class ThermalEscPosJob {

    private static final int HEAD_WIDTH_DOTS = 576;
    private static final double HEAD_DPI = 203.0;
    private static final double POINTS_DPI = 72.0;

    private final HardwarePrinterScanner printerScanner;

    @Async
    public void printTicket(TicketPrintData data) {
        log.info("Start print: {}", data.ticketCode());

        try {
            PrintService printerService = printerScanner.getThermalPrintService();
            if (printerService == null) {
                log.error("Thermal print not found");
                return;
            }

            byte[] payload = buildEscPosPayload(data);

            DocPrintJob job = printerService.createPrintJob();
            Doc doc = new SimpleDoc(payload, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);

            log.info("Print {} success in [{}]", data.ticketCode(), printerService.getName());

        } catch (Exception e) {
            log.error("Error when print: {}", data.ticketCode(), e);
        }
    }

    private byte[] buildEscPosPayload(TicketPrintData data) throws IOException {
        BufferedImage image = renderTicketImage(data);
        byte[] raster = toEscPosRaster(image);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(new byte[]{0x1B, 0x40});       // ESC @  - khởi tạo máy in
        out.write(raster);                        // GS v 0 - ảnh raster vé
        out.write(new byte[]{0x1B, 0x64, 0x03});  // ESC d 3 - feed 3 dòng
        out.write(new byte[]{0x1D, 0x56, 0x00});  // GS V 0 - cắt giấy (full cut)
        return out.toByteArray();
    }

    private BufferedImage renderTicketImage(TicketPrintData data) {
        TicketEscPosTemplate template = new TicketEscPosTemplate(data);

        double scale = HEAD_DPI / POINTS_DPI;
        int logicalWidthPt = (int) Math.round(HEAD_WIDTH_DOTS / scale);
        int logicalHeightPt = template.estimatedHeightPt();
        int heightDots = (int) Math.ceil(logicalHeightPt * scale);

        BufferedImage image = new BufferedImage(HEAD_WIDTH_DOTS, heightDots, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, HEAD_WIDTH_DOTS, heightDots);
        g.scale(scale, scale);
        template.draw(g, logicalWidthPt);
        g.dispose();

        return image;
    }

    private byte[] toEscPosRaster(BufferedImage image) throws IOException {
        int widthDots = image.getWidth();
        int heightDots = image.getHeight();
        int bytesPerRow = widthDots / 8;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(new byte[]{0x1D, 0x76, 0x30, 0x00}); // GS v 0, m=0 (normal)
        out.write(bytesPerRow & 0xFF);
        out.write((bytesPerRow >> 8) & 0xFF);
        out.write(heightDots & 0xFF);
        out.write((heightDots >> 8) & 0xFF);

        for (int y = 0; y < heightDots; y++) {
            for (int xByte = 0; xByte < bytesPerRow; xByte++) {
                int b = 0;
                for (int bit = 0; bit < 8; bit++) {
                    int x = xByte * 8 + bit;
                    int gray = image.getRGB(x, y) & 0xFF;
                    boolean black = gray < 128;
                    if (black) {
                        b |= (0x80 >> bit);
                    }
                }
                out.write(b);
            }
        }
        return out.toByteArray();
    }
}
