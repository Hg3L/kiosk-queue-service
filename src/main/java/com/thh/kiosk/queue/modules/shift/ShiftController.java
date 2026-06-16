package com.thh.kiosk.queue.modules.shift;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.SHIFT_ROOT_V1)
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping
    public ApiResponse<List<ShiftResponse>> getAllShifts() {
        List<ShiftResponse> shifts = shiftService.getAllShifts();
        return ApiResponse.success(shifts);
    }

    @PostMapping
    public ApiResponse<Void> createShift(
            @RequestBody @Valid ShiftRequest request
    ) {
        shiftService.createShift(request);
        return ApiResponse.success();
    }

    @PutMapping(EndpointConstants.ID_PATH)
    public ApiResponse<Void> updateShift(
            @PathVariable Long id,
            @RequestBody @Valid ShiftRequest request
    ) {
        shiftService.updateShift(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping(EndpointConstants.ID_PATH)
    public ApiResponse<Void> deleteShift(
            @PathVariable Long id
    ) {
        shiftService.deleteShift(id);
        return ApiResponse.success();
    }
}
