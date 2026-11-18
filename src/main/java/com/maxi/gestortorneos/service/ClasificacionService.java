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
import com.maxi.gestortorneos.clasificacion.desempate.ResolutorDeEmpates;
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
    private final ResolutorDeEmpates resolutor;

    public ClasificacionService(TorneoRepository torneoRepository,
                                InscripcionRepository inscripcionRepository,
                                SerieRepository serieRepository,
                                CalculadoraClasificacion calculadora,
                                ResolutorDeEmpates resolutor) {
        this.torneoRepository = torneoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.serieRepository = serieRepository;
        this.calculadora = calculadora;
        this.resolutor = resolutor;
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
        List<FilaClasificacion> tabla = calculadora.calcular(equipos, series);
        List<List<FilaClasificacion>> puestos = resolutor.resolver(tabla, series);

        List<FilaClasificacionResponse> respuesta = new ArrayList<>();
        int posicion = 1;

        for (List<FilaClasificacion> grupo : puestos) {
            boolean empatados = grupo.size() > 1;

            for (FilaClasificacion fila : grupo) {
                respuesta.add(FilaClasificacionResponse.desde(fila, posicion, empatados));
            }
            posicion += grupo.size();
        }
        return respuesta;
    }
}
