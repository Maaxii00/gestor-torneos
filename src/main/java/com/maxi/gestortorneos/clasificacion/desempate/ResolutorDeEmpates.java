package com.maxi.gestortorneos.clasificacion.desempate;

import com.maxi.gestortorneos.clasificacion.FilaClasificacion;
import com.maxi.gestortorneos.model.Serie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class ResolutorDeEmpates {

    private final List<CriterioDesempate> criterios;

    public ResolutorDeEmpates(List<CriterioDesempate> criterios) {
        this.criterios = List.copyOf(criterios);
    }

    /**
     * Ordena la tabla y devuelve los equipos agrupados por puesto.
     * Un grupo de más de uno es un empate que ningún criterio pudo romper.
     */
    public List<List<FilaClasificacion>> resolver(List<FilaClasificacion> tabla,
                                                  List<Serie> series) {
        List<List<FilaClasificacion>> resultado = new ArrayList<>();

        for (List<FilaClasificacion> grupo : agruparPor(tabla, FilaClasificacion::seriesGanadas)) {
            resultado.addAll(desempatar(grupo, tabla, series));
        }
        return resultado;
    }

    private List<List<FilaClasificacion>> desempatar(List<FilaClasificacion> grupo,
                                                     List<FilaClasificacion> tabla,
                                                     List<Serie> series) {
        if (grupo.size() == 1) {
            return List.of(grupo);
        }

        Set<Long> empatados = grupo.stream()
                .map(fila -> fila.equipo().getId())
                .collect(Collectors.toSet());

        ContextoDesempate contexto = new ContextoDesempate(series, tabla, empatados);

        for (CriterioDesempate criterio : criterios) {

            List<List<FilaClasificacion>> particiones =
                    agruparPor(grupo, fila -> criterio.puntuar(fila, contexto));

            if (particiones.size() == 1) {
                continue;
            }

            List<List<FilaClasificacion>> resueltos = new ArrayList<>();
            for (List<FilaClasificacion> particion : particiones) {
                resueltos.addAll(desempatar(particion, tabla, series));
            }
            return resueltos;
        }

        return List.of(grupo);                  // empate sin resolver
    }

    /** Agrupa por una puntuación, de mayor a menor, conservando ese orden. */
    private List<List<FilaClasificacion>> agruparPor(List<FilaClasificacion> filas,
                                                     ToIntFunction<FilaClasificacion> puntuacion) {
        Map<Integer, List<FilaClasificacion>> porPuntuacion = new LinkedHashMap<>();

        filas.stream()
                .sorted(Comparator.comparingInt(puntuacion).reversed())
                .forEach(fila -> porPuntuacion
                        .computeIfAbsent(puntuacion.applyAsInt(fila), clave -> new ArrayList<>())
                        .add(fila));

        return new ArrayList<>(porPuntuacion.values());
    }
}