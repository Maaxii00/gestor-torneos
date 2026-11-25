package com.maxi.gestortorneos.clasificacion.desempate;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;
import com.maxi.gestortorneos.model.Serie;

public class ResultadoDesempate implements CriterioDesempate {

    @Override
    public String nombre() {
        return "Partido de desempate";
    }

    @Override
    public int puntuar(FilaClasificacion fila, ContextoDesempate contexto) {
        Long equipoId = fila.equipo().getId();
        int ganadas = 0;

        for (Serie serie : contexto.seriesDesempateEntreEmpatados()) {
            if (serie.getGanador().getId().equals(equipoId)) {
                ganadas++;
            }
        }
        return ganadas;
    }
}