package com.thh.kiosk.queue.modules.setting;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SettingMapper {

    SettingEntity toEntity(SettingRequest request);

    SettingResponse toResponse(SettingEntity entity);
}
