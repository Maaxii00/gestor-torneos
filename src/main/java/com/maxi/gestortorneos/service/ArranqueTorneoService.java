package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.ArranqueResponse;
import com.maxi.gestortorneos.dto.TorneoResponse;
import com.maxi.gestortorneos.exception.ConflictoException;
import com.maxi.gestortorneos.exception.OperacionNoPermitidaException;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.generador.Emparejamiento;
import com.maxi.gestortorneos.generador.GeneradorLiga;
import com.maxi.gestortorneos.model.*;
import com.maxi.gestortorneos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArranqueTorneoService {

    private final InscripcionRepository inscripcionRepository;
    private final RondaRepository rondaRepository;
    private final TorneoRepository torneoRepository;
    private final FaseRepository faseRepository;
    private final SerieRepository serieRepository;
    private final GeneradorLiga generadorLiga;

    public ArranqueTorneoService(InscripcionRepository inscripcionRepository, RondaRepository rondaRepository,
                              TorneoRepository torneoRepository, FaseRepository faseRepository, SerieRepository serieRepository, GeneradorLiga generadorLiga) {
        this.inscripcionRepository = inscripcionRepository;
        this.rondaRepository = rondaRepository;
        this.torneoRepository = torneoRepository;
        this.faseRepository = faseRepository;
        this.serieRepository = serieRepository;
        this.generadorLiga = generadorLiga;
    }

    @Transactional
    public ArranqueResponse iniciar(Long torneoId, Long solicitanteId) {

        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un torneo con id " + torneoId));
        if (!torneo.getOrganizador().getId().equals(solicitanteId)) {
            throw new OperacionNoPermitidaException("Solo el organizador puede iniciar el torneo");
        }
        torneo.iniciar();
        if (torneo.getFormato() == FormatoTorneo.SUIZO){
            throw new ConflictoException("Formato de Torneo aún no disponible");
        }

        List<Inscripcion> inscripciones = inscripcionRepository.findConEquipoYTorneoPorTorneoId(torneoId);
        List<Equipo> listaEquipos = new ArrayList<>();
        for (Inscripcion inscripcion : inscripciones){
            listaEquipos.add(inscripcion.getEquipo());
        }

        List<List<Emparejamiento>> jornadas = generadorLiga.generar(listaEquipos);

        Fase fase = faseRepository.save(new Fase(torneo, "Liguilla", TipoFase.LIGA, 1));

        int victorias = torneo.getFormato().getVictoriasEnLiguilla();
        int seriesCreadas = 0;

        for (int i = 0; i < jornadas.size(); i++) {
            int numero = i + 1;
            Ronda ronda = rondaRepository.save(
                    new Ronda(fase, numero, "Jornada " + numero, victorias));
            for (Emparejamiento emparejamiento : jornadas.get(i)) {
                serieRepository.save(
                        new Serie(ronda, emparejamiento.equipoA(), emparejamiento.equipoB()));
                seriesCreadas++;
            }
        }

        return new ArranqueResponse(TorneoResponse.desde(torneo), jornadas.size(), seriesCreadas);
    }
}
