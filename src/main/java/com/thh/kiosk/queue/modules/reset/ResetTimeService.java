package com.thh.kiosk.queue.modules.reset;

import static com.thh.kiosk.queue.core.constant.TimeConstants.VN_ZONE;

import com.thh.kiosk.queue.core.exception.BusinessException;
import com.thh.kiosk.queue.core.exception.ErrorCode;
import com.thh.kiosk.queue.core.util.TimeValidationUtils;
import com.thh.kiosk.queue.modules.shift.ShiftRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResetTimeService {

    private final ResetTimeRepository resetTimeRepository;
    private final ShiftRepository shiftRepository;
    private final ResetConfigMapper resetConfigMapper;
    private final TimeKeeperService timeKeeperService;

    @Value("${kiosk.default.reset-config.max-edits-per-month}")
    private int maxEditsPerMonth;

    public ResetConfigResponse getConfig() {
        ResetTimeEntity entity = getOrCreateEntity();
        refreshEditCountIfNewMonth(entity);
        return buildResponse(entity);
    }

    @Transactional
    public ResetConfigResponse updateConfig(ResetConfigRequest request) {

        ResetTimeEntity entity = getOrCreateEntity();
        refreshEditCountIfNewMonth(entity);

        if (maxEditsPerMonth > 0 && entity.getEditCount() >= maxEditsPerMonth) {
            //String nextDate = YearMonth.now(VN_ZONE).plusMonths(1).atDay(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            throw new BusinessException(ErrorCode.CONFIG_CHANGE_LIMIT_EXCEEDED);
        }

        boolean hasOverlap = shiftRepository.findAll().stream()
                .anyMatch(shift -> TimeValidationUtils.isTimeInsideShift(request.resetTime(), shift.getStartTime(), shift.getEndTime()));

        if (hasOverlap) {
            throw new BusinessException(ErrorCode.CONFIG_CONFLICT_SHIFT);
        }

        entity.setResetTime(request.resetTime());
        entity.setExportPath(request.exportPath());
        entity.setEditCount(entity.getEditCount() + 1);
        entity.setLastEditMonth(YearMonth.now(VN_ZONE).toString());

        resetTimeRepository.save(entity);

        timeKeeperService.rescheduleResetTask(request.resetTime());

        log.info("Config reset change: {}, export path: {}, {}/{} changes.",
                request.resetTime(), request.exportPath(), entity.getEditCount(), maxEditsPerMonth);

        return buildResponse(entity);
    }

    private ResetTimeEntity getOrCreateEntity() {
        return resetTimeRepository.findById(1L).orElseGet(() -> {
            ResetTimeEntity newEntity = new ResetTimeEntity();
            newEntity.setId(1L);
            newEntity.setLastEditMonth(YearMonth.now(VN_ZONE).toString());
            return resetTimeRepository.save(newEntity);
        });
    }

    private void refreshEditCountIfNewMonth(ResetTimeEntity entity) {
        String currentMonth = YearMonth.now(VN_ZONE).toString();
        if (entity.getLastEditMonth() == null || !entity.getLastEditMonth().equals(currentMonth)) {
            entity.setEditCount(0);
            entity.setLastEditMonth(currentMonth);
            resetTimeRepository.save(entity);
        }
    }

    private ResetConfigResponse buildResponse(ResetTimeEntity entity) {
        int remaining = maxEditsPerMonth == 0 ? 999 : Math.max(0, maxEditsPerMonth - entity.getEditCount());
        String nextDate = YearMonth.now(VN_ZONE).plusMonths(1).atDay(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return resetConfigMapper.toResponse(entity, remaining, maxEditsPerMonth, nextDate);
    }
}
