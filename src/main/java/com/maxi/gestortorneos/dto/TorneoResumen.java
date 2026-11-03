package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.EstadoTorneo;
import com.maxi.gestortorneos.model.Torneo;

public record TorneoResumen(Long id, String nombre, EstadoTorneo estado) {
    public static TorneoResumen desde(Torneo torneo) {
        return new TorneoResumen(
                torneo.getId(),
                torneo.getNombre(),
                torneo.getEstado()
        );
    }
}
