package com.thh.kiosk.queue.modules.printer;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.PRINTER_ROOT_V1)
@RequiredArgsConstructor
public class ThermalPrinterController {

    private final PrinterService printerService;

    @PostMapping(EndpointConstants.TEST_PATH)
    public ApiResponse<Void> testPrint() {
        printerService.testPrint(PrinterType.ESC_POS);
        return ApiResponse.success();
    }

    @GetMapping(EndpointConstants.HEALTH_PATH)
    public ApiResponse<Object> thermalPrinterStatus(){
        return ApiResponse.builder()
                .data(printerService.getPrinterStatus())
                .build();
    }
}
