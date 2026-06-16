package com.thh.kiosk.queue.modules.reset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResetConfigMapper {

    @Mapping(target = "resetTime", source = "entity.resetTime")
    @Mapping(target = "exportPath", source = "entity.exportPath")
    @Mapping(target = "remainingEdits", source = "remainingEdits")
    @Mapping(target = "maxEdits", source = "maxEdits")
    @Mapping(target = "nextRefreshDate", source = "nextRefreshDate")
    ResetConfigResponse toResponse(
            ResetTimeEntity entity,
            int remainingEdits,
            int maxEdits,
            String nextRefreshDate
    );
}
