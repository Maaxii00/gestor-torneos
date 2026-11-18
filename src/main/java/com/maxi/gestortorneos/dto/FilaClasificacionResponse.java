package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;

public record FilaClasificacionResponse(
        int posicion,
        boolean empateSinResolver,
        EquipoResumen equipo,
        int seriesJugadas,
        int seriesGanadas,
        int seriesPerdidas,
        int partidasGanadas,
        int partidasPerdidas,
        int diferenciaDePartidas) {

    public static FilaClasificacionResponse desde(FilaClasificacion fila,
                                                  int posicion,
                                                  boolean empateSinResolver) {
        return new FilaClasificacionResponse(
                posicion,
                empateSinResolver,
                EquipoResumen.desde(fila.equipo()),
                fila.seriesJugadas(),
                fila.seriesGanadas(),
                fila.seriesPerdidas(),
                fila.partidasGanadas(),
                fila.partidasPerdidas(),
                fila.diferenciaDePartidas());
    }
}