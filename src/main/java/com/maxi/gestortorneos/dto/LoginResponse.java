package com.maxi.gestortorneos.dto;

import com.maxi.gestortorneos.model.Usuario;

public record LoginResponse(String token, String tipo, UsuarioResponse usuario) {

    public static LoginResponse de(String token, Usuario usuario) {
        return new LoginResponse(token, "Bearer", UsuarioResponse.desde(usuario));
    }
}
