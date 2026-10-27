package com.maxi.gestortorneos.dto;
import com.maxi.gestortorneos.model.FormatoTorneo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearTorneoRequest(
        @NotBlank @Size(max = 120) String nombre,
        @NotNull FormatoTorneo formato,
        @NotNull Long organizadorId
) {}