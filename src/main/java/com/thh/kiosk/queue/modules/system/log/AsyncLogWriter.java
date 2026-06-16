package com.thh.kiosk.queue.modules.system.log;

import com.thh.kiosk.queue.modules.system.log.dto.LogAsyncDto;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsyncLogWriter {

    private final LogRepository logRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeAsyncLog(LogAsyncDto request) {
        LogEntity log = new LogEntity();
        log.setLevel(request.level());
        log.setComponent(request.component());
        log.setAction(request.action());
        log.setSessionId(request.sessionId());
        log.setMessage(request.message());

        if (request.details() != null && !request.details().isEmpty()) {
            log.setDetails(request.details());
        }

        logRepository.save(log);
    }
}
