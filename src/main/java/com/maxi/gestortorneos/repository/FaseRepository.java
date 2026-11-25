package com.maxi.gestortorneos.repository;

import com.maxi.gestortorneos.model.Fase;
import com.maxi.gestortorneos.model.TipoFase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaseRepository extends JpaRepository<Fase, Long> {
    Optional<Fase> findByTorneoIdAndTipo(Long torneoId, TipoFase tipo);
    long countByTorneoId(Long torneoId);
}