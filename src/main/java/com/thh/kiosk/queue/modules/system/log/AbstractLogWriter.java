package com.thh.kiosk.queue.modules.system.log;

import com.thh.kiosk.queue.config.security.UserContextHolder;
import com.thh.kiosk.queue.modules.system.log.dto.LogAsyncDto;
import com.thh.kiosk.queue.modules.system.log.dto.LogParamDto;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractLogWriter {

    @Autowired
    private AsyncLogWriter asyncLogWriter;

    protected void logInfo(LogParamDto param) {
        sendLog(LogLevel.INFO, param);
    }

    protected void logWarn(LogParamDto param) {
        sendLog(LogLevel.WARN, param);
    }

    protected void logError(LogParamDto param) {
        sendLog(LogLevel.ERROR, param);
    }

    private void sendLog(LogLevel level, LogParamDto param) {
        String currentSessionId = UserContextHolder.getCurrentSessionId();

        LogAsyncDto request = new LogAsyncDto(
                level,
                param.component(),
                param.action(),
                currentSessionId,
                param.message(),
                param.details()
        );

        asyncLogWriter.executeAsyncLog(request);
    }
}
