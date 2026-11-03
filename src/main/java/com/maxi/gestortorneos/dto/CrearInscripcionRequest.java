package com.maxi.gestortorneos.dto;
import jakarta.validation.constraints.NotNull;

public record CrearInscripcionRequest(
        @NotNull Long torneoId,
        @NotNull Long equipoId
) {}
