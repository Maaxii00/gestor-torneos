package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;

public record FilaClasificacionResponse(
        int posicion,
        EquipoResumen equipo,
        int seriesJugadas,
        int seriesGanadas,
        int seriesPerdidas,
        int partidasGanadas,
        int partidasPerdidas,
        int diferenciaDePartidas) {

    public static FilaClasificacionResponse desde(FilaClasificacion fila, int posicion) {
        return new FilaClasificacionResponse(
                posicion,
                EquipoResumen.desde(fila.equipo()),
                fila.seriesJugadas(),
                fila.seriesGanadas(),
                fila.seriesPerdidas(),
                fila.partidasGanadas(),
                fila.partidasPerdidas(),
                fila.diferenciaDePartidas());
    }
}