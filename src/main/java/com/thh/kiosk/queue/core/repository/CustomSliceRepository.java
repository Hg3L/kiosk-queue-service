package com.thh.kiosk.queue.core.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;

public interface CustomSliceRepository<T> {

    Slice<T> findSlice(Specification<T> spec, Pageable pageable, Class<T> domainClass);
}
