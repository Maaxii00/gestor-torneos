package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.EstadoSerie;
import com.maxi.gestortorneos.model.Serie;

public record SerieResumen(
        Long id,
        EquipoResumen equipoA,
        EquipoResumen equipoB,
        int victoriasEquipoA,
        int victoriasEquipoB,
        EstadoSerie estado,
        EquipoResumen ganador) {

    public static SerieResumen desde(Serie serie) {
        return new SerieResumen(
                serie.getId(),
                EquipoResumen.desde(serie.getEquipoA()),
                EquipoResumen.desde(serie.getEquipoB()),
                serie.getVictoriasEquipoA(),
                serie.getVictoriasEquipoB(),
                serie.getEstado(),
                serie.getGanador() == null
                        ? null
                        : EquipoResumen.desde(serie.getGanador()));
    }
}