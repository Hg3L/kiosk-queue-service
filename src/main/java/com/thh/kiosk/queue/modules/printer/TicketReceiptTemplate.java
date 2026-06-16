package com.thh.kiosk.queue.modules.printer;

import com.thh.kiosk.queue.modules.printer.dto.TicketPrintData;

import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class TicketReceiptTemplate implements Printable {

    private final TicketPrintData data;
    private static final int MARGIN = 4;

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g = (Graphics2D) graphics;
        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        setupHints(g);
        g.setColor(Color.BLACK);

        int pageW = (int) pageFormat.getImageableWidth();
        int y = 20;

        y = drawWrapped(g, data.title(), boldFont(11), y, pageW);
        y += 8;

        y = drawCentered(g, data.ticketCode(), monoFont(48), y, pageW);
        y += 6;

        y = drawWrapped(g, data.counterName(), plainFont(9), y, pageW);
        y += 4;

        y = drawCentered(g, formatNow(), plainFont(9), y, pageW);
        y += 14;

        return PAGE_EXISTS;
    }

    public int estimatedHeightPt() {
        int lineH11 = fontHeight(boldFont(11));
        int lineH48 = fontHeight(monoFont(48));
        int lineH9  = fontHeight(plainFont(9));

        int titleLines = estimateLines(data.title(), boldFont(11), 180);
        int counterLines = estimateLines(data.counterName(), plainFont(9), 180);

        int y = 20;
        y += lineH11 * titleLines + 8;
        y += lineH48 + 6;
        y += lineH9  * counterLines + 4;
        y += lineH9  + 14;
        return y;
    }

    private int drawCentered(Graphics2D g, String text, Font font, int y, int pageW) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int x = (pageW - fm.stringWidth(text)) / 2;
        g.drawString(text, Math.max(MARGIN, x), y);
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

    private Font boldFont(int size)  { return new Font("Arial", Font.BOLD, size); }
    private Font plainFont(int size) { return new Font("Arial", Font.PLAIN, size); }
    private Font monoFont(int size)  { return new Font("Courier New", Font.BOLD, size); }

    private void setupHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
    }
}