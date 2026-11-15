package com.maxi.gestortorneos.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "partidos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_partido_serie_numero",
                columnNames = {"serie_id", "numero"}))
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @Column(nullable = false)
    private int numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ganador_id", nullable = false)
    private Equipo ganador;

    @CreationTimestamp
    @Column(name = "jugado_en", nullable = false, updatable = false)
    private Instant jugadoEn;

    protected Partido() {}

    public Partido(Serie serie, int numero, Equipo ganador) {
        this.serie = serie;
        this.numero = numero;
        this.ganador = ganador;
    }

    public Long getId() { return id; }
    public Serie getSerie() { return serie; }
    public int getNumero() { return numero; }
    public Equipo getGanador() { return ganador; }
    public Instant getJugadoEn() { return jugadoEn; }
}