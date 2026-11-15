package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.PartidoResponse;
import com.maxi.gestortorneos.dto.RegistrarPartidoRequest;
import com.maxi.gestortorneos.service.PartidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    @PostMapping("/api/series/{serieId}/partidos")
    @ResponseStatus(HttpStatus.CREATED)
    public PartidoResponse registrar(@PathVariable Long serieId,
                                     @Valid @RequestBody RegistrarPartidoRequest peticion,
                                     @AuthenticationPrincipal Long solicitanteId) {
        return partidoService.registrar(serieId, peticion.ganadorId(), solicitanteId);
    }
}
