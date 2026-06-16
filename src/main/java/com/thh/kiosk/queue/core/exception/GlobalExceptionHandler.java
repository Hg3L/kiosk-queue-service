package com.thh.kiosk.queue.core.exception;

import com.thh.kiosk.queue.core.model.dto.ErrorApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorApiResponse> handleBusinessException(
            BusinessException ex,
            WebRequest request
    ) {
        logException(ex);
        ErrorCode errorCode = ex.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(
                        buildErrorApiResponse(
                                errorCode,
                                ex.getArgs(),
                                request
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorApiResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        logException(ex);

        String errorMessage = messageSource.getMessage(
                ErrorCode.VALIDATION_FAILED.getMessage(),
                null,
                LocaleContextHolder.getLocale()
        );

        if (ex.getBindingResult().hasFieldErrors()) {
            errorMessage = ex.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
        }

        return ResponseEntity
                .status(ErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(
                        buildErrorApiResponse(
                                ErrorCode.VALIDATION_FAILED.getCode(),
                                ErrorCode.VALIDATION_FAILED.getHttpStatus().value(),
                                errorMessage,
                                request
                        )
                );
    }

    @ExceptionHandler(
            {
                    Exception.class,
                    RuntimeException.class
            }
    )
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorApiResponse> handleGeneralException(
            Exception ex,
            WebRequest request
    ) {
        logException(ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        buildErrorApiResponse(
                                ErrorCode.UNKNOWN_ERROR,
                                null,
                                request
                        )
                );
    }

    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorApiResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            WebRequest request
    ) {
        logException(ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        buildErrorApiResponse(
                                ErrorCode.VALIDATION_FAILED,
                                null,
                                request
                        )
                );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorApiResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex,
            WebRequest request
    ) {
        logException(ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        buildErrorApiResponse(
                                ErrorCode.FILE_TOO_LARGE,
                                null,
                                request
                        )
                );
    }

    private void logException(Exception e) {
        log.error(
                "Exception occurred: {} - {}",
                e.getClass().getSimpleName(),
                e.getMessage(),
                e
        );
    }

    private ErrorApiResponse buildErrorApiResponse(
            ErrorCode errorCode,
            Object[] args,
            WebRequest request
    ) {
        String localizedMessage = messageSource.getMessage(
                errorCode.getMessage(),
                args,
                LocaleContextHolder.getLocale()
        );

        return buildErrorApiResponse(
                errorCode.getCode(),
                errorCode.getHttpStatus().value(),
                localizedMessage,
                request
        );
    }

    private ErrorApiResponse buildErrorApiResponse(
            int code,
            int status,
            String message,
            WebRequest request
    ) {
        return ErrorApiResponse.builder()
                .code(code)
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
    }
}