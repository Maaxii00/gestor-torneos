package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.dto.RondaResponse;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.Ronda;
import com.maxi.gestortorneos.model.Serie;
import com.maxi.gestortorneos.repository.SerieRepository;
import com.maxi.gestortorneos.repository.TorneoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarioService {

    private final TorneoRepository torneoRepository;
    private final SerieRepository serieRepository;

    public CalendarioService(TorneoRepository torneoRepository, SerieRepository serieRepository) {
        this.torneoRepository = torneoRepository;
        this.serieRepository = serieRepository;
    }

    @Transactional(readOnly = true)
    public List<RondaResponse> deTorneo(Long torneoId) {
        if (!torneoRepository.existsById(torneoId)) {
            throw new RecursoNoEncontradoException("No existe un torneo con id " + torneoId);
        }

        Map<Ronda, List<Serie>> seriesPorRonda = serieRepository.findCalendarioPorTorneoId(torneoId)
                .stream()
                .collect(Collectors.groupingBy(Serie::getRonda, LinkedHashMap::new, Collectors.toList()));

        return seriesPorRonda.entrySet().stream()
                .map(entrada -> RondaResponse.desde(entrada.getKey(), entrada.getValue()))
                .toList();
    }
}