package com.maxi.gestortorneos.clasificacion.desempate;

import com.maxi.gestortorneos.clasificacion.CalculadoraClasificacion;
import com.maxi.gestortorneos.clasificacion.FilaClasificacion;
import com.maxi.gestortorneos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResolutorDeEmpatesTest {

    private final CalculadoraClasificacion calculadora = new CalculadoraClasificacion();

    private final ResolutorDeEmpates resolutor = new ResolutorDeEmpates(List.of(
            new SeriesGanadasEntreEmpatados(),
            new DiferenciaDePartidasEntreEmpatados(),
            new DiferenciaDePartidasGlobal(),
            new StrengthOfVictory()));

    private Ronda ronda;
    private Equipo a;
    private Equipo b;
    private Equipo c;
    private Equipo d;

    @BeforeEach
    void preparar() {
        Usuario usuario = new Usuario("org@test.com", "hash", "Organizador", Rol.USUARIO);

        a = equipo(1L, "Alfa", "ALF", usuario);
        b = equipo(2L, "Bravo", "BRA", usuario);
        c = equipo(3L, "Charlie", "CHA", usuario);
        d = equipo(4L, "Delta", "DEL", usuario);

        Torneo torneo = new Torneo("Liga", FormatoTorneo.LIGA_CLASICA, usuario);
        Fase fase = new Fase(torneo, "Liguilla", TipoFase.LIGA, 1);
        ronda = new Ronda(fase, 1, "Jornada 1", 2);
    }

    private Equipo equipo(Long id, String nombre, String tag, Usuario capitan) {
        Equipo equipo = new Equipo(nombre, tag, capitan);
        ReflectionTestUtils.setField(equipo, "id", id);
        return equipo;
    }

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

    private List<List<FilaClasificacion>> clasificar(List<Equipo> equipos, List<Serie> series) {
        return resolutor.resolver(calculadora.calcular(equipos, series), series);
    }

    private List<String> tags(List<FilaClasificacion> grupo) {
        return grupo.stream().map(fila -> fila.equipo().getTag()).toList();
    }

    @Test
    @DisplayName("Sin empates, manda el número de series ganadas")
    void sinEmpates() {
        List<Serie> series = List.of(
                serieGanadaPor(a, b, 0),
                serieGanadaPor(a, c, 0),
                serieGanadaPor(b, c, 0));

        List<List<FilaClasificacion>> puestos = clasificar(List.of(a, b, c), series);

        assertThat(puestos).hasSize(3);
        assertThat(tags(puestos.get(0))).containsExactly("ALF");
        assertThat(tags(puestos.get(1))).containsExactly("BRA");
        assertThat(tags(puestos.get(2))).containsExactly("CHA");
    }

    @Test
    @DisplayName("Con dos empatados decide el enfrentamiento directo")
    void dosEmpatadosDecideElDirecto() {
        List<Serie> series = List.of(
                serieGanadaPor(b, a, 0),
                serieGanadaPor(a, c, 0),
                serieGanadaPor(b, c, 0));

        List<List<FilaClasificacion>> puestos = clasificar(List.of(a, b, c), series);
        assertThat(tags(puestos.get(0))).containsExactly("BRA");
        assertThat(tags(puestos.get(1))).containsExactly("ALF");
    }

    @Test
    @DisplayName("Con tres empatados en círculo, se reinicia el desempate al quedar dos")
    void tresEmpatadosEnCirculo() {
        List<Serie> series = List.of(
                serieGanadaPor(a, b, 1),
                serieGanadaPor(b, c, 1),
                serieGanadaPor(c, a, 1),
                serieGanadaPor(a, d, 0),
                serieGanadaPor(b, d, 1),
                serieGanadaPor(c, d, 1));

        List<List<FilaClasificacion>> puestos = clasificar(List.of(a, b, c, d), series);

        assertThat(puestos).hasSize(4);
        assertThat(tags(puestos.get(0))).containsExactly("ALF");
        assertThat(tags(puestos.get(1))).containsExactly("BRA");
        assertThat(tags(puestos.get(2))).containsExactly("CHA");
        assertThat(tags(puestos.get(3))).containsExactly("DEL");
    }

    @Test
    @DisplayName("Dos equipos idénticos que nunca se enfrentaron comparten puesto")
    void empateSinResolver() {
        List<Serie> series = List.of(
                serieGanadaPor(a, c, 0),
                serieGanadaPor(a, d, 0),
                serieGanadaPor(b, c, 0),
                serieGanadaPor(b, d, 0));

        List<List<FilaClasificacion>> puestos = clasificar(List.of(a, b, c, d), series);

        assertThat(puestos.get(0)).hasSize(2);
        assertThat(tags(puestos.get(0))).containsExactlyInAnyOrder("ALF", "BRA");
    }
}