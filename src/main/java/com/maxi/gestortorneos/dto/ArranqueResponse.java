package com.maxi.gestortorneos.dto;

public record ArranqueResponse(
        TorneoResponse torneo,
        int jornadasGeneradas,
        int seriesGeneradas) {
}