package com.thh.kiosk.queue.modules.shift;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShiftMapper {

    ShiftResponse toShiftResponse(ShiftEntity shiftEntity);

    ShiftEntity toShiftEntity(ShiftRequest request);

    void updateShiftResponse(ShiftRequest request, @MappingTarget ShiftEntity shiftEntity);
}
