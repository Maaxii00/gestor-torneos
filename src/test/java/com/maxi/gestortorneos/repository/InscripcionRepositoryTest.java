package com.maxi.gestortorneos.repository;

import com.maxi.gestortorneos.model.*;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InscripcionRepositoryTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EquipoRepository equipoRepository;
    @Autowired private TorneoRepository torneoRepository;
    @Autowired private InscripcionRepository inscripcionRepository;
    @Autowired private EntityManager entityManager;

    @Test
    @DisplayName("Trae las inscripciones del torneo con el equipo ya cargado")
    void traeInscripcionesConEquipoCargado() {
        Usuario org = usuarioRepository.save(
                new Usuario("org@test.com", "hash", "Org", Rol.USUARIO));
        Equipo kc = equipoRepository.save(new Equipo("Karmine Corp", "KC", org));
        Equipo g2 = equipoRepository.save(new Equipo("G2 Esports", "G2", org));
        Torneo lec  = torneoRepository.save(new Torneo("LEC", FormatoTorneo.LIGA_CLASICA, org));
        Torneo otro = torneoRepository.save(new Torneo("Otro", FormatoTorneo.LIGA_CLASICA, org));

        inscripcionRepository.save(new Inscripcion(kc, lec));
        inscripcionRepository.save(new Inscripcion(g2, lec));
        inscripcionRepository.save(new Inscripcion(kc, otro));

        // Vaciar la memoria de Hibernate para que todo venga de la base de datos
        entityManager.flush();
        entityManager.clear();

        List<Inscripcion> resultado =
                inscripcionRepository.findConEquipoYTorneoPorTorneoId(lec.getId());
        assertThat(resultado)
                .allSatisfy(i -> assertThat(Hibernate.isInitialized(i.getEquipo())).isTrue());
        assertThat(resultado)
                .extracting(i -> i.getEquipo().getTag())
                .containsExactlyInAnyOrder("KC", "G2");
    }
}