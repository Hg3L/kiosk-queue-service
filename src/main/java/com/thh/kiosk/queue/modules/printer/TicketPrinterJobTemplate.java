package com.thh.kiosk.queue.modules.printer;

import com.thh.kiosk.queue.modules.counter.CounterNameUtils;
import com.thh.kiosk.queue.modules.printer.dto.TicketPrintData;

import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class TicketPrinterJobTemplate implements Printable {

    private final TicketPrintData data;
    private static final int MARGIN = 4;

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g = (Graphics2D) graphics;

        double paperWidth = pageFormat.getPaper().getWidth();
        double imageableWidth = pageFormat.getImageableWidth();
        double compensation = Math.max(0, (paperWidth - imageableWidth) / 2 - pageFormat.getImageableX());

        g.translate(pageFormat.getImageableX() + compensation, pageFormat.getImageableY());
        setupHints(g);
        g.setColor(Color.BLACK);

        int pageW = (int) imageableWidth;
        int y = 8;

        y = drawWrapped(g, data.title(), boldFont(9), y, pageW);
        y += 4;

        String formattedTicketCode = data.ticketCode().replaceFirst("^[A-Z]+", "");
        y = drawCentered(g, formattedTicketCode, monoFont(20), y, pageW);
        y += 3;

        String fullCounterName = CounterNameUtils.generateCounterName(data.ticketCode(), data.counterName());
        y = drawWrapped(g, fullCounterName, plainFont(8), y, pageW);
        y += 2;

        y = drawCentered(g, formatNow(), plainFont(8), y, pageW);
        y += 6;

        return PAGE_EXISTS;
    }

    public int estimatedHeightPt() {
        int lineH9 = fontHeight(boldFont(9));
        int lineH30 = fontHeight(monoFont(30));
        int lineH8 = fontHeight(plainFont(8));

        int titleLines = estimateLines(data.title(), boldFont(9), 180);
        int counterLines = estimateLines(data.counterName(), plainFont(8), 180);

        int y = 8;
        y += lineH9 * titleLines + 4;
        y += lineH30 + 3;
        y += lineH8 * counterLines + 2;
        y += lineH8 + 6;
        return y;
    }

    private int drawCentered(Graphics2D g, String text, Font font, int y, int pageW) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int x = (pageW - fm.stringWidth(text)) / 2;
        g.drawString(text, Math.max(MARGIN, x), y + fm.getAscent());
        return y + fm.getHeight();
    }

    private int drawWrapped(Graphics2D g, String text, Font font, int y, int pageW) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int maxW = pageW - MARGIN * 2;

        if (fm.stringWidth(text) <= maxW) {
            return drawCentered(g, text, font, y, pageW);
        }

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String test = line.isEmpty() ? word : line + " " + word;
            if (fm.stringWidth(test) > maxW) {
                y = drawCentered(g, line.toString(), font, y, pageW);
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (!line.isEmpty()) {
            y = drawCentered(g, line.toString(), font, y, pageW);
        }
        return y;
    }

    private int estimateLines(String text, Font font, int maxW) {
        FontMetrics fm = new Canvas().getFontMetrics(font);
        if (fm.stringWidth(text) <= maxW) return 1;
        return (int) Math.ceil((double) fm.stringWidth(text) / maxW) + 1;
    }

    private int fontHeight(Font font) {
        return new Canvas().getFontMetrics(font).getHeight();
    }

    private String formatNow() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private Font boldFont(int size) {
        return new Font("Arial", Font.BOLD, size);
    }

    private Font plainFont(int size) {
        return new Font("Arial", Font.PLAIN, size);
    }

    private Font monoFont(int size) {
        return new Font("Courier New", Font.BOLD, size);
    }

    private void setupHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}