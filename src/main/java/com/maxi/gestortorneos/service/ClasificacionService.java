package com.maxi.gestortorneos.service;

import com.maxi.gestortorneos.clasificacion.CalculadoraClasificacion;
import com.maxi.gestortorneos.clasificacion.FilaClasificacion;
import com.maxi.gestortorneos.dto.FilaClasificacionResponse;
import com.maxi.gestortorneos.exception.RecursoNoEncontradoException;
import com.maxi.gestortorneos.model.Equipo;
import com.maxi.gestortorneos.model.Inscripcion;
import com.maxi.gestortorneos.model.Serie;
import com.maxi.gestortorneos.model.TipoFase;
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

    private List<List<FilaClasificacion>> calcularPuestos(Long torneoId) {
        if (!torneoRepository.existsById(torneoId)) {
            throw new RecursoNoEncontradoException("No existe un torneo con id " + torneoId);
        }

        List<Equipo> equipos = inscripcionRepository.findConEquipoYTorneoPorTorneoId(torneoId).stream()
                .map(Inscripcion::getEquipo)
                .toList();

        List<Serie> series = serieRepository.findConEquiposPorTorneoIdYTipoFase(torneoId, TipoFase.LIGA);
        List<Serie> seriesDesempate = serieRepository.findConEquiposPorTorneoIdYTipoFase(torneoId, TipoFase.DESEMPATE);

        List<FilaClasificacion> tabla = calculadora.calcular(equipos, series);
        return resolutor.resolver(tabla, series, seriesDesempate);
    }


    @Transactional(readOnly = true)
    public List<FilaClasificacionResponse> deTorneo(Long torneoId) {
        List<List<FilaClasificacion>> puestos = calcularPuestos(torneoId);
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

    @Transactional(readOnly = true)
    public List<List<Equipo>> gruposSinResolver(Long torneoId) {
        return calcularPuestos(torneoId).stream()
                .filter(grupo -> grupo.size() > 1)
                .map(grupo -> grupo.stream().map(FilaClasificacion::equipo).toList())
                .toList();
    }

}
