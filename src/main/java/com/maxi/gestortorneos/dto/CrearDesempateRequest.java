package com.maxi.gestortorneos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CrearDesempateRequest(
        @NotNull @Min(1) @Max(3) Integer victoriasParaGanar) {}
