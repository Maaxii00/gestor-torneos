package com.maxi.gestortorneos.dto;

import jakarta.validation.constraints.NotNull;

public record RegistrarPartidoRequest(@NotNull Long ganadorId) {}