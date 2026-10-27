package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.*;
import com.maxi.gestortorneos.service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    @PostMapping
    public ResponseEntity<TorneoResponse> crear(@Valid @RequestBody CrearTorneoRequest peticion) {
        TorneoResponse creado = torneoService.crear(
                peticion.nombre(), peticion.formato(), peticion.organizadorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<TorneoResponse> listar() {
        return torneoService.listar();
    }

    @GetMapping("/{id}")
    public TorneoResponse porId(@PathVariable Long id) {
        return torneoService.buscarPorId(id);
    }
}