package com.thh.kiosk.queue.core.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class BaseSearchRequest {
    private int page = 1;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDir = "desc";

    public Pageable getPageable() {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(this.page - 1, this.size, sort);
    }
}
