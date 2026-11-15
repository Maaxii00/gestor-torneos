package com.maxi.gestortorneos.clasificacion;

import com.maxi.gestortorneos.model.Equipo;

public record FilaClasificacion(
        Equipo equipo,
        int seriesJugadas,
        int seriesGanadas,
        int seriesPerdidas,
        int partidasGanadas,
        int partidasPerdidas) {

    public int diferenciaDePartidas() {
        return partidasGanadas - partidasPerdidas;
    }
}