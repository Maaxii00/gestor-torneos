package com.maxi.gestortorneos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rondas",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ronda_fase_numero",
                columnNames = {"fase_id", "numero"}))
public class Ronda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fase_id", nullable = false)
    private Fase fase;

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(name = "victorias_para_ganar_serie", nullable = false)
    private int victoriasParaGanarSerie;

    protected Ronda() {}

    public Ronda(Fase fase, int numero, String nombre, int victoriasParaGanarSerie) {
        this.fase = fase;
        this.numero = numero;
        this.nombre = nombre;
        this.victoriasParaGanarSerie = victoriasParaGanarSerie;
    }

    public Long getId() { return id; }
    public Fase getFase() { return fase; }
    public int getNumero() { return numero; }
    public String getNombre() { return nombre; }
    public int getVictoriasParaGanarSerie() { return victoriasParaGanarSerie; }
}