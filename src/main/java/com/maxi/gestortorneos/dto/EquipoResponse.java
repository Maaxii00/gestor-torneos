package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.Equipo;

public record EquipoResponse(Long id, String nombre, String tag, UsuarioResponse capitan) {

    public static EquipoResponse desde(Equipo equipo) {
        return new EquipoResponse(
                equipo.getId(),
                equipo.getNombre(),
                equipo.getTag(),
                UsuarioResponse.desde(equipo.getCapitan())
        );
    }
}