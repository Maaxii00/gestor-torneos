package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.TorneoResponse;
import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.exception.OperacionNoPermitidaException;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.EstadoSerie;
import com.maxi.gestortorneos.model.Torneo;
import com.maxi.gestortorneos.repository.SerieRepository;
import com.maxi.gestortorneos.repository.TorneoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinalizacionTorneoService {

    private final TorneoRepository torneoRepository;
    private final SerieRepository serieRepository;
    private final ClasificacionService clasificacionService;

    public FinalizacionTorneoService(TorneoRepository torneoRepository, SerieRepository serieRepository, ClasificacionService clasificacionService) {
        this.torneoRepository = torneoRepository;
        this.serieRepository = serieRepository;
        this.clasificacionService = clasificacionService;
    }

    @Transactional
    public TorneoResponse finalizar(Long torneoId, Long solicitanteId) {

        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un torneo con id " + torneoId));

        if (!torneo.getOrganizador().getId().equals(solicitanteId)) {
            throw new OperacionNoPermitidaException("Solo el organizador puede finalizar el torneo");
        }

        long pendientes = serieRepository.countByRondaFaseTorneoIdAndEstadoNot(
                torneoId, EstadoSerie.FINALIZADA);
        if (pendientes > 0) {
            throw new ConflictoException(
                    "El torneo no se puede finalizar: quedan " + pendientes + " series por jugar");
        }

        if (!clasificacionService.gruposSinResolver(torneoId).isEmpty()) {
            throw new ConflictoException(
                    "El torneo no se puede finalizar: hay empates sin resolver");
        }
        torneo.finalizar();
        return TorneoResponse.desde(torneo);
    }
}