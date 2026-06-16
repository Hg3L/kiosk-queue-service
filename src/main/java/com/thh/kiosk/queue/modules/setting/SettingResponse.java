package com.thh.kiosk.queue.modules.setting;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SettingResponse {
    private SettingKey key;

    private String value;
}
