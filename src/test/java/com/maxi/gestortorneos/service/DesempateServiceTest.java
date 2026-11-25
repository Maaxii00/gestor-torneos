package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.DesempateResponse;
import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.exception.OperacionNoPermitidaException;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.generador.GeneradorLiga;
import com.maxi.gestortorneos.model.*;
import com.maxi.gestortorneos.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesempateServiceTest {

    private static final Long ORGANIZADOR_ID = 1L;
    private static final Long TORNEO_ID = 1L;

    @Mock private TorneoRepository torneoRepository;
    @Mock private FaseRepository faseRepository;
    @Mock private SerieRepository serieRepository;
    @Mock private RondaRepository rondaRepository;
    @Mock private ClasificacionService clasificacionService;

    private DesempateService servicio;
    private Torneo torneo;
    private List<Equipo> tresEmpatados;

    @BeforeEach
    void preparar() {
        servicio = new DesempateService(torneoRepository, faseRepository, serieRepository,
                rondaRepository, clasificacionService, new GeneradorLiga());

        Usuario organizador = new Usuario("org@test.com", "hash", "Org", Rol.USUARIO);
        ReflectionTestUtils.setField(organizador, "id", ORGANIZADOR_ID);

        torneo = new Torneo("Liga", FormatoTorneo.LIGA_CLASICA, organizador);
        ReflectionTestUtils.setField(torneo, "id", TORNEO_ID);
        ReflectionTestUtils.setField(torneo, "estado", EstadoTorneo.EN_CURSO);

        tresEmpatados = List.of(
                equipo(2L, "KC"), equipo(38L, "FNC"), equipo(39L, "TH"));
    }

    @Test
    @DisplayName("Lanza 404 si el torneo no existe")
    void lanza404SiElTorneoNoExiste() {
        when(torneoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.crear(999L, 1, ORGANIZADOR_ID))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Lanza 403 si no lo pide el organizador")
    void lanza403SiNoEsElOrganizador() {
        when(torneoRepository.findById(TORNEO_ID)).thenReturn(Optional.of(torneo));

        assertThatThrownBy(() -> servicio.crear(TORNEO_ID, 1, 2L))
                .isInstanceOf(OperacionNoPermitidaException.class);

        verify(serieRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lanza 409 si el torneo no está en curso")
    void lanza409SiNoEstaEnCurso() {
        ReflectionTestUtils.setField(torneo, "estado", EstadoTorneo.FINALIZADO);
        when(torneoRepository.findById(TORNEO_ID)).thenReturn(Optional.of(torneo));

        assertThatThrownBy(() -> servicio.crear(TORNEO_ID, 1, ORGANIZADOR_ID))
                .isInstanceOf(ConflictoException.class);

        verify(serieRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lanza 409 si quedan series por jugar, sin mirar la clasificación")
    void lanza409SiQuedanSeriesPendientes() {
        when(torneoRepository.findById(TORNEO_ID)).thenReturn(Optional.of(torneo));
        when(serieRepository.countByRondaFaseTorneoIdAndEstadoNot(TORNEO_ID, EstadoSerie.FINALIZADA))
                .thenReturn(2L);

        assertThatThrownBy(() -> servicio.crear(TORNEO_ID, 1, ORGANIZADOR_ID))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("2");

        verify(clasificacionService, never()).gruposSinResolver(any());
        verify(serieRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lanza 409 si no hay empates sin resolver")
    void lanza409SiNoHayEmpates() {
        when(torneoRepository.findById(TORNEO_ID)).thenReturn(Optional.of(torneo));
        when(serieRepository.countByRondaFaseTorneoIdAndEstadoNot(TORNEO_ID, EstadoSerie.FINALIZADA))
                .thenReturn(0L);
        when(clasificacionService.gruposSinResolver(TORNEO_ID)).thenReturn(List.of());

        assertThatThrownBy(() -> servicio.crear(TORNEO_ID, 1, ORGANIZADOR_ID))
                .isInstanceOf(ConflictoException.class);

        verify(faseRepository, never()).save(any());
        verify(serieRepository, never()).save(any());
    }

    @Test
    @DisplayName("El primer desempate crea la fase, la ronda 1 y una mini-liguilla de 3 series")
    void primerDesempateCreaFaseYRonda() {
        prepararTorneoListoParaDesempate();
        when(faseRepository.findByTorneoIdAndTipo(TORNEO_ID, TipoFase.DESEMPATE))
                .thenReturn(Optional.empty());
        when(faseRepository.countByTorneoId(TORNEO_ID)).thenReturn(1L);
        when(faseRepository.save(any(Fase.class))).thenAnswer(inv -> inv.getArgument(0));

        DesempateResponse respuesta = servicio.crear(TORNEO_ID, 1, ORGANIZADOR_ID);

        ArgumentCaptor<Fase> fase = ArgumentCaptor.forClass(Fase.class);
        verify(faseRepository).save(fase.capture());
        assertThat(fase.getValue().getTipo()).isEqualTo(TipoFase.DESEMPATE);
        assertThat(fase.getValue().getOrden()).isEqualTo(2);

        ArgumentCaptor<Ronda> ronda = ArgumentCaptor.forClass(Ronda.class);
        verify(rondaRepository).save(ronda.capture());
        assertThat(ronda.getValue().getNombre()).isEqualTo("Desempate 1");
        assertThat(ronda.getValue().getVictoriasParaGanarSerie()).isEqualTo(1);

        verify(serieRepository, times(3)).save(any(Serie.class));
        assertThat(respuesta).isEqualTo(new DesempateResponse(1, 3));
    }

    @Test
    @DisplayName("Un segundo desempate reutiliza la fase y crea la ronda 2")
    void segundoDesempateReutilizaLaFase() {
        prepararTorneoListoParaDesempate();
        Fase existente = new Fase(torneo, "Desempate", TipoFase.DESEMPATE, 2);
        ReflectionTestUtils.setField(existente, "id", 10L);
        when(faseRepository.findByTorneoIdAndTipo(TORNEO_ID, TipoFase.DESEMPATE))
                .thenReturn(Optional.of(existente));
        when(rondaRepository.countByFaseId(10L)).thenReturn(1L);

        servicio.crear(TORNEO_ID, 1, ORGANIZADOR_ID);

        verify(faseRepository, never()).save(any());

        ArgumentCaptor<Ronda> ronda = ArgumentCaptor.forClass(Ronda.class);
        verify(rondaRepository).save(ronda.capture());
        assertThat(ronda.getValue().getNombre()).isEqualTo("Desempate 2");
        assertThat(ronda.getValue().getFase()).isSameAs(existente);
    }

    private void prepararTorneoListoParaDesempate() {
        when(torneoRepository.findById(TORNEO_ID)).thenReturn(Optional.of(torneo));
        when(serieRepository.countByRondaFaseTorneoIdAndEstadoNot(TORNEO_ID, EstadoSerie.FINALIZADA))
                .thenReturn(0L);
        when(clasificacionService.gruposSinResolver(TORNEO_ID)).thenReturn(List.of(tresEmpatados));
        when(rondaRepository.save(any(Ronda.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Equipo equipo(Long id, String tag) {
        Equipo equipo = new Equipo("Equipo " + tag, tag, torneo.getOrganizador());
        ReflectionTestUtils.setField(equipo, "id", id);
        return equipo;
    }
}