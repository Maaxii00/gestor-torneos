    package com.maxi.gestortorneos.model;


    import jakarta.persistence.*;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.JdbcTypeCode;
    import org.hibernate.type.SqlTypes;

    import java.time.Instant;

    @Entity
    @Table(name = "torneos")

    public class Torneo {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = false, length = 120)
        private String nombre;

        @Enumerated(EnumType.STRING)
        @JdbcTypeCode(SqlTypes.VARCHAR)
        @Column(nullable = false, length = 20)
        private FormatoTorneo formato;

        @Enumerated(EnumType.STRING)
        @JdbcTypeCode(SqlTypes.VARCHAR)
        @Column(nullable = false, length = 20)
        private EstadoTorneo estado;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "organizador_id", nullable = false)
        private Usuario organizador;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private Instant creadoEn;

        protected Torneo (){}

        public Torneo(String nombre, FormatoTorneo formato, Usuario organizador){
            this.nombre= nombre;
            this.formato=formato;
            this.estado = EstadoTorneo.ABIERTO;
            this.organizador = organizador;
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public FormatoTorneo getFormato() {
            return formato;
        }

        public EstadoTorneo getEstado() {
            return estado;
        }

        public Usuario getOrganizador() {
            return organizador;
        }

        public Instant getCreadoEn() {
            return creadoEn;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public void setFormato(FormatoTorneo formato) {
            this.formato = formato;
        }

        public void setEstado(EstadoTorneo estado) {
            this.estado = estado;
        }

    }
