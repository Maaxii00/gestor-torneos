package com.maxi.gestortorneos.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "inscripciones",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inscripcion_equipo_torneo",
                columnNames = {"equipo_id", "torneo_id"}))

public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant inscritoEn;

    protected Inscripcion(){}

    public Inscripcion(Equipo equipo, Torneo torneo){
        this.equipo= equipo;
        this.torneo=torneo;
    }

    public Long getId() {
        return id;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public Instant getInscritoEn() {
        return inscritoEn;
    }

}
