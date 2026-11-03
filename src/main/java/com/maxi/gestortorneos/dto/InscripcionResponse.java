package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.Inscripcion;

import java.time.Instant;

public record InscripcionResponse(
        Long id,
        EquipoResumen equipo,
        TorneoResumen torneo,
        Instant inscritoEn
) {
    public static InscripcionResponse desde(Inscripcion inscripcion) {
        return new InscripcionResponse(
                inscripcion.getId(),
                EquipoResumen.desde(inscripcion.getEquipo()),
                TorneoResumen.desde(inscripcion.getTorneo()),
                inscripcion.getInscritoEn()
        );
    }
}
