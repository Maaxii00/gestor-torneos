package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.PartidoResponse;
import com.maxi.gestortorneos.exception.OperacionNoPermitidaException;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.Equipo;
import com.maxi.gestortorneos.model.Partido;
import com.maxi.gestortorneos.model.Serie;
import com.maxi.gestortorneos.repository.EquipoRepository;
import com.maxi.gestortorneos.repository.PartidoRepository;
import com.maxi.gestortorneos.repository.SerieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidoService {

    private final SerieRepository serieRepository;
    private final EquipoRepository equipoRepository;
    private final PartidoRepository partidoRepository;

    public PartidoService(SerieRepository serieRepository,
                          EquipoRepository equipoRepository,
                          PartidoRepository partidoRepository) {
        this.serieRepository = serieRepository;
        this.equipoRepository = equipoRepository;
        this.partidoRepository = partidoRepository;
    }

    @Transactional
    public PartidoResponse registrar(Long serieId, Long ganadorId, Long solicitanteId) {

        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una serie con id " + serieId));

        Long organizadorId = serie.getRonda().getFase().getTorneo()
                .getOrganizador().getId();

        if (!organizadorId.equals(solicitanteId)) {
            throw new OperacionNoPermitidaException(
                    "Solo el organizador puede registrar resultados");
        }

        Equipo ganador = equipoRepository.findById(ganadorId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un equipo con id " + ganadorId));

        serie.registrarVictoriaDe(ganador);

        int numero = (int) partidoRepository.countBySerieId(serieId) + 1;
        Partido partido = partidoRepository.save(new Partido(serie, numero, ganador));

        return PartidoResponse.desde(partido);
    }
}
