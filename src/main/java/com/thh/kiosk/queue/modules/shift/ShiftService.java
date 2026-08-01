package com.thh.kiosk.queue.modules.shift;

import com.thh.kiosk.queue.core.constant.WebSocketConstants;
import com.thh.kiosk.queue.core.exception.BusinessException;
import com.thh.kiosk.queue.core.exception.ErrorCode;
import com.thh.kiosk.queue.core.util.TimeValidationUtils;
import com.thh.kiosk.queue.modules.reset.ResetTimeRepository;
import com.thh.kiosk.queue.modules.reset.ResetTimeService;
import com.thh.kiosk.queue.modules.system.log.LogTag;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;

    private final ShiftMapper shiftMapper;

    private final SimpMessagingTemplate messagingTemplate;

    private final ResetTimeRepository resetTimeRepository;

    private void broadcastShiftChange() {
        messagingTemplate.convertAndSend(
                WebSocketConstants.SETTING_DESTINATION,
                WebSocketConstants.Payload.SHIFTS_CHANGED.name()
        );
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getAllShifts() {
        return shiftRepository.findAllByOrderByStartTimeAsc().stream()
                .map(shiftMapper::toShiftResponse)
                .toList();
    }

    @Transactional
    public void createShift(ShiftRequest request) {
        validateShiftLogic(request.startTime(), request.endTime(), null);

        ShiftEntity entity = shiftMapper.toShiftEntity(request);

        shiftRepository.save(entity);
        log.info("Shift created: {} ({} - {})", entity.getName(), entity.getStartTime(), entity.getEndTime());
        broadcastShiftChange();
    }

    @Transactional
    public void updateShift(Long id, ShiftRequest request) {
        ShiftEntity entity = getShiftById(id);

        //checkIfShiftIsCurrentlyActiveAndThrow(entity);

        validateShiftLogic(request.startTime(), request.endTime(), id);

        shiftMapper.updateShiftResponse(request, entity);

        shiftRepository.save(entity);
        log.info("{} Shift updated: ID {}", LogTag.SHIFT, id);
        broadcastShiftChange();
    }

    @Transactional
    public void deleteShift(Long id) {
        ShiftEntity entity = getShiftById(id);

        //checkIfShiftIsCurrentlyActiveAndThrow(entity);

        shiftRepository.delete(entity);
        log.info("{} Shift hard-deleted: ID {}",LogTag.SHIFT, id);
        broadcastShiftChange();
    }


    private ShiftEntity getShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(
                        () -> {
                            log.warn("{} Shift with ID {} not found", LogTag.SHIFT, id);
                            return new BusinessException(ErrorCode.SHIFT_NOT_FOUND);
                        }
                );
    }

    private void validateShiftLogic(LocalTime start, LocalTime end, Long excludeId) {
        if (!start.isBefore(end)) {
            log.warn("{} Invalid shift time range: start {} is not before end {}", LogTag.SHIFT, start, end);
            throw new BusinessException(ErrorCode.SHIFT_INVALID_TIME_RANGE);
        }

        long overlaps = shiftRepository.countOverlappingShifts(start, end, excludeId);
        if (overlaps > 0) {
            log.warn("{} Attempted to create/update shift with time range {} - {} that overlaps with existing shifts",
                    LogTag.SHIFT,
                    start,
                    end
            );
            throw new BusinessException(ErrorCode.SHIFT_OVERLAP);
        }

        LocalTime resetTime = resetTimeRepository.getCurrentResetTime();
        if (TimeValidationUtils.isTimeInsideShift(resetTime, start, end)) {
            log.warn("{} Attempted to create/update shift with time range {} - {} that includes reset time {}",
                    LogTag.SHIFT,
                    start,
                    end,
                    resetTime
            );
            throw new BusinessException(ErrorCode.SHIFT_CONFLICT_RESET_TIME);
        }
    }

    private void checkIfShiftIsCurrentlyActiveAndThrow(ShiftEntity shift) {
        LocalTime now = LocalTime.now();
        boolean isActive = !now.isBefore(shift.getStartTime()) && !now.isAfter(shift.getEndTime());
        if (isActive) {
            throw new BusinessException(ErrorCode.SHIFT_IN_USE);
        }
    }
}
