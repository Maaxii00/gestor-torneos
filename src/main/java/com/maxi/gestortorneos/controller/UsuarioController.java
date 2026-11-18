package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.CrearUsuarioRequest;
import com.maxi.gestortorneos.dto.UsuarioResponse;
import com.maxi.gestortorneos.model.Usuario;
import com.maxi.gestortorneos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest peticion) {
        Usuario creado = usuarioService.registrar(
                peticion.email(),
                peticion.password(),
                peticion.nombre()
        );
        return UsuarioResponse.desde(creado);
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar().stream()
                .map(UsuarioResponse::desde)
                .toList();
    }
}