package com.maxi.gestortorneos.model;

import com.maxi.gestortorneos.exception.ConflictoException;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "series")
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ronda_id", nullable = false)
    private Ronda ronda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_a_id", nullable = false)
    private Equipo equipoA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_b_id", nullable = false)
    private Equipo equipoB;

    @Column(name = "victorias_equipo_a", nullable = false)
    private int victoriasEquipoA;

    @Column(name = "victorias_equipo_b", nullable = false)
    private int victoriasEquipoB;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private EstadoSerie estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ganador_id")
    private Equipo equipoGanador;

    protected Serie() {}

    public Serie(Ronda ronda, Equipo equipoA, Equipo equipoB) {
        this.ronda = ronda;
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.estado = EstadoSerie.PENDIENTE;
    }

    public Long getId() { return id; }
    public Ronda getRonda() { return ronda; }
    public Equipo getEquipoA() { return equipoA; }
    public Equipo getEquipoB() { return equipoB; }
    public int getVictoriasEquipoA() { return victoriasEquipoA; }
    public int getVictoriasEquipoB() { return victoriasEquipoB; }
    public EstadoSerie getEstado() { return estado; }
    public Equipo getGanador() { return equipoGanador; }



    public void registrarVictoriaDe(Equipo ganador) {
        if (estado == EstadoSerie.FINALIZADA) {
            throw new ConflictoException("No se juegan partidos de una serie cerrada");
        }
        boolean esA = ganador.getId().equals(equipoA.getId());
        boolean esB = ganador.getId().equals(equipoB.getId());
        if (!esA && !esB) {
            throw new ConflictoException("No puedes ganar una serie en la que no juegas");
        }
        int victoriasDelGanador;
        if (esA) {
            victoriasEquipoA++;
            victoriasDelGanador = victoriasEquipoA;
        } else {
            victoriasEquipoB++;
            victoriasDelGanador = victoriasEquipoB;
        }
        estado = EstadoSerie.EN_CURSO;
        int necesarias = ronda.getVictoriasParaGanarSerie();
        if (victoriasDelGanador == necesarias) {
            estado = EstadoSerie.FINALIZADA;
            equipoGanador = ganador;
        }
    }
}