package com.maxi.gestortorneos.model;

import com.maxi.gestortorneos.exception.ConflictoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SerieTest {

    private Equipo equipoA;
    private Equipo equipoB;
    private Serie serie;

    private Equipo equipo(Long id, String nombre, String tag, Usuario capitan) {
        Equipo equipo = new Equipo(nombre, tag, capitan);
        ReflectionTestUtils.setField(equipo, "id", id);
        return equipo;
    }

    @BeforeEach
    void prepararSerieBo3() {
        Usuario usuario = new Usuario("org@test.com", "hash", "Organizador", Rol.USUARIO);

        equipoA = equipo(10L, "G2 Esports", "G2", usuario);
        equipoB = equipo(20L, "Fnatic", "FNC", usuario);

        Torneo torneo = new Torneo("LEC", FormatoTorneo.LIGA_CLASICA, usuario);
        Fase fase = new Fase(torneo, "Liguilla", TipoFase.LIGA, 1);
        Ronda ronda = new Ronda(fase, 1, "Jornada 1", 2);    // 2 victorias = Bo3

        serie = new Serie(ronda, equipoA, equipoB);
    }

    @Test
    @DisplayName("La primera victoria deja la serie 1-0 y en curso")
    void primeraVictoria() {
        serie.registrarVictoriaDe(equipoA);

        assertThat(serie.getVictoriasEquipoA()).isEqualTo(1);
        assertThat(serie.getVictoriasEquipoB()).isZero();
        assertThat(serie.getEstado()).isEqualTo(EstadoSerie.EN_CURSO);
        assertThat(serie.getGanador()).isNull();
    }

    @Test
    @DisplayName("Dos victorias de A cierran el Bo3 con A como ganador")
    void dosVictoriasDeACierranLaSerie() {
        serie.registrarVictoriaDe(equipoA);
        serie.registrarVictoriaDe(equipoA);

        assertThat(serie.getVictoriasEquipoA()).isEqualTo(2);
        assertThat(serie.getVictoriasEquipoB()).isZero();
        assertThat(serie.getEstado()).isEqualTo(EstadoSerie.FINALIZADA);
        assertThat(serie.getGanador()).isSameAs(equipoA);
    }

    @Test
    @DisplayName("Yendo 1-1, la victoria de B cierra la serie con B como ganador")
    void remontadaDeB() {
        serie.registrarVictoriaDe(equipoA);
        serie.registrarVictoriaDe(equipoB);
        serie.registrarVictoriaDe(equipoB);

        assertThat(serie.getVictoriasEquipoA()).isEqualTo(1);
        assertThat(serie.getVictoriasEquipoB()).isEqualTo(2);
        assertThat(serie.getEstado()).isEqualTo(EstadoSerie.FINALIZADA);
        assertThat(serie.getGanador()).isSameAs(equipoB);
    }

    @Test
    @DisplayName("No se puede registrar una victoria en una serie ya cerrada")
    void serieYaCerrada() {
        serie.registrarVictoriaDe(equipoA);
        serie.registrarVictoriaDe(equipoA);          // 2-0, cerrada

        assertThatThrownBy(() -> serie.registrarVictoriaDe(equipoB))
                .isInstanceOf(ConflictoException.class);

        assertThat(serie.getVictoriasEquipoB()).isZero();
        assertThat(serie.getGanador()).isSameAs(equipoA);
    }

    @Test
    @DisplayName("No se puede registrar la victoria de un equipo que no juega la serie")
    void equipoAjenoALaSerie() {
        Usuario otro = new Usuario("otro@test.com", "hash", "Otro", Rol.USUARIO);
        Equipo intruso = equipo(99L, "Team Heretics", "TH", otro);

        assertThatThrownBy(() -> serie.registrarVictoriaDe(intruso))
                .isInstanceOf(ConflictoException.class);

        assertThat(serie.getEstado()).isEqualTo(EstadoSerie.PENDIENTE);
        assertThat(serie.getVictoriasEquipoA()).isZero();
        assertThat(serie.getVictoriasEquipoB()).isZero();
    }
}