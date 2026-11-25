package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.CrearDesempateRequest;
import com.maxi.gestortorneos.dto.DesempateResponse;
import com.maxi.gestortorneos.service.DesempateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class DesempateController {

    private final DesempateService desempateService;

    public DesempateController(DesempateService desempateService) {
        this.desempateService = desempateService;
    }

    @PostMapping("/api/torneos/{torneoId}/desempates")
    @ResponseStatus(HttpStatus.CREATED)
    public DesempateResponse crear(@PathVariable Long torneoId,
                                   @Valid @RequestBody CrearDesempateRequest peticion,
                                   @AuthenticationPrincipal Long solicitanteId) {
        return desempateService.crear(torneoId, peticion.victoriasParaGanar(), solicitanteId);
    }
}
