    package com.maxi.gestortorneos.repository;
    import com.maxi.gestortorneos.model.EstadoTorneo;
    import com.maxi.gestortorneos.model.Torneo;
    import org.springframework.data.jpa.repository.JpaRepository;

    public interface TorneoRepository extends JpaRepository<Torneo, Long> {
        long countByOrganizadorIdAndEstadoNot(Long organizadorId, EstadoTorneo estado);
    }