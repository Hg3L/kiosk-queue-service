package com.thh.kiosk.queue.core.exception;


import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    VALIDATION_FAILED(9998, HttpStatus.BAD_REQUEST, "system.error.validation"),
    UNKNOWN_ERROR(9999, HttpStatus.INTERNAL_SERVER_ERROR, "system.internal_error"),

    /*
    * Counter 1000 -> 1019
    * */
    COUNTER_NOT_FOUND(1000, HttpStatus.BAD_REQUEST,"counter.error.not_found"),
    COUNTER_PREFIX_EXISTS(1001, HttpStatus.BAD_REQUEST, "counter.prefix_exists"),
    COUNTER_ALREADY_SELECTED(1002, HttpStatus.BAD_REQUEST, "counter.error.already_selected"),
    COUNTER_IN_USE(1003, HttpStatus.BAD_REQUEST, "counter.error.in-use"),
    COUNTER_ALREADY_ASSIGNED(1004, HttpStatus.BAD_REQUEST, "counter.error.already_assigned"),
    COUNTER_ALREADY_IN_SHIFT(1005, HttpStatus.BAD_REQUEST, "counter.error.already_in_shift"),
    /*
    * Ticket 1020 -> 1039
    * */
    TICKET_NOT_FOUND(1020, HttpStatus.BAD_REQUEST, "ticket.error.ticket_not_found"),

    /*
     * UI Setting 1040 -> 1049
     * */
    SETTING_NOT_FOUND(1040, HttpStatus.BAD_REQUEST, "setting.error.not_found"),
    SETTING_INVALID(1040, HttpStatus.BAD_REQUEST, "setting.error.invalid"),
    /*
    * File/folder/path & export 1050 -> 1069
    * */
    FILE_NOT_FOUND(1040, HttpStatus.BAD_REQUEST, "file.error.not_found"),
    FILE_FORMAT_UNSUPPORTED(1041, HttpStatus.BAD_REQUEST, "file.error.format_unsupported"),
    FILE_TOO_LARGE(1042, HttpStatus.BAD_REQUEST, "file.error.too_large"),
    EXPORT_PATH_NOT_FOUND(1043, HttpStatus.BAD_REQUEST, "export_path.error.not_found"),
    REPORT_FILE_LOCKED(1044, HttpStatus.BAD_REQUEST, "report_file.error.locked"),
    EXPORT_REPORT_FAILED(1045, HttpStatus.INTERNAL_SERVER_ERROR, "export_report.error.failed"),
    /*
     * Shift 1070 -> 1079
     * */
    SHIFT_NOT_FOUND(1070, HttpStatus.BAD_REQUEST, "shift.error.not_found"),
    SHIFT_INVALID_TIME_RANGE(1071, HttpStatus.BAD_REQUEST, "shift.error.time_range_invalid"),
    SHIFT_OVERLAP(1072, HttpStatus.BAD_REQUEST, "shift.error.overlap"),
    SHIFT_IN_USE(1073, HttpStatus.BAD_REQUEST, "shift.error.in_use"),
    SHIFT_CONFLICT_RESET_TIME(1074,  HttpStatus.BAD_REQUEST, "shift.error.conflict_reset_time"),
    SHIFT_OVER(1075, HttpStatus.BAD_REQUEST, "shift.error.over"),
    /*
    * Reset time 1080 -> 1089
    * */
    CONFIG_CHANGE_LIMIT_EXCEEDED(1080, HttpStatus.BAD_REQUEST, "config.change.limit_exceeded"),
    CONFIG_CONFLICT_SHIFT(1081,  HttpStatus.BAD_REQUEST, "config.conflict_shift"),
    ;
    private final int code;

    private final HttpStatus httpStatus;

    private final String message;
}
