package com.thh.kiosk.queue.core.model.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseDataResponse {

    String id;

    String status;

    Instant createdAt;

    Instant updatedAt;

    String createdBy;

    String updatedBy;
}
