package com.maxi.gestortorneos.clasificacion.desempate;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;
import com.maxi.gestortorneos.model.EstadoSerie;
import com.maxi.gestortorneos.model.Serie;

public class StrengthOfVictory implements CriterioDesempate {

    @Override
    public String nombre() {
        return "Strength of Victory";
    }

    @Override
    public int puntuar(FilaClasificacion fila, ContextoDesempate contexto) {
        Long equipoId = fila.equipo().getId();
        int puntuacion = 0;

        for (Serie serie : contexto.series()) {
            if (serie.getEstado() != EstadoSerie.FINALIZADA) {
                continue;
            }
            if (!serie.getGanador().getId().equals(equipoId)) {
                continue;
            }

            Long rivalId = serie.getEquipoA().getId().equals(equipoId)
                    ? serie.getEquipoB().getId()
                    : serie.getEquipoA().getId();

            puntuacion += seriesGanadasDe(rivalId, contexto);
        }
        return puntuacion;
    }

    private int seriesGanadasDe(Long equipoId, ContextoDesempate contexto) {
        return contexto.tabla().stream()
                .filter(f -> f.equipo().getId().equals(equipoId))
                .findFirst()
                .map(FilaClasificacion::seriesGanadas)
                .orElse(0);
    }
}