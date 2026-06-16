package com.thh.kiosk.queue.modules.reset;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResetConfigResponse {
    private LocalTime resetTime;
    private String exportPath;
    private int remainingEdits;
    private int maxEdits;
    private String nextRefreshDate;
}
