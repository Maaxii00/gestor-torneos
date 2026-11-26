package com.maxi.gestortorneos.repository;

import com.maxi.gestortorneos.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SerieRepositoryTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EquipoRepository equipoRepository;
    @Autowired private TorneoRepository torneoRepository;
    @Autowired private FaseRepository faseRepository;
    @Autowired private RondaRepository rondaRepository;
    @Autowired private SerieRepository serieRepository;

    @Test
    @DisplayName("Solo devuelve las series de la fase pedida y del torneo pedido")
    void soloDevuelveSeriesDeLaFaseYTorneoPedidos() {
        Usuario org = usuarioRepository.save(
                new Usuario("org@test.com", "hash", "Org", Rol.USUARIO));
        Equipo kc  = equipoRepository.save(new Equipo("Karmine Corp", "KC", org));
        Equipo g2  = equipoRepository.save(new Equipo("G2 Esports", "G2", org));

        Torneo lec  = torneoRepository.save(new Torneo("LEC", FormatoTorneo.LIGA_CLASICA, org));
        Torneo otro = torneoRepository.save(new Torneo("Otro", FormatoTorneo.LIGA_CLASICA, org));
        Serie deLiga = crearSerie(lec, TipoFase.LIGA, 1, kc, g2);
        crearSerie(lec, TipoFase.ELIMINATORIA, 2, kc, g2);
        crearSerie(otro, TipoFase.LIGA, 1, kc, g2);

        List<Serie> resultado =
                serieRepository.findConEquiposPorTorneoIdYTipoFase(lec.getId(), TipoFase.LIGA);

        assertThat(resultado)
                .extracting(Serie::getId)
                .containsExactly(deLiga.getId());
    }

    @Test
    @DisplayName("El calendario sale ordenado por fase, jornada y serie")
    void calendarioOrdenado() {
        Usuario org = usuarioRepository.save(new Usuario("org@test.com", "hash", "Org", Rol.USUARIO));
        Equipo kc = equipoRepository.save(new Equipo("Karmine Corp", "KC", org));
        Equipo g2 = equipoRepository.save(new Equipo("G2 Esports", "G2", org));
        Torneo lec = torneoRepository.save(new Torneo("LEC", FormatoTorneo.LIGA_CLASICA, org));

        Fase liga = faseRepository.save(new Fase(lec, "Liguilla", TipoFase.LIGA, 1));
        Ronda jornada2 = rondaRepository.save(new Ronda(liga, 2, "Jornada 2", 2));
        Ronda jornada1 = rondaRepository.save(new Ronda(liga, 1, "Jornada 1", 2));

        Serie deLaJornada2 = serieRepository.save(new Serie(jornada2, kc, g2));
        Serie deLaJornada1 = serieRepository.save(new Serie(jornada1, g2, kc));

        List<Serie> calendario = serieRepository.findCalendarioPorTorneoId(lec.getId());

        assertThat(calendario)
                .extracting(Serie::getId)
                .containsExactly(deLaJornada1.getId(), deLaJornada2.getId());
    }

    private Serie crearSerie(Torneo torneo, TipoFase tipo, int orden, Equipo a, Equipo b) {
        Fase fase = faseRepository.save(new Fase(torneo, "Fase " + orden, tipo, orden));
        Ronda ronda = rondaRepository.save(new Ronda(fase, 1, "Ronda 1", 2));
        return serieRepository.save(new Serie(ronda, a, b));
    }
}