package com.maxi.gestortorneos.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String mensaje,
        Map<String, String> campos
) {
    public static ErrorResponse de(HttpStatus estado, String mensaje) {
        return new ErrorResponse(
                Instant.now(),
                estado.value(),
                estado.getReasonPhrase(),
                mensaje,
                Map.of()
        );
    }
}