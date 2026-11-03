package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.InscripcionResponse;
import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.exception.OperacionNoPermitidaException;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.*;
import com.maxi.gestortorneos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EquipoRepository equipoRepository;
    private final TorneoRepository torneoRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository,
                              EquipoRepository equipoRepository,
                              TorneoRepository torneoRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.equipoRepository = equipoRepository;
        this.torneoRepository = torneoRepository;
    }

    @Transactional
    public InscripcionResponse inscribir(Long torneoId, Long equipoId, Long solicitanteId) {

        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un torneo con id " + torneoId));

        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un equipo con id " + equipoId));

        if (!equipo.getCapitan().getId().equals(solicitanteId)) {
            throw new OperacionNoPermitidaException(
                    "Solo el capitán puede inscribir a su equipo");
        }

        if (torneo.getEstado() != EstadoTorneo.ABIERTO) {
            throw new ConflictoException(
                    "El torneo no admite inscripciones: está " + torneo.getEstado());
        }

        if (inscripcionRepository.existsByEquipoIdAndTorneoId(equipoId, torneoId)) {
            throw new ConflictoException("Ese equipo ya está inscrito en este torneo");
        }

        Inscripcion inscripcion = inscripcionRepository.save(new Inscripcion(equipo, torneo));
        return InscripcionResponse.desde(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> listarPorTorneo(Long torneoId) {
        if (!torneoRepository.existsById(torneoId)) {
            throw new RecursoNoEncontradoException("No existe un torneo con id " + torneoId);
        }

        return inscripcionRepository.findByTorneoId(torneoId).stream()
                .map(InscripcionResponse::desde)
                .toList();
    }
}
