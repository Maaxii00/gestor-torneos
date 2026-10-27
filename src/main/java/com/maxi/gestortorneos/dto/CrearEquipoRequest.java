package com.maxi.gestortorneos.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearEquipoRequest(
        @NotBlank @Size(max = 60) String nombre,
        @NotBlank @Size(min = 2, max = 5) String tag,
        @NotNull Long capitanId
) {}