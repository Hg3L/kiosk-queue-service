package com.thh.kiosk.queue.modules.printer.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrinterDeviceDto {
    private String printerName;
    private boolean isSelected;
}
