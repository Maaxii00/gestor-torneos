package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.clasificacion.CalculadoraClasificacion;
import com.maxi.gestortorneos.clasificacion.FilaClasificacion;
import com.maxi.gestortorneos.dto.FilaClasificacionResponse;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.Equipo;
import com.maxi.gestortorneos.model.Inscripcion;
import com.maxi.gestortorneos.model.Serie;
import com.maxi.gestortorneos.repository.InscripcionRepository;
import com.maxi.gestortorneos.repository.SerieRepository;
import com.maxi.gestortorneos.repository.TorneoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClasificacionService {

    private final TorneoRepository torneoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final SerieRepository serieRepository;
    private final CalculadoraClasificacion calculadora;

    public ClasificacionService(TorneoRepository torneoRepository, InscripcionRepository inscripcionRepository, SerieRepository serieRepository,
    CalculadoraClasificacion calculadora){
        this.torneoRepository = torneoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.serieRepository = serieRepository;
        this.calculadora = calculadora;
    }


    @Transactional(readOnly = true)
    public List<FilaClasificacionResponse> deTorneo(Long torneoId) {

        if (!torneoRepository.existsById(torneoId)) {
            throw new RecursoNoEncontradoException("No existe un torneo con id " + torneoId);
        }

        List<Equipo> equipos = inscripcionRepository.findByTorneoId(torneoId).stream()
                .map(Inscripcion::getEquipo)
                .toList();

        List<Serie> series = serieRepository.findConEquiposPorTorneoId(torneoId);

        List<FilaClasificacion> filas = calculadora.calcular(equipos, series);

        List<FilaClasificacionResponse> respuesta = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            respuesta.add(FilaClasificacionResponse.desde(filas.get(i), i + 1));
        }
        return respuesta;
    }
}
