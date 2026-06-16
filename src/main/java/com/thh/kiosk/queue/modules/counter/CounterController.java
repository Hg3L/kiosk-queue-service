package com.thh.kiosk.queue.modules.counter;

import com.thh.kiosk.queue.core.constant.EndpointConstants;
import com.thh.kiosk.queue.core.model.dto.ApiResponse;
import com.thh.kiosk.queue.core.util.MessageUtils;
import com.thh.kiosk.queue.modules.counter.dto.CounterResponse;
import com.thh.kiosk.queue.modules.counter.dto.CreateCounterRequest;
import com.thh.kiosk.queue.modules.counter.dto.SelectCounterRequest;
import com.thh.kiosk.queue.modules.counter.dto.UpdateCounterRequest;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EndpointConstants.COUNTER_ROOT_V1)
@RequiredArgsConstructor
public class CounterController {

    private final CounterService counterService;

    @PatchMapping(EndpointConstants.ID_PATH + "/select")
    public ApiResponse<Void> selectCounter(
            @PathVariable Long id,
            @RequestBody @Valid SelectCounterRequest request
    ) {
        counterService.selectCounter(id, request);
        return ApiResponse.success();
    }

    @PatchMapping(EndpointConstants.ID_PATH + "/change")
    public ApiResponse<Void> changeCounter(
            @PathVariable Long id
    ) {
        counterService.changeCounter(id);
        return ApiResponse.success();
    }

    @GetMapping("/active")
    public ApiResponse<List<CounterResponse>> getAllActiveCounters() {
        List<CounterResponse> counters = counterService.getAllActiveCounters();
        return ApiResponse.success(counters);
    }

    @GetMapping
    public ApiResponse<List<CounterResponse>> getAllCounters() {
        List<CounterResponse> counters = counterService.getAllCounters();
        return ApiResponse.success(counters);
    }

    @GetMapping(EndpointConstants.ID_PATH)
    public ApiResponse<CounterResponse> getCounterById(
            @PathVariable Long id
    ) {
        CounterResponse counter = counterService.getCounterById(id);
        return ApiResponse.success(counter);
    }

    @PostMapping
    public ApiResponse<Void> createCounter(
            @RequestBody @Valid CreateCounterRequest request
    ) {
        counterService.addCounter(request);
        return ApiResponse.success(
                MessageUtils.getMessage("counter.create.success")
        );
    }

    @PutMapping(EndpointConstants.ID_PATH)
    public ApiResponse<Void> updateCounter(
            @RequestBody @Valid UpdateCounterRequest request,
            @PathVariable Long id
    ) {
        counterService.updateCounter(id, request);
        return ApiResponse.success(
                MessageUtils.getMessage("counter.update.success")
        );
    }

    @DeleteMapping(EndpointConstants.ID_PATH)
    public ApiResponse<Void> deleteCounter(@PathVariable Long id) {
        counterService.deleteCounter(id);
        return ApiResponse.success(
                MessageUtils.getMessage("counter.delete.success")
        );
    }
}
