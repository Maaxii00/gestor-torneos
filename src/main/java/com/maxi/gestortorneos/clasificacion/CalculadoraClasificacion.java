package com.maxi.gestortorneos.clasificacion;

import com.maxi.gestortorneos.model.Equipo;
import com.maxi.gestortorneos.model.EstadoSerie;
import com.maxi.gestortorneos.model.Serie;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CalculadoraClasificacion {

    public List<FilaClasificacion> calcular(List<Equipo> equipos, List<Serie> series) {

        Map<Long, Acumulador> acumuladores = new HashMap<>();
        for (Equipo equipo : equipos) {
            acumuladores.put(equipo.getId(), new Acumulador());
        }

        for (Serie serie : series) {
            if (serie.getEstado() != EstadoSerie.FINALIZADA) {
                continue;
            }

            Acumulador acumuladorA = acumuladores.get(serie.getEquipoA().getId());
            Acumulador acumuladorB = acumuladores.get(serie.getEquipoB().getId());

            acumuladorA.partidasGanadas  += serie.getVictoriasEquipoA();
            acumuladorA.partidasPerdidas += serie.getVictoriasEquipoB();
            acumuladorB.partidasGanadas  += serie.getVictoriasEquipoB();
            acumuladorB.partidasPerdidas += serie.getVictoriasEquipoA();

            if (serie.getGanador().getId().equals(serie.getEquipoA().getId())) {
                acumuladorA.seriesGanadas++;
                acumuladorB.seriesPerdidas++;
            } else {
                acumuladorB.seriesGanadas++;
                acumuladorA.seriesPerdidas++;
            }
        }

        return equipos.stream()
                .map(equipo -> {
                    Acumulador acc = acumuladores.get(equipo.getId());
                    return new FilaClasificacion(
                            equipo,
                            acc.seriesGanadas + acc.seriesPerdidas,
                            acc.seriesGanadas,
                            acc.seriesPerdidas,
                            acc.partidasGanadas,
                            acc.partidasPerdidas);
                })
                .sorted(Comparator
                        .comparingInt(FilaClasificacion::seriesGanadas).reversed()
                        .thenComparing(fila -> fila.equipo().getNombre()))
                .toList();
    }

    private static class Acumulador {
        int seriesGanadas;
        int seriesPerdidas;
        int partidasGanadas;
        int partidasPerdidas;
    }
}