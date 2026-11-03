package com.maxi.gestortorneos.repository;

import com.maxi.gestortorneos.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    boolean existsByEquipoIdAndTorneoId(Long equipoId, Long torneoId);

    List<Inscripcion> findByTorneoId(Long torneoId);
}