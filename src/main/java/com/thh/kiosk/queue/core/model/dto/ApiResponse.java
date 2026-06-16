package com.thh.kiosk.queue.core.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thh.kiosk.queue.core.util.MessageUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> extends BaseApiResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static ApiResponse<Void> success() {
        return ApiResponse.<Void>builder()
                .message(
                        MessageUtils.getMessage("system.success")
                )
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .message(
                        MessageUtils.getMessage("system.success")
                )
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

}
