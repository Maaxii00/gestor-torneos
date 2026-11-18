package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.CrearInscripcionRequest;
import com.maxi.gestortorneos.dto.InscripcionResponse;
import com.maxi.gestortorneos.service.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public InscripcionResponse inscribir(
            @Valid @RequestBody CrearInscripcionRequest peticion,
            @AuthenticationPrincipal Long solicitanteId) {

        return inscripcionService.inscribir(peticion.torneoId(), peticion.equipoId(), solicitanteId);
    }

    @GetMapping("/api/torneos/{torneoId}/inscripciones")
    public List<InscripcionResponse> porTorneo(@PathVariable Long torneoId) {
        return inscripcionService.listarPorTorneo(torneoId);
    }
}