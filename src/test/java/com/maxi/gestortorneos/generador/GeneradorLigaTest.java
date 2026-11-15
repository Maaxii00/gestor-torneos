package com.maxi.gestortorneos.generador;

import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.model.Equipo;
import com.maxi.gestortorneos.model.Rol;
import com.maxi.gestortorneos.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneradorLigaTest {

    private final GeneradorLiga generador = new GeneradorLiga();

    private List<Equipo> equipos(int cuantos) {
        Usuario capitan = new Usuario("c@test.com", "hash", "Capitán", Rol.USUARIO);
        List<Equipo> lista = new ArrayList<>();
        for (int i = 1; i <= cuantos; i++) {
            lista.add(new Equipo("Equipo " + i, "E" + i, capitan));
        }
        return lista;
    }

    @Test
    @DisplayName("Con 4 equipos genera 3 jornadas de 2 series")
    void conNumeroParDeEquipos() {
        List<List<Emparejamiento>> jornadas = generador.generar(equipos(4));

        assertThat(jornadas).hasSize(3);
        assertThat(jornadas).allSatisfy(jornada -> assertThat(jornada).hasSize(2));
    }

    @Test
    @DisplayName("Con 5 equipos genera 5 jornadas de 2 series, porque uno descansa")
    void conNumeroImparDeEquipos() {
        List<List<Emparejamiento>> jornadas = generador.generar(equipos(5));

        assertThat(jornadas).hasSize(5);
        assertThat(jornadas).allSatisfy(jornada -> assertThat(jornada).hasSize(2));
    }

    @Test
    @DisplayName("Cada pareja de equipos se enfrenta exactamente una vez")
    void cadaParejaSeEnfrentaUnaVez() {
        List<List<Emparejamiento>> jornadas = generador.generar(equipos(6));

        List<Set<String>> parejas = jornadas.stream()
                .flatMap(List::stream)
                .map(e -> Set.of(e.equipoA().getNombre(), e.equipoB().getNombre()))
                .toList();

        assertThat(parejas).hasSize(15);          // 6 × 5 / 2
        assertThat(parejas).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Ningún equipo juega dos veces en la misma jornada")
    void ningunEquipoRepiteEnUnaJornada() {
        List<List<Emparejamiento>> jornadas = generador.generar(equipos(6));

        for (List<Emparejamiento> jornada : jornadas) {
            List<String> queJuegan = jornada.stream()
                    .flatMap(e -> Stream.of(e.equipoA().getNombre(), e.equipoB().getNombre()))
                    .toList();

            assertThat(queJuegan).doesNotHaveDuplicates();
        }
    }

    @Test
    @DisplayName("Lanza conflicto si hay menos de dos equipos")
    void conMenosDeDosEquipos() {
        assertThatThrownBy(() -> generador.generar(equipos(1)))
                .isInstanceOf(ConflictoException.class);
    }
}