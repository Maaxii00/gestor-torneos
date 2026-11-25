package com.maxi.gestortorneos.service;
import com.maxi.gestortorneos.dto.DesempateResponse;
import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.exception.OperacionNoPermitidaException;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.generador.Emparejamiento;
import com.maxi.gestortorneos.generador.GeneradorLiga;
import com.maxi.gestortorneos.model.*;
import com.maxi.gestortorneos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DesempateService {

    private final TorneoRepository torneoRepository;
    private final SerieRepository serieRepository;
    private final FaseRepository faseRepository;
    private final RondaRepository rondaRepository;
    private final ClasificacionService clasificacionService;
    private final GeneradorLiga generadorLiga;

    public DesempateService (TorneoRepository torneoRepository,
                             FaseRepository faseRepository,
                             SerieRepository serieRepository,
                             RondaRepository rondaRepository,
                             ClasificacionService clasificacionService,
                             GeneradorLiga generadorLiga) {

        this.torneoRepository = torneoRepository;
        this.faseRepository = faseRepository;
        this.serieRepository = serieRepository;
        this.rondaRepository = rondaRepository;
        this.clasificacionService = clasificacionService;
        this.generadorLiga = generadorLiga;
    }

    @Transactional
    public DesempateResponse crear(Long torneoId, int victoriasParaGanar, Long solicitanteId) {

        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un torneo con id " + torneoId));

        if (!torneo.getOrganizador().getId().equals(solicitanteId)) {
            throw new OperacionNoPermitidaException("Solo el organizador puede organizar desempates");
        }

        if (torneo.getEstado() != EstadoTorneo.EN_CURSO) {
            throw new ConflictoException("Solo se organizan desempates en un torneo en curso");
        }

        long pendientes = serieRepository.countByRondaFaseTorneoIdAndEstadoNot(
                torneoId, EstadoSerie.FINALIZADA);
        if (pendientes > 0) {
            throw new ConflictoException(
                    "Quedan " + pendientes + " series por jugar antes de organizar un desempate");
        }

        List<List<Equipo>> grupos = clasificacionService.gruposSinResolver(torneoId);
        if (grupos.isEmpty()) {
            throw new ConflictoException("No hay empates sin resolver");
        }

        Fase fase = faseRepository.findByTorneoIdAndTipo(torneoId, TipoFase.DESEMPATE)
                .orElseGet(() -> faseRepository.save(new Fase(
                        torneo, "Desempate", TipoFase.DESEMPATE,
                        (int) faseRepository.countByTorneoId(torneoId) + 1)));

        int numero = (int) rondaRepository.countByFaseId(fase.getId()) + 1;
        Ronda ronda = rondaRepository.save(
                new Ronda(fase, numero, "Desempate " + numero, victoriasParaGanar));

        int seriesCreadas = 0;
        for (List<Equipo> grupo : grupos) {
            for (List<Emparejamiento> jornada : generadorLiga.generar(grupo)) {
                for (Emparejamiento emparejamiento : jornada) {
                    serieRepository.save(
                            new Serie(ronda, emparejamiento.equipoA(), emparejamiento.equipoB()));
                    seriesCreadas++;
                }
            }
        }

        return new DesempateResponse(grupos.size(), seriesCreadas);
    }
}
