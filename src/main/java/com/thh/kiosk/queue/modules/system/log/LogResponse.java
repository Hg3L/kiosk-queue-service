package com.thh.kiosk.queue.modules.system.log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thh.kiosk.queue.core.model.dto.BaseDataResponse;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(
        {
                "id",
                "status",
                "updatedAt",
                "updatedBy"
        }
)
public class LogResponse extends BaseDataResponse {

    String level;

    String component;

    String action;

    String sessionId;

    String message;

    @Builder.Default
    Map<String, Object> details = new HashMap<>();
}
