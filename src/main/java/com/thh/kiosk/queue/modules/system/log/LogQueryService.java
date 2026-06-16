package com.thh.kiosk.queue.modules.system.log;

import com.thh.kiosk.queue.core.model.dto.SliceResponse;
import com.thh.kiosk.queue.core.query.BaseSpecification;
import com.thh.kiosk.queue.core.query.SearchCriteria;
import com.thh.kiosk.queue.core.query.SearchOperation;
import com.thh.kiosk.queue.modules.system.log.dto.LogFilterRequest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogQueryService extends AbstractLogWriter {

    private final LogRepository logRepository;
    private final LogMapper logMapper;

    public SliceResponse<LogResponse> searchLogs(LogFilterRequest req) {

        List<Specification<LogEntity>> specs = new ArrayList<>();

        if (req.getLevel() != null) {
            specs.add(new BaseSpecification<>(new SearchCriteria("level", SearchOperation.EQUALITY, req.getLevel())));
        }
        if (req.getComponent() != null) {
            specs.add(new BaseSpecification<>(new SearchCriteria("component", SearchOperation.EQUALITY, req.getComponent())));
        }
        if (req.getFromDate() != null) {
            specs.add(new BaseSpecification<>(new SearchCriteria("createdAt", SearchOperation.GREATER_THAN, req.getFromDate())));
        }
        if (req.getToDate() != null) {
            specs.add(new BaseSpecification<>(new SearchCriteria("createdAt", SearchOperation.LESS_THAN, req.getToDate())));
        }

        Specification<LogEntity> finalSpec = Specification.allOf(specs);

        Pageable pageable = req.getPageable();
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
        }

        Slice<LogEntity> logSlice = logRepository.findSlice(
                finalSpec,
                pageable,
                LogEntity.class
        );

        return SliceResponse.of(logSlice, logMapper::toSystemLogResponse);
    }
}
