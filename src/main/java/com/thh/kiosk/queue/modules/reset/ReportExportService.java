package com.thh.kiosk.queue.modules.reset;

import static com.thh.kiosk.queue.core.constant.TimeConstants.TIME_FORMATTER;

import com.thh.kiosk.queue.core.exception.BusinessException;
import com.thh.kiosk.queue.core.exception.ErrorCode;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.system.log.ServiceLogTag;
import com.thh.kiosk.queue.modules.ticket.TicketEntity;
import com.thh.kiosk.queue.modules.ticket.TicketStatus;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ServiceLogTag(LogTag.EXPORT_REPORT)
public class ReportExportService {

    public void exportDailyReport(List<TicketEntity> tickets, String exportDirPath, LocalDate logicalDate) {
        if (exportDirPath == null || exportDirPath.isBlank()) {
            log.warn("No directory to save file. Skip export");
            return;
        }

        File dir = new File(exportDirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("Cannot export file to path: {}", exportDirPath);
            throw new BusinessException(ErrorCode.EXPORT_PATH_NOT_FOUND);
        }

        File file = generateUniqueFile(dir, logicalDate);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            ReportStyles styles = new ReportStyles(workbook);

            createSummarySheet(workbook, tickets, logicalDate, styles);

            Map<String, List<TicketEntity>> ticketsByCounter = tickets.stream()
                    .collect(Collectors.groupingBy(t ->
                            t.getCounter() != null ? t.getCounter().getName() : "Chưa gắn quầy"
                    ));

            int counterIdx = 1;
            for (Map.Entry<String, List<TicketEntity>> entry : ticketsByCounter.entrySet()) {
                createCounterSheet(workbook, counterIdx++, entry.getKey(), entry.getValue(), styles);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            log.info("Export success at: {}", file.getAbsolutePath());
        } catch (java.io.FileNotFoundException e) {
            log.error("File is locked or cannot be accessed: {}", file.getAbsolutePath(), e);
            throw new BusinessException(ErrorCode.REPORT_FILE_LOCKED);
        } catch (Exception e) {
            log.error("Error when export: ", e);
            throw new BusinessException(ErrorCode.EXPORT_REPORT_FAILED);
        }
    }


    private File generateUniqueFile(File dir, LocalDate logicalDate) {
        String baseName = "Report_Kiosk_" + logicalDate.toString();
        String extension = ".xlsx";
        File file = new File(dir, baseName + extension);
        int counter = 1;

        while (file.exists()) {
            file = new File(dir, baseName + "_" + counter + extension);
            counter++;
        }
        return file;
    }

    private void createSummarySheet(Workbook workbook, List<TicketEntity> tickets, LocalDate date, ReportStyles styles) {
        Sheet sheet = workbook.createSheet("Tổng Hợp");

        long total = tickets.size();
        long completed = tickets.stream().filter(t -> TicketStatus.COMPLETED.equals(t.getStatus())).count();
        long skipped = tickets.stream().filter(t -> TicketStatus.SKIPPED.equals(t.getStatus())).count();
        long waiting = tickets.stream().filter(t -> TicketStatus.WAITING.equals(t.getStatus())).count();

        Row titleRow = sheet.createRow(1);
        Cell titleCell = titleRow.createCell(1);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        titleCell.setCellValue("BÁO CÁO KẾT QUẢ PHỤC VỤ NGÀY " + date.format(dtf));
        titleCell.setCellStyle(styles.summaryTitleStyle);

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));

        createDataRow(sheet, 2, "Tổng số vé đã in", String.valueOf(total), styles.dataStyle);
        createDataRow(sheet, 3, "Số vé phục vụ thành công", String.valueOf(completed), styles.dataStyle);
        createDataRow(sheet, 4, "Số vé bỏ qua", String.valueOf(skipped), styles.dataStyle);
        createDataRow(sheet, 5, "Số vé chưa xử lý", String.valueOf(waiting), styles.dataStyle);

        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 4000);

        applyThickOutsideBorder(sheet, 1, 5, 1, 2);
    }

    private void createCounterSheet(Workbook workbook, int counterIdx, String rawName, List<TicketEntity> tickets, ReportStyles styles) {
        String formattedName = capitalizeWords(rawName);
        String fullSheetTitle = "Quầy " + counterIdx + " - " + formattedName;

        String sheetTabName = fullSheetTitle.length() > 31 ? fullSheetTitle.substring(0, 31) : fullSheetTitle;
        Sheet sheet = workbook.createSheet(sheetTabName);

        Row titleRow = sheet.createRow(1);
        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue(fullSheetTitle);
        titleCell.setCellStyle(styles.counterHeaderStyle);

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5));

        Row headerRow = sheet.createRow(2);
        String[] headers = {"Mã Vé", "Trạng Thái", "Giờ Lấy Vé", "Giờ Cập Nhật Cuối", "Thời Gian Chờ Đến Lượt (Phút)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i + 1);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.counterHeaderStyle);
        }

        int rowIdx = 3;
        for (TicketEntity ticket : tickets) {
            Row row = sheet.createRow(rowIdx++);

            Cell cell0 = row.createCell(1);
            cell0.setCellValue(ticket.getTicketCode());
            cell0.setCellStyle(styles.dataStyle);

            Cell cell1 = row.createCell(2);
            cell1.setCellValue(translateStatus(ticket.getStatus()));
            cell1.setCellStyle(styles.dataStyle);

            Instant created = ticket.getCreatedAt();
            Instant updated = ticket.getUpdatedAt();

            Cell cell2 = row.createCell(3);
            cell2.setCellValue(created != null ? TIME_FORMATTER.format(created) : "");
            cell2.setCellStyle(styles.dataStyle);

            Cell cell3 = row.createCell(4);
            cell3.setCellValue(updated != null ? TIME_FORMATTER.format(updated) : "");
            cell3.setCellStyle(styles.dataStyle);

            Cell cell4 = row.createCell(5);
            if (created != null && updated != null && !TicketStatus.WAITING.equals(ticket.getStatus())) {
                long minutes = Duration.between(created, updated).toMinutes();
                cell4.setCellValue(minutes);
            } else {
                cell4.setCellValue("");
            }
            cell4.setCellStyle(styles.dataStyle);
        }

        for (int i = 1; i <= 5; i++) {
            sheet.autoSizeColumn(i);
        }

        applyThickOutsideBorder(sheet, 1, rowIdx - 1, 1, 5);
    }

    private void createDataRow(Sheet sheet, int rowIdx, String col1, String col2, CellStyle style) {
        Row row = sheet.createRow(rowIdx);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(col1);
        cell1.setCellStyle(style);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(col2);
        cell2.setCellStyle(style);
    }

    private void applyThickOutsideBorder(Sheet sheet, int startRow, int endRow, int startCol, int endCol) {
        CellRangeAddress region = new CellRangeAddress(startRow, endRow, startCol, endCol);
        RegionUtil.setBorderTop(BorderStyle.THICK, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THICK, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THICK, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THICK, region, sheet);
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isBlank()) return str;
        String[] words = str.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private String translateStatus(TicketStatus status) {
        if (status == null) return "";
        return switch (status) {
            case WAITING -> "Đang chờ";
            case SERVING -> "Đang phục vụ";
            case COMPLETED -> "Hoàn thành";
            case SKIPPED -> "Bỏ qua";
        };
    }

    private static class ReportStyles {
        XSSFCellStyle summaryTitleStyle;
        XSSFCellStyle counterHeaderStyle;
        XSSFCellStyle dataStyle;

        ReportStyles(XSSFWorkbook workbook) {
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            summaryTitleStyle = workbook.createCellStyle();
            summaryTitleStyle.cloneStyleFrom(dataStyle);
            summaryTitleStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)226, (byte)239, (byte)218}, null));
            summaryTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            summaryTitleStyle.setAlignment(HorizontalAlignment.CENTER);
            summaryTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            summaryTitleStyle.setFont(boldFont);

            counterHeaderStyle = workbook.createCellStyle();
            counterHeaderStyle.cloneStyleFrom(dataStyle);
            counterHeaderStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)217, (byte)225, (byte)242}, null));
            counterHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            counterHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            counterHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            counterHeaderStyle.setFont(boldFont);
        }
    }
}