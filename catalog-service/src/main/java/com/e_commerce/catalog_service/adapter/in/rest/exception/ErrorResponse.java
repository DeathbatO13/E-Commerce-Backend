package com.e_commerce.catalog_service.adapter.in.rest.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, LocalDateTime.now());
    }
}
