package com.maxi.gestortorneos.clasificacion.desempate;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;
import com.maxi.gestortorneos.model.Serie;

public class DiferenciaDePartidasEntreEmpatados implements CriterioDesempate {

    @Override
    public String nombre() {
        return "Diferencia de partidas en el enfrentamiento directo";
    }

    @Override
    public int puntuar(FilaClasificacion fila, ContextoDesempate contexto) {
        Long equipoId = fila.equipo().getId();
        int diferencia = 0;

        for (Serie serie : contexto.seriesEntreEmpatados()) {
            if (serie.getEquipoA().getId().equals(equipoId)) {
                diferencia += serie.getVictoriasEquipoA() - serie.getVictoriasEquipoB();
            } else if (serie.getEquipoB().getId().equals(equipoId)) {
                diferencia += serie.getVictoriasEquipoB() - serie.getVictoriasEquipoA();
            }
        }
        return diferencia;
    }
}