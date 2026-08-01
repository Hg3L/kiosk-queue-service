package com.thh.kiosk.queue.modules.ticket;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketSequenceManager {

    private final TicketRepository ticketRepository;

    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Integer> sequenceCache = new ConcurrentHashMap<>();

    public String generateNextCode(String prefix) {
        Object lock = locks.computeIfAbsent(prefix, k -> new Object());

        synchronized (lock) {
            int nextNumber = getNextNumberWithMemoryRecovery(prefix);
            sequenceCache.put(prefix, nextNumber);
            return String.format("%s%04d", prefix, nextNumber);
        }
    }

    public String peekNextCode(String prefix) {
        Object lock = locks.computeIfAbsent(prefix, k -> new Object());

        synchronized (lock) {
            int nextNumber = getNextNumberWithMemoryRecovery(prefix);
            return String.format("%s%04d", prefix, nextNumber);
        }
    }

    public void resetAllSequences() {
        log.info("Clear all sequence caches and locks for all prefixes.");
        sequenceCache.clear();
        locks.clear();
    }

    private int getNextNumberWithMemoryRecovery(String prefix) {
        if (sequenceCache.containsKey(prefix)) {
            return sequenceCache.get(prefix) + 1;
        }

        log.info("Restore ticket in cache {}...", prefix);
        Instant startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();

        Optional<String> maxCodeOpt = ticketRepository.findMaxTicketCodeByPrefixAndDate(prefix, startOfToday);

        if (maxCodeOpt.isPresent()) {
            String numberPart = maxCodeOpt.get().replaceAll("[^\\d.]", "");
            int lastNumber = Integer.parseInt(numberPart);
            log.info("Found the last number for counter {}: {}. Continuing from {}", prefix, lastNumber, lastNumber + 1);
            return lastNumber + 1;
        } else {
            log.info("Counter {} has no tickets today. Starting count from 0001.", prefix);
            return 1;
        }
    }
}
