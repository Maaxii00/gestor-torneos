package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.CrearEquipoRequest;
import com.maxi.gestortorneos.dto.EquipoResponse;
import com.maxi.gestortorneos.service.EquipoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @PostMapping
    public ResponseEntity<EquipoResponse> crear(
            @Valid @RequestBody CrearEquipoRequest peticion,
            @AuthenticationPrincipal Long capitanId) {

        EquipoResponse creado = equipoService.crear(
                peticion.nombre(), peticion.tag(), capitanId);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<EquipoResponse> listar() {
        return equipoService.listar();
    }

    @GetMapping("/{id}")
    public EquipoResponse porId(@PathVariable Long id) {
        return equipoService.buscarPorId(id);
    }

    @GetMapping("/tag/{tag}")
    public EquipoResponse porTag(@PathVariable String tag) {
        return equipoService.buscarPorTag(tag);
    }
}