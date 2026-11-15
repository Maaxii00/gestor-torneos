package com.maxi.gestortorneos.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "fases",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_fase_torneo_orden",
                columnNames = {"torneo_id", "orden"}))
public class Fase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private TipoFase tipo;

    @Column(nullable = false)
    private int orden;

    protected Fase() {}

    public Fase(Torneo torneo, String nombre, TipoFase tipo, int orden) {
        this.torneo = torneo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.orden = orden;
    }

    public Long getId() { return id; }
    public Torneo getTorneo() { return torneo; }
    public String getNombre() { return nombre; }
    public TipoFase getTipo() { return tipo; }
    public int getOrden() { return orden; }
}