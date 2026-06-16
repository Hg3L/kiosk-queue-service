package com.thh.kiosk.queue.core.model.dto;

import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SliceResponse<T> {

    List<T> content;
    int currentPage;
    int pageSize;
    boolean hasNext;
    boolean hasPrevious;

    public static <E, T> SliceResponse<T> of(Slice<E> slice, Function<E, T> mapper) {
        return SliceResponse.<T>builder()
                .content(
                        slice.getContent().stream().map(mapper).collect(Collectors.toList())
                )
                .currentPage(slice.getNumber() + 1)
                .pageSize(slice.getSize())
                .hasNext(slice.hasNext())
                .hasPrevious(slice.hasPrevious())
                .build();
    }
}
