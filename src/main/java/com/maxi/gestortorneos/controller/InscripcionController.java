package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.CrearInscripcionRequest;
import com.maxi.gestortorneos.dto.InscripcionResponse;
import com.maxi.gestortorneos.service.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping("/api/inscripciones")
    public ResponseEntity<InscripcionResponse> inscribir(
            @Valid @RequestBody CrearInscripcionRequest peticion,
            @AuthenticationPrincipal Long solicitanteId) {

        InscripcionResponse creada = inscripcionService.inscribir(
                peticion.torneoId(), peticion.equipoId(), solicitanteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/api/torneos/{torneoId}/inscripciones")
    public List<InscripcionResponse> porTorneo(@PathVariable Long torneoId) {
        return inscripcionService.listarPorTorneo(torneoId);
    }
}