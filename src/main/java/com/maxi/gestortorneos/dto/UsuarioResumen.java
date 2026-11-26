package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.Usuario;

public record UsuarioResumen(Long id, String nombre) {

    public static UsuarioResumen desde(Usuario usuario) {
        return new UsuarioResumen(usuario.getId(), usuario.getNombre());
    }
}

