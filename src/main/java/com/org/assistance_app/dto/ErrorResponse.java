package com.org.assistance_app.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String errorCode,
        String path,
        String traceId,
        Map<String, String> validationErrors
) {
    public static ErrorResponse of(
            int status,
            String error,
            String message,
            String errorCode,
            String path,
            String traceId
    ) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                errorCode,
                path,
                traceId,
                null
        );
    }
}