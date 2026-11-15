package com.maxi.gestortorneos.clasificacion;

import com.maxi.gestortorneos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraClasificacionTest {

    private final CalculadoraClasificacion calculadora = new CalculadoraClasificacion();

    private Ronda ronda;
    private Equipo a;
    private Equipo b;
    private Equipo c;

    @BeforeEach
    void preparar() {
        Usuario usuario = new Usuario("org@test.com", "hash", "Organizador", Rol.USUARIO);

        a = equipo(1L, "Alfa", "ALF", usuario);
        b = equipo(2L, "Bravo", "BRA", usuario);
        c = equipo(3L, "Charlie", "CHA", usuario);

        Torneo torneo = new Torneo("Liga", FormatoTorneo.LIGA_CLASICA, usuario);
        Fase fase = new Fase(torneo, "Liguilla", TipoFase.LIGA, 1);
        ronda = new Ronda(fase, 1, "Jornada 1", 2);          // Bo3
    }

    private Equipo equipo(Long id, String nombre, String tag, Usuario capitan) {
        Equipo equipo = new Equipo(nombre, tag, capitan);
        ReflectionTestUtils.setField(equipo, "id", id);
        return equipo;
    }

    /** Juega una serie entera: el perdedor gana sus partidas y luego el ganador cierra a 2. */
    private Serie serieGanadaPor(Equipo ganador, Equipo perdedor, int partidasDelPerdedor) {
        Serie serie = new Serie(ronda, ganador, perdedor);
        for (int i = 0; i < partidasDelPerdedor; i++) {
            serie.registrarVictoriaDe(perdedor);
        }
        for (int i = 0; i < 2; i++) {
            serie.registrarVictoriaDe(ganador);
        }
        return serie;
    }

    private FilaClasificacion filaDe(List<FilaClasificacion> tabla, Equipo equipo) {
        return tabla.stream()
                .filter(fila -> fila.equipo().getId().equals(equipo.getId()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    @DisplayName("Sin series jugadas, todos los equipos aparecen a cero")
    void sinSeriesJugadas() {
        List<FilaClasificacion> tabla = calculadora.calcular(List.of(a, b, c), List.of());

        assertThat(tabla).hasSize(3);
        assertThat(tabla).allSatisfy(fila -> {
            assertThat(fila.seriesJugadas()).isZero();
            assertThat(fila.seriesGanadas()).isZero();
            assertThat(fila.partidasGanadas()).isZero();
            assertThat(fila.diferenciaDePartidas()).isZero();
        });
    }

    @Test
    @DisplayName("Una serie 2-0 cuenta para los dos equipos")
    void unaSerieCuentaParaLosDos() {
        Serie serie = serieGanadaPor(a, b, 0);

        List<FilaClasificacion> tabla = calculadora.calcular(List.of(a, b), List.of(serie));

        FilaClasificacion filaA = filaDe(tabla, a);
        assertThat(filaA.seriesJugadas()).isEqualTo(1);
        assertThat(filaA.seriesGanadas()).isEqualTo(1);
        assertThat(filaA.seriesPerdidas()).isZero();
        assertThat(filaA.partidasGanadas()).isEqualTo(2);
        assertThat(filaA.partidasPerdidas()).isZero();
        assertThat(filaA.diferenciaDePartidas()).isEqualTo(2);

        FilaClasificacion filaB = filaDe(tabla, b);
        assertThat(filaB.seriesGanadas()).isZero();
        assertThat(filaB.seriesPerdidas()).isEqualTo(1);
        assertThat(filaB.partidasGanadas()).isZero();
        assertThat(filaB.partidasPerdidas()).isEqualTo(2);
        assertThat(filaB.diferenciaDePartidas()).isEqualTo(-2);
    }

    @Test
    @DisplayName("Las series sin terminar no cuentan")
    void seriesSinTerminarNoCuentan() {
        Serie enCurso = new Serie(ronda, a, b);
        enCurso.registrarVictoriaDe(a);                  // 1-0, EN_CURSO

        List<FilaClasificacion> tabla = calculadora.calcular(List.of(a, b), List.of(enCurso));

        assertThat(filaDe(tabla, a).seriesJugadas()).isZero();
        assertThat(filaDe(tabla, a).partidasGanadas()).isZero();
    }

    @Test
    @DisplayName("Las partidas se suman a lo largo de varias series")
    void sumaDeVariasSeries() {
        List<Serie> series = List.of(
                serieGanadaPor(a, b, 1),      // Alfa gana 2-1
                serieGanadaPor(c, a, 1));     // Alfa pierde 1-2

        List<FilaClasificacion> tabla = calculadora.calcular(List.of(a, b, c), series);

        FilaClasificacion filaA = filaDe(tabla, a);
        assertThat(filaA.seriesJugadas()).isEqualTo(2);
        assertThat(filaA.seriesGanadas()).isEqualTo(1);
        assertThat(filaA.seriesPerdidas()).isEqualTo(1);
        assertThat(filaA.partidasGanadas()).isEqualTo(3);
        assertThat(filaA.partidasPerdidas()).isEqualTo(3);
        assertThat(filaA.diferenciaDePartidas()).isZero();
    }

    @Test
    @DisplayName("La tabla se ordena por series ganadas, de mayor a menor")
    void ordenPorSeriesGanadas() {
        List<Serie> series = List.of(
                serieGanadaPor(a, b, 0),
                serieGanadaPor(a, c, 1),
                serieGanadaPor(b, c, 0));

        List<FilaClasificacion> tabla = calculadora.calcular(List.of(a, b, c), series);

        assertThat(tabla)
                .extracting(fila -> fila.equipo().getTag())
                .containsExactly("ALF", "BRA", "CHA");
    }
}