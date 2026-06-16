package com.thh.kiosk.queue.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kiosk.default.ui")
public record KioskDefault(
        String name,
        String logoUrl,

        String customerColorPrimary,
        String customerColorSecondary,
        String customerColorBackground,
        String customerColorTextHighlight,

        String viewerColorPrimary,
        String viewerColorSecondary,
        String viewerColorBackground,
        String viewerColorTextHighlight
) {

}
