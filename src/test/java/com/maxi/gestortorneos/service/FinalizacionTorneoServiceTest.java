package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.TorneoResponse;
import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.exception.OperacionNoPermitidaException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.*;
import com.maxi.gestortorneos.repository.SerieRepository;
import com.maxi.gestortorneos.repository.TorneoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinalizacionTorneoServiceTest {

    private static final Long ORGANIZADOR_ID = 1L;

    @Mock
    private TorneoRepository torneoRepository;

    @Mock
    private SerieRepository serieRepository;

    @Mock
    private ClasificacionService clasificacionService;

    private FinalizacionTorneoService servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new FinalizacionTorneoService(torneoRepository, serieRepository, clasificacionService);
    }

    @Test
    @DisplayName("Lanza 404 cuando el torneo no existe")
    void lanza404CuandoElTorneoNoExiste() {
        when(torneoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.finalizar(999L, ORGANIZADOR_ID))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("Lanza 409 si quedan series por jugar y el torneo sigue EN_CURSO")
    void lanza409SiQuedanSeriesPendientes() {
        Torneo torneo = torneoEnCurso();
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(serieRepository.countByRondaFaseTorneoIdAndEstadoNot(1L, EstadoSerie.FINALIZADA))
                .thenReturn(3L);

        assertThatThrownBy(() -> servicio.finalizar(1L, ORGANIZADOR_ID))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("3");

        assertThat(torneo.getEstado()).isEqualTo(EstadoTorneo.EN_CURSO);
        verify(clasificacionService, never()).gruposSinResolver(any());
    }

    @Test
    @DisplayName("Finaliza el torneo cuando todas las series están jugadas")
    void finalizaCuandoNoQuedanSeries() {
        Torneo torneo = torneoEnCurso();
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(serieRepository.countByRondaFaseTorneoIdAndEstadoNot(1L, EstadoSerie.FINALIZADA))
                .thenReturn(0L);

        TorneoResponse respuesta = servicio.finalizar(1L, ORGANIZADOR_ID);

        assertThat(torneo.getEstado()).isEqualTo(EstadoTorneo.FINALIZADO);
        assertThat(respuesta.estado()).isEqualTo(EstadoTorneo.FINALIZADO);
    }

    @Test
    @DisplayName("Lanza 403 si no lo pide el organizador")
    void lanza403SiNoLoPideElOrganizador() {
        Torneo torneo = torneoEnCurso();
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));

        assertThatThrownBy(() -> servicio.finalizar(1L, 2L))
                .isInstanceOf(OperacionNoPermitidaException.class);

        assertThat(torneo.getEstado()).isEqualTo(EstadoTorneo.EN_CURSO);
        verify(serieRepository, never()).countByRondaFaseTorneoIdAndEstadoNot(any(), any());
    }

    private Torneo torneoEnCurso() {
        Usuario organizador = new Usuario("org@test.com", "hash", "Organizador", Rol.USUARIO);
        ReflectionTestUtils.setField(organizador, "id", ORGANIZADOR_ID);

        Torneo torneo = new Torneo("LEC", FormatoTorneo.LIGA_CLASICA, organizador);
        ReflectionTestUtils.setField(torneo, "id", 1L);
        ReflectionTestUtils.setField(torneo, "estado", EstadoTorneo.EN_CURSO);
        return torneo;
    }

    @Test
    @DisplayName("Lanza 409 si hay empates sin resolver aunque no queden series")
    void lanza409SiHayEmpatesSinResolver() {
        Torneo torneo = torneoEnCurso();
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(serieRepository.countByRondaFaseTorneoIdAndEstadoNot(1L, EstadoSerie.FINALIZADA))
                .thenReturn(0L);

        Usuario capitan = new Usuario("cap@test.com", "hash", "Capitán", Rol.USUARIO);
        List<Equipo> empatados = List.of(
                new Equipo("Karmine Corp", "KC", capitan),
                new Equipo("Fnatic", "FNC", capitan));
        when(clasificacionService.gruposSinResolver(1L)).thenReturn(List.of(empatados));

        assertThatThrownBy(() -> servicio.finalizar(1L, ORGANIZADOR_ID))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("empates");

        assertThat(torneo.getEstado()).isEqualTo(EstadoTorneo.EN_CURSO);
    }
}