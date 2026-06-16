package com.thh.kiosk.queue.core.query;

public record SearchCriteria(
        String key,
        SearchOperation operation,
        Object value
) {}
