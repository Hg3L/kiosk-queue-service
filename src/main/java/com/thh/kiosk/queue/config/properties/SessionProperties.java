package com.thh.kiosk.queue.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kiosk.session")
public record SessionProperties(
        String cookieName,
        int cookieMaxAgeInSeconds,
        long cacheTtlHours,
        long cacheMaxSize
) {
}
