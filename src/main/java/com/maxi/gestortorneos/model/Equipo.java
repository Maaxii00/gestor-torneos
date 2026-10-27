package com.maxi.gestortorneos.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "equipos")

public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    @Column(nullable = false, unique = true, length = 5)
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "capitan_id", nullable = false)
    private Usuario capitan;

    protected Equipo(){}

    public Equipo(String nombre, String tag, Usuario capitan){
        this.nombre = nombre;
        this.tag = tag;
        this.capitan = capitan;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTag() {
        return tag;
    }

    public Usuario getCapitan() {
        return capitan;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setCapitan(Usuario capitan) {
        this.capitan = capitan;
    }
}
