package com.maxi.gestortorneos.clasificacion.desempate;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;

public class DiferenciaDePartidasGlobal implements CriterioDesempate {

    @Override
    public String nombre() {
        return "Diferencia de partidas";
    }

    @Override
    public int puntuar(FilaClasificacion fila, ContextoDesempate contexto) {
        return fila.diferenciaDePartidas();
    }
}