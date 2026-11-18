package com.maxi.gestortorneos.clasificacion.desempate;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;
import com.maxi.gestortorneos.model.Serie;

public class SeriesGanadasEntreEmpatados implements CriterioDesempate {

    @Override
    public String nombre() {
        return "Enfrentamiento directo";
    }

    @Override
    public int puntuar(FilaClasificacion fila, ContextoDesempate contexto) {
        Long equipoId = fila.equipo().getId();
        int ganadas = 0;

        for (Serie serie : contexto.seriesEntreEmpatados()) {
            if (serie.getGanador().getId().equals(equipoId)) {
                ganadas++;
            }
        }
        return ganadas;
    }
}