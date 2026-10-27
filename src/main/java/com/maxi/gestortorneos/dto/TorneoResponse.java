package com.maxi.gestortorneos.dto;
import com.maxi.gestortorneos.model.EstadoTorneo;
import com.maxi.gestortorneos.model.FormatoTorneo;
import com.maxi.gestortorneos.model.Torneo;

import java.time.Instant;

public record TorneoResponse (Long id, String nombre, EstadoTorneo estado, FormatoTorneo formato, UsuarioResponse organizador, Instant creadoEn) {

    public static TorneoResponse desde(Torneo torneo) {
        return new TorneoResponse(
                torneo.getId(),
                torneo.getNombre(),
                torneo.getEstado(),
                torneo.getFormato(),
                UsuarioResponse.desde(torneo.getOrganizador()),
                torneo.getCreadoEn()
        );
    }
}