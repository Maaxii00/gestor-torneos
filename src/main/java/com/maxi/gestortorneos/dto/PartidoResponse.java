package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.Partido;

import java.time.Instant;

public record PartidoResponse(
        Long id,
        int numero,
        EquipoResumen ganador,
        Instant jugadoEn,
        SerieResumen serie) {

    public static PartidoResponse desde(Partido partido) {
        return new PartidoResponse(
                partido.getId(),
                partido.getNumero(),
                EquipoResumen.desde(partido.getGanador()),
                partido.getJugadoEn(),
                SerieResumen.desde(partido.getSerie()));
    }
}