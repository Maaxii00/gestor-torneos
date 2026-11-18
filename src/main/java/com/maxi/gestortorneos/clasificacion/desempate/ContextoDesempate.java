package com.maxi.gestortorneos.clasificacion.desempate;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;
import com.maxi.gestortorneos.model.EstadoSerie;
import com.maxi.gestortorneos.model.Serie;

import java.util.List;
import java.util.Set;

public record ContextoDesempate(
        List<Serie> series,
        List<FilaClasificacion> tabla,
        Set<Long> empatados) {

    /** Las series terminadas en las que juegan dos equipos del grupo empatado. */
    public List<Serie> seriesEntreEmpatados() {
        return series.stream()
                .filter(serie -> serie.getEstado() == EstadoSerie.FINALIZADA)
                .filter(serie -> empatados.contains(serie.getEquipoA().getId())
                        && empatados.contains(serie.getEquipoB().getId()))
                .toList();
    }
}