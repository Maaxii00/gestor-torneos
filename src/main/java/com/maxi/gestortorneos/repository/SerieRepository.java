package com.maxi.gestortorneos.repository;

import com.maxi.gestortorneos.model.EstadoSerie;
import com.maxi.gestortorneos.model.Serie;
import com.maxi.gestortorneos.model.TipoFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    @Query("""
           select s from Serie s
           join fetch s.equipoA
           join fetch s.equipoB
           left join fetch s.equipoGanador
           where s.ronda.fase.torneo.id = :torneoId
             and s.ronda.fase.tipo = :tipoFase
           """)
    List<Serie> findConEquiposPorTorneoIdYTipoFase(@Param("torneoId") Long torneoId,
                                                   @Param("tipoFase") TipoFase tipoFase);

    @Query("""
           select s from Serie s
           join fetch s.ronda r
           join fetch r.fase f
           join fetch s.equipoA
           join fetch s.equipoB
           left join fetch s.equipoGanador
           where f.torneo.id = :torneoId
           order by f.orden, r.numero, s.id
           """)
    List<Serie> findCalendarioPorTorneoId(@Param("torneoId") Long torneoId);

    long countByRondaFaseTorneoIdAndEstadoNot(Long torneoId, EstadoSerie estado);
}