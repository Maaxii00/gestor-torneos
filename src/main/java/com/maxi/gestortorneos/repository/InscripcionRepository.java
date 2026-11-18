package com.maxi.gestortorneos.repository;

import com.maxi.gestortorneos.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    boolean existsByEquipoIdAndTorneoId(Long equipoId, Long torneoId);

    @Query("""
       select i from Inscripcion i
       join fetch i.equipo
       join fetch i.torneo
       where i.torneo.id = :torneoId
       """)
    List<Inscripcion> findConEquipoYTorneoPorTorneoId(@Param("torneoId") Long torneoId);
}