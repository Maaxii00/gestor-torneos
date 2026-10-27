package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.Rol;
import com.maxi.gestortorneos.model.Usuario;

public record UsuarioResponse(Long id, String email, String nombre, Rol rol) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRol()
        );
    }
}