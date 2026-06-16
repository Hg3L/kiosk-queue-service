package com.thh.kiosk.queue.modules.counter;

import com.thh.kiosk.queue.modules.counter.dto.SelectCounterRequest;
import com.thh.kiosk.queue.modules.system.log.LogTag;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CounterCacheManager {

    // counter id - ip
    private final Map<Long, String> counterIpCache = new ConcurrentHashMap<>();

    public boolean addCache(Long id, SelectCounterRequest request) {
        String existingIp = counterIpCache.putIfAbsent(id, request.ip());

        if (existingIp == null) {
            log.info("{} Added counter id {} and ip {} into cache by client request", LogTag.COUNTER, id, request.ip());
            return true;
        }

        log.warn("{} Counter id {} already exists in cache, locked by IP: {}", LogTag.COUNTER, id, existingIp);
        return false;
    }

    public void addCache(CounterEntity entity) {
        if (entity.getIp() != null) {
            String existingIp = counterIpCache.putIfAbsent(entity.getId(), entity.getIp());
            if (existingIp == null) {
                log.info("{} Added counter id {} and ip {} into cache by query", LogTag.COUNTER, entity.getId(), entity.getIp());
            } else {
                log.info("{} Counter id {} get by query already exists in cache", LogTag.COUNTER, entity.getId());
            }
        }
    }

    public void deleteCache(Long id) {
        String removedIp = counterIpCache.remove(id);

        if (removedIp != null) {
            log.info("{} Deleted counter id {} (IP: {}) from cache by client request", LogTag.COUNTER, id, removedIp);
        } else {
            log.info("{} Counter id {} does not exist in cache to delete", LogTag.COUNTER, id);
        }
    }
}
