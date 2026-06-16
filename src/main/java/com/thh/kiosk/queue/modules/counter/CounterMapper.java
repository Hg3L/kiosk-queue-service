package com.thh.kiosk.queue.modules.counter;

import com.thh.kiosk.queue.modules.counter.dto.CounterResponse;
import com.thh.kiosk.queue.modules.counter.dto.CreateCounterRequest;
import com.thh.kiosk.queue.modules.counter.dto.UpdateCounterRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CounterMapper {

    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    CounterResponse toCounterResponse(CounterEntity counter);

    CounterEntity toCounterEntity(CreateCounterRequest request);

    void updateEntityFromRequest(UpdateCounterRequest request, @MappingTarget CounterEntity entity);
}
