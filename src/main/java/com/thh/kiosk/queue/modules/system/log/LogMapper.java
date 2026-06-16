package com.thh.kiosk.queue.modules.system.log;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LogMapper {

    @Mapping(target = "details", source = "details",
            defaultExpression = "java(entity.getDetails() != null ? entity.getDetails() : new java.util.HashMap<>())"
    )
    LogResponse toSystemLogResponse(LogEntity entity);
}
