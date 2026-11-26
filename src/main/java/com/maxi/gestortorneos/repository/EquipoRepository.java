package com.maxi.gestortorneos.repository;

import com.maxi.gestortorneos.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    Optional<Equipo> findByNombre(String nombre);
    Optional<Equipo> findByTag(String tag);

    boolean existsByNombre(String nombre);
    List<Equipo> findByCapitanId(Long capitanId);
    boolean existsByTag(String tag);
}