package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.Equipo;

public record EquipoResumen(Long id, String nombre, String tag) {
    public static EquipoResumen desde(Equipo equipo) {
        return new EquipoResumen(
                equipo.getId(),
                equipo.getNombre(),
                equipo.getTag()
        );
    }
}
