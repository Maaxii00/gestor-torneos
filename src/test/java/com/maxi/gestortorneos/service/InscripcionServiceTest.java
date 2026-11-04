package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.Inscripcion;
import com.maxi.gestortorneos.repository.EquipoRepository;
import com.maxi.gestortorneos.repository.InscripcionRepository;
import com.maxi.gestortorneos.repository.TorneoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.maxi.gestortorneos.dto.InscripcionResponse;
import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.exception.OperacionNoPermitidaException;
import com.maxi.gestortorneos.model.*;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private TorneoRepository torneoRepository;

    private InscripcionService servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new InscripcionService(
                inscripcionRepository,
                equipoRepository,
                torneoRepository);
    }

    @Test
    @DisplayName("Lanza 404 cuando el torneo no existe")
    void lanza404CuandoElTorneoNoExiste() {

        // Given: el repositorio no encuentra el torneo 999
        when(torneoRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then: inscribir falla con la excepción de recurso no encontrado
        assertThatThrownBy(() -> servicio.inscribir(999L, 1L, 1L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");

        // Y no se ha guardado nada
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario("capitan@test.com", "hash", "Capitán", Rol.USUARIO);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private Equipo equipo(Long id, Usuario capitan) {
        Equipo equipo = new Equipo("LUA Gaming", "LUA", capitan);
        ReflectionTestUtils.setField(equipo, "id", id);
        return equipo;
    }

    private Torneo torneo(Long id, EstadoTorneo estado) {
        Torneo torneo = new Torneo("LEC", FormatoTorneo.LIGA_CLASICA, usuario(99L));
        ReflectionTestUtils.setField(torneo, "id", id);
        ReflectionTestUtils.setField(torneo, "estado", estado);
        return torneo;
    }

    @Test
    @DisplayName("Lanza 404 cuando el equipo no existe")
    void lanza404CuandoElEquipoNoExiste() {

        when(torneoRepository.findById(1L))
                .thenReturn(Optional.of(torneo(1L, EstadoTorneo.ABIERTO)));
        when(equipoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.inscribir(1L, 999L, 1L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");

        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Lanza 403 cuando el solicitante no es el capitán del equipo")
    void lanza403CuandoElSolicitanteNoEsElCapitan() {

        when(torneoRepository.findById(1L))
                .thenReturn(Optional.of(torneo(1L, EstadoTorneo.ABIERTO)));
        when(equipoRepository.findById(2L))
                .thenReturn(Optional.of(equipo(2L, usuario(7L))));

        // El capitán es el usuario 7, pero quien pide la inscripción es el 99
        assertThatThrownBy(() -> servicio.inscribir(1L, 2L, 99L))
                .isInstanceOf(OperacionNoPermitidaException.class);

        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Lanza 409 cuando el torneo no está abierto")
    void lanza409CuandoElTorneoNoEstaAbierto() {

        when(torneoRepository.findById(1L))
                .thenReturn(Optional.of(torneo(1L, EstadoTorneo.EN_CURSO)));
        when(equipoRepository.findById(2L))
                .thenReturn(Optional.of(equipo(2L, usuario(7L))));

        assertThatThrownBy(() -> servicio.inscribir(1L, 2L, 7L))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("EN_CURSO");

        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Lanza 409 cuando el equipo ya está inscrito en ese torneo")
    void lanza409CuandoElEquipoYaEstaInscrito() {

        when(torneoRepository.findById(1L))
                .thenReturn(Optional.of(torneo(1L, EstadoTorneo.ABIERTO)));
        when(equipoRepository.findById(2L))
                .thenReturn(Optional.of(equipo(2L, usuario(7L))));
        when(inscripcionRepository.existsByEquipoIdAndTorneoId(2L, 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> servicio.inscribir(1L, 2L, 7L))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("ya está inscrito");

        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Guarda la inscripción y devuelve el DTO cuando todo es correcto")
    void guardaLaInscripcionCuandoTodoEsCorrecto() {

        Usuario capitan = usuario(7L);
        Equipo equipo = equipo(2L, capitan);
        Torneo torneo = torneo(1L, EstadoTorneo.ABIERTO);

        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(equipoRepository.findById(2L)).thenReturn(Optional.of(equipo));
        when(inscripcionRepository.existsByEquipoIdAndTorneoId(2L, 1L)).thenReturn(false);
        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenReturn(new Inscripcion(equipo, torneo));

        InscripcionResponse respuesta = servicio.inscribir(1L, 2L, 7L);

        assertThat(respuesta.equipo().tag()).isEqualTo("LUA");
        assertThat(respuesta.torneo().nombre()).isEqualTo("LEC");

        verify(inscripcionRepository).save(any(Inscripcion.class));
    }
}