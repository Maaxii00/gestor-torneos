package com.maxi.gestortorneos.generador;

import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.model.Equipo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class GeneradorLiga {

    /**
     * Genera las jornadas de una liguilla: cada equipo se enfrenta
     * una vez a cada uno de los demás, sin repetirse dentro de una jornada.
     *
     * @return una lista de jornadas; cada jornada, su lista de emparejamientos
     */
    public List<List<Emparejamiento>> generar(List<Equipo> equipos) {
        if (equipos.size() < 2) {
            throw new ConflictoException("Debe haber al menos 2 equipos");
        }
        List<Equipo> rotacion = new ArrayList<>(equipos);
        if (rotacion.size() % 2 != 0){
            rotacion.add(null);
        }
        int n = rotacion.size();
        List<List<Emparejamiento>> jornadas = new ArrayList<>();

        for (int vuelta=0; vuelta<n-1;vuelta++) {
            List<Emparejamiento> jornada = new ArrayList<>();
            for (int i = 0; i < n / 2; i++) {
                Equipo equipoA = rotacion.get(i);
                Equipo equipoB = rotacion.get(n-1-i);
                if (equipoA!= null && equipoB!= null){
                    Emparejamiento emparejamiento = new Emparejamiento(equipoA, equipoB);
                    jornada.add(emparejamiento);
                }
            }
            jornadas.add(jornada);
            Equipo ultimo = rotacion.remove(n - 1);
            rotacion.add(1, ultimo);
        }
        return jornadas;
    }
}