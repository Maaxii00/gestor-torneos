package com.maxi.gestortorneos.repository;

import com.maxi.gestortorneos.model.Serie;
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
           """)
    List<Serie> findConEquiposPorTorneoId(@Param("torneoId") Long torneoId);
}