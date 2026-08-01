package com.thh.kiosk.queue.modules.counter;

import com.thh.kiosk.queue.core.constant.WebSocketConstants;
import com.thh.kiosk.queue.core.exception.BusinessException;
import com.thh.kiosk.queue.core.exception.ErrorCode;
import com.thh.kiosk.queue.core.model.enums.CommonStatus;
import com.thh.kiosk.queue.modules.counter.dto.CounterResponse;
import com.thh.kiosk.queue.modules.counter.dto.CreateCounterRequest;
import com.thh.kiosk.queue.modules.counter.dto.SelectCounterRequest;
import com.thh.kiosk.queue.modules.counter.dto.UpdateCounterRequest;
import com.thh.kiosk.queue.modules.shift.ShiftRepository;
import com.thh.kiosk.queue.modules.system.log.LogTag;
import com.thh.kiosk.queue.modules.system.log.ServiceLogTag;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@ServiceLogTag(LogTag.COUNTER)
public class CounterService {

    private final CounterRepository counterRepository;

    private final CounterMapper counterMapper;

    private final CounterCacheManager counterCacheManager;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ShiftRepository shiftRepository;

    private void broadcastCounterChange() {
        simpMessagingTemplate.convertAndSend(
                WebSocketConstants.SETTING_DESTINATION,
                WebSocketConstants.Payload.COUNTERS_CHANGED.name()
        );
    }

    public void changeCounter(Long id) {
        counterCacheManager.deleteCache(id);
        CounterEntity entity = getCounterEntityById(id);
        String oldIp = entity.getIp();
        entity.setIp(null);
        counterRepository.save(entity);
        log.info("Ip {} has been removed for counter id {}",
                id,
                oldIp
        );
        broadcastCounterChange();
    }

    public void selectCounter(Long id, SelectCounterRequest request) {
        if (!counterCacheManager.addCache(id, request)) {
            log.warn("Counter id {} is already in cache",
                    id
            );
            throw new BusinessException(ErrorCode.COUNTER_ALREADY_SELECTED);
        }
        CounterEntity entity = counterRepository.findByIdAndStatusAndIpIsNull(id, CommonStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Counter with id {} and status {} and with null ip not found",
                            id,
                            CommonStatus.ACTIVE
                    );
                    return new BusinessException(ErrorCode.COUNTER_ALREADY_ASSIGNED);
                });
        entity.setIp(request.ip());
        counterRepository.save(entity);
        log.info("Ip {} has been set for counter id {}",
                id,
                request.ip()
        );
        broadcastCounterChange();
    }

    @Transactional(readOnly = true)
    public List<CounterResponse> getAllActiveCounters() {
        return counterRepository.findAllByStatusOrderByCreatedAtAsc(CommonStatus.ACTIVE).stream()
                .map(counter -> {
                    counterCacheManager.addCache(counter);
                    return counterMapper.toCounterResponse(counter);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CounterResponse> getAllCounters() {
        return counterRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(counter -> {
                    counterCacheManager.addCache(counter);
                    return counterMapper.toCounterResponse(counter);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CounterResponse getCounterById(Long id) {
        CounterEntity entity = getCounterEntityById(id);

        return counterMapper.toCounterResponse(entity);
    }

    @Transactional
    public void addCounter(CreateCounterRequest request) {

        if (counterRepository.existsByPrefixAndStatus(
                request.prefix(),
                CommonStatus.ACTIVE
        )) {
            throw new BusinessException(ErrorCode.COUNTER_PREFIX_EXISTS);
        }

        CounterEntity entity = counterMapper.toCounterEntity(request);
        counterRepository.save(entity);
        log.info("Counter with name {} and prefix {} has been added", request.name(), request.prefix());
        broadcastCounterChange();
    }

    @Transactional
    public void updateCounter(Long id, UpdateCounterRequest request) {
        requireOutOfShift();
        CounterEntity entity = counterRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Counter with id {} not found", id);
                    return new BusinessException(ErrorCode.COUNTER_NOT_FOUND);
                });
        CommonStatus oldStatus = entity.getStatus();
        counterMapper.updateEntityFromRequest(request, entity);

        if (oldStatus == CommonStatus.ACTIVE
                && entity.getStatus() == CommonStatus.INACTIVE
                && entity.getIp() != null
        ) {
            throw new BusinessException(ErrorCode.COUNTER_IN_USE);
        }
        counterRepository.save(entity);

        log.info("Counter with id {} has been updated", entity.getId());

        broadcastCounterChange();
    }

    @Transactional
    public void deleteCounter(Long id) {
        requireOutOfShift();
        CounterEntity entity = getCounterEntityById(id);
        if (entity.getIp() != null) {
            throw new BusinessException(ErrorCode.COUNTER_IN_USE);
        }

        counterRepository.delete(entity);
        log.info("Counter with id {} has been soft deleted", entity.getId());
        broadcastCounterChange();
    }

    private CounterEntity getCounterEntityById(Long id) {
        return counterRepository.findByIdAndStatus(id, CommonStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Counter with id {} and status {} not found", id, CommonStatus.ACTIVE);
                    return new BusinessException(ErrorCode.COUNTER_NOT_FOUND);
                });
    }

    private void requireInShift() {
        if (!shiftRepository.existsShiftByWorkingTime(LocalTime.now())) {
            throw new BusinessException(ErrorCode.SHIFT_OVER);
        }
    }

    private void requireOutOfShift() {
        if (shiftRepository.existsShiftByWorkingTime(LocalTime.now())) {
            throw new BusinessException(ErrorCode.COUNTER_ALREADY_IN_SHIFT);
        }
    }
}
