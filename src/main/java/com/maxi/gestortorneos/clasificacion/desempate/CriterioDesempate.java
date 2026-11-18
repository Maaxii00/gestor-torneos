package com.maxi.gestortorneos.clasificacion.desempate;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;

public interface CriterioDesempate {

    String nombre();

    /** Puntúa a un equipo dentro del grupo empatado. Más alto es mejor. */
    int puntuar(FilaClasificacion fila, ContextoDesempate contexto);
}